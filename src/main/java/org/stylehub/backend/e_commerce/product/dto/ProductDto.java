package org.stylehub.backend.e_commerce.product.dto;

import org.springframework.web.multipart.MultipartFile;

public record ProductDto(
        String productName,
        String productDescription,
        MultipartFile thumbnail
) {
}
