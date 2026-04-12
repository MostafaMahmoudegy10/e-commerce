package org.stylehub.backend.e_commerce.modules.customer.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.CustomerProfileSetupRequest;
import org.stylehub.backend.e_commerce.modules.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/setup")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> setupProfile(@ModelAttribute
                                                                CustomerProfileSetupRequest request) {
        return ResponseEntity.ok(customerProfileService.setupProfile(request));
    }

    @GetMapping("/view")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> viewProfile() {
        return ResponseEntity.ok(customerProfileService.viewProfile());
    }
}
