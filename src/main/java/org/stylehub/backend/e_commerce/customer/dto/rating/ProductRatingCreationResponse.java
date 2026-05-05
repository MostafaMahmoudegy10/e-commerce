package org.stylehub.backend.e_commerce.customer.dto.rating;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRatingCreationResponse(
        String productNameEn,
        String productNameAr,
        String customerName,
        String customerId,
        Integer stars
) {
}
