package org.stylehub.backend.e_commerce.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderFilterRequest;
import org.stylehub.backend.e_commerce.order.entity.Order;

public interface BrandOrderQueryRepository {

    Page<Order> findBrandOrders(
            String externalId,
            BrandOrderFilterRequest filter,
            Pageable pageable
    );
}
