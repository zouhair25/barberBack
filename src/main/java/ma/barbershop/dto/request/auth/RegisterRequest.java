package ma.barbershop.dto.request.auth;

import jakarta.validation.constraints.*;
import ma.barbershop.domain.enums.Role;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Role role  // USER or BARBER
) {}
