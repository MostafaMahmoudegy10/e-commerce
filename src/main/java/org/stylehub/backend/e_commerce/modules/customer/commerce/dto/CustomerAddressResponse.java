package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record CustomerAddressResponse(
        UUID addressId,
        String recipientName,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String country,
        String postalCode,
        Boolean isDefault,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
