package ma.barbershop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "opening_hours",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_centre_soin_id", "day_of_week"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_centre_soin_id", nullable = false)
    private UserCentreSoin userCentreSoin;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 0=Sunday, 1=Monday ... 6=Saturday

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "is_closed", nullable = false)
    private boolean closed = false;

    @Column(name = "slot_duration_min", nullable = false)
    private Integer slotDurationMin = 30;
}
