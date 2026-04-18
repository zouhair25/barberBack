package ma.barbershop.dto.request.appointment;

import jakarta.validation.constraints.NotNull;
import ma.barbershop.domain.enums.AppointmentStatus;

public record UpdateAppointmentStatusRequest(@NotNull AppointmentStatus status) {}
