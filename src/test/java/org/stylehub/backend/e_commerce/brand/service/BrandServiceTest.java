package org.stylehub.backend.e_commerce.brand.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stylehub.backend.e_commerce.brand.dto.BrandCreationRequest;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private UserSyncService userSyncService;

    @InjectMocks
    private BrandService brandService;

    @Test
    void updateBrandDoesNotRequireWebsiteUrl() {
        BrandCreationRequest request = new BrandCreationRequest(
                "brand-1",
                "Style Hub",
                "stylehub",
                "Brand bio",
                null,
                "brand@test.com",
                "https://cdn.example.com/logo.png"
        );

        User user = new User();
        user.setEmail("brand@test.com");

        Brand existingBrand = new Brand();

        when(this.userSyncService.sync("brand-1", "BRAND_OWNER", "brand@test.com")).thenReturn(user);
        when(this.brandRepository.findByUser_ExternalUserId("brand-1")).thenReturn(Optional.of(existingBrand));
        when(this.brandRepository.save(existingBrand)).thenReturn(existingBrand);

        assertDoesNotThrow(() -> this.brandService.updateBrand(request));

        assertEquals("Style Hub", existingBrand.getBrandName());
        assertEquals("Brand bio", existingBrand.getDescription());
        assertEquals("https://cdn.example.com/logo.png", existingBrand.getBrandImageUrl());
        verify(this.brandRepository).save(existingBrand);
    }
}
