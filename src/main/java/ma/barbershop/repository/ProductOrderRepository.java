package ma.barbershop.repository;

import ma.barbershop.domain.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    Page<ProductOrder> findByBarberIdOrderByOrderDateDesc(Long barberId, Pageable pageable);
    Optional<ProductOrder> findByIdAndBarberId(Long id, Long barberId);
}
