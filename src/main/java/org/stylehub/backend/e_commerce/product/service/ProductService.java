package org.stylehub.backend.e_commerce.product.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.modules.catalog.category.CategoryService;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private BrandService brandService;
    private final ImageService imageService;
    private final CurrentUserProvider  currentUserProvider;
    private final CategoryRepository categoryRepository;

    @Transactional
   public ProductCreationResponse addNewProduct(ProductCreationRequest request){
       // first validate the dto requested
        validateProductCreationRequest(request);
        // get global brand Id
       String brandId=currentUserProvider.externalId();
       // check if this brand in the system
       this.brandService.isBrandExists(brandId);
       // check if this product is made or not for this brand
       boolean isExisted= this.productRepository.existsProductByBrand_User_ExternalUserId(request.productNameEn(),
               brandId);
       if(isExisted){
           throw new IllegalArgumentException("Product already exists for this brand add another product");
       }
       // first fetch the category
       Category category=categoryRepository.findCategoryByIdAndBrand_User_ExternalUserId(
               request.categoryId(),brandId
       ).orElseThrow(()-> new IllegalArgumentException("""
               category You Requested Not Present For Your Brand Please Add It First And Try Again
               """));
       // second manage image and thumbnail
       UploadResponse image= imageService.uploadImage(request.thumbnail());

       // create product entity
       Product product=new Product();
       product.setThumbnail(image.imageUrl());
       product.setPublicId(image.publicId());
       product.setProductDescriptionAr(request.productDescriptionAr());
       product.setProductDescriptionEn(request.productDescriptionEn());
       product.setProductNameAr(request.productNameAr());
       product.setProductNameEn(request.productNameEn());
       product.setPrice(request.productPrice());
       product.setCategory(category);
       product.setBrand(category.getBrand());

       return toResponse(product);

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
        if(request.productNameEn()==null || request.productNameEn().isBlank()){
            throw new IllegalArgumentException("Product name en is required");
        }
        if(request.productNameAr()==null || request.productNameAr().isBlank()){
            throw new IllegalArgumentException("Product name ar is required");
        }
        if(request.productDescriptionEn()==null || request.productDescriptionEn().isBlank()){
            throw new IllegalArgumentException("Description en is required");
        }
        if(request.productDescriptionAr()==null || request.productDescriptionAr().isBlank()){
            throw new IllegalArgumentException("Description ar is required");
        }
        if(request.productPrice()==null|| request.productPrice().doubleValue()<=0){
            throw new IllegalArgumentException("Price is required above 0");
        }
        if(request.categoryId()==null ){
            throw new IllegalArgumentException("Category id is required");
        }
        if(request.thumbnail()==null){
            throw new IllegalArgumentException("Image is required");
        }
   }



}
