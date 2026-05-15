package ma.barbershop.dto.response.auth;

import ma.barbershop.domain.enums.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String firstName,
        String lastName,
        Role role,
        Long userCentreSoinId
) {}
