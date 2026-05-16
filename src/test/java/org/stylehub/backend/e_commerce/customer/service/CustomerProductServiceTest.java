package org.stylehub.backend.e_commerce.customer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductDetailsDto;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.service.ProductRatingSummaryService;
import org.stylehub.backend.e_commerce.customer.repository.CustomerProductRepository;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.platform.media.ProductColorImagesRepo;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductColorImages;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.service.ProductColorService;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.service.ProductVariantService;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProductServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private ProductColorService productColorService;
    @Mock
    private ProductVariantService productVariantService;
    @Mock
    private ProductRatingSummaryService productRatingSummaryService;
    @Mock
    private ProductColorImagesRepo productColorImagesRepo;
    @Mock
    private CustomerProductRepository customerProductRepository;
    @Mock
    private CustomerProfileService customerProfileService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CustomerProductService customerProductService;

    @Test
    void findProductDetailsUsesFirstColorImageAsThumbnail() {
        UUID productId = UUID.randomUUID();
        String brandId = "brand-1";

        Product product = new Product();
        product.setId(productId);
        product.setThumbnail("product-thumb");
        product.setProductNameEn("Sneaker");
        product.setProductNameAr("Sneaker AR");
        product.setProductDescriptionEn("desc en");
        product.setProductDescriptionAr("desc ar");

        ProductColor color = new ProductColor();
        color.setId(UUID.randomUUID());
        color.setColorCode("#000");
        color.setProduct(product);

        ProductColorImages image = new ProductColorImages();
        image.setImageUrl("color-image-1");
        image.setProductColor(color);

        ProductVariant variant = new ProductVariant();
        variant.setId(UUID.randomUUID());
        variant.setProductColor(color);
        variant.setSize("42");
        variant.setStock(8);
        variant.setSku("SKU-1");
        variant.setPriceOverride(BigDecimal.TEN);

        when(this.productService.findProductById(productId, brandId)).thenReturn(product);
        when(this.productColorService.findAllProductColorsByProductIdAndBrandId(productId, brandId)).thenReturn(List.of(color));
        when(this.productColorImagesRepo.findAllByProductColor_Product_Id(productId)).thenReturn(List.of(image));
        when(this.productVariantService.findAllByProductId(productId)).thenReturn(List.of(variant));
        when(this.productRatingSummaryService.getAvgRatingOfProduct(productId)).thenReturn(BigDecimal.valueOf(4.5));

        ProductDetailsDto response = this.customerProductService.findProductDetails(brandId, productId);

        assertEquals("color-image-1", response.thumbnail());
        assertEquals(List.of("color-image-1"), response.colorDetails().get(0).colorImages());
        verify(this.productColorImagesRepo).findAllByProductColor_Product_Id(productId);
    }

    @Test
    void findProductDetailsFallsBackToProductThumbnailWhenNoColorImagesExist() {
        UUID productId = UUID.randomUUID();
        String brandId = "brand-1";

        Product product = new Product();
        product.setId(productId);
        product.setThumbnail("product-thumb");
        product.setProductNameEn("Sneaker");
        product.setProductNameAr("Sneaker AR");
        product.setProductDescriptionEn("desc en");
        product.setProductDescriptionAr("desc ar");

        ProductColor color = new ProductColor();
        color.setId(UUID.randomUUID());
        color.setColorCode("#fff");
        color.setProduct(product);

        when(this.productService.findProductById(productId, brandId)).thenReturn(product);
        when(this.productColorService.findAllProductColorsByProductIdAndBrandId(productId, brandId)).thenReturn(List.of(color));
        when(this.productColorImagesRepo.findAllByProductColor_Product_Id(productId)).thenReturn(List.of());
        when(this.productVariantService.findAllByProductId(productId)).thenReturn(List.of());
        when(this.productRatingSummaryService.getAvgRatingOfProduct(productId)).thenReturn(BigDecimal.ZERO);

        ProductDetailsDto response = this.customerProductService.findProductDetails(brandId, productId);

        assertEquals("product-thumb", response.thumbnail());
    }
}
