package ma.barbershop.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ville")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Ville {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String name;

    //@JsonIgnore
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "pays_id" )
    //private  Pays pays;



}
