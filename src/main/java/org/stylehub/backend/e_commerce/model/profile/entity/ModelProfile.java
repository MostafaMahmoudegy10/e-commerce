package org.stylehub.backend.e_commerce.model.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.model.profile.enums.BodyType;
import org.stylehub.backend.e_commerce.model.profile.enums.SkinTone;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "model_profiles")
public class ModelProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String modelName;

    private String modelEmail;

    private String bio;

    private String city;

    private Integer age;

    private Integer heightCm;

    private Integer weightKg;

    private String hairColor;

    private BigDecimal ratingAvg;

    private Integer ratingCount;

    private Boolean isAvailable;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    private SkinTone skinTone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.isAvailable=true;
        this.ratingAvg=BigDecimal.ZERO;
        this.ratingCount=0;
    }
}
