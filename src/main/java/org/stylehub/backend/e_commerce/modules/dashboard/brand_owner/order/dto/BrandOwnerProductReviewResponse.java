package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record BrandOwnerProductReviewResponse(
        UUID reviewId,
        UUID productId,
        String productNameEn,
        Integer rating,
        String comment,
        String customerEmail,
        Timestamp createdAt
) {
}
