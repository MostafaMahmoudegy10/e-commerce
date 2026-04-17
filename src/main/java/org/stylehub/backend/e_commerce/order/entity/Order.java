package org.stylehub.backend.e_commerce.order.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
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

    @Column(name = "paid_at")
    private Timestamp paidAt;

    @Column(name = "shipped_at")
    private Timestamp shippedAt;

    @Column(name = "delivered_at")
    private Timestamp deliveredAt;

    @Column(name = "cancelled_at")
    private Timestamp cancelledAt;

    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false, name = "brand_id")
    private Brand brand;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
    mappedBy = "order")
    private List<OrderItem> orderItems;
}
