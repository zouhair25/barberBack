package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.Client;
import ma.barbershop.domain.entity.UserCentreSoin;

public record ClientResponse(
        Long id,
        ClientSummary user,
        UserCentreSoinSummary userCentreSoin
) {

    public static ClientResponse from(Client c){
        if(c==null) return  null;
        return  new ClientResponse(
                c.getId(),
                ClientSummary.from(c.getUser()),
                UserCentreSoinSummary.from(c.getUserCentreSoin())
        );
    }
}
