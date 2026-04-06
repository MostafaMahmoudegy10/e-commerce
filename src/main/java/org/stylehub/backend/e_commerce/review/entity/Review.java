package org.stylehub.backend.e_commerce.review.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "decimal(5,2)")
    private BigDecimal rating;

    private String reviewComment;
}
