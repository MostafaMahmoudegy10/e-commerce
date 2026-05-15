package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductFilterRequest;
import org.stylehub.backend.e_commerce.product.entity.Product;

public interface BrandCatalogProductQueryRepository {

    Page<Product> findBrandProducts(String externalId, BrandProductFilterRequest filter, Pageable pageable);
}
