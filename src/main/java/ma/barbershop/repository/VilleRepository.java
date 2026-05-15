package ma.barbershop.repository;

import ma.barbershop.domain.entity.Ville;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VilleRepository extends JpaRepository<Ville,Long> {


    List<Ville> findByNameContainingIgnoreCase(String keyword);
 }
