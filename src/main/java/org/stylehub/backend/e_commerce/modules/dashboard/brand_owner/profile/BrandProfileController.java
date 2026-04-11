package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile.dto.BrandSetupRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile.dto.BrandSetupResponse;

@RestController
@RequestMapping("api/v1/brands/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class BrandProfileController {

    private final BrandProfileService brandProfileService;

    @PostMapping("/setup")
    public ResponseEntity<BrandSetupResponse> setupBrand(@ModelAttribute BrandSetupRequest request) {
        return ResponseEntity.ok(brandProfileService.setupBrand(request));
    }
}
