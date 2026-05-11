package org.stylehub.backend.e_commerce.modules.dashboard.notification.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;

public record DashboardNotificationTypeCountRow(
        DashboardNotificationType type,
        long count
) {
}
