package org.stylehub.backend.e_commerce.order.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(nullable = false,name = "customer_id")
    private CustomerProfile customer;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "brand_id",nullable = false)
    private Brand brand;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;


    public String generateOrderNumber() {
        return "SH-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
