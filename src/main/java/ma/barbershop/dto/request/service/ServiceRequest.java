package ma.barbershop.dto.request.service;

import jakarta.validation.constraints.*;
import ma.barbershop.domain.enums.ServiceCategory;

import java.math.BigDecimal;

public record ServiceRequest(
        @NotBlank String name,
        @NotNull ServiceCategory category,
        String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @Min(5) Integer durationMin,
        Integer displayOrder
) {}
