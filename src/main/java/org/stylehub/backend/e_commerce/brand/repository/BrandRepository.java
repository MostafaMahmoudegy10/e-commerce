package org.stylehub.backend.e_commerce.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.brand.entity.Brand;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

    boolean existsByUser_Id(UUID userId);

    @Query("""
            select b from Brand b
            where b.user.externalUserId=:externalUserId
                        """)
    Optional<Brand> findByUser_ExternalUserId(@Param("externalUserId") UUID idExternalUser);

}
