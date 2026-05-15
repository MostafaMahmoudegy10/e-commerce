package org.stylehub.backend.e_commerce.customer.profile.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.customer.profile.dto.CustomerProfileDeletedRequest;
import org.stylehub.backend.e_commerce.customer.profile.dto.CustomerProfileSetupRequest;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.repository.CustomerProfileRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserSyncService userSyncService;
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerProfileService.class);

    @Transactional
    @RabbitListener(queues = "customer.created.user.service.q")
    public void setupProfile(CustomerProfileSetupRequest request) {
        syncProfile(request);
    }

    @Transactional
    @RabbitListener(queues = "customer.updated.user.service.q")
    public void updateProfile(CustomerProfileSetupRequest request) {
        syncProfile(request);
    }

    @Transactional
    @RabbitListener(queues = "customer.deleted.user.service.q")
    public void deleteProfile(CustomerProfileDeletedRequest request) {
        if (request == null || isBlank(request.userId())) {
            throw new IllegalArgumentException("User id is required.");
        }

        this.customerProfileRepository.deleteByUser_ExternalUserId(request.userId());
        this.userSyncService.deleteByExternalId(request.userId());
    }

    private void syncProfile(CustomerProfileSetupRequest request) {
        validateRequest(request);

        User user = userSyncService.sync(request.userId(),"CUSTOMER",request.email());

        if (customerProfileRepository.existsByUsernameAndUser_ExternalUserIdNot(request.username(),
                request.userId())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        CustomerProfile profile = customerProfileRepository
                .findCustomerProfileByUser_ExternalUserId(request.userId())
                .orElseGet(CustomerProfile::new);

        profile.setUser(user);
        profile.setUsername(request.username());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setBio(request.bio());
        char gender;
        if(request.gender()=="FEMALE"){
            gender = 'F';
        }else{
            gender='M';
        }
        profile.setGender(Gender.fromCode(gender));
        profile.setProfileImageUrl(request.profileImageUrl());
        profile.setCustomerEmail(request.email());

        customerProfileRepository.save(profile);

    }
    public CustomerProfile findCustomerProfileByExternalUserId(String id) {
        LOGGER.info("Starting findCustomerProfileByExternalUserId customerId={}",id);
        CustomerProfile customerProfile= this.customerProfileRepository.findCustomerProfileByUser_ExternalUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found please complete you profile first."));
        LOGGER.info("Finished findCustomerProfileByExternalUserId customerProfile={}",customerProfile);
        return customerProfile;
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
