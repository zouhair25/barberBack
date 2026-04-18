package ma.barbershop.dto.request.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String description,
        String sku,
        @NotNull @DecimalMin("0.01") BigDecimal priceSell,
        BigDecimal priceCost,
        Integer stockQuantity,
        Integer stockAlertMin,
        boolean forSale,
        String category
) {}
