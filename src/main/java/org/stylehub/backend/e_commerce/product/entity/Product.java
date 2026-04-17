package org.stylehub.backend.e_commerce.product.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,name = "product_name_en")
    private String productNameEn;


    @Column(nullable = false,name = "product_name_ar")
    private String productNameAr;

    @Column(nullable = false,name = "product_description_en")
    private String productDescriptionEn;

    @Column(nullable = false,name = "product_description_ar")
    private String productDescriptionAr;

    @Column(name = "thumbnail",nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "public_id",nullable = false)
    private String publicId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @ManyToOne
    @JoinColumn(name = "brand_id",nullable = false)
    private Brand brand;

    @OneToOne
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private List<ProductItem>  productItems;

}
