package org.stylehub.backend.e_commerce.address.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,columnDefinition = "varchar(30)")
    private String countryName;

    @Column(unique = true,columnDefinition = "varchar(3)")
    private String countryCode;

    @Column(unique = true,columnDefinition = "varchar(5)")
    private String phoneCode;

    @Column(columnDefinition = "char(3)")
    private String currencyCode;
}
