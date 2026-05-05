package org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.dto;

public record CalculateSummaryDto(
        Long ratingCount,
        Double avgRating,
        Long stars1Count,
        Long stars2Count,
        Long stars3Count,
        Long stars4Count,
        Long stars5Count
) {
}