package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.Appointment;
import ma.barbershop.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentProResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        Integer queuePosition,
        String notes,
        boolean reminderSent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ClientResponse client,
        ServiceSummary service,
        UserCentreSoinSummary userCentreSoin
) {
    public static AppointmentProResponse from(Appointment a) {
        return new AppointmentProResponse(
                a.getId(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getQueuePosition(),
                a.getNotes(),
                a.isReminderSent(),
                a.getCreatedAt(),
                a.getUpdatedAt(),
                ClientResponse.from(a.getClient()),
                ServiceSummary.from(a.getService()),
                UserCentreSoinSummary.from(a.getUserCentreSoin())
        );
    }
}
