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

    List<Appointment> findByUserCentreSoinIdAndStartTimeBetween(Long userCentreSoinId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.client JOIN FETCH a.service JOIN FETCH a.userCentreSoin WHERE a.userCentreSoin.id = :userCentreSoinId AND a.startTime BETWEEN :from AND :to AND a.status NOT IN ('CANCELLED', 'NO_SHOW') ORDER BY a.startTime ASC")
    List<Appointment> findActiveByBarberAndDateRange(Long userCentreSoinId, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT a FROM Appointment a JOIN FETCH a.client JOIN FETCH a.service JOIN FETCH a.userCentreSoin WHERE a.client.id = :clientId ORDER BY a.startTime DESC",
           countQuery = "SELECT COUNT(a) FROM Appointment a WHERE a.client.id = :clientId")
    Page<Appointment> findByClientIdOrderByStartTimeDesc(Long clientId, Pageable pageable);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.client JOIN FETCH a.service JOIN FETCH a.userCentreSoin WHERE a.id = :id AND a.userCentreSoin.id = :userCentreSoinId")
    Optional<Appointment> findByIdAndUserCentreSoinId(Long id, Long userCentreSoinId);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.client JOIN FETCH a.service JOIN FETCH a.userCentreSoin WHERE a.id = :id AND a.client.id = :clientId")
    Optional<Appointment> findByIdAndClientId(Long id, Long clientId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.userCentreSoin.id = :userCentreSoinId AND DATE(a.startTime) = DATE(:date) AND a.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS')")
    long countQueueForToday(Long userCentreSoinId, LocalDateTime date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.userCentreSoin.id = :userCentreSoinId AND a.startTime < :startTime AND a.status IN ('PENDING', 'CONFIRMED')")
    long countAhead(Long userCentreSoinId, LocalDateTime startTime);

    // For reminders
    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :from AND :to AND a.reminderSent = false AND a.status IN ('PENDING', 'CONFIRMED')")
    List<Appointment> findAppointmentsNeedingReminder(LocalDateTime from, LocalDateTime to);
}
