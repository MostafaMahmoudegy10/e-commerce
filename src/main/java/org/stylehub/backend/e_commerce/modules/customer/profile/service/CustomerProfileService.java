package org.stylehub.backend.e_commerce.modules.customer.profile.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.CustomerProfileSetupRequest;
import org.stylehub.backend.e_commerce.modules.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.modules.customer.profile.repository.CustomerProfileRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserSyncService userSyncService;

    @Transactional
    @RabbitListener(queues = "customer.created.user.service.q")
    public void setupProfile(CustomerProfileSetupRequest request) {
        validateRequest(request);

        User user = userSyncService.create(request.userId(),"CUSTOMER");

        if (customerProfileRepository.existsByUsernameAndUser_ExternalUserIdNot(request.username(),
                request.userId())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        CustomerProfile profile = customerProfileRepository
                .findByUser_ExternalUserId(request.userId())
                .orElseGet(CustomerProfile::new);

        profile.setUser(user);
        profile.setUsername(request.username());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setBio("no bio");
        profile.setGender(Gender.fromCode('M'));
        profile.setProfileImageUrl("image.jpg");

        customerProfileRepository.save(profile);


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

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


}
