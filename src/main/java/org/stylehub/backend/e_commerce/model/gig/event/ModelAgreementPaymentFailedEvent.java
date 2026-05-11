package org.stylehub.backend.e_commerce.model.gig.event;

import java.time.Instant;
import java.util.UUID;

public record ModelAgreementPaymentFailedEvent(
        UUID agreementId,
        String agreementNumber,
        UUID paymentId,
        UUID brandUserId,
        UUID modelUserId,
        String failureReason,
        Instant failedAt
) {
}
