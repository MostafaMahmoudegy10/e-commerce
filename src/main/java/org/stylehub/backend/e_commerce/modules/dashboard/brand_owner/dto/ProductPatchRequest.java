package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.dto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPatchRequest(
        String productNameEn,
        String productNameAr,
        String productDescriptionEn,
        String productDescriptionAr,
        BigDecimal productPrice,
        UUID categoryId,
        MultipartFile thumbnail
) {
}
