package org.stylehub.backend.e_commerce.model.gig.event;

import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;

import java.time.Instant;
import java.util.UUID;

public record ModelGigRequestCancelledEvent(
        UUID requestId,
        String requestNumber,
        UUID brandId,
        UUID brandUserId,
        String brandName,
        UUID modelProfileId,
        UUID modelUserId,
        RequestStatus requestStatus,
        Instant cancelledAt
) {
}
