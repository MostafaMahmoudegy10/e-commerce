package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ModelGigRequestViewResponse(
        UUID requestId,
        String requestNumber,
        UUID brandId,
        String brandName,
        String brandImageUrl,
        AvailableFor availableFor,
        String title,
        String description,
        BigDecimal proposedPrice,
        Instant deadline,
        String location,
        RequestStatus requestStatus,
        String rejectionReason,
        Instant createdAt,
        Instant respondedAt
) {
}
