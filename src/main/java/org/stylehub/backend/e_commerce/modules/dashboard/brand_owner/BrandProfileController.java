package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.brand.dto.BrandCreationRequest;
import org.stylehub.backend.e_commerce.brand.service.BrandService;

import java.util.Map;

@RestController
@RequestMapping("api/v1/brands/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class BrandProfileController {

    private final BrandService brandProfileService;

    @PostMapping("/setup")
    public ResponseEntity<Map<String,Object>> setupBrand(@ModelAttribute BrandCreationRequest request) {
        return ResponseEntity.ok(brandProfileService.setupBrand(request));
    }
}