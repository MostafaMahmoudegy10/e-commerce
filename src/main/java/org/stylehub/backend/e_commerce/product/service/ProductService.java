package org.stylehub.backend.e_commerce.product.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.dto.ProductPatchRequest;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final ImageService imageService;
    private final CurrentUserProvider currentUserProvider;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductCreationResponse addNewProduct(ProductCreationRequest request) {
        validateProductCreationRequest(request);

        String brandId = getCurrentBrand();
        boolean isExisted = this.productRepository.existsProductByBrand_User_ExternalUserId(
                request.productNameEn(),
                brandId
        );
        if (isExisted) {
            throw new IllegalArgumentException("Product already exists for this brand add another product");
        }

        Category category = categoryRepository.findCategoryByIdAndBrand_User_ExternalUserId(
                request.categoryId(),
                brandId
        ).orElseThrow(() -> new IllegalArgumentException("""
                category You Requested Not Present For Your Brand Please Add It First And Try Again
                """));

        UploadResponse image = imageService.uploadImage(request.thumbnail());

        Product product = new Product();
        product.setThumbnail(image.imageUrl());
        product.setPublicId(image.publicId());
        product.setProductDescriptionAr(request.productDescriptionAr());
        product.setProductDescriptionEn(request.productDescriptionEn());
        product.setProductNameAr(request.productNameAr());
        product.setProductNameEn(request.productNameEn());
        product.setPrice(request.productPrice());
        product.setCategory(category);
        product.setBrand(category.getBrand());

        Product savedProduct = this.productRepository.save(product);
        return toResponse(savedProduct);
    }

    @Transactional
    public ProductCreationResponse patchBrandProduct(UUID productId, ProductPatchRequest patchRequest) {
        String brandId = getCurrentBrand();
        Product product = findProductForBrand(productId, brandId);

        if (patchRequest.productNameEn() != null && !patchRequest.productNameEn().isBlank()) {
            boolean isExisted = this.productRepository.existsProductByBrand_User_ExternalUserIdAndIdNot(
                    patchRequest.productNameEn(),
                    brandId,
                    productId
            );
            if (isExisted) {
                throw new IllegalArgumentException("Product already exists for this brand add another product");
            }
            product.setProductNameEn(patchRequest.productNameEn());
        }
        if (patchRequest.productNameAr() != null && !patchRequest.productNameAr().isBlank()) {
            product.setProductNameAr(patchRequest.productNameAr());
        }
        if (patchRequest.productDescriptionEn() != null && !patchRequest.productDescriptionEn().isBlank()) {
            product.setProductDescriptionEn(patchRequest.productDescriptionEn());
        }
        if (patchRequest.productDescriptionAr() != null && !patchRequest.productDescriptionAr().isBlank()) {
            product.setProductDescriptionAr(patchRequest.productDescriptionAr());
        }
        if (patchRequest.productPrice() != null) {
            if (patchRequest.productPrice().doubleValue() <= 0) {
                throw new IllegalArgumentException("Price is required above 0");
            }
            product.setPrice(patchRequest.productPrice());
        }
        if (patchRequest.categoryId() != null) {
            Category category = categoryRepository.findCategoryByIdAndBrand_User_ExternalUserId(
                    patchRequest.categoryId(),
                    brandId
            ).orElseThrow(() -> new IllegalArgumentException("""
                    category You Requested Not Present For Your Brand Please Add It First And Try Again
                    """));
            product.setCategory(category);
            product.setBrand(category.getBrand());
        }
        if (patchRequest.thumbnail() != null && !patchRequest.thumbnail().isEmpty()) {
            safelyDeleteProductThumbnail(product.getPublicId());
            UploadResponse image = imageService.uploadImage(patchRequest.thumbnail());
            product.setThumbnail(image.imageUrl());
            product.setPublicId(image.publicId());
        }

        return toResponse(this.productRepository.saveAndFlush(product));
    }

    @Transactional
    public void deleteBrandProduct(UUID productId) {
        String brandId = getCurrentBrand();
        Product product = findProductForBrand(productId, brandId);
        product.setIsArchived(true);
        product.setIsActive(false);
        this.productRepository.save(product);
    }

    @Transactional
    public ProductCreationResponse toggleProductVisibility(UUID productId, boolean isActive) {
        String brandId = getCurrentBrand();
        Product product = findProductForBrand(productId, brandId);
        product.setIsActive(isActive);
        return toResponse(this.productRepository.save(product));
    }

    @Transactional
    public ProductCreationResponse archiveProduct(UUID productId, boolean archived) {
        String brandId = getCurrentBrand();
        Product product = findProductForBrand(productId, brandId);
        product.setIsArchived(archived);
        if (archived) {
            product.setIsActive(false);
        }
        return toResponse(this.productRepository.save(product));
    }

    private ProductCreationResponse toResponse(Product product) {
        return new ProductCreationResponse(
                product.getId(),
                product.getProductNameEn(),
                product.getProductNameAr(),
                product.getThumbnail(),
                product.getCategory().getCategoryNameEn()
        );
    }

    private void validateProductCreationRequest(ProductCreationRequest request) {
        if (request.productNameEn() == null || request.productNameEn().isBlank()) {
            throw new IllegalArgumentException("Product name en is required");
        }
        if (request.productNameAr() == null || request.productNameAr().isBlank()) {
            throw new IllegalArgumentException("Product name ar is required");
        }
        if (request.productDescriptionEn() == null || request.productDescriptionEn().isBlank()) {
            throw new IllegalArgumentException("Description en is required");
        }
        if (request.productDescriptionAr() == null || request.productDescriptionAr().isBlank()) {
            throw new IllegalArgumentException("Description ar is required");
        }
        if (request.productPrice() == null || request.productPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price is required above 0");
        }
        if (request.categoryId() == null) {
            throw new IllegalArgumentException("Category id is required");
        }
        if (request.thumbnail() == null || request.thumbnail().isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }
    }

    private Product findProductForBrand(UUID productId, String brandId) {
        return this.productRepository.findProductByIdAndBrand_User_ExternalUserId(productId, brandId)
                .orElseThrow(() -> new IllegalArgumentException("""
                        product You Requested Not Present For Your Brand Please Add It First And Try Again
                        """));
    }

    private void safelyDeleteProductThumbnail(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        this.imageService.deleteImage(publicId);
    }

    private String getCurrentBrand() {
        String brandId = currentUserProvider.externalId();
        this.brandService.isBrandExists(brandId);
        return brandId;
    }
}
