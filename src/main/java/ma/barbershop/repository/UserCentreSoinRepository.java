package ma.barbershop.repository;
import ma.barbershop.domain.entity.UserCentreSoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCentreSoinRepository extends JpaRepository<UserCentreSoin, Long> {
    Optional<UserCentreSoin> findByUserId(Long userId);

    @Query("SELECT b FROM UserCentreSoin b WHERE b.visible = true AND b.user.active = true AND b.user.deletedAt IS NULL")
    Page<UserCentreSoin> findAllVisible(Pageable pageable);
}
