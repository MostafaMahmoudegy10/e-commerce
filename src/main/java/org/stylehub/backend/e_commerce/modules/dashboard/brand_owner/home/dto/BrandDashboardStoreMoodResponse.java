package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

public record BrandDashboardStoreMoodResponse(
        long deliveredOrders,
        long lowStockAlerts,
        long newNotifications
) {
}
