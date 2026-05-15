package ma.barbershop.repository;

import ma.barbershop.domain.entity.CentreSoin;
import ma.barbershop.domain.entity.ClosingDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentreSoinRepository extends JpaRepository<CentreSoin,Long> {


}
