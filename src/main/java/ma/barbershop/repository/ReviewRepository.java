package ma.barbershop.repository;

import ma.barbershop.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBarberIdAndVisibleTrueOrderByCreatedAtDesc(Long barberId, Pageable pageable);
    Page<Review> findByBarberIdOrderByCreatedAtDesc(Long barberId, Pageable pageable);
    Optional<Review> findByAppointmentId(Long appointmentId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.barber.id = :barberId AND r.visible = true")
    Double findAverageRatingByBarberId(Long barberId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.barber.id = :barberId AND r.visible = true")
    Long countVisibleByBarberId(Long barberId);
}
