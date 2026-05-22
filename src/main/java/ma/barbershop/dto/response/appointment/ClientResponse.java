package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.Client;
import ma.barbershop.domain.entity.User;
import ma.barbershop.domain.entity.UserCentreSoin;

public record ClientResponse(
        Long id,
        User user,
        UserCentreSoin userCentreSoin
) {

    public  static  ClientResponse from(Client client){
        if(client==null) return  null;
        return  new ClientResponse(client.getId(),client.getUser(),client.getUserCentreSoin());
    }
}
