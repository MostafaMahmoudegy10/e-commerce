package org.stylehub.backend.e_commerce.product.color.variant.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantCreationRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantCreationResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantStockUpdateRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantStockUpdateResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.service.ProductColorService;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProductVariantService.class);

    private final ProductVariantRepository productVariantRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ProductService productService;
    private final ProductColorService productColorService;

    @Transactional
    public ProductVariantCreationResponse upsertVariant(
            ProductVariantCreationRequest request,
            UUID productId,
            UUID colorId
    ) {
        LOGGER.info("Starting upsert of ProductVariant");

        validateProductVariantCreationRequest(request);

        String brandExternalUserId = currentUserProvider.externalId();

        Product product = productService.findProductById(productId, brandExternalUserId);
        LOGGER.info(
                "Product id={} found for brand={}",
                product.getId(),
                brandExternalUserId
        );

        ProductColor productColor =
                productColorService.findProductColorByIdAndProductIdAndBrandExternalUserId(
                        colorId,
                        productId,
                        brandExternalUserId
                );

        LOGGER.info(
                "ProductColor id={} found for productId={} and brand={}",
                productColor.getId(),
                productId,
                brandExternalUserId
        );

        ProductVariant existingVariant =
                productVariantRepository.findProductVariantByProductColor_IdAndSize(
                        productColor.getId(),
                        request.size()
                );

        if (existingVariant == null) {
            return createVariant(request, productColor);
        }

        return updateVariant(request, existingVariant);
    }

    @Transactional
    public ProductVariantStockUpdateResponse patchVariantStock(
            UUID productId,
            UUID colorId,
            UUID variantId,
            ProductVariantStockUpdateRequest request
    ) {
        LOGGER.info(
                "Stock update started for productId={}, colorId={}, variantId={}",
                productId,
                colorId,
                variantId
        );

        validateStockUpdateRequest(request);

        String brandExternalUserId = currentUserProvider.externalId();
        productService.findProductById(productId, brandExternalUserId);
        ProductColor productColor = productColorService
                .findProductColorByIdAndProductIdAndBrandExternalUserId(
                        colorId,
                        productId,
                        brandExternalUserId
                );

        ProductVariant variant = productVariantRepository
                .findByIdAndProductColor_Id(variantId, productColor.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(
                                "ProductVariant not found with id=%s for colorId=%s",
                                variantId,
                                colorId
                        )
                ));

        variant.setStock(request.stock());
        ProductVariant savedVariant = productVariantRepository.save(variant);

        LOGGER.info(
                "Stock update completed for variantId={}, newStock={}",
                savedVariant.getId(),
                savedVariant.getStock()
        );

        return new ProductVariantStockUpdateResponse(
                savedVariant.getId(),
                productId,
                colorId,
                savedVariant.getProductColor().getColorCode(),
                savedVariant.getSize(),
                savedVariant.getSku(),
                savedVariant.getStock(),
                savedVariant.getPriceOverride()
        );
    }

    private ProductVariantCreationResponse createVariant(
            ProductVariantCreationRequest request,
            ProductColor productColor
    ) {
        if (productVariantRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("SKU already exists");
        }

        ProductVariant productVariant = new ProductVariant();
        productVariant.setSku(request.sku());
        productVariant.setSize(request.size());
        productVariant.setStock(request.stock());
        productVariant.setPriceOverride(request.price());
        productVariant.setProductColor(productColor);

        ProductVariant savedVariant = productVariantRepository.save(productVariant);

        LOGGER.info("ProductVariant created with id={}", savedVariant.getId());

        return mapToResponse(savedVariant);
    }

    private ProductVariantCreationResponse updateVariant(
            ProductVariantCreationRequest request,
            ProductVariant existingVariant
    ) {
        boolean skuUsedByAnotherVariant =
                productVariantRepository.existsBySkuAndIdNot(
                        request.sku(),
                        existingVariant.getId()
                );

        if (skuUsedByAnotherVariant) {
            throw new IllegalArgumentException("SKU already exists");
        }

        existingVariant.setSku(request.sku());
        existingVariant.setStock(request.stock());
        existingVariant.setPriceOverride(request.price());

        ProductVariant updatedVariant = productVariantRepository.save(existingVariant);

        LOGGER.info("ProductVariant updated with id={}", updatedVariant.getId());

        return mapToResponse(updatedVariant);
    }

    private void validateProductVariantCreationRequest(
            ProductVariantCreationRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Product variant request is required");
        }

        if (request.sku() == null || request.sku().isBlank()) {
            throw new IllegalArgumentException("SKU is required");
        }

        if (request.size() == null || request.size().isBlank()) {
            throw new IllegalArgumentException("Size is required");
        }

        if (request.stock() == null) {
            throw new IllegalArgumentException("Stock is required");
        }

        if (request.stock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        if (request.price() == null) {
            throw new IllegalArgumentException("Price is required");
        }

        if (request.price().signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    private void validateStockUpdateRequest(ProductVariantStockUpdateRequest request) {
        if (request == null || request.stock() == null) {
            throw new IllegalArgumentException("Stock is required");
        }
        if (request.stock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    private ProductVariantCreationResponse mapToResponse(ProductVariant variant) {
        return new ProductVariantCreationResponse(
                variant.getId(),
                variant.getSku(),
                variant.getProductColor().getColorCode(),
                variant.getStock(),
                variant.getSize()
        );
    }
}
