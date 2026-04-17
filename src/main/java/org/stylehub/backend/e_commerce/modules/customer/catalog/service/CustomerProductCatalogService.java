package org.stylehub.backend.e_commerce.modules.customer.catalog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.*;
import org.stylehub.backend.e_commerce.modules.customer.history.entity.RecentlyViewedProduct;
import org.stylehub.backend.e_commerce.modules.customer.history.repository.RecentlyViewedProductRepository;
import org.stylehub.backend.e_commerce.modules.customer.review.entity.ProductReview;
import org.stylehub.backend.e_commerce.modules.customer.review.repository.ProductReviewRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.product_item.repository.ProductItemRepository;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerProductCatalogService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final ProductReviewRepository productReviewRepository;
    private final RecentlyViewedProductRepository recentlyViewedProductRepository;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> findProducts(
            UUID brandId,
            UUID categoryId,
            String queryText,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            String sortBy,
            String sortDirection,
            Pageable pageable
    ) {
        Page<Product> productPage = this.productRepository.findAllForCustomer(
                brandId,
                categoryId,
                normalizeQuery(queryText),
                minPrice,
                maxPrice,
                minRating,
                pageable
        );
        List<ProductSummaryResponse> data = productPage.getContent().stream()
                .map(this::toSummaryResponse)
                .toList();
        data = sortSummaries(data, sortBy, sortDirection);

        return Map.of(
                "data", data,
                "totalElements", productPage.getTotalElements(),
                "page", productPage.getNumber(),
                "size", productPage.getSize(),
                "totalPages", productPage.getTotalPages()
        );
    }

    @Transactional
    public ProductDetailsResponse findProductDetails(UUID productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (Boolean.FALSE.equals(product.getIsActive()) || Boolean.TRUE.equals(product.getIsArchived())) {
            throw new IllegalArgumentException("Product not available");
        }

        trackRecentlyViewed(product);

        List<ProductItem> variants = this.productItemRepository.findAllByProduct_Id(productId);
        List<ProductReview> reviews = this.productReviewRepository.findAllByProduct_IdOrderByCreatedAtDesc(productId);

        return new ProductDetailsResponse(
                product.getId(),
                product.getBrand().getId(),
                product.getBrand().getBrandName(),
                normalizeAverage(this.productReviewRepository.findAverageRatingByBrandId(product.getBrand().getId())),
                product.getCategory().getId(),
                product.getCategory().getCategoryNameEn(),
                product.getProductNameEn(),
                product.getProductNameAr(),
                product.getProductDescriptionEn(),
                product.getProductDescriptionAr(),
                product.getThumbnail(),
                product.getPrice(),
                normalizeAverage(this.productReviewRepository.findAverageRatingByProductId(productId)),
                this.productReviewRepository.countByProduct_Id(productId),
                variants.stream().map(this::toVariantResponse).toList(),
                reviews.stream().map(this::toReviewResponse).toList()
        );
    }

    @Transactional
    public ProductRecommendationsResponse findRecommendations() {
        List<ProductSummaryResponse> recentlyViewed = this.recentlyViewedProductRepository
                .findTop10ByUser_ExternalUserIdOrderByViewedAtDesc(currentUserProvider.externalId())
                .stream()
                .map(RecentlyViewedProduct::getProduct)
                .filter(product -> Boolean.TRUE.equals(product.getIsActive()) && Boolean.FALSE.equals(product.getIsArchived()))
                .map(this::toSummaryResponse)
                .toList();

        UUID preferredCategoryId = this.recentlyViewedProductRepository
                .findTop10ByUser_ExternalUserIdOrderByViewedAtDesc(currentUserProvider.externalId())
                .stream()
                .findFirst()
                .map(item -> item.getProduct().getCategory().getId())
                .orElse(null);

        List<Product> baseRecommendations = preferredCategoryId == null
                ? this.productRepository.findTop20ByIsActiveTrueAndIsArchivedFalseOrderByIdDesc()
                : this.productRepository.findTop20ByCategory_IdAndIsActiveTrueAndIsArchivedFalseOrderByIdDesc(preferredCategoryId);

        List<ProductSummaryResponse> recommendations = baseRecommendations.stream()
                .map(this::toSummaryResponse)
                .sorted(Comparator.comparing(ProductSummaryResponse::productAverageRating).reversed())
                .limit(10)
                .toList();

        return new ProductRecommendationsResponse(recommendations, recentlyViewed);
    }

    private ProductSummaryResponse toSummaryResponse(Product product) {
        UUID productId = product.getId();
        UUID brandId = product.getBrand().getId();
        return new ProductSummaryResponse(
                productId,
                brandId,
                product.getBrand().getBrandName(),
                product.getCategory().getId(),
                product.getCategory().getCategoryNameEn(),
                product.getProductNameEn(),
                product.getProductNameAr(),
                product.getThumbnail(),
                product.getPrice(),
                normalizeAverage(this.productReviewRepository.findAverageRatingByProductId(productId)),
                normalizeAverage(this.productReviewRepository.findAverageRatingByBrandId(brandId)),
                this.productReviewRepository.countByProduct_Id(productId)
        );
    }

    private ProductVariantResponse toVariantResponse(ProductItem productItem) {
        List<String> imageUrls = productItem.getProductItemImages() == null
                ? Collections.emptyList()
                : productItem.getProductItemImages().stream().map(ProductItemImage::getImageUrl).toList();

        List<ProductVariantSizeResponse> sizes = productItem.getSizeList() == null
                ? Collections.emptyList()
                : productItem.getSizeList().stream()
                .map(this::toSizeResponse)
                .toList();

        return new ProductVariantResponse(
                productItem.getId(),
                productItem.getColor(),
                productItem.getSku(),
                imageUrls,
                sizes
        );
    }

    private ProductVariantSizeResponse toSizeResponse(Size size) {
        return new ProductVariantSizeResponse(size.getSizeName(), size.getStock());
    }

    private ProductReviewResponse toReviewResponse(ProductReview review) {
        return new ProductReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getEmail(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    private double normalizeAverage(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.round(value * 100.0) / 100.0;
    }

    private String normalizeQuery(String queryText) {
        if (queryText == null || queryText.isBlank()) {
            return null;
        }
        return queryText.trim();
    }

    private List<ProductSummaryResponse> sortSummaries(
            List<ProductSummaryResponse> summaries,
            String sortBy,
            String sortDirection
    ) {
        if (sortBy == null || sortBy.isBlank()) {
            return summaries;
        }

        Comparator<ProductSummaryResponse> comparator = switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparing(ProductSummaryResponse::price);
            case "rating" -> Comparator.comparing(ProductSummaryResponse::productAverageRating);
            case "name" -> Comparator.comparing(ProductSummaryResponse::productNameEn, String.CASE_INSENSITIVE_ORDER);
            case "newest" -> Comparator.comparing(ProductSummaryResponse::productId);
            case "relevance" -> Comparator.comparing(ProductSummaryResponse::productAverageRating)
                    .thenComparing(ProductSummaryResponse::reviewCount);
            default -> Comparator.comparing(ProductSummaryResponse::productId);
        };

        if ("desc".equalsIgnoreCase(sortDirection) || "relevance".equalsIgnoreCase(sortBy)) {
            comparator = comparator.reversed();
        }

        return summaries.stream().sorted(comparator).toList();
    }

    private void trackRecentlyViewed(Product product) {
        User user = this.userRepository.findByExternalUserId(currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        RecentlyViewedProduct viewed = this.recentlyViewedProductRepository
                .findByUser_ExternalUserIdAndProduct_Id(currentUserProvider.externalId(), product.getId())
                .orElseGet(RecentlyViewedProduct::new);
        viewed.setUser(user);
        viewed.setProduct(product);
        viewed.setViewedAt(Timestamp.valueOf(LocalDateTime.now()));
        this.recentlyViewedProductRepository.save(viewed);
    }
}
