package org.stylehub.backend.e_commerce.model.review.dto;

import java.time.Instant;
import java.util.UUID;

public record ModelAgreementReviewResponse(
        UUID reviewId,
        UUID agreementId,
        String agreementNumber,
        UUID modelProfileId,
        String modelName,
        Integer stars,
        String comment,
        Instant createdAt,
        Instant updatedAt,
        boolean reviewed
) {
}
