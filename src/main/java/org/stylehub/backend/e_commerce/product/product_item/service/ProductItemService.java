package org.stylehub.backend.e_commerce.product.product_item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemCreateRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.SizeDtoReqRes;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.product_item.repository.ProductItemRepository;
import org.stylehub.backend.e_commerce.product.product_item.repository.SizeRepo;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductItemRepository productItemRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ProductService productService;
    private final ImageService imageService;
    private final SizeRepo sizeRepo;

    @Transactional
    public ProductItemResponse createProductItem(UUID productId, ProductItemCreateRequest request) {
        String externalBrandId = getExternalBrandId();

        Product product = productService.findProductById(productId, externalBrandId);

        validateProductItemCreationRequest(request);

        ProductItem productItem = new ProductItem();
        productItem.setProduct(product);
        productItem.setColor(request.color());
        productItem.setSku(request.sku());

        List<UploadResponse> uploadResponses = request.productItemImages()
                .stream()
                .map(imageService::uploadImage)
                .toList();

        List<ProductItemImage> images = uploadResponses.stream()
                .map(uploadResponse -> toProductItemImage(uploadResponse, productItem))
                .toList();

        List<Size> sizes = request.sizeAndStockList().stream()
                .map(dto -> toNewSize(dto, productItem))
                .toList();

        productItem.getProductItemImages().addAll(images);
        productItem.getSizeList().addAll(sizes);

        ProductItem savedProductItem = productItemRepository.save(productItem);

        return new ProductItemResponse(
                "Product Item Created !",
                savedProductItem.getId()
        );
    }

    @Transactional
    public ProductItemResponse updateProductItem(UUID productId, UUID productItemId, ProductItemPatchRequest request) {
        String brandId = getExternalBrandId();

        Product product = productService.findProductById(productId,brandId);

        ProductItem item = findProductItem(productItemId,productId,brandId);

        if (!item.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("Product item does not belong to product");
        }

        if (request.sku() != null && !request.sku().isBlank()) {
            item.setSku(request.sku());
        }

        patchSizes(request.size(), productItemId, brandId, item);

        ProductItem savedItem = productItemRepository.saveAndFlush(item);

        return toResponse(savedItem);
    }
    public void deleteProductItem(UUID productId, UUID productItemId) {
        ProductItem productItem = findProductItem(productItemId, productId, getExternalBrandId());
        safelyDeleteIagesOfProductItem(productItem.getProductItemImages());
        productItemRepository.delete(productItem);
    }

    public ProductItem findProductItem(UUID productItemId,UUID productId, String brandId) {
        return productItemRepository.findByIdAndProduct_IdAndProduct_Brand_User_ExternalUserId(productItemId,productId, brandId)
                .orElseThrow(() -> new IllegalArgumentException("Product Item Not Found For This Brand !"));
    }
    private void safelyDeleteIagesOfProductItem(List<ProductItemImage> productItemImages) {
        productItemImages.forEach(image -> {
            this.imageService.deleteImage(image.getPublicId());
        });
    }

    private ProductItem patchSizes(List<SizeDtoReqRes> requestedSizes, UUID productItemId, String brandId, ProductItem item) {
        if (requestedSizes == null || requestedSizes.isEmpty()) {
            return item;
        }

        for (SizeDtoReqRes requestedSize : requestedSizes) {
            Optional<Size> existingSize = resolveExistingSize(requestedSize, productItemId, brandId);
            existingSize.ifPresentOrElse(
                    size -> {
                        if (requestedSize.sizeName() != null && !requestedSize.sizeName().isBlank()) {
                            size.setSizeName(requestedSize.sizeName());
                        }
                        if (requestedSize.stock() != null) {
                            adjustSizeStock(size, requestedSize.stock());
                        }
                    },
                    () -> {
                        if (requestedSize.sizeName() == null || requestedSize.sizeName().isBlank()) {
                            throw new IllegalArgumentException("Size name is required for new size");
                        }

                        Size newSize = new Size();
                        newSize.setSizeName(requestedSize.sizeName());
                        newSize.setStock(Optional.ofNullable(requestedSize.stock()).orElse(0));
                        newSize.setProductItem(item);
                        Size savedNewSize = sizeRepo.save(newSize);
                        item.getSizeList().add(savedNewSize);
                    }
            );
        }
        return item;
    }

    private Optional<Size> resolveExistingSize(SizeDtoReqRes requestedSize, UUID productItemId, String brandId) {
        if (requestedSize.id() != null) {
            return sizeRepo.findByIdAndProductItem_IdAndProductItem_Product_Brand_User_ExternalUserId(
                    requestedSize.id(),
                    productItemId,
                    brandId
            );
        }

        if (requestedSize.sizeName() != null && !requestedSize.sizeName().isBlank()) {
            return sizeRepo.findBySizeNameAndProductItem_IdAndProductItem_Product_Brand_User_ExternalUserId(
                    requestedSize.sizeName(),
                    productItemId,
                    brandId
            );
        }

        return Optional.empty();
    }

    private void validateProductItemCreationRequest(ProductItemCreateRequest request) {
        if (request.color() == null || request.color().isBlank()) {
            throw new IllegalArgumentException("Color Not Found");
        }

        if (request.sku() == null || request.sku().isBlank()) {
            throw new IllegalArgumentException("Sku Not Found");
        }

        if (request.productItemImages() == null || request.productItemImages().isEmpty()) {
            throw new IllegalArgumentException("Images For Product Item Not Found");
        }

        if (request.sizeAndStockList() == null || request.sizeAndStockList().isEmpty()) {
            throw new IllegalArgumentException("Size List Not Found");
        }

        for (Size size : request.sizeAndStockList()) {
            if (size.getSizeName() == null) {
                throw new IllegalArgumentException("Size Not Found");
            }
            if (size.getStock() == null) {
                throw new IllegalArgumentException("Stock Number Not Found");
            }
        }
    }

    private ProductItemImage toProductItemImage(UploadResponse uploadResponse, ProductItem item) {
        ProductItemImage productItemImage = new ProductItemImage();
        productItemImage.setImageUrl(uploadResponse.imageUrl());
        productItemImage.setPublicId(uploadResponse.publicId());
        productItemImage.setProductItem(item);
        return productItemImage;
    }

    private Size toNewSize(Size dto, ProductItem productItem) {
        Size size = new Size();
        size.setSizeName(dto.getSizeName());
        size.setStock(dto.getStock());
        size.setProductItem(productItem);
        return size;
    }

    private ProductItemResponse toResponse(ProductItem item) {
        return new ProductItemResponse(
                "Product Item Updated !",
                item.getId()
        );
    }

    private void adjustSizeStock(Size existingSize, Integer requestedStock) {
        int currentStock = Optional.ofNullable(existingSize.getStock()).orElse(0);
        int delta = requestedStock - currentStock;

        if (delta < 0) {
            existingSize.removeFromStock(Math.abs(delta));
            return;
        }

        if (delta > 0) {
            existingSize.addToStock(delta);
        }
    }

    private String getExternalBrandId() {
        return currentUserProvider.externalId();
    }
}
