package org.stylehub.backend.e_commerce.model.gig.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreementPayment;

import java.util.Optional;
import java.util.UUID;

public interface ModelAgreementPaymentRepository extends JpaRepository<ModelAgreementPayment, UUID> {

    Optional<ModelAgreementPayment> findByAgreement_Id(UUID agreementId);

    @Query("""
            select p
            from ModelAgreementPayment p
            join fetch p.agreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            where a.id = :agreementId
              and a.brand.user.externalUserId = :externalId
            """)
    Optional<ModelAgreementPayment> findByAgreementIdAndBrandExternalId(
            @Param("agreementId") UUID agreementId,
            @Param("externalId") String externalId
    );

    @Query("""
            select p
            from ModelAgreementPayment p
            join fetch p.agreement a
            join fetch a.brand b
            join fetch b.user
            join fetch a.modelProfile mp
            join fetch mp.user
            where a.id = :agreementId
              and a.modelProfile.user.externalUserId = :externalId
            """)
    Optional<ModelAgreementPayment> findByAgreementIdAndModelExternalId(
            @Param("agreementId") UUID agreementId,
            @Param("externalId") String externalId
    );
}
