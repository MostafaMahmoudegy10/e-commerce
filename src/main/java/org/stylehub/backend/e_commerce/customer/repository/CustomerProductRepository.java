package org.stylehub.backend.e_commerce.customer.repository;

import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductFilterRequest;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductsResponse;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

public interface CustomerProductRepository {
    PageResponse<FindAllProductsResponse> findAllProductsFilter(FindAllProductFilterRequest filter, Pageable pageable,String brandId);
}
