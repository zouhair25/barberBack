package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.CentreSoin;
import ma.barbershop.domain.entity.UserCentreSoin;

public record UserCentreSoinSummary(
        Long id,
        CentreSoinSummary centreSoin,
        ClientSummary client
) {
    public static UserCentreSoinSummary from(UserCentreSoin ucs) {
        if (ucs == null) return null;
        return new UserCentreSoinSummary(
                ucs.getId() ,
                CentreSoinSummary.from(ucs.getCentreSoin()),
                ClientSummary.from(ucs.getUser())
        );
    }
}

