package ma.barbershop.repository;

import ma.barbershop.domain.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client,Long> {

    @Query("SELECT c FROM Client c WHERE c.userCentreSoin.id = :userCentreSoinId")
    Page<Client> findClientByUserCentreSoin(Long userCentreSoinId, Pageable pageable);
}
