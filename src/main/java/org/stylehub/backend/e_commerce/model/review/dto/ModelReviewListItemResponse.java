package org.stylehub.backend.e_commerce.model.review.dto;

import java.time.Instant;
import java.util.UUID;

public record ModelReviewListItemResponse(
        UUID reviewId,
        UUID agreementId,
        String agreementNumber,
        UUID brandId,
        String brandName,
        Integer stars,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
}
