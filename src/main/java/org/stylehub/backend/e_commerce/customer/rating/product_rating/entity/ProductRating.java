package org.stylehub.backend.e_commerce.customer.rating.product_rating.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "product_rating",
uniqueConstraints = @UniqueConstraint(name = "uq_product_customer",
        columnNames = {"product_id","customer_id"}),
check = @CheckConstraint(constraint = "stars BETWEEN 1 AND 5"))
public class ProductRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,name = "stars",columnDefinition = "int")
    private Integer stars;

    @Column(nullable = false,name = "comment",columnDefinition = "text")
    private String comment;

    @CreationTimestamp
    private Instant createdAt=Instant.now();

    @UpdateTimestamp
    private Instant updatedAt=Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",nullable = false)
    private CustomerProfile customer;

}
