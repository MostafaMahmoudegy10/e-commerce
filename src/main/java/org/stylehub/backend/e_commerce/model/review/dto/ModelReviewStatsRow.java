package org.stylehub.backend.e_commerce.model.review.dto;

public record ModelReviewStatsRow(
        Long ratingCount,
        Double avgRating,
        Long oneStarCount,
        Long twoStarCount,
        Long threeStarCount,
        Long fourStarCount,
        Long fiveStarCount
) {
}
