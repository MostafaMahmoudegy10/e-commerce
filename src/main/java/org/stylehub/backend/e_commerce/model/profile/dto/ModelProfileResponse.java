package org.stylehub.backend.e_commerce.model.profile.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.BodyType;
import org.stylehub.backend.e_commerce.model.profile.enums.SkinTone;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ModelProfileResponse(
        UUID modelId,
        String modelName,
        String modelEmail,
        String bio,
        String city,
        Integer age,
        Integer heightCm,
        Integer weightKg,
        String hairColor,
        BigDecimal ratingAvg,
        Integer ratingCount,
        Boolean isAvailable,
        BodyType bodyType,
        SkinTone skinTone,
        Gender gender,
        List<String> modelImages,
        List<ModelAvailableForView> availableFor,
        ModelCustomerSummaryResponse customer
) {
}
