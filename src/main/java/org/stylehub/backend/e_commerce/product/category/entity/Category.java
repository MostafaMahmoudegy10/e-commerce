package org.stylehub.backend.e_commerce.product.category.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private Gender categoryGender;

    @OneToMany(mappedBy = "category")
    private List<Category> subCategory;

    @ManyToMany(mappedBy = "categories")
    private List<User>users=new ArrayList<>();
}

