package org.stylehub.backend.e_commerce.customer.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.customer.dto.product.ColorDetailsDto;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductsResponse;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductDetailsDto;
import org.stylehub.backend.e_commerce.customer.dto.product.VariantsDetailsDto;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductFilterRequest;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.service.ProductRatingSummaryService;
import org.stylehub.backend.e_commerce.customer.repository.CustomerProductRepository;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.media.ProductColorImagesRepo;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductColorImages;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.service.ProductColorService;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.service.ProductVariantService;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerProductService {

    private final ProductService productService;
    private final ProductColorService productColorService;
    private final ProductVariantService productVariantService;
    private final ProductRatingSummaryService productRatingSummaryService;
    private final ProductColorImagesRepo  productColorImagesRepo;
    private final CustomerProductRepository customerProductRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerProductService.class);

    public PageResponse<FindAllProductsResponse> findAllBrandProducts(FindAllProductFilterRequest filter, Pageable pageable,String brandId) {
        return this.customerProductRepository.findAllProductsFilter(filter, pageable,brandId);
    }

    public ProductDetailsDto findProductDetails(String brandId, UUID productId) {
        LOGGER.info("starting to fetch product details={}", productId);
        Product product = this.productService.findProductById(productId, brandId);

        List<ProductColor> colors =
                productColorService.findAllProductColorsByProductIdAndBrandId(productId, brandId);

        List<ProductColorImages> allImages =
                productColorImagesRepo.findAllByProductColor_Id(productId);

        Map<UUID, List<String>> imagesByColorId =
                allImages.stream()
                        .collect(Collectors.groupingBy(
                                image -> image.getProductColor().getId(),
                                Collectors.mapping(
                                        ProductColorImages::getImageUrl,
                                        Collectors.toList()
                                )
                        ));

        List<ProductVariant> allVariants =
                productVariantService.findAllByProductId(productId);

        Map<UUID, List<ProductVariant>> variantsByColorId =
                allVariants.stream()
                        .collect(Collectors.groupingBy(
                                variant -> variant.getProductColor().getId()
                        ));

        List<ColorDetailsDto> colorDetails =
                colors.stream()
                        .map(color -> {
                            List<String> colorImages =
                                    imagesByColorId.getOrDefault(color.getId(), List.of());

                            List<VariantsDetailsDto> variants =
                                    variantsByColorId
                                            .getOrDefault(color.getId(), List.of())
                                            .stream()
                                            .map(variant -> {
                                                return new VariantsDetailsDto(
                                                        variant.getId(),
                                                        variant.getSize(),
                                                        variant.getStock(),
                                                        variant.getSku(),
                                                        variant.getPriceOverride()
                                                );
                                            })
                                            .toList();

                            return new ColorDetailsDto(
                                    color.getId(),
                                    color.getColorCode(),
                                    colorImages,
                                    variants
                            );
                        })
                        .toList();

        String thumbnail = colorDetails.stream()
                .flatMap(color -> color.colorImages().stream())
                .findFirst()
                .orElse(null);

        BigDecimal avgRating =this.productRatingSummaryService.getAvgRatingOfProduct(productId);
        return new ProductDetailsDto(
                thumbnail,
                product.getProductNameEn(),
                product.getProductNameAr(),
                product.getProductDescriptionEn(),
                product.getProductDescriptionAr(),
                colorDetails,
                avgRating
        );
    }



}

