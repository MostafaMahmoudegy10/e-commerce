package org.stylehub.backend.e_commerce.model.profile.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.BodyType;
import org.stylehub.backend.e_commerce.model.profile.enums.SkinTone;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.math.BigDecimal;
import java.util.UUID;

public record ModelSearchRow(
        UUID modelId,
        String modelName,
        String modelEmail,
        String city,
        Integer age,
        Integer heightCm,
        Integer weightKg,
        String hairColor,
        BodyType bodyType,
        SkinTone skinTone,
        Gender gender,
        BigDecimal ratingAvg,
        Integer ratingCount,
        Boolean isAvailable,
        String profileImage
) {
}
