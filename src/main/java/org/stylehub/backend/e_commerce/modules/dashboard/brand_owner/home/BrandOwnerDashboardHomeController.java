package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardHomeResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.service.BrandDashboardHomeService;

@RestController
@RequestMapping("api/v1/brands/dashboard/home")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerDashboardHomeController {

    private final BrandDashboardHomeService brandDashboardHomeService;

    @GetMapping
    public ResponseEntity<BrandDashboardHomeResponse> getHomeDashboard(
            @RequestParam(defaultValue = "7D") String range
    ) {
        return ResponseEntity.ok(this.brandDashboardHomeService.getHomeDashboard(range));
    }
}
