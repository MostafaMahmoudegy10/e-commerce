package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record ModelGigRequestDecisionResponse(
        UUID requestId,
        String requestNumber,
        RequestStatus requestStatus,
        Instant respondedAt,
        UUID agreementId,
        String agreementNumber,
        AgreementStatus agreementStatus,
        PaymentStatus paymentStatus
) {
}
