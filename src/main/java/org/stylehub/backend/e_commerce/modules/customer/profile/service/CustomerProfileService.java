package org.stylehub.backend.e_commerce.modules.customer.profile.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.CustomerProfileSetupRequest;
import org.stylehub.backend.e_commerce.modules.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.modules.customer.profile.repository.CustomerProfileRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserSyncService userSyncService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public Map<String, Object> setupProfile(CustomerProfileSetupRequest request) {
        validateRequest(request);

        if (customerProfileRepository.existsByUsernameAndUser_ExternalUserIdNot(request.username(), currentUserProvider.externalId())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        User user = userSyncService.upsert(currentUserProvider);

        CustomerProfile profile = customerProfileRepository
                .findByUser_ExternalUserId(currentUserProvider.externalId())
                .orElseGet(CustomerProfile::new);

        profile.setUser(user);
        profile.setUsername(request.username());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setBio(request.bio());
        profile.setGender(Gender.fromCode(request.gender()));
        profile.setProfileImageUrl(request.profileImageUrl());

        CustomerProfile savedProfile = customerProfileRepository.save(profile);

        user.setIsProfileCompleted(true);

        return Map.of(
                "message", "Customer profile saved successfully.",
                "profileId", savedProfile.getId(),
                "username", savedProfile.getUsername()
        );
    }



    @Transactional
    public Map<String, Object> viewProfile() {
        CustomerProfile profile = customerProfileRepository
                .findByUser_ExternalUserId(currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Customer profile not found."));

        return Map.of(
                "username", profile.getUsername(),
                "firstName", profile.getFirstName(),
                "lastName", profile.getLastName(),
                "phoneNumber", profile.getPhoneNumber(),
                "bio", profile.getBio(),
                "gender", profile.getGender(),
                "profileImageUrl", profile.getProfileImageUrl()
        );
    }
    private void validateRequest(CustomerProfileSetupRequest request) {
        if (isBlank(request.username())) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (isBlank(request.firstName())) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (isBlank(request.lastName())) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (isBlank(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (request.gender() == null) {
            throw new IllegalArgumentException("Gender is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
