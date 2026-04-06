package org.stylehub.backend.e_commerce.admins.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.user.entity.User;

@Entity
@Table(name = "admins")
@Getter
@Setter
@EqualsAndHashCode
public class Admin {

    @Id
    private AdminComposedPK id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("brandId")
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

}
