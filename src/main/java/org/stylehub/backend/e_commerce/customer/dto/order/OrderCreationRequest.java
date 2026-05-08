package org.stylehub.backend.e_commerce.customer.dto.order;

import java.util.UUID;

public record OrderCreationRequest(
        UUID cartId,
        String streetEn,
        String streetAr,
        String cityEn,
        String cityAr,
        String buildingNumber
) {
}
