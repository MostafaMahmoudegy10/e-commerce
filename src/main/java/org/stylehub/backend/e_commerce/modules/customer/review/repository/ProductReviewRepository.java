package org.stylehub.backend.e_commerce.modules.customer.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.modules.customer.review.entity.ProductReview;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {

    boolean existsByUser_ExternalUserIdAndProduct_Id(String externalUserId, UUID productId);

    Optional<ProductReview> findByUser_ExternalUserIdAndProduct_Id(String externalUserId, UUID productId);

    Optional<ProductReview> findByIdAndUser_ExternalUserId(UUID reviewId, String externalUserId);

    List<ProductReview> findAllByProduct_IdOrderByCreatedAtDesc(UUID productId);

    @Query("""
            select coalesce(avg(pr.rating), 0)
            from ProductReview pr
            where pr.product.id = :productId
            """)
    Double findAverageRatingByProductId(UUID productId);

    long countByProduct_Id(UUID productId);

    @Query("""
            select coalesce(avg(pr.rating), 0)
            from ProductReview pr
            join pr.product p
            join p.brand b
            where b.id = :brandId
            """)
    Double findAverageRatingByBrandId(UUID brandId);

    @Query("""
            select count(pr)
            from ProductReview pr
            join pr.product p
            join p.brand b
            where b.id = :brandId
            """)
    long countByBrandId(UUID brandId);
}
