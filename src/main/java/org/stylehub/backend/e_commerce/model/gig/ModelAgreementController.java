package org.stylehub.backend.e_commerce.model.gig;

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
@RequestMapping("api/v1/model/agreements")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ModelAgreementController {

    private final ModelAgreementService modelAgreementService;

    @GetMapping
    public ResponseEntity<PageResponse<GigAgreementViewResponse>> findModelAgreements(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "status", required = false) AgreementStatus status
    ) {
        return ResponseEntity.ok(this.modelAgreementService.findModelAgreements(pageable, status));
    }

    @GetMapping("/{agreementId}")
    public ResponseEntity<GigAgreementViewResponse> findAgreementDetails(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelAgreementService.findModelAgreementDetails(agreementId));
    }
}
