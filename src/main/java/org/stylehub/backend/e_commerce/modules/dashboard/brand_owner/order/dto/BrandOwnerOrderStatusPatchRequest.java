package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

public record BrandOwnerOrderStatusPatchRequest(
        OrderStatus orderStatus
) {
}
