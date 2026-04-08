package org.stylehub.backend.e_commerce.brand.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "varchar(255)",nullable = false)
    private String brandName;

    @Column(columnDefinition = "varchar(255)")
    private String brandEmail;

    @Column(columnDefinition = "text",nullable = false)
    private String description;




}
