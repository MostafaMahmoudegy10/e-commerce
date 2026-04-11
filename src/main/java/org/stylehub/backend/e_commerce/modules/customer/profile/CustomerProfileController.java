package org.stylehub.backend.e_commerce.modules.customer.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.CustomerProfileSetupRequest;
import org.stylehub.backend.e_commerce.modules.customer.profile.service.CustomerProfileService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer/profile")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER', 'BRAND_OWNER')")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @PostMapping("/setup")
    public ResponseEntity<Map<String, Object>> setupProfile(@RequestBody CustomerProfileSetupRequest request) {
        return ResponseEntity.ok(customerProfileService.setupProfile(request));
    }

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewProfile() {
        return ResponseEntity.ok(customerProfileService.viewProfile());
    }
}
