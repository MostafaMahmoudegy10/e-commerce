package org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.repository.ProductRatingRepository;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.dto.CalculateSummaryDto;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.entity.ProductRatingSummary;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.repository.ProductRatingSummaryRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductRatingSummaryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProductRatingSummaryService.class);

    private final ProductRatingRepository productRatingRepository;
    private final ProductRatingSummaryRepository productRatingSummaryRepository;

    @Async
    @Transactional
    public void recalculateProductRatingSummaryAsync(Product product) {
        CalculateSummaryDto result =
                productRatingRepository.calculateSummary(product.getId());

        LOGGER.info(
                "New rating summary calculated for product={}, result={}",
                product.getProductNameEn(),
                result
        );

        ProductRatingSummary summary = productRatingSummaryRepository
                .findProductRatingSummariesByProduct_Id(product.getId())
                .orElseGet(() -> {
                    ProductRatingSummary newSummary = new ProductRatingSummary();
                    newSummary.setProduct(product);
                    return newSummary;
                });

        saveProductSummary(summary, result);
    }

    private ProductRatingSummary saveProductSummary(
            ProductRatingSummary productRatingSummary,
            CalculateSummaryDto result
    ) {
        productRatingSummary.setAvgRating(
                BigDecimal.valueOf(result.avgRating())
        );
        productRatingSummary.setRatingCount(
                Math.toIntExact(result.ratingCount())
        );
        productRatingSummary.setRating1Count(
                Math.toIntExact(result.stars1Count())
        );
        productRatingSummary.setRating2Count(
                Math.toIntExact(result.stars2Count())
        );
        productRatingSummary.setRating3Count(
                Math.toIntExact(result.stars3Count())
        );
        productRatingSummary.setRating4Count(
                Math.toIntExact(result.stars4Count())
        );
        productRatingSummary.setRating5Count(
                Math.toIntExact(result.stars5Count())
        );

        ProductRatingSummary savedProductSummary =
                productRatingSummaryRepository.save(productRatingSummary);

        LOGGER.info(
                "Saved rating summary for productId={}",
                savedProductSummary.getProduct().getId()
        );

        return savedProductSummary;
    }
}