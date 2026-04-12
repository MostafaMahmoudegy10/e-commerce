package org.stylehub.backend.e_commerce.modules.customer.profile.dto;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

public record CustomerProfileSetupRequest(
        String userName,
        String firstName,
        String lastName,
        String phoneNumber,
        String bio,
        Character gender,
        String profileImageUrl
) {
}
