package ma.barbershop.repository;

import ma.barbershop.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserCentreSoinIdAndVisibleTrueOrderByCreatedAtDesc(Long userCentreSoinId, Pageable pageable);
    Page<Review> findByUserCentreSoinIdOrderByCreatedAtDesc(Long userCentreSoinId, Pageable pageable);
    Optional<Review> findByAppointmentId(Long appointmentId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.userCentreSoin.id = :userCentreSoinId AND r.visible = true")
    Double findAverageRatingByUserCentreSoinId(Long userCentreSoinId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.userCentreSoin.id = :userCentreSoinId AND r.visible = true")
    Long countVisibleByUserCentreSoinId(Long userCentreSoinId);
}
