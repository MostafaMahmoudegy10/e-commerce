package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.SubmissionReviewStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GigAgreementSubmissionViewResponse(
        UUID submissionId,
        UUID agreementId,
        String note,
        SubmissionReviewStatus reviewStatus,
        String reviewFeedback,
        Instant createdAt,
        Instant reviewedAt,
        List<GigAgreementSubmissionAssetViewResponse> assets
) {
}
