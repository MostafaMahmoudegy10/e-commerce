package org.stylehub.backend.e_commerce.cart.item.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

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

    @Column(nullable = false, name = "size_name")
    private String sizeName;

    @Formula("price*quantity")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_item_id",nullable = false)
    private ProductItem productItem;

    @ManyToOne
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;
}
