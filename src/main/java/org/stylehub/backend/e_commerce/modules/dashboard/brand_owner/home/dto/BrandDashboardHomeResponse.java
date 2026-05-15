package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import java.util.List;

public record BrandDashboardHomeResponse(
        String brandName,
        String range,
        BrandDashboardSummaryResponse summary,
        List<BrandDashboardRevenuePointResponse> revenueSeries,
        List<BrandDashboardOrderStatusCountResponse> orderStatusDistribution,
        List<BrandDashboardRecentProductResponse> recentProducts,
        BrandDashboardGlanceResponse todayAtGlance,
        BrandDashboardStoreMoodResponse storeMood,
        List<BrandDashboardRecentOrderResponse> recentOrders,
        List<BrandDashboardNotificationPreviewResponse> needAttention
) {
}
