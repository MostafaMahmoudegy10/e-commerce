package org.stylehub.backend.e_commerce.model.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreement;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementRepository;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileRepository;
import org.stylehub.backend.e_commerce.model.profile.service.ModelProfileAccessService;
import org.stylehub.backend.e_commerce.model.review.dto.ModelAgreementReviewResponse;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewListItemResponse;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewSummaryRow;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewStatsResponse;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewStatsRow;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewUpsertRequest;
import org.stylehub.backend.e_commerce.model.review.entity.ModelReview;
import org.stylehub.backend.e_commerce.model.review.repository.ModelReviewRepository;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelReviewService {

    private static final String EMAIL_REVIEW_COMMENT = "Submitted directly from email.";

    private final ModelAgreementRepository modelAgreementRepository;
    private final ModelReviewRepository modelReviewRepository;
    private final ModelProfileRepository modelProfileRepository;
    private final ModelProfileAccessService modelProfileAccessService;
    private final CurrentUserProvider currentUserProvider;

    public PageResponse<ModelReviewListItemResponse> findModelReviews(Pageable pageable) {
        this.modelProfileAccessService.requireCurrentModelProfile();

        Page<ModelReview> page = this.modelReviewRepository.findAllByModelExternalId(
                currentUserProvider.externalId(),
                pageable
        );

        List<ModelReviewListItemResponse> items = page.getContent().stream()
                .map(this::mapToListItem)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public ModelReviewStatsResponse getModelReviewStats() {
        ModelProfile modelProfile = this.modelProfileAccessService.requireCurrentModelProfile();

        ModelReviewStatsRow stats = this.modelReviewRepository.calculateStats(modelProfile.getId());

        return new ModelReviewStatsResponse(
                modelProfile.getRatingAvg() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : modelProfile.getRatingAvg(),
                modelProfile.getRatingCount() == null ? 0 : modelProfile.getRatingCount(),
                stats == null || stats.oneStarCount() == null ? 0L : stats.oneStarCount(),
                stats == null || stats.twoStarCount() == null ? 0L : stats.twoStarCount(),
                stats == null || stats.threeStarCount() == null ? 0L : stats.threeStarCount(),
                stats == null || stats.fourStarCount() == null ? 0L : stats.fourStarCount(),
                stats == null || stats.fiveStarCount() == null ? 0L : stats.fiveStarCount()
        );
    }

    public ModelAgreementReviewResponse findBrandAgreementReview(UUID agreementId) {
        ModelAgreement agreement = findCompletedBrandAgreement(agreementId);

        return this.modelReviewRepository.findByAgreement_Id(agreementId)
                .map(this::mapToResponse)
                .orElseGet(() -> new ModelAgreementReviewResponse(
                        null,
                        agreement.getId(),
                        agreement.getAgreementNumber(),
                        agreement.getModelProfile().getId(),
                        agreement.getModelProfile().getModelName(),
                        null,
                        null,
                        null,
                        null,
                        false
                ));
    }

    @Transactional
    public ModelAgreementReviewResponse upsertBrandAgreementReview(UUID agreementId, ModelReviewUpsertRequest request) {
        validateRequest(request);

        ModelAgreement agreement = findCompletedBrandAgreement(agreementId);
        ModelReview review = this.modelReviewRepository.findByAgreement_Id(agreementId)
                .orElseGet(ModelReview::new);

        review.setAgreement(agreement);
        review.setBrand(agreement.getBrand());
        review.setModelProfile(agreement.getModelProfile());
        review.setStars(request.stars());
        review.setComment(request.comment().trim());

        ModelReview savedReview = this.modelReviewRepository.save(review);
        recalculateModelProfileRating(agreement.getModelProfile());

        return mapToResponse(savedReview);
    }

    @Transactional
    public ModelAgreementReviewResponse upsertBrandAgreementReviewFromEmail(
            UUID agreementId,
            String brandExternalId,
            Integer stars
    ) {
        validateStars(stars);

        ModelAgreement agreement = findCompletedBrandAgreement(agreementId, brandExternalId);
        ModelReview review = this.modelReviewRepository.findByAgreement_Id(agreementId)
                .orElseGet(ModelReview::new);

        review.setAgreement(agreement);
        review.setBrand(agreement.getBrand());
        review.setModelProfile(agreement.getModelProfile());
        review.setStars(stars);
        if (review.getId() == null || review.getComment() == null || review.getComment().isBlank()) {
            review.setComment(EMAIL_REVIEW_COMMENT);
        }

        ModelReview savedReview = this.modelReviewRepository.save(review);
        recalculateModelProfileRating(agreement.getModelProfile());

        return mapToResponse(savedReview);
    }

    private void validateRequest(ModelReviewUpsertRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Review request is required");
        }
        validateStars(request.stars());
        if (request.comment() == null || request.comment().isBlank()) {
            throw new IllegalArgumentException("comment is required");
        }
    }

    private void validateStars(Integer stars) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new IllegalArgumentException("stars must be between 1 and 5");
        }
    }

    private ModelAgreement findCompletedBrandAgreement(UUID agreementId) {
        return findCompletedBrandAgreement(agreementId, currentUserProvider.externalId());
    }

    private ModelAgreement findCompletedBrandAgreement(UUID agreementId, String brandExternalId) {
        ModelAgreement agreement = this.modelAgreementRepository
                .findByIdAndBrandExternalId(agreementId, brandExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));

        if (agreement.getAgreementStatus() != AgreementStatus.COMPLETED) {
            throw new IllegalStateException("Agreement must be completed before review");
        }

        return agreement;
    }

    private void recalculateModelProfileRating(ModelProfile modelProfile) {
        ModelReviewSummaryRow summary = this.modelReviewRepository.calculateSummary(modelProfile.getId());
        long ratingCount = summary == null || summary.ratingCount() == null ? 0L : summary.ratingCount();
        double avgRating = summary == null || summary.avgRating() == null ? 0.0 : summary.avgRating();

        modelProfile.setRatingCount(Math.toIntExact(ratingCount));
        modelProfile.setRatingAvg(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        this.modelProfileRepository.save(modelProfile);
    }

    private ModelAgreementReviewResponse mapToResponse(ModelReview review) {
        return new ModelAgreementReviewResponse(
                review.getId(),
                review.getAgreement().getId(),
                review.getAgreement().getAgreementNumber(),
                review.getModelProfile().getId(),
                review.getModelProfile().getModelName(),
                review.getStars(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                true
        );
    }

    private ModelReviewListItemResponse mapToListItem(ModelReview review) {
        return new ModelReviewListItemResponse(
                review.getId(),
                review.getAgreement().getId(),
                review.getAgreement().getAgreementNumber(),
                review.getBrand().getId(),
                review.getBrand().getBrandName(),
                review.getStars(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
