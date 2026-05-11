package org.stylehub.backend.e_commerce.modules.dashboard.notification.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;

public record DashboardNotificationTypeCountResponse(
        DashboardNotificationType type,
        long count
) {
}
