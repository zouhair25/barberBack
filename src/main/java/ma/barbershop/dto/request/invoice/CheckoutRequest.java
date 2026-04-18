package ma.barbershop.dto.request.invoice;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutRequest(
        @NotNull Long appointmentId,
        List<ProductLineItem> products,
        BigDecimal taxRate,
        String notes,
        boolean generatePdf
) {
    public record ProductLineItem(
            @NotNull Long productId,
            @NotNull Integer quantity
    ) {}
}
