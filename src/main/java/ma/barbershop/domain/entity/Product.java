package ma.barbershop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private BarberProfile barber;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String sku;

    @Column(name = "price_sell", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSell;

    @Column(name = "price_cost", precision = 10, scale = 2)
    private BigDecimal priceCost;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "stock_alert_min", nullable = false)
    private Integer stockAlertMin = 5;

    @Column(name = "is_for_sale", nullable = false)
    private boolean forSale = true; // visible to clients

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    private String category;

    @Column(name = "photo_url")
    private String photoUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
