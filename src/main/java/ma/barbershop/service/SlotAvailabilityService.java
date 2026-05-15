package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SlotAvailabilityService {

    private final UserCentreSoinRepository userCentreSoinRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final ClosingDayRepository closingDayRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableSlots(Long userCentreSoinId, LocalDate date, Long serviceId) {
        UserCentreSoin barber = userCentreSoinRepository.findById(userCentreSoinId)
                .orElseThrow(() -> new ResourceNotFoundException("Barber", userCentreSoinId));

        // Check if it's a closing day
        if (closingDayRepository.existsByUserCentreSoinIdAndClosedDate(userCentreSoinId, date)) {
            return Collections.emptyList();
        }

        // Get opening hours for that day (0=Sunday, 1=Monday...)
        int dayOfWeek = date.getDayOfWeek().getValue() % 7; // Convert ISO (Mon=1) to (Sun=0)
        Optional<OpeningHours> hoursOpt = openingHoursRepository
                .findByUserCentreSoinIdAndDayOfWeek(userCentreSoinId, dayOfWeek);

        if (hoursOpt.isEmpty() || hoursOpt.get().isClosed()) {
            return Collections.emptyList();
        }

        OpeningHours hours = hoursOpt.get();
        if (hours.getOpenTime() == null || hours.getCloseTime() == null) {
            return Collections.emptyList();
        }

        // Service duration
        int slotDuration = hours.getSlotDurationMin();
        if (serviceId != null) {
            serviceRepository.findById(serviceId)
                    .ifPresent(svc -> {/* duration already in slotDuration */});
        }

        // Generate all possible slots
        List<LocalDateTime> allSlots = generateSlots(date, hours.getOpenTime(), hours.getCloseTime(), slotDuration);

        // Get existing appointments for that day
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(23, 59, 59);
        List<Appointment> existing = appointmentRepository
                .findActiveByBarberAndDateRange(userCentreSoinId, dayStart, dayEnd);

        // Remove occupied slots
        return allSlots.stream()
                .filter(slot -> isSlotFree(slot, slotDuration, existing))
                .filter(slot -> slot.isAfter(LocalDateTime.now())) // no past slots
                .toList();
    }

    private List<LocalDateTime> generateSlots(LocalDate date, LocalTime openTime, LocalTime closeTime, int durationMin) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime current = date.atTime(openTime);
        LocalDateTime end = date.atTime(closeTime);

        while (!current.plusMinutes(durationMin).isAfter(end)) {
            slots.add(current);
            current = current.plusMinutes(durationMin);
        }
        return slots;
    }

    private boolean isSlotFree(LocalDateTime slotStart, int durationMin, List<Appointment> existing) {
        LocalDateTime slotEnd = slotStart.plusMinutes(durationMin);
        return existing.stream().noneMatch(apt ->
                apt.getStartTime().isBefore(slotEnd) && apt.getEndTime().isAfter(slotStart)
        );
    }

    public boolean isSlotAvailable(Long userCentreSoinId, LocalDateTime startTime, int durationMin) {
        List<LocalDateTime> available = getAvailableSlots(userCentreSoinId, startTime.toLocalDate(), null);
        return available.contains(startTime);
    }
}
