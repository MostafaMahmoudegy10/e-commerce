package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ModelGigRequestCreationResponse(
        UUID requestId,
        String requestNumber,
        UUID brandId,
        UUID modelProfileId,
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
