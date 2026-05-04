package org.stylehub.backend.e_commerce.product.color.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "product_colors",uniqueConstraints = @UniqueConstraint(name = "uq_color_code_product_id"
        ,columnNames = {"product_id","color_code"})
)
public class ProductColor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "color_code",nullable = false)
    private String colorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;


}
