package ma.barbershop.repository;

import ma.barbershop.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBarberIdAndActiveTrue(Long barberId);
    List<Product> findByBarberIdAndActiveTrueAndForSaleTrue(Long barberId);
    Optional<Product> findByIdAndBarberId(Long id, Long barberId);

    @Query("SELECT p FROM Product p WHERE p.barber.id = :barberId AND p.active = true AND p.stockQuantity <= p.stockAlertMin")
    List<Product> findLowStock(Long barberId);
}
