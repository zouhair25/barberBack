package ma.barbershop.controller;

import lombok.RequiredArgsConstructor;
import ma.barbershop.security.UserPrincipal;
import ma.barbershop.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/barber/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> daily(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(statisticsService.getDailyStats(principal));
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<Map<String, Object>>> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "DAY") String groupBy,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(statisticsService.getRevenueByPeriod(principal.getBarberId(), from, to, groupBy));
    }

    @GetMapping("/services")
    public ResponseEntity<List<Map<String, Object>>> topServices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(statisticsService.getTopServices(principal.getBarberId(), from, to));
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Map<String, Object>>> loyalClients(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(statisticsService.getLoyalClients(principal.getBarberId(), limit));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> topProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(statisticsService.getTopProducts(principal.getBarberId(), from, to));
    }
}
