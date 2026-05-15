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
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentFailureRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentSuccessRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionDecisionResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionReviewRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementViewResponse;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementPaymentService;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementService;
import org.stylehub.backend.e_commerce.model.gig.service.ModelAgreementSubmissionService;
import org.stylehub.backend.e_commerce.model.review.dto.ModelAgreementReviewResponse;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewUpsertRequest;
import org.stylehub.backend.e_commerce.model.review.service.ModelReviewService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/model-agreements")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerModelAgreementController {

    private final ModelAgreementService modelAgreementService;
    private final ModelAgreementSubmissionService modelAgreementSubmissionService;
    private final ModelAgreementPaymentService modelAgreementPaymentService;
    private final ModelReviewService modelReviewService;

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

    @GetMapping("/{agreementId}/submissions")
    public ResponseEntity<List<GigAgreementSubmissionViewResponse>> findAgreementSubmissions(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelAgreementSubmissionService.findBrandSubmissions(agreementId));
    }

    @PostMapping("/{agreementId}/submissions/{submissionId}/approve")
    public ResponseEntity<GigAgreementSubmissionDecisionResponse> approveSubmission(
            @PathVariable UUID agreementId,
            @PathVariable UUID submissionId
    ) {
        return ResponseEntity.ok(this.modelAgreementSubmissionService.approveSubmission(agreementId, submissionId));
    }

    @PostMapping("/{agreementId}/submissions/{submissionId}/request-revision")
    public ResponseEntity<GigAgreementSubmissionDecisionResponse> requestRevision(
            @PathVariable UUID agreementId,
            @PathVariable UUID submissionId,
            @RequestBody GigAgreementSubmissionReviewRequest request
    ) {
        return ResponseEntity.ok(this.modelAgreementSubmissionService.requestRevision(agreementId, submissionId, request));
    }

    @GetMapping("/{agreementId}/payment")
    public ResponseEntity<GigAgreementPaymentResponse> findAgreementPayment(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelAgreementPaymentService.findBrandPayment(agreementId));
    }

    @PostMapping("/{agreementId}/payments/success")
    public ResponseEntity<GigAgreementPaymentResponse> markPaymentSuccessful(
            @PathVariable UUID agreementId,
            @RequestBody(required = false) GigAgreementPaymentSuccessRequest request
    ) {
        return ResponseEntity.ok(this.modelAgreementPaymentService.markPaymentSuccessful(agreementId, request));
    }

    @PostMapping("/{agreementId}/payments/failure")
    public ResponseEntity<GigAgreementPaymentResponse> markPaymentFailed(
            @PathVariable UUID agreementId,
            @RequestBody GigAgreementPaymentFailureRequest request
    ) {
        return ResponseEntity.ok(this.modelAgreementPaymentService.markPaymentFailed(agreementId, request));
    }

    @GetMapping("/{agreementId}/review")
    public ResponseEntity<ModelAgreementReviewResponse> findAgreementReview(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(this.modelReviewService.findBrandAgreementReview(agreementId));
    }

    @PostMapping("/{agreementId}/review")
    public ResponseEntity<ModelAgreementReviewResponse> upsertAgreementReview(
            @PathVariable UUID agreementId,
            @RequestBody ModelReviewUpsertRequest request
    ) {
        return ResponseEntity.ok(this.modelReviewService.upsertBrandAgreementReview(agreementId, request));
    }
}
