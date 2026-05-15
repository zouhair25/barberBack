package ma.barbershop.dto.request.userCentreSoin;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

public record OpeningHoursRequest(List<DaySchedule> schedule) {

    public record DaySchedule(
            @NotNull Integer dayOfWeek,
            LocalTime openTime,
            LocalTime closeTime,
            boolean closed,
            @NotNull Integer slotDurationMin
    ) {}
}
