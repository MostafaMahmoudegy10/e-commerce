package org.stylehub.backend.e_commerce.model.profile.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.math.BigDecimal;

public record ModelAvailableForCreation(
        AvailableFor availableFor,
        BigDecimal pricePerSession
) {
}
