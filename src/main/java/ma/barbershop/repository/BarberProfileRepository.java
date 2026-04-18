package ma.barbershop.repository;

import ma.barbershop.domain.entity.BarberProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BarberProfileRepository extends JpaRepository<BarberProfile, Long> {
    Optional<BarberProfile> findByUserId(Long userId);

    @Query("SELECT b FROM BarberProfile b WHERE b.visible = true AND b.user.active = true AND b.user.deletedAt IS NULL")
    Page<BarberProfile> findAllVisible(Pageable pageable);
}
