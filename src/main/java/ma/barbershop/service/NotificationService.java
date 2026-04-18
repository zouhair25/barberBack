package ma.barbershop.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.*;
import ma.barbershop.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final boolean twilioEnabled;
    private final String fromNumber;
    private final String whatsappFrom;

    public NotificationService(NotificationRepository notificationRepository,
                                @Value("${app.twilio.account-sid}") String accountSid,
                                @Value("${app.twilio.auth-token}") String authToken,
                                @Value("${app.twilio.from-number}") String fromNumber,
                                @Value("${app.twilio.whatsapp-from}") String whatsappFrom) {
        this.notificationRepository = notificationRepository;
        this.fromNumber = fromNumber;
        this.whatsappFrom = whatsappFrom;

        if (accountSid != null && !accountSid.isBlank()) {
            Twilio.init(accountSid, authToken);
            this.twilioEnabled = true;
        } else {
            this.twilioEnabled = false;
            log.warn("Twilio not configured - reminders will be logged only");
        }
    }

    public void sendAppointmentReminder(Appointment apt) {
        String clientPhone = apt.getClient().getPhone();
        if (clientPhone == null || clientPhone.isBlank()) {
            log.warn("No phone for client {}", apt.getClient().getId());
            return;
        }

        String barberName = apt.getBarber().getShopName();
        String time = apt.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
        String message = String.format(
                "Rappel RDV: Vous avez un rendez-vous chez %s le %s pour %s. À bientôt!",
                barberName, time, apt.getService().getName()
        );

        sendSms(apt.getClient(), apt, message);
    }

    private void sendSms(User user, Appointment apt, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .appointment(apt)
                .channel(NotificationChannel.SMS)
                .message(message)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            if (twilioEnabled && user.getPhone() != null) {
                Message.creator(new PhoneNumber(user.getPhone()), new PhoneNumber(fromNumber), message).create();
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("SMS sent to {}", user.getPhone());
            } else {
                log.info("SMS (not sent - Twilio disabled): {} -> {}", user.getPhone(), message);
                notification.setStatus(NotificationStatus.SENT); // mark as sent in dev
                notification.setSentAt(LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("SMS failed to {}", user.getPhone(), e);
            notification.setStatus(NotificationStatus.FAILED);
        } finally {
            notificationRepository.save(notification);
        }
    }
}
