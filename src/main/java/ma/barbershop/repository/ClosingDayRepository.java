package ma.barbershop.repository;

import ma.barbershop.domain.entity.ClosingDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClosingDayRepository extends JpaRepository<ClosingDay, Long> {
    List<ClosingDay> findByUserCentreSoinIdAndClosedDateBetween(Long userCentreSoinId, LocalDate from, LocalDate to);
    boolean existsByUserCentreSoinIdAndClosedDate(Long userCentreSoinId, LocalDate date);
}
