package ma.barbershop.dto.request.userCentreSoin;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClosingDayRequest(
        @NotNull LocalDate date,
        String reason
) {}
