package ma.barbershop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_centre_soin")
@Getter
@Setter
@Builder
public class UserCentreSoin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;


    @OneToMany(mappedBy = "UserCentreSoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OpeningHours> openingHours = new ArrayList<>();

    @OneToMany(mappedBy = "UserCentreSoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BarberService> services = new ArrayList<>();

    @OneToMany(mappedBy = "UserCentreSoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    public String getShopName() {

        return "";
    }
}
