package ma.barbershop.dto.request.appointment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookAppointmentRequest(
        @NotNull Long userCenterSoinId,
        @NotNull Long serviceId,
        @NotNull LocalDateTime startTime,
        String notes
) {}
