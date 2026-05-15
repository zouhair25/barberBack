package ma.barbershop.dto.request.userCentreSoin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCompleteRequest(
        @NotBlank String shopName,
        @NotNull Long ville,
        @NotNull Long userId,
        @NotBlank String adresse,
        @NotBlank String fix

) {


}
