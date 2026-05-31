package ma.barbershop.repository;

import ma.barbershop.domain.entity.Quartier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartierRepository extends JpaRepository<Quartier,Long> {
}
