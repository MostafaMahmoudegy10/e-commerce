package org.stylehub.backend.e_commerce.address.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "varchar(50)")
    private String cityName;

    @Column(columnDefinition = "varchar(10)")
    private String cityCode;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

}
