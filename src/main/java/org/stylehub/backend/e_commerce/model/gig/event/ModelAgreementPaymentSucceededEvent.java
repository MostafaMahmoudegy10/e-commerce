package org.stylehub.backend.e_commerce.model.gig.event;

import java.time.Instant;
import java.util.UUID;

public record ModelAgreementPaymentSucceededEvent(
        UUID agreementId,
        String agreementNumber,
        UUID paymentId,
        UUID brandUserId,
        UUID modelUserId,
        Instant paidAt,
        Instant completedAt
) {
}
