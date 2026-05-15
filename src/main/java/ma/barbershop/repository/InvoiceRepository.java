package ma.barbershop.repository;

import ma.barbershop.domain.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndUserCentreSoinId(Long id, Long userCentreSoinId);
    Optional<Invoice> findByAppointmentId(Long appointmentId);

    Page<Invoice> findByUserCentreSoinIdAndIssuedAtBetweenOrderByIssuedAtDesc(Long userCentreSoinId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE i.userCentreSoin.id = :userCentreSoinId AND i.issuedAt BETWEEN :from AND :to")
    java.math.BigDecimal sumRevenueByBarberAndPeriod(Long userCentreSoinId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.userCentreSoin.id = :userCentreSoinId")
    Optional<String> findLastInvoiceNumber(Long userCentreSoinId);
}
