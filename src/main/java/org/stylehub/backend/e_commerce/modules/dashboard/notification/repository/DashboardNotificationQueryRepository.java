package org.stylehub.backend.e_commerce.modules.dashboard.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;

import java.util.UUID;

public interface DashboardNotificationQueryRepository {

    Page<DashboardNotification> findNotifications(UUID userId, DashboardNotificationFilterRequest filter, Pageable pageable);
}
