package org.stylehub.backend.e_commerce.model.profile.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;
import java.util.UUID;

public record ModelAvailableForRow(
        UUID modelId,
        AvailableFor availableFor,
        BigDecimal pricePerSession
) {
}
