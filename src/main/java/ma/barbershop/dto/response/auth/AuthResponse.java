package ma.barbershop.dto.response.auth;

import ma.barbershop.domain.entity.UserCentreSoin;
import ma.barbershop.domain.enums.Role;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String firstName,
        String lastName,
        Role role,
        List<UserCentreSoin> userCentreSoins,
        UserCentreSoin userCentreSoin
) {}
