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
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,columnDefinition = "varchar(30)")
    private String countryName;

    @Column(unique = true,columnDefinition = "varchar(3)")
    private String countryCode;

    @Column(unique = true,columnDefinition = "varchar(5)")
    private String phoneCode;

    @Column(columnDefinition = "char(3)")
    private String currencyCode;
}
