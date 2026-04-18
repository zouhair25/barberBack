package ma.barbershop.dto.request.appointment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookAppointmentRequest(
        @NotNull Long barberId,
        @NotNull Long serviceId,
        @NotNull LocalDateTime startTime,
        String notes
) {}
