package org.stylehub.backend.e_commerce.model.profile.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForRow;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileAvailableFor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ModelProfileAvailableForRepository extends JpaRepository<ModelProfileAvailableFor, UUID> {

    @Query("""
            select new org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForRow(
                mpaf.modelProfile.id,
                mpaf.availableFor,
                mpaf.pricePerSession
            )
            from ModelProfileAvailableFor mpaf
            where mpaf.modelProfile.id in :modelIds
            order by mpaf.modelProfile.id asc, mpaf.availableFor asc
            """)
    List<ModelAvailableForRow> findAvailableForByModelProfileIds(@Param("modelIds") Collection<UUID> modelIds);
}
