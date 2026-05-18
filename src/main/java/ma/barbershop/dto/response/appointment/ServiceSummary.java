package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.BarberService;
import ma.barbershop.domain.enums.ServiceCategory;

import java.math.BigDecimal;

public record ServiceSummary(
        Long id,
        String name,
        ServiceCategory category,
        BigDecimal price,
        Integer durationMin
) {
    public static ServiceSummary from(BarberService s) {
        if (s == null) return null;
        return new ServiceSummary(
                s.getId(),
                s.getName(),
                s.getCategory(),
                s.getPrice(),
                s.getDurationMin()
        );
    }
}
