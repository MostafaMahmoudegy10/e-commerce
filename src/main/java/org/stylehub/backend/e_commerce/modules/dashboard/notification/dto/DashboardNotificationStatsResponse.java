package org.stylehub.backend.e_commerce.modules.dashboard.notification.dto;

import java.util.List;

public record DashboardNotificationStatsResponse(
        long totalNotifications,
        long unread,
        long read,
        List<DashboardNotificationTypeCountResponse> countsByType
) {
}
