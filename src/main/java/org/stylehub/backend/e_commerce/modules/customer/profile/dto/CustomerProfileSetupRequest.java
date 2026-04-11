package org.stylehub.backend.e_commerce.modules.customer.profile.dto;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

public record CustomerProfileSetupRequest(
        String username,
        String firstName,
        String lastName,
        String phoneNumber,
        String bio,
        Gender gender,
        String profileImageUrl
) {
}
