package org.stylehub.backend.e_commerce.model.gig.event;

import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ModelGigRequestCreatedEvent(
        UUID requestId,
        String requestNumber,
        UUID brandId,
        UUID brandUserId,
        String brandName,
        UUID modelProfileId,
        UUID modelUserId,
        AvailableFor availableFor,
        String title,
        String description,
        BigDecimal proposedPrice,
        Instant deadline,
        String location,
        RequestStatus requestStatus,
        Instant createdAt
) {
}
