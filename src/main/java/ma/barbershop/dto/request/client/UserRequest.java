package ma.barbershop.dto.request.client;


import jakarta.validation.constraints.NotBlank;
import ma.barbershop.domain.enums.Role;

public record UserRequest(
        @NotBlank Long id,
        @NotBlank String fistName,
        @NotBlank String lastName,
        String email,
        String phone,
        String ville,
        String quartier,
        Role role
) {
}
