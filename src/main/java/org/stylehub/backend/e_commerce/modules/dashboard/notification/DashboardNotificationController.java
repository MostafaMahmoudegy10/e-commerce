package org.stylehub.backend.e_commerce.modules.dashboard.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationReadAllResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationStatsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.service.DashboardNotificationService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/dashboard/notifications")
@PreAuthorize("hasAnyRole('CUSTOMER','BRAND_OWNER')")
@RequiredArgsConstructor
public class DashboardNotificationController {

    private final DashboardNotificationService dashboardNotificationService;

    @GetMapping
    public ResponseEntity<PageResponse<DashboardNotificationViewResponse>> findNotifications(
            @ModelAttribute DashboardNotificationFilterRequest filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.dashboardNotificationService.findNotifications(filter, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardNotificationStatsResponse> getStats() {
        return ResponseEntity.ok(this.dashboardNotificationService.getStats());
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<DashboardNotificationViewResponse> markAsRead(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(this.dashboardNotificationService.markAsRead(notificationId));
    }

    @PostMapping("/read-all")
    public ResponseEntity<DashboardNotificationReadAllResponse> markAllAsRead() {
        return ResponseEntity.ok(this.dashboardNotificationService.markAllAsRead());
    }
}
