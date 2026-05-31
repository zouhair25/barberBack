package ma.barbershop.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import ma.barbershop.dto.request.userCentreSoin.UserCentreSoinRequest;
import ma.barbershop.dto.response.appointment.UserCentreSoinSummary;

public record ClientRequest(
    UserRequest user,
    UserCentreSoinRequest userCentreSoin
) {
}
