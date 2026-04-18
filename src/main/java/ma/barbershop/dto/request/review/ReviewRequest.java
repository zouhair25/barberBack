package ma.barbershop.dto.request.review;

import jakarta.validation.constraints.*;

public record ReviewRequest(
        @NotNull @Min(1) @Max(5) Integer rating,
        String comment
) {}
