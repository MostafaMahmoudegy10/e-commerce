package org.stylehub.backend.e_commerce.platform.media.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;

import java.util.UUID;

@Entity
@Getter
@Setter
public class ProductColorImages {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "public_id",columnDefinition = "varchar(255)",nullable = false)
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id",nullable = false)
    private ProductColor productColor;


}
