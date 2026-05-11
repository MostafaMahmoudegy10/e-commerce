package org.stylehub.backend.e_commerce.model.profile.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelProfileImageRow;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileImages;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ModelProfileImagesRepository extends JpaRepository<ModelProfileImages, UUID> {

    @Query("""
            select new org.stylehub.backend.e_commerce.model.profile.dto.ModelProfileImageRow(
                mpi.modelProfile.id,
                mpi.id,
                mpi.profileImage
            )
            from ModelProfileImages mpi
            where mpi.modelProfile.id in :modelIds
            order by mpi.modelProfile.id asc, mpi.id asc
            """)
    List<ModelProfileImageRow> findImagesByModelProfileIds(@Param("modelIds") Collection<UUID> modelIds);
}
