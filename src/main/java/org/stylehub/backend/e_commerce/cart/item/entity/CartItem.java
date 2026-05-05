package org.stylehub.backend.e_commerce.cart.item.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;

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
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_variant_id",nullable = false)
    private ProductVariant productVariant;

    public Integer updateCartItemQuantity(Integer newRequested) {
        this.quantity+=newRequested;
        return quantity;
    }


}
