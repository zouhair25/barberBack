package ma.barbershop.dto.request.userCentreSoin;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record BarberProfileRequest(
        @NotBlank String shopName,
        String bio,
        String address,
        String city,
        BigDecimal latitude,
        BigDecimal longitude,
        String phone
) {}
