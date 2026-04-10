package org.stylehub.backend.e_commerce;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.security.current_user.dto.AuthenticatedUser;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthDemoController {

    private final CurrentUserProvider currentUserProvider;

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
}