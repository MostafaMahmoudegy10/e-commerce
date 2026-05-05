//package org.stylehub.backend.e_commerce.modules.customer.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.stylehub.backend.e_commerce.cart.entity.Cart;
//import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
//import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
//import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
//import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
//import org.stylehub.backend.e_commerce.modules.customer.dto.AddToCartRequest;
//import org.stylehub.backend.e_commerce.modules.customer.dto.AddToCartResponse;
//import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
//import org.stylehub.backend.e_commerce.product.variant.entity.ProductItem;
//import org.stylehub.backend.e_commerce.product.variant.repository.ProductItemRepository;
//
//@Service
//@RequiredArgsConstructor
//public class CustomerCartService {
//
//    private final CurrentUserProvider  currentUserProvider;
//    private final CartRepository cartRepository;
//    private final CartItemRepository  cartItemRepository;
//    private final ProductItemRepository productItemRepository;
//    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCartService.class);
//
//    @Transactional
//    public  AddToCartResponse addToCart(AddToCartRequest request) {
//       // MAKE SURE CART IS EXISTS FOR THIS USER AND ACTIVE
//       String externalUserId =currentUserProvider.externalId();
//       Cart cart = cartRepository.findActiveCartByCustomerExternalUserId(externalUserId, CartStatus.ACTIVE)
//               .orElseGet(()->{
//                   return new Cart();
//               });
//       // get the product related to this brand i add to cart from
//        ProductItem productItem = productItemRepository.findByIdAndProduct_IdAndProduct_Brand_User_ExternalUserId(
//                request.productItemId(),
//                request.productId(),
//                request.brandExternalId())
//                .orElseThrow(()->new IllegalArgumentException("Product Item Not Found For This Brand"));
//
//       CartItem cartItem = handleCartItem(productItem,cart);
//
//    }
//
//    private CartItem handleCartItem(ProductItem product, Cart cart) {
//        // find cart item that belong to product and this cart
//        CartItem cartItem = this.cartItemRepository.findCartItemByProductItem_IdAndCart_Id(product.getId(),cart.getId())
//                .orElseGet(()->{
//
//                });
//    }
//
//}
