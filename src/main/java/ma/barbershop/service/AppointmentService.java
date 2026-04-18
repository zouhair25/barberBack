package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.AppointmentStatus;
import ma.barbershop.dto.request.appointment.*;
import ma.barbershop.exception.*;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BarberProfileRepository barberProfileRepository;
    private final BarberServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final SlotAvailabilityService slotService;

    @Transactional
    public Appointment book(BookAppointmentRequest req, UserPrincipal principal) {
        BarberProfile barber = barberProfileRepository.findById(req.barberId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber", req.barberId()));

        BarberService service = serviceRepository.findById(req.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", req.serviceId()));

        if (!service.getBarber().getId().equals(barber.getId())) {
            throw new BusinessException("Service does not belong to this barber");
        }

        if (!slotService.isSlotAvailable(req.barberId(), req.startTime(), service.getDurationMin())) {
            throw new BusinessException("Slot is not available");
        }

        User client = userRepository.getReferenceById(principal.getId());
        LocalDateTime endTime = req.startTime().plusMinutes(service.getDurationMin());

        // Assign queue position
        long queuePos = appointmentRepository.countQueueForToday(req.barberId(), req.startTime()) + 1;

        Appointment apt = Appointment.builder()
                .barber(barber)
                .client(client)
                .service(service)
                .startTime(req.startTime())
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .queuePosition((int) queuePos)
                .notes(req.notes())
                .build();

        return appointmentRepository.save(apt);
    }

    @Transactional(readOnly = true)
    public Page<Appointment> getClientAppointments(UserPrincipal principal, Pageable pageable) {
        return appointmentRepository.findByClientIdOrderByStartTimeDesc(principal.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getQueuePosition(Long appointmentId, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findByIdAndClientId(appointmentId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        long ahead = appointmentRepository.countAhead(apt.getBarber().getId(), apt.getStartTime());

        return Map.of(
                "appointmentId", appointmentId,
                "status", apt.getStatus(),
                "startTime", apt.getStartTime(),
                "queuePosition", apt.getQueuePosition(),
                "peopleAhead", ahead
        );
    }

    @Transactional
    public Appointment cancelByClient(Long appointmentId, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findByIdAndClientId(appointmentId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        if (apt.getStatus() == AppointmentStatus.COMPLETED || apt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel a " + apt.getStatus().name().toLowerCase() + " appointment");
        }

        apt.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(apt);
    }

    @Transactional
    public Appointment updateStatus(Long appointmentId, UpdateAppointmentStatusRequest req, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findByIdAndBarberId(appointmentId, principal.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        apt.setStatus(req.status());
        return appointmentRepository.save(apt);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getBarberAppointments(LocalDateTime from, LocalDateTime to,
                                                    AppointmentStatus status, UserPrincipal principal) {
        return appointmentRepository.findActiveByBarberAndDateRange(
                principal.getBarberId(), from, to);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getTodayQueue(UserPrincipal principal) {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return appointmentRepository.findActiveByBarberAndDateRange(principal.getBarberId(), start, end);
    }
}
