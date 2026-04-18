package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.barbershop.domain.entity.Appointment;
import ma.barbershop.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderSchedulerService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    @Value("${app.reminders.hours-before}")
    private int hoursBefore;

    @Scheduled(fixedDelayString = "${app.reminders.check-interval-ms}")
    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(hoursBefore);
        LocalDateTime windowEnd = windowStart.plusMinutes(15);

        List<Appointment> appointments = appointmentRepository
                .findAppointmentsNeedingReminder(windowStart, windowEnd);

        log.info("Sending reminders for {} appointments", appointments.size());

        for (Appointment apt : appointments) {
            try {
                notificationService.sendAppointmentReminder(apt);
                apt.setReminderSent(true);
                appointmentRepository.save(apt);
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment {}", apt.getId(), e);
            }
        }
    }
}
