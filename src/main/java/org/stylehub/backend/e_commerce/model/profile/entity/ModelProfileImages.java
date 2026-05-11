package org.stylehub.backend.e_commerce.model.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "model_profile_images")
public class ModelProfileImages {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String publicId;

    private String profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_profile_id")
    private ModelProfile modelProfile;
}
