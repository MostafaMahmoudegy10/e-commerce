package org.stylehub.backend.e_commerce.product.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.image.dto.UploadResponse;
import org.stylehub.backend.e_commerce.image.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.image.service.ImageService;
import org.stylehub.backend.e_commerce.product.dto.ProductDto;
import org.stylehub.backend.e_commerce.product.dto.ProductDtoRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductDtoResponse;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemDto;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageService imageService;

    @Transactional
    public ProductDtoResponse addNewProduct(ProductDtoRequest productDtoRequest){
        // here we need to create a product first
        if(productDtoRequest.productId() == null){
            Product product=this.mappedProduct(productDtoRequest.productDto());
            List<ProductItem>productItems=this.getProductItems(productDtoRequest.productItemList(),product);
            product.setProductItems(productItems);
            // now we saved the product
            product = productRepository.save(product);
            return new ProductDtoResponse(product.getProductName());
        }
        return null;
    }




    private Product mappedProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductName(productDto.productName());
        product.setProductDescription(productDto.productDescription());
        // image service to upload it to the cloudry
        UploadResponse image = null;
        try {
            image = imageService.uploadImage(productDto.thumbnail());
            product.setThumbnail(image.imageUrl());
            product.setPublicId(image.publicId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return product;
    }

    private List<ProductItem> getProductItems (List<ProductItemDto>dtos,Product product){
        List<ProductItem> productItems = dtos.stream()
                .map(pi -> {
                    ProductItem productItem=new ProductItem();
                    productItem.setPrice(pi.price());
                    productItem.setSku(pi.sku());
                    productItem.setStock(pi.stock());
                    productItem.setColor(pi.color());
                    productItem.setSize(pi.size());
                    productItem.setProduct(product);
                    productItem.setProductItemImages(
                        pi.imagesOfProductItem().stream().map(
                                file -> {
                                    UploadResponse response = null;
                                    try {
                                        response = this.imageService.uploadImage(file);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ProductItemImage productItemImage = new ProductItemImage();
                                    productItemImage.setImageUrl(response.imageUrl());
                                    productItemImage.setPublicId(response.publicId());
                                    return productItemImage;
                                }
                        ).toList()
                    );
                  return productItem;
                })
                .toList();
        return productItems;
    }



}
