package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BrandOwnerSalesPointResponse(
        LocalDate date,
        BigDecimal revenue,
        Long ordersCount
) {
}
