package org.stylehub.backend.e_commerce.customer.rating.product_rating.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.customer.dto.rating.ProductRatingCreation;
import org.stylehub.backend.e_commerce.customer.dto.rating.ProductRatingCreationResponse;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.entity.ProductRating;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.repository.ProductRatingRepository;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.entity.ProductRatingSummary;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.service.ProductRatingSummaryService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductRatingService {

    private final CurrentUserProvider  currentUserProvider;
    private  final ProductRatingRepository productRatingRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductRatingService.class);
    private final CustomerProfileService customerProfileService;
    private final ProductService productService;
    private final ProductRatingSummaryService  productRatingSummaryService;

    @Transactional
    public ProductRatingCreationResponse upsertNewRate(String brandId, UUID productId, ProductRatingCreation productRatingCreation) {
        LOGGER.info("Upserting product rating for product id={}",productId);
        Integer stars= productRatingCreation.stars();
        if(stars==null || stars< 1 || stars > 5) throw  new   IllegalArgumentException("stars must be between 1 and 5");

        // find customer profile
        CustomerProfile customer=this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());
        LOGGER.info("Customer customerName={} Trying to rate product",customer.getFirstName()+" "+customer.getLastName());

        // find product by product Id that will customer rate for certain brand
        Product product= this.productService.findProductForBrand(productId,brandId);
        LOGGER.info("product you trying to rate productName={}",product.getProductNameEn());

        // check if customer rate this product before or not
        var isExistedRate=this.productRatingRepository.findProductRatingByCustomer_IdAndProduct_Id(customer.getId(),productId);
        if(isExistedRate.isPresent()){
            LOGGER.info("product rating already exists for product id={} we start to update it",productId);
            ProductRating existedProductRating=isExistedRate.get();
            String prevStars=existedProductRating.getStars().toString();
            String inComingStar=null;
            String prevComment=existedProductRating.getComment();
            String inComingComment=null;

            if(productRatingCreation.stars()!=null){
                existedProductRating.setStars(productRatingCreation.stars());
                inComingStar=productRatingCreation.stars().toString();
            }
            if(!productRatingCreation.comment().isEmpty()){
                existedProductRating.setComment(productRatingCreation.comment());
                inComingComment=productRatingCreation.comment();
            }
            ProductRating saved=this.productRatingRepository.save(existedProductRating);

            if(inComingStar!=null){
                LOGGER.info("prev rateP={} and exists rateE ",prevStars,inComingStar);
            }
            if(inComingComment!=null){
                LOGGER.info("prev commentP={} and exists commentE ",prevComment,inComingComment);
            }
            //now we will call the product rating summary
            LOGGER.info("updating the product rating summary for rate={}",saved.getStars());

            this.productRatingSummaryService.recalculateProductRatingSummaryAsync(product);

            return new ProductRatingCreationResponse(
                    product.getProductNameEn(),
                    product.getProductNameAr(),
                    customer.getFirstName()+" "+customer.getLastName(),
                    currentUserProvider.externalId(),
                    saved.getStars()
            );
        }
        LOGGER.info("creating new rate for product={} ",product.getProductNameEn());
        ProductRating newProductRating=new ProductRating();
        newProductRating.setStars(stars);
        newProductRating.setComment(productRatingCreation.comment());
        newProductRating.setProduct(product);
        newProductRating.setCustomer(customer);
        ProductRating savedProductRating=this.productRatingRepository.save(newProductRating);
        this.productRatingSummaryService.recalculateProductRatingSummaryAsync(product);

        // now we will calculate the rating
        return new ProductRatingCreationResponse(
                product.getProductNameEn(),
                product.getProductNameAr(),
                customer.getFirstName()+" "+customer.getLastName(),
                currentUserProvider.externalId(),
                savedProductRating.getStars()
        );
    }
}
