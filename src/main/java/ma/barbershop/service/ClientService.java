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
    public Client createClient(ClientRequest request, UserPrincipal userPrincipal) {
        User user = null;
        if (request.userId() != null) {
            user = userRepository.findById(request.userId()).orElse(null);
        }
        if (user == null) {
            Ville ville = request.villeId() != null
                    ? userCentreSoinService.findVilleById(request.villeId())
                    : null;
            user = userCentreSoinService.createUser(
                    request.email(),
                    request.lastName(),
                    request.firstName(),
                    request.phone(),
                    null,
                    ville);
        }
        UserCentreSoin userCentreSoin = userCentreSoinService.findUserCentreSoin(
                userPrincipal.getUserCentreSoin().getId());
        return userCentreSoinService.createClient(user, userCentreSoin);
    }


}
