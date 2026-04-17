package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.ReturnRequestStatus;

public record BrandOwnerReturnDecisionRequest(
        ReturnRequestStatus status,
        String brandResponse
) {
}
