package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile.dto.BrandSetupRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile.dto.BrandSetupResponse;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandProfileService {

    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ImageService imageService;

    @Transactional
    public BrandSetupResponse setupBrand(BrandSetupRequest request) {
        validateRequest(request);

        User user = resolveCurrentUser();

        Brand brand = brandRepository.findByUser_Id(user.getId()).orElseGet(Brand::new);
        brand.setUser(user);
        brand.setBrandName(request.brandName());
        brand.setBrandEmail(request.brandEmail());
        brand.setDescription(request.description());

        try {
            UploadResponse uploadResponse = imageService.uploadImage(request.brandImage());
            brand.setBrandImageUrl(uploadResponse.imageUrl());
            brand.setPublicId(uploadResponse.publicId());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload brand image", e);
        }

        Brand saved = brandRepository.save(brand);
        return new BrandSetupResponse(
                saved.getId(),
                saved.getBrandName(),
                saved.getBrandEmail(),
                saved.getDescription(),
                saved.getBrandImageUrl()
        );
    }

    private User resolveCurrentUser() {
        String externalId = currentUserProvider.externalId();
        return userRepository.findByExternalUserId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Current user was not found in local DB"));
    }

    private void validateRequest(BrandSetupRequest request) {
        if (request.brandName() == null || request.brandName().isBlank()) {
            throw new IllegalArgumentException("brandName is required");
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        if (request.brandImage() == null || request.brandImage().isEmpty()) {
            throw new IllegalArgumentException("brandImage is required");
        }
    }
}
