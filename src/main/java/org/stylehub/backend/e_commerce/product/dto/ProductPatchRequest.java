package org.stylehub.backend.e_commerce.product.dto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPatchRequest(
        String productNameEn,
        String productDescriptionEn,
        String productNameAr,
        String productDescriptionAr,
        BigDecimal productPrice,
        UUID categoryId,
        MultipartFile thumbnail
) {
}
