package org.stylehub.backend.e_commerce.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.internal.Target;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.customer.dto.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.dto.AddToCartResponse;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomerCartService {

    private final CurrentUserProvider  currentUserProvider;
    private final CustomerProfileService customerProfileService;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository  cartRepository;
    private final CartItemRepository cartItemRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCartService.class);


    @Transactional
    public  Integer upsertToCart(String brandId, AddToCartRequest request) {
        LOGGER.info("Customer customer={} cart for brandId={}", currentUserProvider.getEmail(),brandId);
        // validate the quantity of how many for one variant
        if(request.quantity()<0){
            throw  new IllegalArgumentException("Quantity should be greater than 0");
        }
        // Now i will get customer profile based on currentUserProvider
       CustomerProfile customerProfile= customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.getEmail());

        // now we will search for an exists active cart for this customer to this brand
       Optional<Cart> cartExisted=cartRepository.findCartByCartStatusAndCustomer_User_ExternalUserId(CartStatus.ACTIVE,currentUserProvider.externalId());

       // then it will append some cart items
        Cart cart=cartExisted.orElseGet(Cart::new);
        if(cart.getId()==null){
            cart.setCartStatus(CartStatus.ACTIVE);
            cart.setCustomer(customerProfile);
            cart=cartRepository.save(cart);
        }

        ProductVariant variantExists= productVariantRepository.findById(request.productVariantId())
                .orElseThrow(()->new IllegalArgumentException("Product variant not found"));

        // check if requested quantity is bigger than quantity
        if(request.quantity()>variantExists.getStock()){
            throw new IllegalStateException("Not enough stock ");
        }
        // check for existing cart item by cart id and variant id
        Optional<CartItem>existingCartItem=this.cartItemRepository.
                findCartItemByCart_IdAndProductVariant_Id(cart.getId(),variantExists.getId());

        // we now add new variant to this cart item
        CartItem cartItem=null;
        if (existingCartItem.isPresent()) {
            cartItem= existingCartItem.get();
          var newQuantity=cartItem.updateCartItemQuantity(request.quantity());
          if(newQuantity>variantExists.getStock()){
              throw new IllegalStateException("Not enough stock ");
          }
          cartItem.setQuantity(newQuantity);
          var savedCartItem = cartItemRepository.save(cartItem);
          return savedCartItem.getQuantity();
        }
        cartItem.setCart(cart);
        cartItem.setQuantity(request.quantity());
        cartItem.setPrice(variantExists.getPriceOverride());
        cartItem.setProductVariant(variantExists);
        var savedCartItem = cartItemRepository.save(cartItem);
        return savedCartItem.getQuantity();
    }
}
