package org.stylehub.backend.e_commerce.customer.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record VariantsDetailsDto(
     UUID productVariantId,
     String size,
     Integer stock,
     String sku,
     BigDecimal price
) {
}
