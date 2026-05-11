package org.stylehub.backend.e_commerce.model.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "model_profile_available_for", uniqueConstraints = {
        @UniqueConstraint(name = "uq_model_available_for",columnNames = {"model_profile_id," +
                "availableFor," +
                "pricePerSession"})
})
public class ModelProfileAvailableFor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_profile_id",nullable = false)
    private ModelProfile  modelProfile;

    @Enumerated(EnumType.STRING)
    private AvailableFor availableFor;

    @Column(name = "price_per_session",nullable = false)
    private BigDecimal pricePerSession;
}
