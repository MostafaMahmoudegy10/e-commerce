package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Model Search", description = "Search and filter model profiles for brand collaboration discovery.")
public class BrandOwnerModelSearchController {

    private final ModelSearchService modelSearchService;

    @Operation(summary = "Search/filter models", description = "Finds model profiles using filter criteria such as gender, size, body type, and availability.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid model search filter"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a brand owner")
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ModelSearchResponse>> searchModels(
            @ModelAttribute ModelSearchFilterRequest filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.modelSearchService.searchModels(filter, pageable));
    }
}
