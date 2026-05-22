package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.model;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.gig.dto.BrandGigRequestViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestCreationRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestCreationResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestDecisionResponse;
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.gig.service.ModelGigRequestService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/models")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
@Tag(name = "Brand Model Collaboration", description = "Manage collaboration requests, agreements, submissions, payments, and reviews between brands and models.")
public class BrandOwnerModelGigRequestController {

    private final ModelGigRequestService modelGigRequestService;

    @GetMapping("/requests")
    public ResponseEntity<PageResponse<BrandGigRequestViewResponse>> findBrandRequests(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "status", required = false) RequestStatus status
    ) {
        return ResponseEntity.ok(this.modelGigRequestService.findBrandRequests(pageable, status));
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<BrandGigRequestViewResponse> findBrandRequestDetails(@PathVariable UUID requestId) {
        return ResponseEntity.ok(this.modelGigRequestService.findBrandRequestDetails(requestId));
    }

    @Operation(summary = "Send collaboration request", description = "Sends a collaboration request from the authenticated brand to a model.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collaboration request sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a brand owner"),
            @ApiResponse(responseCode = "404", description = "Model profile was not found")
    })
    @PostMapping("/{modelId}/requests")
    public ResponseEntity<ModelGigRequestCreationResponse> createRequest(
            @PathVariable UUID modelId,
            @RequestBody ModelGigRequestCreationRequest request
    ) {
        return ResponseEntity.ok(this.modelGigRequestService.createRequest(modelId, request));
    }

    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<ModelGigRequestDecisionResponse> cancelRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(this.modelGigRequestService.cancelRequest(requestId));
    }
}
