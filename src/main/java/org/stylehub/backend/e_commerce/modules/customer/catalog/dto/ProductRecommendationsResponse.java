package org.stylehub.backend.e_commerce.modules.customer.catalog.dto;

import java.util.List;

public record ProductRecommendationsResponse(
        List<ProductSummaryResponse> recommendations,
        List<ProductSummaryResponse> recentlyViewed
) {
}
