package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.dto.request.auth.RegisterRequest;
import ma.barbershop.dto.request.userCentreSoin.*;
import ma.barbershop.dto.response.auth.AuthResponse;
import ma.barbershop.exception.*;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import ma.barbershop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/us")
@RequiredArgsConstructor
public class UserCentreSoinController {

    private final UserService userService;
    private final UserCentreSoinRepository userCentreSoinRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final ClosingDayRepository closingDayRepository;

    @GetMapping
    public ResponseEntity<UserCentreSoin> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserCentreSoin profile = userCentreSoinRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber profile not found"));
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/register-complete")
    public ResponseEntity<UserCentreSoin> registerComplete(@Valid @RequestBody RegisterCompleteRequest req){
        return ResponseEntity.ok(userService.registerComplete(req));
    }

   /* @PutMapping
    @Transactional
    public ResponseEntity<UserCentreSoin> updateProfile(
            @Valid @RequestBody BarberProfileRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserCentreSoin profile = userCentreSoinRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber profile not found"));

        profile.setShopName(req.shopName());
        profile.setBio(req.bio());
        profile.setAddress(req.address());
        profile.setCity(req.city());
        profile.setLatitude(req.latitude());
        profile.setLongitude(req.longitude());
        profile.setPhone(req.phone());

        return ResponseEntity.ok(userCentreSoinRepository.save(profile));
    }*/

    @PutMapping("/hours")
    @Transactional
    public ResponseEntity<List<OpeningHours>> updateHours(
            @Valid @RequestBody OpeningHoursRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userCentreSoinId = principal.getUserCentreSoins().getFirst().getId();
        openingHoursRepository.deleteByUserCentreSoinId(userCentreSoinId);

        UserCentreSoin barber = userCentreSoinRepository.findById(userCentreSoinId).orElseThrow();

        List<OpeningHours> saved = req.schedule().stream().map(day ->
                openingHoursRepository.save(OpeningHours.builder()
                        .userCentreSoin(barber)
                        .dayOfWeek(day.dayOfWeek())
                        .openTime(day.openTime())
                        .closeTime(day.closeTime())
                        .closed(day.closed())
                        .slotDurationMin(day.slotDurationMin())
                        .build())
        ).toList();

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/closing-days")
    @Transactional
    public ResponseEntity<ClosingDay> addClosingDay(
            @Valid @RequestBody ClosingDayRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userCentreSoinId = principal.getUserCentreSoins().getFirst().getId();
        if (closingDayRepository.existsByUserCentreSoinIdAndClosedDate(userCentreSoinId, req.date())) {
            throw new BusinessException("Closing day already exists for this date");
        }

        UserCentreSoin barber = userCentreSoinRepository.findById(userCentreSoinId).orElseThrow();
        ClosingDay cd = ClosingDay.builder()
                .userCentreSoin(barber)
                .closedDate(req.date())
                .reason(req.reason())
                .build();

        return ResponseEntity.ok(closingDayRepository.save(cd));
    }

    @DeleteMapping("/closing-days/{id}")
    @Transactional
    public ResponseEntity<Void> removeClosingDay(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        ClosingDay cd = closingDayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClosingDay", id));

        if (!cd.getUserCentreSoin().getId().equals(principal.getUserCentreSoins().getFirst().getId())) {
            throw new UnauthorizedAccessException();
        }

        closingDayRepository.delete(cd);
        return ResponseEntity.noContent().build();
    }
}
