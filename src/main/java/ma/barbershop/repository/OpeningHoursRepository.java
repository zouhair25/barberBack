package ma.barbershop.repository;

import ma.barbershop.domain.entity.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {
    List<OpeningHours> findByUserCentreSoinId(Long userCentreSoinId);
    Optional<OpeningHours> findByUserCentreSoinIdAndDayOfWeek(Long userCentreSoinId, Integer dayOfWeek);
    void deleteByUserCentreSoinId(Long userCentreSoinId);
}
