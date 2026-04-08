package org.stylehub.backend.e_commerce.product.category.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.ArrayList;
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

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @Enumerated(EnumType.STRING)
    private Gender categoryGender;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subCategory;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}

