package org.stylehub.backend.e_commerce.model.gig.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreement;

import java.util.Optional;
import java.util.UUID;

public interface ModelAgreementRepository extends JpaRepository<ModelAgreement, UUID> {

    @Query(value = """
            select a
            from ModelAgreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            join fetch a.request r
            where a.modelProfile.user.externalUserId = :externalId
              and (:status is null or a.agreementStatus = :status)
            order by a.createdAt desc
            """,
            countQuery = """
            select count(a.id)
            from ModelAgreement a
            where a.modelProfile.user.externalUserId = :externalId
              and (:status is null or a.agreementStatus = :status)
            """)
    Page<ModelAgreement> findAllByModelExternalId(
            @Param("externalId") String externalId,
            @Param("status") AgreementStatus status,
            Pageable pageable
    );

    @Query(value = """
            select a
            from ModelAgreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            join fetch a.request r
            where a.brand.user.externalUserId = :externalId
              and (:status is null or a.agreementStatus = :status)
            order by a.createdAt desc
            """,
            countQuery = """
            select count(a.id)
            from ModelAgreement a
            where a.brand.user.externalUserId = :externalId
              and (:status is null or a.agreementStatus = :status)
            """)
    Page<ModelAgreement> findAllByBrandExternalId(
            @Param("externalId") String externalId,
            @Param("status") AgreementStatus status,
            Pageable pageable
    );

    @Query("""
            select a
            from ModelAgreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            join fetch a.request r
            where a.id = :agreementId
              and a.modelProfile.user.externalUserId = :externalId
            """)
    Optional<ModelAgreement> findByIdAndModelExternalId(@Param("agreementId") UUID agreementId, @Param("externalId") String externalId);

    @Query("""
            select a
            from ModelAgreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            join fetch a.request r
            where a.id = :agreementId
              and a.brand.user.externalUserId = :externalId
            """)
    Optional<ModelAgreement> findByIdAndBrandExternalId(@Param("agreementId") UUID agreementId, @Param("externalId") String externalId);
}
