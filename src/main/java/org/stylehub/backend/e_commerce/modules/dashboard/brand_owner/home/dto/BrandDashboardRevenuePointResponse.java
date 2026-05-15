package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BrandDashboardRevenuePointResponse(
        LocalDate day,
        BigDecimal revenue
) {
}
