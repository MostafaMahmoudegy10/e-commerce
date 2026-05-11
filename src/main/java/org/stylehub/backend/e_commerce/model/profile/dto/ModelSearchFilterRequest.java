package org.stylehub.backend.e_commerce.model.profile.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;

import java.util.List;

public record ModelSearchFilterRequest(
        String search,
        Integer minAge,
        Integer maxAge,
        Integer minHeightCm,
        Integer maxHeightCm,
        Integer minWeightKg,
        Integer maxWeightKg,
        List<AvailableFor> availableFor,
        Boolean isAvailable
) {
}
