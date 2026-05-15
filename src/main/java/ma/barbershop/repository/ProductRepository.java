package ma.barbershop.repository;

import ma.barbershop.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserCentreSoinIdAndActiveTrue(Long userCentreSoinId);
    List<Product> findByUserCentreSoinIdAndActiveTrueAndForSaleTrue(Long userCentreSoinId);
    Optional<Product> findByIdAndUserCentreSoinId(Long id, Long userCentreSoinId);

    @Query("SELECT p FROM Product p WHERE p.userCentreSoin.id = :userCentreSoinId AND p.active = true AND p.stockQuantity <= p.stockAlertMin")
    List<Product> findLowStock(Long userCentreSoinId);
}
