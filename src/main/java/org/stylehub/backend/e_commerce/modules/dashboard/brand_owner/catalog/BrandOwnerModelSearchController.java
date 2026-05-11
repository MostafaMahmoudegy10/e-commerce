package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchFilterRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchResponse;
import org.stylehub.backend.e_commerce.model.profile.service.ModelSearchService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

@RestController
@RequestMapping("api/v1/brands/models")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerModelSearchController {

    private final ModelSearchService modelSearchService;

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ModelSearchResponse>> searchModels(
            @ModelAttribute ModelSearchFilterRequest filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.modelSearchService.searchModels(filter, pageable));
    }
}
