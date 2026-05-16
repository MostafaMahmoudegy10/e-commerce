package org.stylehub.backend.e_commerce.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.customer.dto.cart.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.dto.cart.CartItemViewResponse;
import org.stylehub.backend.e_commerce.customer.dto.cart.UpdateCartItemQuantityRequest;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;
import org.stylehub.backend.e_commerce.platform.mail.publisher.EmailEventPublisher;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


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
        validateAddToCartQuantity(request.quantity());
        // Now i will get customer profile based on currentUserProvider
       CustomerProfile customerProfile= customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());

        // we will get product variant with this brand Id
        ProductVariant variant= this.productVariantRepository.findProductVariantByIdAndBrandId(request.productVariantId(),brandId)
                .orElseThrow(()->new IllegalArgumentException("Product variant not found for this brand please add product to this brand"));

        // find brand that i will add the variant from
        Brand brand =this.brandService.findBrandByExternalId(brandId);

        // now we will search for an exists active cart for this customer to this brand
        Cart cart = findOrCreateActiveCart(customerProfile, brand);
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

    public PageResponse<CartItemViewResponse> viewCart(String brandExternalId, Pageable pageable){
        // find customer profile
        CustomerProfile customer= this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());

        // find related brand
        Brand brand = this.brandService.findBrandByExternalId(brandExternalId);

        // first we get the cart by status-> active , cust id , brand id
        Cart cart = findOrCreateActiveCart(customer, brand);
        Page<CartItemViewResponse> response= this.cartItemRepository.findCartViewResponseByCart_Id(cart.getId(),pageable);

        return new PageResponse<CartItemViewResponse>(
                    response.getContent(),
                    response.getNumber(),
                    response.getSize(),
                    response.getTotalElements(),
                    response.getTotalPages(),
                    response.hasNext(),
                    response.hasPrevious()
                );
    }

    @Transactional
    public Map<String, Integer> changeCartItemQuantity(
            String brandId,
            UUID cartId,
            UUID cartItemId,
            UpdateCartItemQuantityRequest request
    ) {
        validateCartItemQuantity(request.quantity());

        Cart cart = findActiveCartForBrand(brandId);
        validateRequestedCart(cart, cartId);

        CartItem cartItem = findCartItem(cartId, cartItemId);
        if (request.quantity() == 0) {
            this.cartItemRepository.delete(cartItem);
            return Map.of("quantity", 0);
        }

        ProductVariant variant = cartItem.getProductVariant();
        InsufficientStockRequestedEvent event = new InsufficientStockRequestedEvent(
                cart.getBrand().getBrandName(),
                cart.getBrand().getBrandEmail(),
                variant.getProductColor().getProduct().getProductNameEn(),
                variant.getSku(),
                request.quantity(),
                variant.getStock(),
                cart.getCustomer().getUsername(),
                Instant.now()
        );
        stockAvailable(request.quantity(), variant.getSku(), variant.getStock(), event);

        cartItem.setQuantity(request.quantity());
        CartItem updatedCartItem = this.cartItemRepository.save(cartItem);
        return Map.of("quantity", updatedCartItem.getQuantity());
    }

    @Transactional
    public String removeFromCart(String brandId, UUID cartId, UUID cartItemId) {
        Cart cart = findActiveCartForBrand(brandId);
        validateRequestedCart(cart, cartId);

        CartItem cartItem = findCartItem(cartId, cartItemId);
        this.cartItemRepository.delete(cartItem);
        return "cart item deleted successfully";
    }

    public boolean stockAvailable(Integer requestedQuantity,String sku, Integer stock, InsufficientStockRequestedEvent eventFirstStock) {
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

    private void validateAddToCartQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }
    }

    private void validateCartItemQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity should be greater than or equal to 0");
        }
    }

    private Cart findOrCreateActiveCart(CustomerProfile customerProfile, Brand brand) {
        Optional<Cart> cartExisted = this.cartRepository.findCartByCartStatusAndCustomer_IdAndBrand_Id(
                CartStatus.ACTIVE,
                customerProfile.getId(),
                brand.getId()
        );

        return cartExisted.orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCartStatus(CartStatus.ACTIVE);
            newCart.setBrand(brand);
            newCart.setCustomer(customerProfile);
            return this.cartRepository.save(newCart);
        });
    }

    private Cart findActiveCartForBrand(String brandExternalId) {
        CustomerProfile customer = this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());
        Brand brand = this.brandService.findBrandByExternalId(brandExternalId);

        return this.cartRepository.findCartByCartStatusAndCustomer_IdAndBrand_Id(
                        CartStatus.ACTIVE,
                        customer.getId(),
                        brand.getId()
                )
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found"));
    }

    private void validateRequestedCart(Cart cart, UUID requestedCartId) {
        if (!cart.getId().equals(requestedCartId)) {
            throw new IllegalArgumentException("Cart not found for this brand");
        }
    }

    private CartItem findCartItem(UUID cartId, UUID cartItemId) {
        return this.cartItemRepository.findByIdAndCart_Id(cartItemId, cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
    }


}
