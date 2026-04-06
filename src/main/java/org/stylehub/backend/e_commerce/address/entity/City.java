package org.stylehub.backend.e_commerce.address.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(50)")
    private String cityName;

    @Column(columnDefinition = "varchar(10)")
    private String cityCode;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

}
