package org.stylehub.backend.e_commerce.customer.profile.dto;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

public record CustomerProfileSetupRequest(
        String userId,
        String username,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
//        String bio,
//        Character gender,
//        String profileImageUrl
) {
}
