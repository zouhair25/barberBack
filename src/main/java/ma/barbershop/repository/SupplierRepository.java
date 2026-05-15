package ma.barbershop.repository;

import ma.barbershop.domain.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByUserCentreSoinId(Long userCentreSoinId);
    Optional<Supplier> findByIdAndUserCentreSoinId(Long id, Long userCentreSoinId);
}
