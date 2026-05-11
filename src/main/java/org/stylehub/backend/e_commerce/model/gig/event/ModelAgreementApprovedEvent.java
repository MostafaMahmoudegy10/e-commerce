package org.stylehub.backend.e_commerce.model.gig.event;

import java.time.Instant;
import java.util.UUID;

public record ModelAgreementApprovedEvent(
        UUID agreementId,
        String agreementNumber,
        UUID submissionId,
        UUID brandId,
        UUID brandUserId,
        UUID modelProfileId,
        UUID modelUserId,
        Instant reviewedAt
) {
}
