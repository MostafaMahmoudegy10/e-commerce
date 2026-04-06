package org.stylehub.backend.e_commerce.user.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.TrueFalseConverter;
import org.springframework.context.annotation.Configuration;
import org.stylehub.backend.e_commerce.address.entity.Address;
import org.stylehub.backend.e_commerce.product.category.entity.Category;
import org.stylehub.backend.e_commerce.review.entity.Review;
import org.stylehub.backend.e_commerce.user.entity.converter.GenderConverter;
import org.stylehub.backend.e_commerce.user.entity.converter.RoleConverter;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @Column(name = "user_id",unique = true,nullable = false)
//    private UUID userId;

    @Embedded
    private Name name;

    @Column(name = "email",columnDefinition = "varchar(255)",
            length = 255, nullable = false,unique = true)
    private String email;

    @Basic
    private Integer age;

    @Column(name = "is_model",nullable = false)
    private boolean isModel;

    @Column(name = "role", nullable = false, length = 20)
    @Convert(converter = RoleConverter.class)
    private Role role;

    @Column(name = "gender", columnDefinition = "CHAR(1)")
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = {CascadeType.REMOVE,
    CascadeType.PERSIST,CascadeType.MERGE})
    private List<Address> addresses=new ArrayList<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE,
            CascadeType.PERSIST,CascadeType.MERGE})
    private List<Review>reviews=new ArrayList<>();

    @Basic
    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToMany
    @JoinTable(
            name = "users_category",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories=new ArrayList<>();


}
