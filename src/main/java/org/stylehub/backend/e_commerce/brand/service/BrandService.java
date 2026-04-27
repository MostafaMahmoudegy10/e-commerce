package org.stylehub.backend.e_commerce.brand.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.dto.BrandCreationRequest;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final UserSyncService userSyncService;


    @Transactional
    @RabbitListener(queues = "brand.created.user.service.q")
    public void setupBrand(BrandCreationRequest brandCreationRequest) {
        validateBrandCreationRequest(brandCreationRequest);

        brandRepository.findByUser_ExternalUserId(brandCreationRequest.brandId())
                .ifPresent(brand -> {
                    throw new IllegalArgumentException("Brand profile already exists for this user.");
                });

        User user = userSyncService.create(brandCreationRequest.brandId(),"BRAND_OWNER");

        Brand brand = new Brand();
        brand.setBrandName(brandCreationRequest.brandName());
        brand.setBrandEmail(user.getEmail());
        brand.setDescription(brandCreationRequest.bio());
        brand.setBrandImageUrl(brandCreationRequest.websiteUrl());
        brand.setUser(user);

      brandRepository.save(brand);

    }

    private void validateBrandCreationRequest(BrandCreationRequest brandCreationRequest) {
        if (brandCreationRequest.brandName() == null) {
            throw new IllegalArgumentException("Brand name is required.");
        }
        if (brandCreationRequest.brandId() == null) {
            throw new IllegalArgumentException("BrandId number is required.");
        }
        if (brandCreationRequest.bio() == null) {
            throw new IllegalArgumentException("Bio is required.");
        }
        if (brandCreationRequest.websiteUrl() == null) {
            throw new IllegalArgumentException("Website URL is required.");
        }
        if (brandCreationRequest.brandName() == null) {
            throw new IllegalArgumentException("brandName is required.");
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
