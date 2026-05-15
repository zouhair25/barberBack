package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.BarberService;
import ma.barbershop.domain.entity.UserCentreSoin;
import ma.barbershop.dto.request.service.ServiceRequest;
import ma.barbershop.exception.*;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barber/services")
@RequiredArgsConstructor
public class BarberServiceController {

    private final BarberServiceRepository serviceRepository;
    private final UserCentreSoinRepository userCentreSoinRepository;

    @GetMapping
    public ResponseEntity<List<BarberService>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(serviceRepository
                .findByUserCentreSoinIdAndActiveTrueOrderByDisplayOrderAsc(principal.getUserCentreSoinId()));
    }

    @PostMapping
    public ResponseEntity<BarberService> create(
            @Valid @RequestBody ServiceRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserCentreSoin barber = userCentreSoinRepository.findById(principal.getUserCentreSoinId()).orElseThrow();

        BarberService service = BarberService.builder()
                .userCentreSoin(barber)
                .name(req.name())
                .category(req.category())
                .description(req.description())
                .price(req.price())
                .durationMin(req.durationMin())
                .displayOrder(req.displayOrder() != null ? req.displayOrder() : 0)
                .active(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(serviceRepository.save(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarberService> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        BarberService service = serviceRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoinId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        service.setName(req.name());
        service.setCategory(req.category());
        service.setDescription(req.description());
        service.setPrice(req.price());
        service.setDurationMin(req.durationMin());
        if (req.displayOrder() != null) service.setDisplayOrder(req.displayOrder());

        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        BarberService service = serviceRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoinId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        service.setActive(false);
        serviceRepository.save(service);
        return ResponseEntity.noContent().build();
    }
}
