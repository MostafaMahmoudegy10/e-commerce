package org.stylehub.backend.e_commerce.product.product_item.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.platform.media.repository.ProductItemImageRepository;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemCreateRequest;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemPatchRequest;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemResponse;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemSizeRequest;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.product_item.repository.ProductItemRepository;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;
import org.stylehub.backend.e_commerce.product.product_item.size.SizeRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductItemService {

    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final CurrentUserProvider currentUserProvider;
    private final ImageService imageService;
    private final ProductItemImageRepository productItemImageRepository;
    private final SizeRepository sizeRepository;

    @Transactional
    public ProductItemResponse addNewProductItem(UUID productId, ProductItemCreateRequest request) {
        validateProductItemCreateRequest(request);

        String brandId = getCurrentBrand();
        Product product = findProductForBrand(productId, brandId);
        ensureColorIsUnique(productId, request.color(), brandId, null);

        ProductItem productItem = new ProductItem();
        productItem.setColor(request.color());
        productItem.setSku(request.sku());
        productItem.setProduct(product);
        productItem.setSizeList(buildSizes(request.sizeList(), productItem));
        productItem.setProductItemImages(buildImages(request.imagesOfProductItem(), productItem));

        return toResponse(this.productItemRepository.saveAndFlush(productItem));
    }

    @Transactional
    public ProductItemResponse patchProductItem(UUID productItemId, ProductItemPatchRequest request) {
        String brandId = getCurrentBrand();
        ProductItem productItem = findProductItemForBrand(productItemId, brandId);

        if (request.color() != null && !request.color().isBlank()) {
            ensureColorIsUnique(productItem.getProduct().getId(), request.color(), brandId, productItemId);
            productItem.setColor(request.color());
        }
        if (request.sku() != null && !request.sku().isBlank()) {
            productItem.setSku(request.sku());
        }
        if (request.sizeList() != null && !request.sizeList().isEmpty()) {
            validateSizes(request.sizeList());
            this.sizeRepository.deleteByProductItem_Id(productItemId);
            productItem.setSizeList(buildSizes(request.sizeList(), productItem));
        }
        if (request.imagesOfProductItem() != null && !request.imagesOfProductItem().isEmpty()) {
            replaceImages(productItem, request.imagesOfProductItem());
        }

        return toResponse(this.productItemRepository.saveAndFlush(productItem));
    }

    @Transactional
    public void deleteProductItem(UUID productItemId) {
        String brandId = getCurrentBrand();
        ProductItem productItem = findProductItemForBrand(productItemId, brandId);
        deleteStoredImages(productItemId);
        this.sizeRepository.deleteByProductItem_Id(productItemId);
        this.productItemRepository.delete(productItem);
    }

    private void validateProductItemCreateRequest(ProductItemCreateRequest request) {
        if (request.color() == null || request.color().isBlank()) {
            throw new IllegalArgumentException("Color is required");
        }
        if (request.sku() == null || request.sku().isBlank()) {
            throw new IllegalArgumentException("Sku is required");
        }
        if (request.sizeList() == null || request.sizeList().isEmpty()) {
            throw new IllegalArgumentException("At least one size is required");
        }
        validateSizes(request.sizeList());
        if (request.imagesOfProductItem() == null || request.imagesOfProductItem().isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }
    }

    private void validateSizes(List<ProductItemSizeRequest> sizeList) {
        for (ProductItemSizeRequest sizeRequest : sizeList) {
            if (sizeRequest.sizeName() == null || sizeRequest.sizeName().isBlank()) {
                throw new IllegalArgumentException("Size name is required");
            }
            if (sizeRequest.stock() == null || sizeRequest.stock() < 0) {
                throw new IllegalArgumentException("Stock should be zero or more");
            }
        }
    }

    private Product findProductForBrand(UUID productId, String brandId) {
        return this.productRepository.findProductByIdAndBrand_User_ExternalUserId(productId, brandId)
                .orElseThrow(() -> new IllegalArgumentException("""
                        product You Requested Not Present For Your Brand Please Add It First And Try Again
                        """));
    }

    private ProductItem findProductItemForBrand(UUID productItemId, String brandId) {
        return this.productItemRepository.findProductItemByIdAndProduct_Brand_User_ExternalUserId(productItemId, brandId)
                .orElseThrow(() -> new IllegalArgumentException("""
                        product item You Requested Not Present For Your Brand Please Add It First And Try Again
                        """));
    }

    private void ensureColorIsUnique(UUID productId, String color, String brandId, UUID currentProductItemId) {
        boolean exists;
        if (currentProductItemId == null) {
            exists = this.productItemRepository.existsByProduct_IdAndColorAndProduct_Brand_User_ExternalUserId(
                    productId,
                    color,
                    brandId
            );
        } else {
            exists = this.productItemRepository.existsByProduct_IdAndColorAndProduct_Brand_User_ExternalUserIdAndIdNot(
                    productId,
                    color,
                    brandId,
                    currentProductItemId
            );
        }

        if (exists) {
            throw new IllegalArgumentException("Product item color already exists for this product");
        }
    }

    private List<Size> buildSizes(List<ProductItemSizeRequest> sizeRequests, ProductItem productItem) {
        return sizeRequests.stream().map(sizeRequest -> {
            Size size = new Size();
            size.setSizeName(sizeRequest.sizeName());
            size.setStock(sizeRequest.stock());
            size.setProductItem(productItem);
            return size;
        }).toList();
    }

    private List<ProductItemImage> buildImages(List<MultipartFile> files, ProductItem productItem) {
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    UploadResponse uploadResponse = imageService.uploadImage(file);
                    ProductItemImage image = new ProductItemImage();
                    image.setImageUrl(uploadResponse.imageUrl());
                    image.setPublicId(uploadResponse.publicId());
                    image.setProductItem(productItem);
                    return image;
                }).toList();
    }

    private void replaceImages(ProductItem productItem, List<MultipartFile> files) {
        deleteStoredImages(productItem.getId());
        this.productItemImageRepository.deleteByProductItem_Id(productItem.getId());
        productItem.setProductItemImages(buildImages(files, productItem));
    }

    private void deleteStoredImages(UUID productItemId) {
        List<ProductItemImage> images = this.productItemImageRepository.findAllByProductItem_Id(productItemId);
        for (ProductItemImage image : images) {
            if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
                this.imageService.deleteImage(image.getPublicId());
            }
        }
    }

    private ProductItemResponse toResponse(ProductItem productItem) {
        List<ProductItemSizeRequest> sizeList = productItem.getSizeList() == null
                ? Collections.emptyList()
                : productItem.getSizeList().stream()
                .map(size -> new ProductItemSizeRequest(size.getSizeName(), size.getStock()))
                .toList();

        List<String> imageUrls = productItem.getProductItemImages() == null
                ? Collections.emptyList()
                : productItem.getProductItemImages().stream()
                .map(ProductItemImage::getImageUrl)
                .toList();

        return new ProductItemResponse(
                productItem.getId(),
                productItem.getProduct().getId(),
                productItem.getProduct().getProductNameEn(),
                productItem.getColor(),
                productItem.getSku(),
                sizeList,
                imageUrls
        );
    }

    private String getCurrentBrand() {
        String brandId = currentUserProvider.externalId();
        this.brandService.isBrandExists(brandId);
        return brandId;
    }
}
