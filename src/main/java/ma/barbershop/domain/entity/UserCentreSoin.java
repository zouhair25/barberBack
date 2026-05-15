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

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;

    @Column(name = "is_confirmed",nullable = false)
    private boolean confirmed = false;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="fix", nullable = false)
    private String fix;

    @Column(name="adresse", nullable = false)
    private String adresse;

    @OneToMany(mappedBy = "userCentreSoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OpeningHours> openingHours = new ArrayList<>();

    @OneToMany(mappedBy = "userCentreSoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BarberService> services = new ArrayList<>();

    @OneToMany(mappedBy = "userCentreSoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "centre_soin_id")
    private CentreSoin centreSoin;



    public String getShopName() {

        return "";
    }
}
