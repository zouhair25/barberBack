package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.dto.response.appointment.ClientResponse;
import ma.barbershop.repository.ClientRepository;
import ma.barbershop.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    @Transactional(readOnly = true)
    public Page<ClientResponse> mesClients(UserPrincipal userPrincipal, Pageable pageable){

        return  clientRepository.findClientByUserCentreSoin(userPrincipal.getUserCentreSoin().getId(),pageable).map(ClientResponse::from);
    }
}
