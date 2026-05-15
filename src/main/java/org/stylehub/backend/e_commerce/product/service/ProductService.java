package org.stylehub.backend.e_commerce.product.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductColorCountRow;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductVariantCountRow;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.FindAllProductForBrand;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.repository.BrandCatalogProductQueryRepository;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardProductStockRow;
import org.stylehub.backend.e_commerce.favourite.repository.FavouriteRepository;
import org.stylehub.backend.e_commerce.platform.media.ProductColorImagesRepo;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductColorImages;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.repository.ProductColorRepository;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final ImageService imageService;
    private final CurrentUserProvider currentUserProvider;
    private final CategoryRepository categoryRepository;
    private final BrandCatalogProductQueryRepository brandCatalogProductQueryRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductVariantRepository productVariantRepository;
    private final FavouriteRepository favouriteRepository;
    private final ProductColorImagesRepo productColorImagesRepo;

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

        UploadResponse image = imageService.uploadImageAsync(request.thumbnail()).join();

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
            UploadResponse image = imageService.uploadImageAsync(patchRequest.thumbnail()).join();
            product.setThumbnail(image.imageUrl());
            product.setPublicId(image.publicId());
        }

        return toResponse(this.productRepository.saveAndFlush(product));
    }

    @Transactional
    public void deleteBrandProduct(UUID productId) {
        String brandId = getCurrentBrand();
        Product product = findProductForBrand(productId, brandId);
        deleteProductColorsTree(product, brandId);
        this.favouriteRepository.deleteByProduct_Id(product.getId());
        safelyDeleteProductThumbnail(product.getPublicId());
        this.productRepository.delete(product);
    }
    public  Product findProductById(UUID productId,String externalUserId) {
        return this.productRepository.findProductByIdAndBrand_User_ExternalUserId(productId,externalUserId)
                .orElseThrow(()->new IllegalArgumentException("Product not found for this brand"));
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

    public Product findProductForBrand(UUID productId, String brandId) {
        return this.productRepository.findProductByIdAndBrand_User_ExternalUserId(productId, brandId)
                .orElseThrow(() -> new IllegalArgumentException("""
                        product You Requested Not Present For Your Brand Please Add It First And Try Again
                        """));
    }

    private void safelyDeleteProductThumbnail(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        this.imageService.deleteImageAsync(publicId);
    }

    private void deleteProductColorsTree(Product product, String brandId) {
        List<ProductColor> productColors = this.productColorRepository
                .findAllByProduct_IdAndProduct_Brand_User_ExternalUserId(product.getId(), brandId);

        if (productColors.isEmpty()) {
            return;
        }

        List<String> colorImagePublicIds = productColors.stream()
                .flatMap(productColor -> this.productColorImagesRepo.findAllByProductColor_Id(productColor.getId()).stream())
                .map(ProductColorImages::getPublicId)
                .toList();

        productColors.forEach(productColor -> {
            this.productVariantRepository.deleteAllByProductColor_Id(productColor.getId());
            this.productColorImagesRepo.deleteAllByProductColor_Id(productColor.getId());
        });

        this.productColorRepository.deleteAll(productColors);

        scheduleColorImageDeletionAfterCommit(colorImagePublicIds);
    }

    private void scheduleColorImageDeletionAfterCommit(List<String> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    imageService.deleteImagesAsync(publicIds);
                }
            });
            return;
        }

        this.imageService.deleteImagesAsync(publicIds);
    }

    private String getCurrentBrand() {
        String brandId = currentUserProvider.externalId();
        this.brandService.isBrandExists(brandId);
        return brandId;
    }

    public PageResponse<BrandProductViewResponse> findAllProductsForBrand(
            BrandProductFilterRequest filter,
            Pageable pageable
    ) {
        Page<Product> page = this.brandCatalogProductQueryRepository.findBrandProducts(
                currentUserProvider.externalId(),
                filter,
                pageable
        );

        List<UUID> productIds = page.getContent().stream()
                .map(Product::getId)
                .toList();

        Map<UUID, Long> colorsCountByProductId = new HashMap<>();
        Map<UUID, Long> variantsCountByProductId = new HashMap<>();
        Map<UUID, Long> stockByProductId = new HashMap<>();

        if (!productIds.isEmpty()) {
            this.productColorRepository.countByProductIds(productIds).forEach(
                    row -> colorsCountByProductId.put(row.productId(), row.colorsCount())
            );
            this.productVariantRepository.countByProductIds(productIds).forEach(
                    row -> variantsCountByProductId.put(row.productId(), row.variantsCount())
            );
            this.productVariantRepository.sumStockByProductIds(productIds).forEach(
                    row -> stockByProductId.put(row.productId(), row.totalStock())
            );
        }

        List<BrandProductViewResponse> items = page.getContent().stream()
                .map(product -> new BrandProductViewResponse(
                        product.getId(),
                        product.getProductNameEn(),
                        product.getProductNameAr(),
                        product.getThumbnail(),
                        product.getCategory().getId(),
                        product.getCategory().getCategoryNameEn(),
                        product.getCategory().getCategoryNameAr(),
                        product.getCategory().getCategoryGender(),
                        colorsCountByProductId.getOrDefault(product.getId(), 0L),
                        variantsCountByProductId.getOrDefault(product.getId(), 0L),
                        stockByProductId.getOrDefault(product.getId(), 0L),
                        product.getPrice(),
                        product.getCreationDate()
                ))
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
