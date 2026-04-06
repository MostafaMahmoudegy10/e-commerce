package org.stylehub.backend.e_commerce.address.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Address {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;

    @Column(name = "street_name",length = 50,columnDefinition = "varchar(50)")
    private String streetName;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
