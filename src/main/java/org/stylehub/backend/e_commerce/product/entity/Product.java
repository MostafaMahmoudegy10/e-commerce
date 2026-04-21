package org.stylehub.backend.e_commerce.product.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"brand", "category", "productItems"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id",nullable = false)
    private Brand brand;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private Set<ProductItem>  productItems=new HashSet<>();

}
