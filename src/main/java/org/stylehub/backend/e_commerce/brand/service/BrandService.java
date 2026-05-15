package org.stylehub.backend.e_commerce.brand.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.dto.BrandCreationRequest;
import org.stylehub.backend.e_commerce.brand.dto.BrandProfileDeletedRequest;
import org.stylehub.backend.e_commerce.brand.dto.BrandProfileRes;
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
        syncBrand(brandCreationRequest);
    }

    @Transactional
    @RabbitListener(queues = "brand.updated.user.service.q")
    public void updateBrand(BrandCreationRequest brandCreationRequest) {
        syncBrand(brandCreationRequest);
    }

    @Transactional
    @RabbitListener(queues = "brand.deleted.user.service.q")
    public void deleteBrand(BrandProfileDeletedRequest request) {
        if (request == null || request.brandId() == null || request.brandId().isBlank()) {
            throw new IllegalArgumentException("Brand id is required.");
        }

        this.brandRepository.deleteByUser_ExternalUserId(request.brandId());
        this.userSyncService.deleteByExternalId(request.brandId());
    }

    private void syncBrand(BrandCreationRequest brandCreationRequest) {
        validateBrandCreationRequest(brandCreationRequest);

        User user = userSyncService.sync(brandCreationRequest.brandId(),"BRAND_OWNER",brandCreationRequest.email());

        Brand brand = this.brandRepository.findByUser_ExternalUserId(brandCreationRequest.brandId())
                .orElseGet(Brand::new);
        brand.setBrandName(brandCreationRequest.brandName());
        brand.setBrandEmail(user.getEmail());
        brand.setDescription(brandCreationRequest.bio());
        brand.setBrandImageUrl(brandCreationRequest.websiteUrl());
        brand.setUser(user);

        this.brandRepository.save(brand);
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

    public Brand findBrandByExternalId(String brandId) {
        return this.brandRepository.findByUser_ExternalUserId(brandId).orElseThrow(()->new IllegalArgumentException("Brand id not found"));
    }

    public BrandProfileRes findBrandProfile(String brandId){
        return this.brandRepository.findBrandProfile(brandId);
    }

}
