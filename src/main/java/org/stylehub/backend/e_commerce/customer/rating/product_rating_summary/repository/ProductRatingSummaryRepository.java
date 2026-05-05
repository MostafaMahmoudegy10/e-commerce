package org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.entity.ProductRatingSummary;

import java.util.Optional;
import java.util.UUID;

public interface ProductRatingSummaryRepository extends JpaRepository<ProductRatingSummary, UUID> {
    Optional<ProductRatingSummary> findProductRatingSummariesByProduct_Id(UUID id);
}
