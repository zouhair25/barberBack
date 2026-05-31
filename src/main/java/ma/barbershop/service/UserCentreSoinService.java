package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCentreSoinService {

    private final UserCentreSoinRepository userCentreSoinRepository;
    private  final UserRepository userRepository;
    private final VilleRepository villeRepository;
    private final QuartierRepository quartierRepository;
    private final ClientRepository clientRepository;

    public UserCentreSoin findUserCentreSoin(Long userCentreSoinId){
        return userCentreSoinRepository.findById(userCentreSoinId)
                .orElseThrow();

    }

    public User findUser(Long userId){
        return userRepository.findById(userId).orElseThrow();

    }

    public  User createUser(String email, String lastname, String firstname, String phone, Quartier quartier, Ville ville){
        User user = User.builder()
                .email(email)
                .firstName(firstname)
                .lastName(lastname)
                .phone(phone)
                .quartier(quartier)
                .ville(ville)
                .build();
        this.userRepository.save(user);
        return  user;
    }

    public Client createClient(User user, UserCentreSoin userCentreSoin){
        Client client = Client.builder()
                .user(user)
                .userCentreSoin(userCentreSoin)
                .build();
        this.clientRepository.save(client);
        return client;
    }

    public Ville findVilleByName(String name){
        return villeRepository.findByNameContainingIgnoreCase(name).getFirst();
    }
    public Ville findVilleById(Long villeId){
        return villeRepository.findById(villeId).orElseThrow();
    }

    public Quartier findQuartierById(Long quartierId){
        return quartierRepository.findById(quartierId).orElseThrow();
    }
    /*public Quartier findQuartierByName(String name){
        return quartierRepository.findOne(name).stream()
                .findFirst()
                .orElse(null);;
    }*/
}
