package org.stylehub.backend.e_commerce.model.gig.event;

import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record ModelGigRequestAcceptedEvent(
        UUID requestId,
        String requestNumber,
        UUID brandId,
        UUID brandUserId,
        String brandName,
        UUID modelProfileId,
        UUID modelUserId,
        UUID agreementId,
        String agreementNumber,
        RequestStatus requestStatus,
        AgreementStatus agreementStatus,
        PaymentStatus paymentStatus,
        Instant respondedAt
) {
}
