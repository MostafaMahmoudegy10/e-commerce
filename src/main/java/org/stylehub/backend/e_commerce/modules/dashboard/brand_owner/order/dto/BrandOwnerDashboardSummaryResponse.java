package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record BrandOwnerDashboardSummaryResponse(
        Long totalOrders,
        Long pendingOrders,
        Long deliveredOrders,
        Long cancelledOrders,
        BigDecimal totalRevenue,
        Long lowStockProductItems,
        List<BrandOwnerOrderResponse> recentOrders,
        List<BrandOwnerSalesPointResponse> salesTimeline
) {
}
