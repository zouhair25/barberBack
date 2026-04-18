package ma.barbershop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "closing_days",
        uniqueConstraints = @UniqueConstraint(columnNames = {"barber_id", "closed_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private BarberProfile barber;

    @Column(name = "closed_date", nullable = false)
    private LocalDate closedDate;

    private String reason;
}
