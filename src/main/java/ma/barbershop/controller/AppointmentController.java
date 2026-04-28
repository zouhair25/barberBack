package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.Appointment;
import ma.barbershop.domain.enums.AppointmentStatus;
import ma.barbershop.dto.request.appointment.*;
import ma.barbershop.security.UserPrincipal;
import ma.barbershop.service.AppointmentService;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // ---- USER endpoints ----


    @PostMapping("/user/appointments")
    public ResponseEntity<Appointment> book(
            @Valid @RequestBody BookAppointmentRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(req, principal));
    }

    @GetMapping("/user/appointments")
    public ResponseEntity<Page<Appointment>> myAppointments(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(appointmentService.getClientAppointments(principal, pageable));
    }

    @GetMapping("/user/appointments/{id}/queue")
    public ResponseEntity<Map<String, Object>> queuePosition(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(appointmentService.getQueuePosition(id, principal));
    }

    @DeleteMapping("/user/appointments/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        appointmentService.cancelByClient(id, principal);
        return ResponseEntity.noContent().build();
    }

    // ---- BARBER endpoints ----

    @GetMapping("/barber/appointments")
    public ResponseEntity<List<Appointment>> barberAppointments(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) AppointmentStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {

        LocalDateTime f = from != null ? from : LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime t = to != null ? to : f.plusDays(7);

        return ResponseEntity.ok(appointmentService.getBarberAppointments(f, t, status, principal));
    }

    @GetMapping("/barber/appointments/today")
    public ResponseEntity<List<Appointment>> todayQueue(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(appointmentService.getTodayQueue(principal));
    }

    @PatchMapping("/barber/appointments/{id}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, req, principal));
    }
}
