package org.stylehub.backend.e_commerce.platform.mail.events;

import java.time.Instant;
import java.util.UUID;

public record ModelReviewRequestedEmailEvent(
        UUID agreementId,
        String agreementNumber,
        String brandExternalId,
        String brandName,
        String brandEmail,
        String modelName,
        Instant completedAt
) {
}
