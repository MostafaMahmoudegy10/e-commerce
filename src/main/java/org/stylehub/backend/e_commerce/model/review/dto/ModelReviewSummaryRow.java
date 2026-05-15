package org.stylehub.backend.e_commerce.model.review.dto;

public record ModelReviewSummaryRow(
        Long ratingCount,
        Double avgRating
) {
}
