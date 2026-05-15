package ma.barbershop.controller;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.*;
import ma.barbershop.service.SlotAvailabilityService;
import ma.barbershop.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserCentreSoinRepository userCentreSoinRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberServiceRepository serviceRepository;
    private final ReviewRepository reviewRepository;
    private final SlotAvailabilityService slotService;
    private final UserService userService;
    @GetMapping("/barbers")
    public ResponseEntity<Page<Map<String, Object>>> listBarbers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<UserCentreSoin> userCentreSoins = userCentreSoinRepository.findAllVisible(pageable);

        Page<Map<String, Object>> result = userCentreSoins.map(b -> {
            long queueCount = appointmentRepository.countQueueForToday(b.getId(), LocalDateTime.now());
            Double avgRating = reviewRepository.findAverageRatingByUserCentreSoinId(b.getId());
            Long reviewCount = reviewRepository.countVisibleByUserCentreSoinId(b.getId());

            Map<String, Object> map = new LinkedHashMap<>();
            String shopName = Optional.ofNullable(b.getCentreSoin())
                    .map(CentreSoin::getShopName)
                    .orElse(null);
            map.put("id", b.getId());
            map.put("shopName", shopName);
            /*map.put("bio", b.getBio());
            map.put("address", b.getAddress());
            map.put("city", b.getCity());
            map.put("phone", b.getPhone());*/
            map.put("queueCount", queueCount);
            map.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null);
            map.put("reviewCount", reviewCount);
            return map;
        });

        return ResponseEntity.ok(result);
    }

    @GetMapping("/barbers/{id}")
    public ResponseEntity<Map<String, Object>> getBarber(@PathVariable Long id) {
        UserCentreSoin barber = userCentreSoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber", id));

        List<BarberService> services = serviceRepository
                .findByUserCentreSoinIdAndActiveTrueOrderByDisplayOrderAsc(id);

        long queueCount = appointmentRepository.countQueueForToday(id, LocalDateTime.now());
        Double avgRating = reviewRepository.findAverageRatingByUserCentreSoinId(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", barber.getId());
        /*result.put("shopName", barber.getShopName());
        result.put("bio", barber.getBio());
        result.put("address", barber.getAddress());
        result.put("city", barber.getCity());
        result.put("phone", barber.getPhone());*/
        result.put("queueCount", queueCount);
        result.put("averageRating", avgRating);
        result.put("services", services.stream().map(s -> Map.of(
                "id", s.getId(),
                "name", s.getName(),
                "category", s.getCategory(),
                "description", s.getDescription() != null ? s.getDescription() : "",
                "price", s.getPrice(),
                "durationMin", s.getDurationMin()
        )).toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/barbers/{id}/slots")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long serviceId) {
        return ResponseEntity.ok(slotService.getAvailableSlots(id, date, serviceId));
    }

    @GetMapping("/villes")
    public List<Ville> searchVilles(@RequestParam("q")  String q){
       return this.userService.searchVilles(q);
    }

}
