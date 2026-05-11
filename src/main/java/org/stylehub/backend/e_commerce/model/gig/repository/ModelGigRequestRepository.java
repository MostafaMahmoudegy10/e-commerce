package org.stylehub.backend.e_commerce.model.gig.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelGigRequest;
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;

import java.util.Optional;
import java.util.UUID;

public interface ModelGigRequestRepository extends JpaRepository<ModelGigRequest, UUID> {

    @Query(value = """
            select r
            from ModelGigRequest r
            join fetch r.brand b
            join fetch b.user
            join fetch r.modelProfile mp
            join fetch mp.user
            where r.modelProfile.user.externalUserId = :externalId
              and (:status is null or r.requestStatus = :status)
            order by r.createdAt desc
            """,
            countQuery = """
            select count(r.id)
            from ModelGigRequest r
            where r.modelProfile.user.externalUserId = :externalId
              and (:status is null or r.requestStatus = :status)
            """)
    Page<ModelGigRequest> findAllByModelExternalId(
            @Param("externalId") String externalId,
            @Param("status") RequestStatus status,
            Pageable pageable
    );

    @Query(value = """
            select r
            from ModelGigRequest r
            join fetch r.brand b
            join fetch b.user
            join fetch r.modelProfile mp
            join fetch mp.user
            where r.brand.user.externalUserId = :externalId
              and (:status is null or r.requestStatus = :status)
            order by r.createdAt desc
            """,
            countQuery = """
            select count(r.id)
            from ModelGigRequest r
            where r.brand.user.externalUserId = :externalId
              and (:status is null or r.requestStatus = :status)
            """)
    Page<ModelGigRequest> findAllByBrandExternalId(
            @Param("externalId") String externalId,
            @Param("status") RequestStatus status,
            Pageable pageable
    );

    @Query("""
            select r
            from ModelGigRequest r
            join fetch r.brand b
            join fetch b.user
            join fetch r.modelProfile mp
            join fetch mp.user
            where r.id = :requestId
              and r.modelProfile.user.externalUserId = :externalId
            """)
    Optional<ModelGigRequest> findByIdAndModelExternalId(@Param("requestId") UUID requestId, @Param("externalId") String externalId);

    @Query("""
            select r
            from ModelGigRequest r
            join fetch r.brand b
            join fetch b.user
            join fetch r.modelProfile mp
            join fetch mp.user
            where r.id = :requestId
              and r.brand.user.externalUserId = :externalId
            """)
    Optional<ModelGigRequest> findByIdAndBrandExternalId(@Param("requestId") UUID requestId, @Param("externalId") String externalId);
}
