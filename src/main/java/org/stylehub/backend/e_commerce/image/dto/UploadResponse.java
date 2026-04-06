package org.stylehub.backend.e_commerce.image.dto;

public record UploadResponse(
        String imageUrl,
        String publicId
) {
}
