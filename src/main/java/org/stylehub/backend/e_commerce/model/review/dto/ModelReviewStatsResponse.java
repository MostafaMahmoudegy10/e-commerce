package org.stylehub.backend.e_commerce.model.review.dto;

import java.math.BigDecimal;

public record ModelReviewStatsResponse(
        BigDecimal ratingAvg,
        Integer ratingCount,
        long oneStarCount,
        long twoStarCount,
        long threeStarCount,
        long fourStarCount,
        long fiveStarCount
) {
}
