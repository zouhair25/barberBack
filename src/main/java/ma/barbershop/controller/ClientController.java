package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.Client;
import ma.barbershop.dto.request.client.ClientRequest;
import ma.barbershop.dto.request.client.UserRequest;
import ma.barbershop.dto.response.appointment.ClientResponse;
import ma.barbershop.security.UserPrincipal;
import ma.barbershop.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    @PostMapping("/me")
    public ResponseEntity<Page<ClientResponse>> mesClients(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(clientService.mesClients(principal,pageable));
    }

    @PostMapping("/create-client")
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientRequest clientRequest,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal){
        return  ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(clientRequest,userPrincipal));
    }
}
