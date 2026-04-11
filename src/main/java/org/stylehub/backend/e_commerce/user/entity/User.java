package org.stylehub.backend.e_commerce.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.TrueFalseConverter;
import org.stylehub.backend.e_commerce.brand.entity.Brand;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_user_id", nullable = false, unique = true, length = 100)
    private String externalUserId;

    @Column(name = "email",nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "is_profile_completed",nullable = false)
    private Boolean isProfileCompleted;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}