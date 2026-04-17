package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.util.UUID;

public record BrandOwnerReviewSummaryResponse(
        UUID brandId,
        String brandName,
        Double averageRating,
        Long totalReviews
) {
}
