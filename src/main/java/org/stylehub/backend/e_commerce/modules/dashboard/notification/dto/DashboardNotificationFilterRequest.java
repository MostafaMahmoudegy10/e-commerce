package org.stylehub.backend.e_commerce.modules.dashboard.notification.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationReadFilter;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;

public record DashboardNotificationFilterRequest(
        DashboardNotificationReadFilter status,
        DashboardNotificationType type,
        String search
) {
}
