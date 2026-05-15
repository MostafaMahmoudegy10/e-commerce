package org.stylehub.backend.e_commerce.model.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;

import java.util.Optional;
import java.util.UUID;

public interface ModelProfileRepository extends JpaRepository<ModelProfile, UUID> {
    Optional<ModelProfile> findModelProfileByUser_Id(UUID id);
    boolean existsByUser_Id(UUID id);
}
