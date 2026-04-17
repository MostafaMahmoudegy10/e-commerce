package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

public record CustomerAddressRequest(
        String recipientName,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String country,
        String postalCode,
        Boolean isDefault
) {
}
