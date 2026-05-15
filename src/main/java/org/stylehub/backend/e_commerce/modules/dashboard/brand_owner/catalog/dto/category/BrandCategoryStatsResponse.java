package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category;

public record BrandCategoryStatsResponse(
        long totalCategories,
        long maleCategories,
        long femaleCategories,
        long withParentCategories,
        long withoutParentCategories
) {
}
