package org.stylehub.backend.e_commerce.modules.customer.favorite.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "favorite",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_product",
                columnNames = {"user_id", "product_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
