package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.CentreSoin;
import ma.barbershop.domain.entity.User;
import ma.barbershop.domain.entity.UserCentreSoin;
import ma.barbershop.domain.entity.Ville;
import ma.barbershop.domain.enums.Role;
import ma.barbershop.dto.request.userCentreSoin.RegisterCompleteRequest;
import ma.barbershop.repository.CentreSoinRepository;
import ma.barbershop.repository.UserCentreSoinRepository;
import ma.barbershop.repository.UserRepository;
import ma.barbershop.repository.VilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final VilleRepository villeRepository;
    private final UserCentreSoinRepository userCentreSoinRepository;
    private final CentreSoinRepository centreSoinRepository;
    private final UserRepository userRepository;
    public List<Ville> searchVilles(String keyword){
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return  villeRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public UserCentreSoin registerComplete(RegisterCompleteRequest req) {
        User user = userRepository.findById(req.userId()).orElseThrow();

        Ville ville = villeRepository.findByNameContainingIgnoreCase(req.ville()).getFirst();

        CentreSoin centreSoin = CentreSoin.builder()
                .shopName(req.shopName())
                .fix(req.fix())
                .address(req.address())
                .ville(ville)
                .active(true)
                .build();

        centreSoin = centreSoinRepository.save(centreSoin);
        UserCentreSoin userCentreSoin = new UserCentreSoin();
        if (user.getRole() == Role.BARBER) {
            userCentreSoin = UserCentreSoin.builder()
                    .user(user)
                    .centreSoin(centreSoin)
                    .visible(true)
                    .build();
            userCentreSoin = userCentreSoinRepository.save(userCentreSoin);
        }

        return userCentreSoin;
    }

}
