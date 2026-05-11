package org.stylehub.backend.e_commerce.model.profile.dto;

import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.model.profile.enums.BodyType;
import org.stylehub.backend.e_commerce.model.profile.enums.SkinTone;

import java.util.List;

public record ModelCreationRequest(
        String city,
        Integer age,
        Integer heightCm,
        Integer weightKg,
        String hairColor,
        BodyType bodyType,
        SkinTone skinTone,
        String bio,
        List<MultipartFile> files
) {
}
