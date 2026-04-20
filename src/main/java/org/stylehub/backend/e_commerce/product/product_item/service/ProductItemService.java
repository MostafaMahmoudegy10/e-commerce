package org.stylehub.backend.e_commerce.product.product_item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemCreateRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemResponse;
import org.stylehub.backend.e_commerce.platform.media.ProductItemImageRepo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductItemRepository productItemRepository;
    private final CurrentUserProvider  currentUserProvider;
    private final ProductService  productService;
    private final ImageService imageService;
    private final ProductItemImageRepo productItemImageRepo;
    private final SizeRepo sizeRepo;

    @Transactional
    public ProductItemResponse createProductItem(UUID productId, ProductItemCreateRequest request) {
            // first get current brand
            String externalBrandId=this.getExternalBrandId(currentUserProvider);

            // make sure that the product is there
            Product product = this.productService.findProductById(productId);

            if(product == null){
                throw new IllegalArgumentException("Product not found for this brand");
            }

            // validate the request dto
            validateProductItemCreationRequest(request);

        ProductItem productItem = new ProductItem();

        // extract image
        List<UploadResponse>uploadResponseList=
                request.productItemImages()
                        .stream()
                        .map(this.imageService::uploadImage).toList();

        // extract them as product item image
        List<ProductItemImage> productItemImageList=uploadResponseList
                .stream().map( (image)->{
                        return this.getProductItemImage(image,productItem);
                        }
                ).toList();

            List<Size>savedSize= request.sizeAndStockList().stream().map((size)->{
                size.setProductItem(productItem);
                return size;
            }).toList();

            // creation of product item
            productItem.setProduct(product);
            productItem.setColor(request.color());
            productItem.setSku(request.sku());
            productItem.getProductItemImages().addAll(productItemImageList);
            productItem.getSizeList().addAll(savedSize);
            ProductItem savedProductItem =this.productItemRepository.save(productItem);

            return new ProductItemResponse(
                    "Product Item Created !",
                       savedProductItem.getId()
            );
    }

    private void validateProductItemCreationRequest(ProductItemCreateRequest request) {
        if(request.color()==null){
            throw new IllegalArgumentException("Color Not Found");
        }
        if(request.sku()==null){
            throw new IllegalArgumentException("Sku Not Found");
        }
        if(request.sizeAndStockList().get(0).getSizeName()==null){
            throw new IllegalArgumentException("Size Not Found");
        }
        if(request.sizeAndStockList().get(0).getStock()==null){
            throw new IllegalArgumentException("Stock Number Not Found");
        }
        if(request.productItemImages()==null){
            throw new IllegalArgumentException("Images For Product Item  Not Found");
        }
    }

    private ProductItemImage getProductItemImage(UploadResponse uploadResponse,ProductItem item) {
        ProductItemImage productItemImage=new ProductItemImage();
        productItemImage.setImageUrl(uploadResponse.imageUrl());
        productItemImage.setPublicId(uploadResponse.publicId());
        productItemImage.setProductItem(item);
        return productItemImage;
    }

    private String getExternalBrandId(CurrentUserProvider currentUserProvider) {
        return this.currentUserProvider.externalId();
    }
}
