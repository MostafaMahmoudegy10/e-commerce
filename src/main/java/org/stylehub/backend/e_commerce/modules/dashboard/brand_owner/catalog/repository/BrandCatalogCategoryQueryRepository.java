package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryStatsResponse;

public interface BrandCatalogCategoryQueryRepository {

    Page<Category> findBrandCategories(String externalId, BrandCategoryFilterRequest filter, Pageable pageable);

    BrandCategoryStatsResponse getBrandCategoryStats(String externalId);
}
