package org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.entity.ProductRatingSummary;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRatingSummaryRepository extends JpaRepository<ProductRatingSummary, UUID> {
    Optional<ProductRatingSummary> findProductRatingSummariesByProduct_Id(UUID id);

    @Query("""
        select ps.avgRating from ProductRatingSummary ps
        join ps.product p
        where p.id=:productId
        """)
    BigDecimal findAvgRatingByProduct_Id(UUID productId);
}
