package org.stylehub.backend.e_commerce.modules.customer.catalog.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record ProductReviewResponse(
        UUID reviewId,
        UUID customerId,
        String customerEmail,
        Integer rating,
        String comment,
        Timestamp createdAt
) {
}
