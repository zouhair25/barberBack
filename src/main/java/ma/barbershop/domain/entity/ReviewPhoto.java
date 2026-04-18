package ma.barbershop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.barbershop.domain.enums.PhotoType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "photo_type", nullable = false)
    private PhotoType photoType;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}
