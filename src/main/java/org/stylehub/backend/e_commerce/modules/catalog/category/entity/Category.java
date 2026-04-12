package org.stylehub.backend.e_commerce.modules.catalog.category.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "category_name_en", nullable = false)
    private String categoryNameEn;

    @Column(name = "category_name_ar", nullable = false)
    private String categoryNameAr;

    @Column(columnDefinition = "text", nullable = false)
    private String categoryDescriptionEn;


    @Column(columnDefinition = "text", nullable = false)
    private String categoryDescriptionAr;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @Enumerated(EnumType.STRING)
    private Gender categoryGender;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;
}

