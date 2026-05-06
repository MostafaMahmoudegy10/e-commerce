package org.stylehub.backend.e_commerce.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.customer.dto.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;
import org.stylehub.backend.e_commerce.platform.mail.publisher.EmailEventPublisher;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomerCartService {

    private final CurrentUserProvider  currentUserProvider;
    private final CustomerProfileService customerProfileService;
    private final BrandService brandService;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository  cartRepository;
    private final CartItemRepository cartItemRepository;
    private final EmailEventPublisher  emailEventPublisher;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCartService.class);


    @Transactional
    public Map<String,Integer> upsertToCart(String brandId, AddToCartRequest request) {
        LOGGER.info("Customer customer={} cart for brandId={}", currentUserProvider.getEmail(),brandId);
        // validate the quantity of how many for one variant
        if(request.quantity()==null||request.quantity()<0){
            throw  new IllegalArgumentException("Quantity should be greater than 0");
        }
        // Now i will get customer profile based on currentUserProvider
       CustomerProfile customerProfile= customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());

        // we will get product variant with this brand Id
        ProductVariant variant= this.productVariantRepository.findProductVariantByIdAndBrandId(request.productVariantId(),brandId)
                .orElseThrow(()->new IllegalArgumentException("Product variant not found for this brand please add product to this brand"));

        // find brand that i will add the variant from
        Brand brand =this.brandService.findBrandByExternalId(brandId);

        // now we will search for an exists active cart for this customer to this brand
       Optional<Cart> cartExisted=cartRepository.findCartByCartStatusAndCustomer_IdAndBrand_Id
               (CartStatus.ACTIVE,customerProfile.getId(),brand.getId());

        Cart cart = cartExisted.orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCartStatus(CartStatus.ACTIVE);
            newCart.setBrand(brand);
            newCart.setCustomer(customerProfile);

            return cartRepository.save(newCart);
        });
        InsufficientStockRequestedEvent eventFirstStock= new
                InsufficientStockRequestedEvent(
                        brand.getBrandName(),
                        brand.getBrandEmail(),
                        variant.getProductColor().getProduct().getProductNameEn(),
                        variant.getSku(),
                        request.quantity(),
                        variant.getStock(),
                        customerProfile.getUsername(),
                        Instant.now()
        );
        // check for requested quantity not bigger than stock
        stockAvailable(request.quantity(),variant.getSku(),variant.getStock(),eventFirstStock);

        // now we will find is there cart item for this variant
        Optional<CartItem>cartItemExists =this.cartItemRepository.findCartItemByCart_IdAndProductVariant_Id(cart.getId(),request.productVariantId());
        if(cartItemExists.isPresent()){
            var existsCartItem=cartItemExists.get();

            //update quantity if stock are available
            InsufficientStockRequestedEvent eventUpdateCartItem= new
                    InsufficientStockRequestedEvent(
                    brand.getBrandName(),
                    brand.getBrandEmail(),
                    variant.getProductColor().getProduct().getProductNameEn(),
                    variant.getSku(),
                    request.quantity()+existsCartItem.getQuantity(),
                    variant.getStock(),
                    customerProfile.getUsername(),
                    Instant.now()
            );
            var updatedCartItem= updateCartItemQuantity(existsCartItem,request.quantity(),variant.getStock(),variant.getSku(),eventUpdateCartItem);
            return Map.of("updated quantity of "+variant.getSku(),updatedCartItem.getQuantity());
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setQuantity(request.quantity());
        newCartItem.setCart(cart);
        newCartItem.setProductVariant(variant);
        newCartItem.setPrice(variant.getEffectivePrice());
        var savedCartItem= cartItemRepository.save(newCartItem);
        return Map.of("Saved quantity of "+variant.getSku(),savedCartItem.getQuantity());
    }

    private boolean stockAvailable(Integer requestedQuantity,String sku, Integer stock, InsufficientStockRequestedEvent eventFirstStock) {
        if(requestedQuantity>stock){
            this.emailEventPublisher.publishInsufficientStockRequested(eventFirstStock);
            throw new IllegalArgumentException("requestedQuantity exceeds the Stock what available now for "+sku+ " is "+stock);
        }
        return true;
    }

    private CartItem updateCartItemQuantity(CartItem cartItem, Integer newRequested, Integer availableStock, String sku, InsufficientStockRequestedEvent eventUpdateCartItem) {
        // we calculated the cart item sum of this variant and the new requested number
        var sum=cartItem.getQuantity()+newRequested;
        if(sum>availableStock) {
            this.emailEventPublisher.publishInsufficientStockRequested(eventUpdateCartItem);
            throw new IllegalArgumentException("requestedQuantity exceeds the Stock what available now for "+sku+ " is "+availableStock);
        }
        cartItem.setQuantity(sum);
        return cartItemRepository.save(cartItem);
    }
}
