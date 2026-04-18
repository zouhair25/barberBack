package ma.barbershop.dto.request.barber;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClosingDayRequest(
        @NotNull LocalDate date,
        String reason
) {}
