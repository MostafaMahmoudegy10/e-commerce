package org.stylehub.backend.e_commerce.product.color.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.ProductColorCreationRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.ProductColorDeleteResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.BrandProductColorViewResponse;
import org.stylehub.backend.e_commerce.platform.media.ProductColorImagesRepo;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductColorImages;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.repository.ProductColorRepository;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class ProductColorService {

    private final ProductService  productService;
    private final CurrentUserProvider currentUserProvider;
    private final ProductColorRepository productColorRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductColorService.class);
    private final ImageService imageService;
    private final ProductColorImagesRepo productColorImagesRepo;
    private final ProductVariantRepository productVariantRepository;


    @Transactional
    public Object upsertNewProductColor(ProductColorCreationRequest productColorCreationRequest,UUID productId) {
        LOGGER.info("Starting Process of adding new product color to productId={}", productId);

        validateProductColorCreationRequest(productColorCreationRequest);
        Product product= this.productService.findProductForBrand(productId,currentUserProvider.externalId());
        LOGGER.info("The ProductId={} you want to add colors to it is exists in the brandId={}", productId,currentUserProvider.externalId());

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

    @Transactional
    public ProductColorDeleteResponse deleteProductColor(UUID productId, UUID colorId) {
        String brandExternalUserId = currentUserProvider.externalId();
        LOGGER.info("Color deletion started for productId={}, colorId={}, brand={}", productId, colorId, brandExternalUserId);

        productService.findProductById(productId, brandExternalUserId);
        ProductColor productColor = findProductColorByIdAndProductIdAndBrandExternalUserId(
                colorId,
                productId,
                brandExternalUserId
        );

        List<ProductColorImages> colorImages = productColorImagesRepo.findAllByProductColor_Id(colorId);
        List<String> publicIds = colorImages.stream().map(ProductColorImages::getPublicId).toList();

        int variantCount = productVariantRepository.findAllByProductColor_Id(colorId).size();
        productVariantRepository.deleteAllByProductColor_Id(colorId);
        LOGGER.info("Deleted variants for colorId={}, count={}", colorId, variantCount);

        productColorImagesRepo.deleteAllByProductColor_Id(colorId);
        LOGGER.info("Deleted image records from DB for colorId={}, count={}", colorId, colorImages.size());

        productColorRepository.delete(productColor);

        if (TransactionSynchronizationManager.isSynchronizationActive() && !publicIds.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    imageService.deleteImagesAsync(publicIds);
                    LOGGER.info("Cloudinary image deletion scheduled after commit for colorId={}, count={}", colorId, publicIds.size());
                }
            });
        }

        return new ProductColorDeleteResponse("Product color deleted successfully", productId, colorId);
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

    protected void replaceProductColorImages(
            ProductColor productColor,
            List<MultipartFile> multipartFiles
    ) {
        List<ProductColorImages> oldImages =
                productColorImagesRepo.findAllByProductColor_Id(productColor.getId());

        List<ProductColorImages> newImages = saveProductColorImages(productColor, multipartFiles);

        if (oldImages.isEmpty()) {
            LOGGER.info("No old images found to replace for colorCode={}", productColor.getColorCode());
            return;
        }

        productColorImagesRepo.deleteAll(oldImages);
        LOGGER.info(
                "Old image records replaced for colorCode={}, oldCount={}, newCount={}",
                productColor.getColorCode(),
                oldImages.size(),
                newImages.size()
        );

        scheduleImageDeletionAfterCommit(
                oldImages.stream()
                        .map(ProductColorImages::getPublicId)
                        .toList(),
                "colorCode=" + productColor.getColorCode()
        );
    }

    private List<ProductColorImages> saveProductColorImages(
            ProductColor productColor,
            List<MultipartFile> multipartFiles
    ) {
        List<UploadResult> uploadResults = uploadImagesInParallel(multipartFiles);

        List<ProductColorImages> colorImages = uploadResults.stream()
                .map(UploadResult::response)
                .map(uploadResponse -> toProductColorImage(productColor, uploadResponse))
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

    private List<UploadResult> uploadImagesInParallel(List<MultipartFile> multipartFiles) {
        List<CompletableFuture<UploadResult>> uploadFutures = multipartFiles.stream()
                .map(file -> imageService.uploadImageAsync(file)
                        .handle((response, throwable) -> new UploadResult(response, throwable)))
                .toList();

        CompletableFuture.allOf(uploadFutures.toArray(CompletableFuture[]::new)).join();

        List<UploadResult> uploadResults = uploadFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<String> uploadedPublicIds = uploadResults.stream()
                .map(UploadResult::response)
                .filter(java.util.Objects::nonNull)
                .map(uploadResponse -> uploadResponse.publicId())
                .toList();

        Optional<Throwable> firstFailure = uploadResults.stream()
                .map(UploadResult::throwable)
                .filter(java.util.Objects::nonNull)
                .findFirst();

        if (firstFailure.isPresent()) {
            imageService.deleteImagesAsync(uploadedPublicIds).join();
            throw new IllegalArgumentException("Failed to upload one or more product color images", firstFailure.get());
        }

        return uploadResults;
    }

    private ProductColorImages toProductColorImage(ProductColor productColor, org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse uploadResponse) {
        ProductColorImages colorImage = new ProductColorImages();
        colorImage.setProductColor(productColor);
        colorImage.setImageUrl(uploadResponse.imageUrl());
        colorImage.setPublicId(uploadResponse.publicId());
        return colorImage;
    }

    private void scheduleImageDeletionAfterCommit(List<String> publicIds, String context) {
        if (publicIds.isEmpty()) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    imageService.deleteImagesAsync(publicIds);
                    LOGGER.info("Scheduled async image deletion after commit for {}, count={}", context, publicIds.size());
                }
            });
            return;
        }

        imageService.deleteImagesAsync(publicIds);
        LOGGER.info("Scheduled async image deletion without transaction sync for {}, count={}", context, publicIds.size());
    }

    private record UploadResult(org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse response, Throwable throwable) {
    }


    public List<ProductColor> findAllProductColorsByProductIdAndBrandId(UUID productId, String brandId) {
        return this.productColorRepository.findAllByProduct_IdAndProduct_Brand_User_ExternalUserId(productId,brandId);

    }

    public List<String> findAllColorImagesByColorId(UUID id) {
        return this.productColorImagesRepo.findImageUrlsByProductColor_Id(id);
    }

    public List<BrandProductColorViewResponse> findBrandProductColors(UUID productId) {
        String brandExternalUserId = currentUserProvider.externalId();
        this.productService.findProductById(productId, brandExternalUserId);

        return this.findAllProductColorsByProductIdAndBrandId(productId, brandExternalUserId).stream()
                .sorted(Comparator.comparing(ProductColor::getColorCode, String.CASE_INSENSITIVE_ORDER))
                .map(color -> {
                    List<org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant> variants =
                            this.productVariantRepository.findAllByProductColor_Id(color.getId());
                    long totalStock = variants.stream()
                            .map(org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant::getStock)
                            .filter(java.util.Objects::nonNull)
                            .mapToLong(Integer::longValue)
                            .sum();

                    return new BrandProductColorViewResponse(
                            color.getId(),
                            color.getColorCode(),
                            this.findAllColorImagesByColorId(color.getId()),
                            (long) variants.size(),
                            totalStock
                    );
                })
                .toList();
    }
}
