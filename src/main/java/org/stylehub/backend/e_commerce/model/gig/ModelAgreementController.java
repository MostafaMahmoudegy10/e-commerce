package org.stylehub.backend.e_commerce.model.gig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelAgreementSubmissionCreateRequest;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementPaymentService;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementService;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementSubmissionService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/model/agreements")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Tag(name = "Brand Model Collaboration", description = "Manage collaboration requests, agreements, submissions, payments, and reviews between brands and models.")
public class ModelAgreementController {

    private final ModelAgreementService modelAgreementService;
    private final ModelAgreementSubmissionService modelAgreementSubmissionService;
    private final ModelAgreementPaymentService modelAgreementPaymentService;

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

    @GetMapping("/{agreementId}/submissions")
    public ResponseEntity<List<GigAgreementSubmissionViewResponse>> findAgreementSubmissions(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelAgreementSubmissionService.findModelSubmissions(agreementId));
    }

    @Operation(summary = "Submit agreement deliverables", description = "Allows the model to upload deliverables for an active brand-model agreement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agreement submission created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid submission data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot submit for this agreement"),
            @ApiResponse(responseCode = "404", description = "Agreement was not found")
    })
    @PostMapping(value = "/{agreementId}/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GigAgreementSubmissionViewResponse> createSubmission(
            @PathVariable UUID agreementId,
            @ModelAttribute ModelAgreementSubmissionCreateRequest request
    ) {
        return ResponseEntity.ok(this.modelAgreementSubmissionService.createSubmission(agreementId, request));
    }

    @GetMapping("/{agreementId}/payment")
    public ResponseEntity<GigAgreementPaymentResponse> findAgreementPayment(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelAgreementPaymentService.findModelPayment(agreementId));
    }
}
