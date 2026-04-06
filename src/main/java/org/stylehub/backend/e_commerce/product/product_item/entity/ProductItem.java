package org.stylehub.backend.e_commerce.product.product_item.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.image.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "color", nullable = false,columnDefinition = "varchar(20)")
    private String color;

    @Convert(converter = SizeConverter.class)
    @Column(nullable = false)
    private Size size;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 30, nullable = false,columnDefinition = "varchar(30)")
    private String sku;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "productItem",
    cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private List<ProductItemImage> productItemImages;


}
