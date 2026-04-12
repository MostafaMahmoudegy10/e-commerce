package org.stylehub.backend.e_commerce.brand.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.dto.BrandCreationRequest;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final UserSyncService userSyncService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public Map<String, Object> setupBrand(BrandCreationRequest brandCreationRequest) {
        validateBrandCreationRequest(brandCreationRequest);

        User user = userSyncService.create(currentUserProvider);

        brandRepository.findByUser_ExternalUserId(currentUserProvider.externalId())
                .ifPresent(brand -> {
                    throw new IllegalArgumentException("Brand profile already exists for this user.");
                });

        Brand brand = new Brand();
        brand.setBrandName(brandCreationRequest.brandName());
        brand.setBrandEmail(currentUserProvider.getEmail());
        brand.setBrandImageUrl(brandCreationRequest.profileImageUrl());
        brand.setDescription(brandCreationRequest.bio());
        brand.setUser(user);

        Brand savedBrand = brandRepository.save(brand);

        return Map.of(
                "message", "Brand profile created successfully.",
                "brandEmail", savedBrand.getBrandEmail()
        );
    }

    private void validateBrandCreationRequest(BrandCreationRequest brandCreationRequest) {
        if (brandCreationRequest.brandName() == null) {
            throw new IllegalArgumentException("Brand name is required.");
        }
        if (brandCreationRequest.phoneNumber() == null) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (brandCreationRequest.bio() == null) {
            throw new IllegalArgumentException("Bio is required.");
        }
        if (brandCreationRequest.profileImageUrl() == null) {
            throw new IllegalArgumentException("Profile image URL is required.");
        }
    }

    public boolean isBrandExists(String globalBrandId){

        if (!this.brandRepository.existsByUser_ExternalUserId(globalBrandId)) {
            throw new IllegalArgumentException(
                    "Brand id not found, please complete your profile in the application"
            );
        }
        return true;
    }
}
