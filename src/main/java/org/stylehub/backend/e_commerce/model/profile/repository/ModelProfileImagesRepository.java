package org.stylehub.backend.e_commerce.model.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileImages;

import java.util.UUID;

public interface ModelProfileImagesRepository extends JpaRepository<ModelProfileImages, UUID> {
}
