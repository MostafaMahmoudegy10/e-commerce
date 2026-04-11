package org.stylehub.backend.e_commerce.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.brand.entity.Brand;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    String existsByUser_Id(UUID userId);
}
