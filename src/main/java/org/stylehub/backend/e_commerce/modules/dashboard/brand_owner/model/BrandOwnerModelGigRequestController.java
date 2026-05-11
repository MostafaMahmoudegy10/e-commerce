package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.model;

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
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.gig.service.ModelGigRequestService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/models")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerModelGigRequestController {

    private final ModelGigRequestService modelGigRequestService;

    @GetMapping("/requests")
    public ResponseEntity<PageResponse<BrandGigRequestViewResponse>> findBrandRequests(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "status", required = false) RequestStatus status
    ) {
        return ResponseEntity.ok(this.modelGigRequestService.findBrandRequests(pageable, status));
    }

    @PostMapping("/{modelId}/requests")
    public ResponseEntity<ModelGigRequestCreationResponse> createRequest(
            @PathVariable UUID modelId,
            @RequestBody ModelGigRequestCreationRequest request
    ) {
        return ResponseEntity.ok(this.modelGigRequestService.createRequest(modelId, request));
    }
}
