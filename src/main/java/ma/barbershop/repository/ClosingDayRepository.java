package ma.barbershop.repository;

import ma.barbershop.domain.entity.ClosingDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClosingDayRepository extends JpaRepository<ClosingDay, Long> {
    List<ClosingDay> findByBarberIdAndClosedDateBetween(Long barberId, LocalDate from, LocalDate to);
    boolean existsByBarberIdAndClosedDate(Long barberId, LocalDate date);
}
