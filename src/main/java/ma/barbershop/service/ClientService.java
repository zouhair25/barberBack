package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.dto.request.client.ClientRequest;
import ma.barbershop.dto.response.appointment.ClientResponse;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.ClientRepository;
import ma.barbershop.repository.UserRepository;
import ma.barbershop.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final UserCentreSoinService userCentreSoinService;
    @Transactional(readOnly = true)
    public Page<ClientResponse> mesClients(UserPrincipal userPrincipal, Pageable pageable){

        return  clientRepository.findClientByUserCentreSoin(userPrincipal.getUserCentreSoin().getId(),pageable).map(ClientResponse::from);
    }

    @Transactional
    public Client createClient(ClientRequest request, UserPrincipal userPrincipal){

        User user = this.userCentreSoinService.findUser(request.user().id());
        if(user==null){
           // Quartier quartier = this.userCentreSoinService.findQuartierById(request.user().quartier());
            Ville ville = this.userCentreSoinService.findVilleByName(request.user().ville());
            User userCreated= this.userCentreSoinService.createUser(
                    request.user().email(),
                    request.user().lastName(),
                    request.user().fistName(),
                    request.user().phone(),
                    null,
                    ville);
            user = userCreated;
        }
        UserCentreSoin userCentreSoin = this.userCentreSoinService.findUserCentreSoin(userPrincipal.getUserCentreSoin().getId());
        Client client = this.userCentreSoinService.createClient(user,userCentreSoin);

        return client;


    }


}
