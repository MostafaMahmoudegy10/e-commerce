package org.stylehub.backend.e_commerce.model.gig.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreementSubmission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModelAgreementSubmissionRepository extends JpaRepository<ModelAgreementSubmission, UUID> {

    @Query("""
            select distinct s
            from ModelAgreementSubmission s
            left join fetch s.assets
            join fetch s.agreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            where a.id = :agreementId
              and a.modelProfile.user.externalUserId = :externalId
            order by s.createdAt desc, s.id desc
            """)
    List<ModelAgreementSubmission> findAllByAgreementIdAndModelExternalId(
            @Param("agreementId") UUID agreementId,
            @Param("externalId") String externalId
    );

    @Query("""
            select distinct s
            from ModelAgreementSubmission s
            left join fetch s.assets
            join fetch s.agreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            where a.id = :agreementId
              and a.brand.user.externalUserId = :externalId
            order by s.createdAt desc, s.id desc
            """)
    List<ModelAgreementSubmission> findAllByAgreementIdAndBrandExternalId(
            @Param("agreementId") UUID agreementId,
            @Param("externalId") String externalId
    );

    @Query("""
            select distinct s
            from ModelAgreementSubmission s
            left join fetch s.assets
            join fetch s.agreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            where s.id = :submissionId
              and a.id = :agreementId
              and a.brand.user.externalUserId = :externalId
            """)
    Optional<ModelAgreementSubmission> findByIdAndAgreementIdAndBrandExternalId(
            @Param("submissionId") UUID submissionId,
            @Param("agreementId") UUID agreementId,
            @Param("externalId") String externalId
    );

    Optional<ModelAgreementSubmission> findFirstByAgreement_IdOrderByCreatedAtDescIdDesc(UUID agreementId);
}
