package ma.barbershop.repository;

import ma.barbershop.domain.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByProductBarberIdOrderByCreatedAtDesc(Long barberId, Pageable pageable);
    Page<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
}
