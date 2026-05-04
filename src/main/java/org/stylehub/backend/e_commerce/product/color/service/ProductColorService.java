package org.stylehub.backend.e_commerce.product.color.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.ProductColorCreationRequest;
import org.stylehub.backend.e_commerce.platform.media.ProductColorImagesRepo;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductColorImages;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.repository.ProductColorRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductColorService {

    private final ProductService  productService;
    private final CurrentUserProvider currentUserProvider;
    private final ProductColorRepository productColorRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductColorService.class);
    private final ImageService imageService;
    private final ProductColorImagesRepo productColorImagesRepo;


    @Transactional
    public Object upsertNewProductColor(ProductColorCreationRequest productColorCreationRequest,UUID productId) {
        LOGGER.info("Starting Process of adding new product color to productId={}", productId);

        // validate the request dto
        validateProductColorCreationRequest(productColorCreationRequest);

        //check product is existing for this brand or not
        Product product= this.productService.findProductForBrand(productId,currentUserProvider.externalId());
        LOGGER.info("The ProductId={} you want to add colors to it is exists in the brandId={}", productId,currentUserProvider.externalId());

        //check if this color for this product is exits before or not
        Optional<ProductColor> optionalProductColor =this.productColorRepository.findProductColorByIdAndColorCode(productId,productColorCreationRequest.colorCode());
        if (optionalProductColor.isPresent()){
            LOGGER.info("the color is exits for the productId={} now we update it with the new info..", productId);
            ProductColor productColor=optionalProductColor.get();
            productColor.setColorCode(productColorCreationRequest.colorCode());
            replaceProductColorImages(productColor,productColorCreationRequest.colorImages());
           var savedProductColor = this.productColorRepository.save(productColor);
            return new Object[]{
                    "productId : " + productId,
                    "productColorId : " + savedProductColor.getId(),
                    "productColorCode : " +savedProductColor.getColorCode(),
            };
        }
        ProductColor productColor=new ProductColor();
        productColor.setColorCode(productColorCreationRequest.colorCode());
        productColor.setProduct(product);
        var savedProductColor=this.productColorRepository.save(productColor);
        this.saveProductColorImages(savedProductColor,productColorCreationRequest.colorImages());

        return Map.of(
                "productId",productId,
                "productColorId",savedProductColor.getId(),
                "productColorCode",savedProductColor.getColorCode()
                );

    }


    public ProductColor findProductColorByIdAndProductIdAndBrandExternalUserId(UUID id, UUID productId, String brandUserExternalUserId) {
        return this.productColorRepository.findProductColorByIdAndProduct_IdAndProduct_Brand_User_ExternalUserId(id, productId, brandUserExternalUserId)
                .orElseThrow(()-> new IllegalArgumentException(
                        String.format(
                                "ProductColor not found with id=%s for productId=%s and brandUser=%s",
                                id, productId, brandUserExternalUserId
                        )
                ));
    }

    private void validateProductColorCreationRequest(ProductColorCreationRequest productColorCreationRequest) {
        if(productColorCreationRequest.colorCode()==null || productColorCreationRequest.colorCode().isBlank()){
            throw new IllegalArgumentException("colorCode is required");
        }
        if(productColorCreationRequest.colorImages().isEmpty()){
            throw new IllegalArgumentException("colorImages is required");
        }
    }

    private void replaceProductColorImages(
            ProductColor productColor,
            List<MultipartFile> multipartFiles
    ) {
        deleteProductColorImages(productColor);
        saveProductColorImages(productColor, multipartFiles);
    }

    private void deleteProductColorImages(ProductColor productColor) {
        List<ProductColorImages> productColorImages =
                productColorImagesRepo.findAllByProductColor_Id(productColor.getId());

        if (productColorImages.isEmpty()) {
            LOGGER.info("No images found for colorCode={}", productColor.getColorCode());
            return;
        }

        productColorImagesRepo.deleteAll(productColorImages);

        LOGGER.info(
                "All old images deleted from database for colorCode={}",
                productColor.getColorCode()
        );

        productColorImages.forEach(productColorImage -> {
            imageService.deleteImage(productColorImage.getPublicId());

            LOGGER.info(
                    "Image deleted from cloud for colorCode={}, publicId={}",
                    productColor.getColorCode(),
                    productColorImage.getPublicId()
            );
        });


    }

    private List<ProductColorImages> saveProductColorImages(
            ProductColor productColor,
            List<MultipartFile> multipartFiles
    ) {

        List<ProductColorImages> colorImages = multipartFiles.stream()
                .map(imageService::uploadImage)
                .map(uploadResponse -> {
                    ProductColorImages colorImage = new ProductColorImages();
                    colorImage.setProductColor(productColor);
                    colorImage.setImageUrl(uploadResponse.imageUrl());
                    colorImage.setPublicId(uploadResponse.publicId());
                    return colorImage;
                })
                .toList();

        List<ProductColorImages> savedImages =
                productColorImagesRepo.saveAll(colorImages);

        LOGGER.info(
                "New images added to colorCode={}, urls={}",
                productColor.getColorCode(),
                savedImages.stream()
                        .map(ProductColorImages::getImageUrl)
                        .toList()
        );

        return savedImages;
    }


}
