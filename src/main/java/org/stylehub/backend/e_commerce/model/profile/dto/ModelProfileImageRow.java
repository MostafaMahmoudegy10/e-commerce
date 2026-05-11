package org.stylehub.backend.e_commerce.model.profile.dto;

import java.util.UUID;

public record ModelProfileImageRow(
        UUID modelId,
        UUID imageId,
        String profileImage
) {
}
