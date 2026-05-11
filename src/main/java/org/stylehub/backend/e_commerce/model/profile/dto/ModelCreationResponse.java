package org.stylehub.backend.e_commerce.model.profile.dto;

import org.stylehub.backend.e_commerce.model.profile.enums.BodyType;
import org.stylehub.backend.e_commerce.model.profile.enums.SkinTone;

import java.math.BigDecimal;
import java.util.UUID;

public record ModelCreationResponse(
        String modelEmail,
        UUID modelId,
        BodyType bodyType,
        SkinTone skinTone,
        BigDecimal ratingAvg
) {
}
