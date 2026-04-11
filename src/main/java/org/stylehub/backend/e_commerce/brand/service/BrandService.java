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
    public Map<String,Object> setupBrand(BrandCreationRequest brandCreationRequest) {
       //first we validate the dto request
        validateBrandCreationRequest(brandCreationRequest);

        brandRepository.findByUser_ExternalUserId(currentUserProvider.externalId())
            .orElseThrow(() -> new RuntimeException("User Already Auhtenticated And Registred"));

        // first we create the user for the brand
       User user= userSyncService.upsert(currentUserProvider);


        Brand brand = new Brand();
        brand.setBrandName(brandCreationRequest.brandName());
        brand.setBrandEmail(currentUserProvider.getEmail());
        brand.setBrandImageUrl(brandCreationRequest.profileImageUrl());
        brand.setDescription(brandCreationRequest.bio());
        brand.setUser(user);

        Brand savedBrand= brandRepository.save(brand);

        return Map.of(
                "message","brand added successfully with "+savedBrand.getBrandEmail()
        );
    }

    private void validateBrandCreationRequest(BrandCreationRequest brandCreationRequest) {
        if(brandCreationRequest.brandName()==null){
            throw  new IllegalArgumentException("Please Enter BrandName");

        }
        if(brandCreationRequest.phoneNumber()==null){
            throw  new IllegalArgumentException("Please Enter phoneNumber");

        }
        if(brandCreationRequest.bio()==null){
            throw  new IllegalArgumentException("Please Enter bio");
        }

        if(brandCreationRequest.profileImageUrl()==null){
            throw  new IllegalArgumentException("Please Enter imageUrl");
        }
    }
}
