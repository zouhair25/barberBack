package ma.barbershop.repository;

import ma.barbershop.domain.entity.Appointment;
import ma.barbershop.domain.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByBarberIdAndStartTimeBetween(Long barberId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId AND a.startTime BETWEEN :from AND :to AND a.status NOT IN ('CANCELLED', 'NO_SHOW') ORDER BY a.startTime ASC")
    List<Appointment> findActiveByBarberAndDateRange(Long barberId, LocalDateTime from, LocalDateTime to);

    Page<Appointment> findByClientIdOrderByStartTimeDesc(Long clientId, Pageable pageable);

    Optional<Appointment> findByIdAndBarberId(Long id, Long barberId);
    Optional<Appointment> findByIdAndClientId(Long id, Long clientId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.barber.id = :barberId AND DATE(a.startTime) = DATE(:date) AND a.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS')")
    long countQueueForToday(Long barberId, LocalDateTime date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.barber.id = :barberId AND a.startTime < :startTime AND a.status IN ('PENDING', 'CONFIRMED')")
    long countAhead(Long barberId, LocalDateTime startTime);

    // For reminders
    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :from AND :to AND a.reminderSent = false AND a.status IN ('PENDING', 'CONFIRMED')")
    List<Appointment> findAppointmentsNeedingReminder(LocalDateTime from, LocalDateTime to);
}
