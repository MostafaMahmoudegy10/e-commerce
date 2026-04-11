package org.stylehub.backend.e_commerce;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.security.current_user.dto.AuthenticatedUser;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthDemoController {

    private final CurrentUserProvider currentUserProvider;
    private final UserSyncService userSyncService;

    @GetMapping("/public/ping")
    public ResponseEntity<Map<String, Object>> publicPing() {
        return ResponseEntity.ok(Map.of(
                "message", "public endpoint is reachable",
                "authenticated", false
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUser> me() {
        return ResponseEntity.ok(currentUserProvider.getCurrentUser());
    }

    @GetMapping("/customer/orders")
    @PreAuthorize("hasAnyRole('CUSTOMER','BRAND_OWNER')")
    public ResponseEntity<Map<String, Object>> customerOrders() {
        return ResponseEntity.ok(Map.of(
                "message", "customer or brand-owner protected endpoint",
                "user", currentUserProvider.getCurrentUser()
        ));
    }

    @GetMapping("/brands/owner/dashboard")
    @PreAuthorize("hasRole('BRAND_OWNER')")
    public ResponseEntity<Map<String, Object>> brandOwnerDashboard() {
        return ResponseEntity.ok(Map.of(
                "message", "brand owner protected endpoint",
                "user", currentUserProvider.getCurrentUser()
        ));
    }

    @PostMapping("/user/setup")
    @PreAuthorize("hasAnyRole('BRAND_OWNER','CUSTOMER')")
    public ResponseEntity< Map<String,Object>>setupUserData(){
        if(currentUserProvider.getCurrentUser()!=null &&
                currentUserProvider.getCurrentUser().isProfileCompleted() &&
                     this.userSyncService.checkIsCompleted(currentUserProvider.getEmail())) {

                    return ResponseEntity.ok(Map.of(
                            "setupTriggered", false,
                            "message", "profile already completed"
                    ));
        }
        this.userSyncService.upsert(currentUserProvider.externalId(),currentUserProvider.getEmail());
        return ResponseEntity.ok(Map.of(
                "setupTriggered" , true,
                "message", "profile updated"
        ));
    }
}