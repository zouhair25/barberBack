package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.Role;
import ma.barbershop.dto.request.auth.*;
import ma.barbershop.dto.response.auth.AuthResponse;
import ma.barbershop.exception.BusinessException;
import ma.barbershop.repository.*;
import ma.barbershop.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserCentreSoinRepository userCentreSoinRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final  TypeUserRepository typeUserRepository;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("Email already registered");
        }
        if (req.role() == Role.ADMIN) {
            throw new BusinessException("Cannot self-register as ADMIN");
        }
        User user = User.builder()
                .email(req.email())
                .phone(req.phone())
                .passwordHash(passwordEncoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .role(req.role())
                .active(true)
                .build();

        user = userRepository.save(user);

        if (req.role() == Role.BARBER) {
            UserCentreSoin profile = UserCentreSoin.builder()
                    .user(user)
                    .visible(true)
                    .build();
            userCentreSoinRepository.save(profile);
            user = userRepository.findById(user.getId()).orElseThrow();
        }

        UserPrincipal principal = new UserPrincipal(user);
        return buildAuthResponse(principal);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return buildAuthResponse(principal);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest req) {
        String hash = hashToken(req.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new BusinessException("Invalid or expired refresh token"));

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new BusinessException("Refresh token expired");
        }

        // Rotate
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        UserPrincipal principal = new UserPrincipal(user);
        return buildAuthResponse(principal);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private AuthResponse buildAuthResponse(UserPrincipal principal) {
        String accessToken = tokenProvider.generateAccessToken(principal);
        String rawRefreshToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.getReferenceById(principal.getId()))
                .tokenHash(hashToken(rawRefreshToken))
                .expiresAt(LocalDateTime.now().plusNanos(refreshTokenExpiryMs * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        if(principal.getUserCentreSoins()!=null){
            System.out.println(principal.getUserCentreSoins());

        }
        return new AuthResponse(
                accessToken,
                rawRefreshToken,
                principal.getId(),
                principal.getEmail(),
                principal.getFirstname(), principal.getLastname(),
                principal.getRole(),
                principal.getUserCentreSoins(),
                principal.getUserCentreSoin()
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
