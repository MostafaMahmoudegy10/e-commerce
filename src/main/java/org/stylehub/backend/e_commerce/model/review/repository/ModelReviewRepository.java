package org.stylehub.backend.e_commerce.model.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewSummaryRow;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewStatsRow;
import org.stylehub.backend.e_commerce.model.review.entity.ModelReview;

import java.util.Optional;
import java.util.UUID;

public interface ModelReviewRepository extends JpaRepository<ModelReview, UUID> {

    Optional<ModelReview> findByAgreement_Id(UUID agreementId);

    @Query(value = """
            select mr
            from ModelReview mr
            join fetch mr.agreement a
            join fetch mr.brand b
            join fetch mr.modelProfile mp
            join fetch mp.user
            where mp.user.externalUserId = :externalId
            order by mr.createdAt desc
            """,
            countQuery = """
            select count(mr.id)
            from ModelReview mr
            where mr.modelProfile.user.externalUserId = :externalId
            """)
    Page<ModelReview> findAllByModelExternalId(@Param("externalId") String externalId, Pageable pageable);

    @Query("""
            select new org.stylehub.backend.e_commerce.model.review.dto.ModelReviewSummaryRow(
                cast(count(mr.id) as long),
                coalesce(avg(mr.stars), 0.0)
            )
            from ModelReview mr
            where mr.modelProfile.id = :modelProfileId
            """)
    ModelReviewSummaryRow calculateSummary(@Param("modelProfileId") UUID modelProfileId);

    @Query("""
            select new org.stylehub.backend.e_commerce.model.review.dto.ModelReviewStatsRow(
                cast(count(mr.id) as long),
                coalesce(avg(mr.stars), 0.0),
                coalesce(sum(case when mr.stars = 1 then 1L else 0L end), 0L),
                coalesce(sum(case when mr.stars = 2 then 1L else 0L end), 0L),
                coalesce(sum(case when mr.stars = 3 then 1L else 0L end), 0L),
                coalesce(sum(case when mr.stars = 4 then 1L else 0L end), 0L),
                coalesce(sum(case when mr.stars = 5 then 1L else 0L end), 0L)
            )
            from ModelReview mr
            where mr.modelProfile.id = :modelProfileId
            """)
    ModelReviewStatsRow calculateStats(@Param("modelProfileId") UUID modelProfileId);
}
