package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;
import java.time.Instant;

public record ModelGigRequestCreationRequest(
        AvailableFor availableFor,
        String title,
        String description,
        BigDecimal proposedPrice,
        Instant deadline,
        String location
) {
}
