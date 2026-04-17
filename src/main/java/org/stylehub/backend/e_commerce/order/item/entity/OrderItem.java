package org.stylehub.backend.e_commerce.order.item.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_price", nullable = false)
    private BigDecimal orderPrice;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    @Column(name = "total_price",nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "size_name", nullable = false)
    private String sizeName;

    @ManyToOne
    @JoinColumn(name = "product_item_id", nullable = false)
    private ProductItem productItem;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
