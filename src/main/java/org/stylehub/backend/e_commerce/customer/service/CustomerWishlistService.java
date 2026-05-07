package org.stylehub.backend.e_commerce.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.favourite.dto.WishlistView;
import org.stylehub.backend.e_commerce.favourite.entity.Favourite;
import org.stylehub.backend.e_commerce.favourite.repository.FavouriteRepository;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerWishlistService {

    private final CurrentUserProvider currentUserProvider;
    private final FavouriteRepository favouriteRepository;
    private final CustomerProfileService customerProfileService;
    private final BrandService  brandService;
    private final ProductService productService;

    @Transactional
    public String addToWishlist(UUID productId, String brandExternalId) {
        // find customer profile
        CustomerProfile customerProfile=this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());
        // brand Profile
        Brand brand = this.brandService.findBrandByExternalId(brandExternalId);


        // find if this customer have a fav product to this product in this brand
        Optional<Favourite> isExists = this.favouriteRepository
                .findFavouriteByBrand_IdAndCustomer_IdAndProduct_Id(brand.getId(),customerProfile.getId(),productId);

        if(isExists.isPresent()){
            return "the product is already in the wishlist";
        }

        Product product=this.productService.findProductForBrand(productId,brandExternalId);
        Favourite favourite = new Favourite();
        favourite.setProduct(product);
        favourite.setCustomer(customerProfile);
        favourite.setBrand(brand);
        favouriteRepository.save(favourite);

        return "product added to wishlist successfully";
    }
    public PageResponse<WishlistView> viewWishlist(String brandExternalId, Pageable pageable){
        // find customer profile
        CustomerProfile customerProfile=this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());
        // brand Profile
        Brand brand = this.brandService.findBrandByExternalId(brandExternalId);

        Page<WishlistView> view=this.favouriteRepository.viewWishlistForCustomer_IdInBrand_Id(customerProfile.getId(),brand.getId(),pageable);

        return new PageResponse<WishlistView>(
                view.getContent(),
                view.getNumber(),
                view.getSize(),
                view.getTotalElements(),
                view.getTotalPages(),
                view.hasNext(),
                view.hasPrevious()
        );
    }

    @Transactional
    public String deleteProductFromWishlist(UUID productId, String brandExternalId) {
        // find customer profile
        CustomerProfile customerProfile=this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());
        // brand Profile
        Brand brand = this.brandService.findBrandByExternalId(brandExternalId);

        // FIND PRODUCT
        Product product=this.productService.findProductForBrand(productId,brandExternalId);

        this.favouriteRepository.deleteByBrand_IdAndProduct_IdAndCustomer_Id
                (brand.getId(),product.getId(),customerProfile.getId());

        return "Product Removed from wishlist successfully";
    }
}
