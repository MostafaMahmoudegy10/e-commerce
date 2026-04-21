package org.stylehub.backend.e_commerce.modules.customer.profile.dto.product;

import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.SizeDtoReqRes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CustomerShowProductDetailsDto(
        UUID productId,
        String productNameAr,
        String productNameEn,
        String productDescriptionEn,
        String productDescriptionAr,
        BigDecimal productPrice,
        String  thumbnail,
        List<ProductColorOptionDto> colorOptions



) {
}
