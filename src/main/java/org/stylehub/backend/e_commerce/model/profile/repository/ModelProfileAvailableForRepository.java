package org.stylehub.backend.e_commerce.model.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileAvailableFor;

import java.util.UUID;

public interface ModelProfileAvailableForRepository extends JpaRepository<ModelProfileAvailableFor, UUID> {
}
