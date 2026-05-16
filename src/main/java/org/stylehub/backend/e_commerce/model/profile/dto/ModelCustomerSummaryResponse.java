package org.stylehub.backend.e_commerce.model.profile.dto;

import java.util.UUID;

public record ModelCustomerSummaryResponse(
        UUID customerId,
        String username,
        String firstName,
        String lastName,
        String customerEmail,
        String phoneNumber,
        String profileImageUrl
) {
}
