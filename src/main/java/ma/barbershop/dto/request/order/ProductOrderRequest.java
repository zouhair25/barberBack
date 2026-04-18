package ma.barbershop.dto.request.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProductOrderRequest(
        Long supplierId,
        LocalDate expectedDate,
        String notes,
        @NotEmpty List<OrderLine> lines
) {
    public record OrderLine(
            @NotNull Long productId,
            @NotNull Integer quantityOrdered,
            @NotNull BigDecimal unitCost
    ) {}
}
