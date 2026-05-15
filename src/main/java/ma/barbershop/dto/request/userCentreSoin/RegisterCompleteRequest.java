package ma.barbershop.dto.request.userCentreSoin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCompleteRequest(
        @NotBlank String shopName,
        @NotNull String ville,
        @NotNull Long userId,
        @NotBlank String address,
        @NotBlank String fix

) {


}
