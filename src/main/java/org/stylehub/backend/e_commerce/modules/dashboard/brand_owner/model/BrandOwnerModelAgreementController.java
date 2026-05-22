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
@Tag(name = "Brand Model Collaboration", description = "Manage collaboration requests, agreements, submissions, payments, and reviews between brands and models.")
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

    @Operation(summary = "Approve agreement submission", description = "Approves a model's submitted deliverables for a brand-model agreement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agreement submission approved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid agreement or submission state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot approve this submission"),
            @ApiResponse(responseCode = "404", description = "Agreement or submission was not found")
    })
    @PostMapping("/{agreementId}/submissions/{submissionId}/approve")
    public ResponseEntity<GigAgreementSubmissionDecisionResponse> approveSubmission(
            @PathVariable UUID agreementId,
            @PathVariable UUID submissionId
    ) {
        return ResponseEntity.ok(this.modelAgreementSubmissionService.approveSubmission(agreementId, submissionId));
    }

    @Operation(summary = "Request agreement revision", description = "Requests changes for a model agreement submission.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Revision requested successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data or submission state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot review this submission"),
            @ApiResponse(responseCode = "404", description = "Agreement or submission was not found")
    })
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

    @Operation(summary = "Mark model agreement payment success", description = "Marks a model collaboration agreement payment as successful.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agreement payment marked as successful"),
            @ApiResponse(responseCode = "400", description = "Invalid payment state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot update this payment"),
            @ApiResponse(responseCode = "404", description = "Agreement payment was not found")
    })
    @PostMapping("/{agreementId}/payments/success")
    public ResponseEntity<GigAgreementPaymentResponse> markPaymentSuccessful(
            @PathVariable UUID agreementId,
            @RequestBody(required = false) GigAgreementPaymentSuccessRequest request
    ) {
        return ResponseEntity.ok(this.modelAgreementPaymentService.markPaymentSuccessful(agreementId, request));
    }

    @Operation(summary = "Mark model agreement payment failure", description = "Marks a model collaboration agreement payment as failed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agreement payment marked as failed"),
            @ApiResponse(responseCode = "400", description = "Invalid payment state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot update this payment"),
            @ApiResponse(responseCode = "404", description = "Agreement payment was not found")
    })
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

    @Operation(summary = "Create or update model agreement review", description = "Creates or updates the brand's review for a completed model collaboration agreement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agreement review saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot review this agreement"),
            @ApiResponse(responseCode = "404", description = "Agreement was not found")
    })
    @PostMapping("/{agreementId}/review")
    public ResponseEntity<ModelAgreementReviewResponse> upsertAgreementReview(
            @PathVariable UUID agreementId,
            @RequestBody ModelReviewUpsertRequest request
    ) {
        return ResponseEntity.ok(this.modelReviewService.upsertBrandAgreementReview(agreementId, request));
    }
}
