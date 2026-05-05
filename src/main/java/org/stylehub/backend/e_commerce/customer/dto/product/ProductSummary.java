package org.stylehub.backend.e_commerce.customer.dto.product;

import java.util.UUID;

public record ProductSummary(
        String productNameAr,
        String productNameEn,
        UUID productId,
        String thumbnail
) {
}
