package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public Map<String, Object> getDailyStats(UserPrincipal principal) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Long barberId = principal.getBarberId();

        BigDecimal revenue = invoiceRepository.sumRevenueByBarberAndPeriod(barberId, start, end);
        long totalAppointments = appointmentRepository.countQueueForToday(barberId, LocalDateTime.now());

        return Map.of(
                "date", LocalDate.now(),
                "revenue", revenue != null ? revenue : BigDecimal.ZERO,
                "totalAppointments", totalAppointments
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByPeriod(Long barberId, LocalDateTime from, LocalDateTime to, String groupBy) {
        String truncFunc = switch (groupBy.toUpperCase()) {
            case "WEEK" -> "week";
            case "MONTH" -> "month";
            default -> "day";
        };

        String sql = """
                SELECT DATE_TRUNC('%s', i.issued_at) AS period,
                       COALESCE(SUM(i.total), 0) AS revenue,
                       COUNT(i.id) AS count
                FROM invoices i
                WHERE i.barber_id = ? AND i.issued_at BETWEEN ? AND ?
                GROUP BY period
                ORDER BY period ASC
                """.formatted(truncFunc);

        return jdbcTemplate.queryForList(sql, barberId,
                java.sql.Timestamp.valueOf(from),
                java.sql.Timestamp.valueOf(to));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopServices(Long barberId, LocalDateTime from, LocalDateTime to) {
        String sql = """
                SELECT s.name, s.category, COUNT(a.id) AS count, SUM(il.total_price) AS revenue
                FROM invoice_lines il
                JOIN services s ON il.service_id = s.id
                JOIN invoices i ON il.invoice_id = i.id
                WHERE i.barber_id = ? AND i.issued_at BETWEEN ? AND ? AND il.line_type = 'SERVICE'
                GROUP BY s.id, s.name, s.category
                ORDER BY count DESC
                LIMIT 10
                """;

        return jdbcTemplate.queryForList(sql, barberId,
                java.sql.Timestamp.valueOf(from),
                java.sql.Timestamp.valueOf(to));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLoyalClients(Long barberId, int limit) {
        String sql = """
                SELECT u.id, u.first_name, u.last_name, u.email, u.phone,
                       COUNT(a.id) AS visit_count,
                       MAX(a.start_time) AS last_visit
                FROM appointments a
                JOIN users u ON a.client_id = u.id
                WHERE a.barber_id = ? AND a.status = 'COMPLETED'
                GROUP BY u.id, u.first_name, u.last_name, u.email, u.phone
                ORDER BY visit_count DESC
                LIMIT ?
                """;

        return jdbcTemplate.queryForList(sql, barberId, limit);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopProducts(Long barberId, LocalDateTime from, LocalDateTime to) {
        String sql = """
                SELECT p.name, p.category, SUM(il.quantity) AS quantity_sold, SUM(il.total_price) AS revenue
                FROM invoice_lines il
                JOIN products p ON il.product_id = p.id
                JOIN invoices i ON il.invoice_id = i.id
                WHERE i.barber_id = ? AND i.issued_at BETWEEN ? AND ? AND il.line_type = 'PRODUCT'
                GROUP BY p.id, p.name, p.category
                ORDER BY quantity_sold DESC
                LIMIT 10
                """;

        return jdbcTemplate.queryForList(sql, barberId,
                java.sql.Timestamp.valueOf(from),
                java.sql.Timestamp.valueOf(to));
    }
}
