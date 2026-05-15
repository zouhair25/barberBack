package ma.barbershop.repository;

import ma.barbershop.domain.entity.TypeUser;
import ma.barbershop.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TypeUserRepository extends JpaRepository<TypeUser,Long> {

    TypeUser findByLabel(Role role);
}
