package org.stylehub.backend.e_commerce.model.gig.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionAssetViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionDecisionResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionReviewRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementSubmissionViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelAgreementSubmissionCreateRequest;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreement;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreementSubmission;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreementSubmissionAsset;
import org.stylehub.backend.e_commerce.model.gig.entity.SubmissionAssetType;
import org.stylehub.backend.e_commerce.model.gig.entity.SubmissionReviewStatus;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementApprovedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementRevisionRequestedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementSubmittedEvent;
import org.stylehub.backend.e_commerce.model.gig.publisher.ModelAgreementEventPublisher;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementRepository;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementSubmissionRepository;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ModelAgreementSubmissionService {

    private static final String AGREEMENT_ASSET_FOLDER_PREFIX = "e-commerce/model-agreements/";

    private final ModelAgreementRepository modelAgreementRepository;
    private final ModelAgreementSubmissionRepository modelAgreementSubmissionRepository;
    private final ImageService imageService;
    private final CurrentUserProvider currentUserProvider;
    private final ModelAgreementEventPublisher modelAgreementEventPublisher;

    @Transactional
    public GigAgreementSubmissionViewResponse createSubmission(
            UUID agreementId,
            ModelAgreementSubmissionCreateRequest request
    ) {
        validateCreateRequest(request);

        ModelAgreement agreement = this.modelAgreementRepository
                .findByIdAndModelExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));

        ensureAgreementAcceptsSubmission(agreement);

        List<UploadedAsset> uploadedAssets = uploadAssets(request.files(), agreementId);

        try {
            ModelAgreementSubmission submission = new ModelAgreementSubmission();
            submission.setAgreement(agreement);
            submission.setNote(normalizeNullableText(request.note()));

            uploadedAssets.stream()
                    .map(this::toSubmissionAsset)
                    .forEach(submission::addAsset);

            ModelAgreementSubmission savedSubmission = this.modelAgreementSubmissionRepository.saveAndFlush(submission);

            agreement.setAgreementStatus(AgreementStatus.SUBMITTED);
            agreement.setDeliveredAt(savedSubmission.getCreatedAt());
            this.modelAgreementRepository.save(agreement);

            this.modelAgreementEventPublisher.publishAgreementSubmitted(
                    new ModelAgreementSubmittedEvent(
                            agreement.getId(),
                            agreement.getAgreementNumber(),
                            savedSubmission.getId(),
                            agreement.getBrand().getId(),
                            agreement.getBrand().getUser().getId(),
                            agreement.getModelProfile().getId(),
                            agreement.getModelProfile().getUser().getId(),
                            agreement.getModelProfile().getModelName(),
                            savedSubmission.getCreatedAt()
                    )
            );

            return mapToViewResponse(savedSubmission);
        } catch (RuntimeException exception) {
            cleanupUploadedAssets(uploadedAssets);
            throw exception;
        }
    }

    public List<GigAgreementSubmissionViewResponse> findModelSubmissions(UUID agreementId) {
        this.modelAgreementRepository.findByIdAndModelExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));

        return this.modelAgreementSubmissionRepository.findAllByAgreementIdAndModelExternalId(
                        agreementId,
                        currentUserProvider.externalId()
                ).stream()
                .map(this::mapToViewResponse)
                .toList();
    }

    public List<GigAgreementSubmissionViewResponse> findBrandSubmissions(UUID agreementId) {
        this.modelAgreementRepository.findByIdAndBrandExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));

        return this.modelAgreementSubmissionRepository.findAllByAgreementIdAndBrandExternalId(
                        agreementId,
                        currentUserProvider.externalId()
                ).stream()
                .map(this::mapToViewResponse)
                .toList();
    }

    @Transactional
    public GigAgreementSubmissionDecisionResponse approveSubmission(UUID agreementId, UUID submissionId) {
        ModelAgreement agreement = this.modelAgreementRepository
                .findByIdAndBrandExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
        ensureAgreementWaitingForReview(agreement);

        ModelAgreementSubmission submission = findBrandSubmission(agreementId, submissionId);
        ensureLatestSubmission(agreementId, submissionId);
        ensurePendingReview(submission);

        Instant reviewedAt = Instant.now();
        submission.setReviewStatus(SubmissionReviewStatus.APPROVED);
        submission.setReviewFeedback(null);
        submission.setReviewedAt(reviewedAt);

        agreement.setAgreementStatus(AgreementStatus.AWAITING_PAYMENT);

        this.modelAgreementSubmissionRepository.save(submission);
        this.modelAgreementRepository.save(agreement);

        this.modelAgreementEventPublisher.publishAgreementApproved(
                new ModelAgreementApprovedEvent(
                        agreement.getId(),
                        agreement.getAgreementNumber(),
                        submission.getId(),
                        agreement.getBrand().getId(),
                        agreement.getBrand().getUser().getId(),
                        agreement.getModelProfile().getId(),
                        agreement.getModelProfile().getUser().getId(),
                        reviewedAt
                )
        );

        return mapToDecisionResponse(agreement, submission);
    }

    @Transactional
    public GigAgreementSubmissionDecisionResponse requestRevision(
            UUID agreementId,
            UUID submissionId,
            GigAgreementSubmissionReviewRequest request
    ) {
        if (request == null || request.feedback() == null || request.feedback().isBlank()) {
            throw new IllegalArgumentException("feedback is required");
        }

        ModelAgreement agreement = this.modelAgreementRepository
                .findByIdAndBrandExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
        ensureAgreementWaitingForReview(agreement);

        ModelAgreementSubmission submission = findBrandSubmission(agreementId, submissionId);
        ensureLatestSubmission(agreementId, submissionId);
        ensurePendingReview(submission);

        Instant reviewedAt = Instant.now();
        submission.setReviewStatus(SubmissionReviewStatus.REVISION_REQUESTED);
        submission.setReviewFeedback(request.feedback().trim());
        submission.setReviewedAt(reviewedAt);

        agreement.setAgreementStatus(AgreementStatus.REVISION_REQUESTED);

        this.modelAgreementSubmissionRepository.save(submission);
        this.modelAgreementRepository.save(agreement);

        this.modelAgreementEventPublisher.publishRevisionRequested(
                new ModelAgreementRevisionRequestedEvent(
                        agreement.getId(),
                        agreement.getAgreementNumber(),
                        submission.getId(),
                        agreement.getBrand().getId(),
                        agreement.getBrand().getUser().getId(),
                        agreement.getModelProfile().getId(),
                        agreement.getModelProfile().getUser().getId(),
                        submission.getReviewFeedback(),
                        reviewedAt
                )
        );

        return mapToDecisionResponse(agreement, submission);
    }

    private void validateCreateRequest(ModelAgreementSubmissionCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Submission request is required");
        }
        if (request.files() == null || request.files().isEmpty()) {
            throw new IllegalArgumentException("files are required");
        }
        boolean hasValidFile = request.files().stream().anyMatch(file -> file != null && !file.isEmpty());
        if (!hasValidFile) {
            throw new IllegalArgumentException("files are required");
        }
    }

    private void ensureAgreementAcceptsSubmission(ModelAgreement agreement) {
        if (agreement.getAgreementStatus() == AgreementStatus.SUBMITTED) {
            throw new IllegalStateException("Current submission is still waiting for brand review");
        }
        if (agreement.getAgreementStatus() != AgreementStatus.IN_PROGRESS
                && agreement.getAgreementStatus() != AgreementStatus.REVISION_REQUESTED) {
            throw new IllegalStateException("Agreement is not open for new submissions");
        }
    }

    private void ensureAgreementWaitingForReview(ModelAgreement agreement) {
        if (agreement.getAgreementStatus() != AgreementStatus.SUBMITTED) {
            throw new IllegalStateException("Agreement is not waiting for submission review");
        }
    }

    private void ensurePendingReview(ModelAgreementSubmission submission) {
        if (submission.getReviewStatus() != SubmissionReviewStatus.PENDING) {
            throw new IllegalStateException("Submission has already been reviewed");
        }
    }

    private void ensureLatestSubmission(UUID agreementId, UUID submissionId) {
        UUID latestSubmissionId = this.modelAgreementSubmissionRepository
                .findFirstByAgreement_IdOrderByCreatedAtDescIdDesc(agreementId)
                .map(ModelAgreementSubmission::getId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (!latestSubmissionId.equals(submissionId)) {
            throw new IllegalStateException("Only the latest submission can be reviewed");
        }
    }

    private ModelAgreementSubmission findBrandSubmission(UUID agreementId, UUID submissionId) {
        return this.modelAgreementSubmissionRepository.findByIdAndAgreementIdAndBrandExternalId(
                        submissionId,
                        agreementId,
                        currentUserProvider.externalId()
                )
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
    }

    private List<UploadedAsset> uploadAssets(List<MultipartFile> files, UUID agreementId) {
        String folder = AGREEMENT_ASSET_FOLDER_PREFIX + agreementId;
        List<UploadCandidate> uploadCandidates = files.stream()
                .filter(Objects::nonNull)
                .filter(file -> !file.isEmpty())
                .map(file -> new UploadCandidate(
                        file,
                        resolveAssetType(file),
                        normalizeNullableText(file.getContentType())
                ))
                .toList();

        List<CompletableFuture<UploadedAssetResult>> futures = uploadCandidates.stream()
                .map(candidate -> this.imageService.uploadAssetAsync(candidate.file(), folder)
                            .handle((response, throwable) -> new UploadedAssetResult(
                                    response,
                                    throwable,
                                    candidate.assetType(),
                                    candidate.mimeType()
                            )))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        List<UploadedAssetResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<UploadedAsset> uploadedAssets = results.stream()
                .filter(result -> result.throwable() == null && result.response() != null)
                .map(result -> new UploadedAsset(
                        result.response().imageUrl(),
                        result.response().publicId(),
                        result.mimeType(),
                        result.assetType()
                ))
                .toList();

        results.stream()
                .filter(result -> result.throwable() != null)
                .findFirst()
                .ifPresent(result -> {
                    cleanupUploadedAssets(uploadedAssets);
                    throw new IllegalArgumentException("Failed to upload one or more agreement files", result.throwable());
                });

        return uploadedAssets;
    }

    private SubmissionAssetType resolveAssetType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return SubmissionAssetType.IMAGE;
            }
            if (contentType.startsWith("video/")) {
                return SubmissionAssetType.VIDEO;
            }
        }
        throw new IllegalArgumentException("Only image and video files are supported");
    }

    private ModelAgreementSubmissionAsset toSubmissionAsset(UploadedAsset uploadedAsset) {
        ModelAgreementSubmissionAsset asset = new ModelAgreementSubmissionAsset();
        asset.setAssetUrl(uploadedAsset.assetUrl());
        asset.setPublicId(uploadedAsset.publicId());
        asset.setMimeType(uploadedAsset.mimeType());
        asset.setAssetType(uploadedAsset.assetType());
        return asset;
    }

    private void cleanupUploadedAssets(List<UploadedAsset> uploadedAssets) {
        uploadedAssets.forEach(asset ->
                this.imageService.deleteAssetAsync(asset.publicId(), toCloudinaryResourceType(asset.assetType())).join()
        );
    }

    private String toCloudinaryResourceType(SubmissionAssetType assetType) {
        return assetType == SubmissionAssetType.VIDEO ? "video" : "image";
    }

    private String normalizeNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private GigAgreementSubmissionViewResponse mapToViewResponse(ModelAgreementSubmission submission) {
        return new GigAgreementSubmissionViewResponse(
                submission.getId(),
                submission.getAgreement().getId(),
                submission.getNote(),
                submission.getReviewStatus(),
                submission.getReviewFeedback(),
                submission.getCreatedAt(),
                submission.getReviewedAt(),
                submission.getAssets().stream()
                        .map(asset -> new GigAgreementSubmissionAssetViewResponse(
                                asset.getId(),
                                asset.getAssetUrl(),
                                asset.getPublicId(),
                                asset.getMimeType(),
                                asset.getAssetType(),
                                asset.getCreatedAt()
                        ))
                        .toList()
        );
    }

    private GigAgreementSubmissionDecisionResponse mapToDecisionResponse(
            ModelAgreement agreement,
            ModelAgreementSubmission submission
    ) {
        return new GigAgreementSubmissionDecisionResponse(
                agreement.getId(),
                agreement.getAgreementNumber(),
                submission.getId(),
                agreement.getAgreementStatus(),
                agreement.getPaymentStatus(),
                submission.getReviewStatus(),
                submission.getReviewFeedback(),
                submission.getReviewedAt(),
                agreement.getDeliveredAt()
        );
    }

    private record UploadedAsset(String assetUrl, String publicId, String mimeType, SubmissionAssetType assetType) {
    }

    private record UploadCandidate(MultipartFile file, SubmissionAssetType assetType, String mimeType) {
    }

    private record UploadedAssetResult(
            UploadResponse response,
            Throwable throwable,
            SubmissionAssetType assetType,
            String mimeType
    ) {
    }
}
