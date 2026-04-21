package org.stylehub.backend.e_commerce.platform.media.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"productItem"})
public class ProductItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "public_id",columnDefinition = "varchar(255)",nullable = false)
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

}
