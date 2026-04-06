package org.stylehub.backend.e_commerce.cart.item.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,name = "price")
    private BigDecimal price;

    @Column(nullable = false,name = "quantity")
    private Integer quantity;

    @Formula("price*quantity")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;
}
