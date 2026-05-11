package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.model;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementViewResponse;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/model-agreements")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerModelAgreementController {

    private final ModelAgreementService modelAgreementService;

    @GetMapping
    public ResponseEntity<PageResponse<GigAgreementViewResponse>> findBrandAgreements(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "status", required = false) AgreementStatus status
    ) {
        return ResponseEntity.ok(this.modelAgreementService.findBrandAgreements(pageable, status));
    }

    @GetMapping("/{agreementId}")
    public ResponseEntity<GigAgreementViewResponse> findAgreementDetails(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelAgreementService.findBrandAgreementDetails(agreementId));
    }
}
