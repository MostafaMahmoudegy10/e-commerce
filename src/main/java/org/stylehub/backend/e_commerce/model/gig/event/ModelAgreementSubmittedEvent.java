package org.stylehub.backend.e_commerce.model.gig.event;

import java.time.Instant;
import java.util.UUID;

public record ModelAgreementSubmittedEvent(
        UUID agreementId,
        String agreementNumber,
        UUID submissionId,
        UUID brandId,
        UUID brandUserId,
        UUID modelProfileId,
        UUID modelUserId,
        String modelName,
        Instant submittedAt
) {
}
