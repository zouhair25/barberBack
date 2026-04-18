package ma.barbershop.repository;

import ma.barbershop.domain.entity.BarberService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarberServiceRepository extends JpaRepository<BarberService, Long> {
    List<BarberService> findByBarberIdAndActiveTrueOrderByDisplayOrderAsc(Long barberId);
    Optional<BarberService> findByIdAndBarberId(Long id, Long barberId);
}
