package org.stylehub.backend.e_commerce.product.color.variant.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "product_variants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_variant_product_color_id_size", columnNames = {"product_color_id","size"})
})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 30)
    private String size;

    @Column(nullable = false)
    private Integer stock= 0;

    @Column(nullable = false,unique = true)
    private String sku;

    @Column(name = "price_override")
    private BigDecimal priceOverride;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id",nullable = false)
    private ProductColor productColor;


}