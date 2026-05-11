package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.SubmissionReviewStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record GigAgreementSubmissionDecisionResponse(
        UUID agreementId,
        String agreementNumber,
        UUID submissionId,
        AgreementStatus agreementStatus,
        PaymentStatus paymentStatus,
        SubmissionReviewStatus reviewStatus,
        String reviewFeedback,
        Instant reviewedAt,
        Instant deliveredAt
) {
}
