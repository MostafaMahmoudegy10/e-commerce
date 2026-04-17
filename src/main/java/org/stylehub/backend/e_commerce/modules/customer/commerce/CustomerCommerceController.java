package org.stylehub.backend.e_commerce.modules.customer.commerce;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.ProductReviewRequest;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.ProductReviewResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.*;
import org.stylehub.backend.e_commerce.modules.customer.commerce.service.CustomerCommerceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerCommerceController {

    private final CustomerCommerceService customerCommerceService;

    @PostMapping("/favorites/{productId}")
    public ResponseEntity<FavoriteResponse> addFavorite(@PathVariable UUID productId) {
        return ResponseEntity.ok(this.customerCommerceService.addFavorite(productId));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteResponse>> viewFavorites() {
        return ResponseEntity.ok(this.customerCommerceService.viewFavorites());
    }

    @PostMapping("/addresses")
    public ResponseEntity<CustomerAddressResponse> addAddress(@RequestBody CustomerAddressRequest request) {
        return ResponseEntity.ok(this.customerCommerceService.addAddress(request));
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<CustomerAddressResponse>> viewAddresses() {
        return ResponseEntity.ok(this.customerCommerceService.viewAddresses());
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> viewNotifications() {
        return ResponseEntity.ok(this.customerCommerceService.viewNotifications());
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markNotificationRead(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(this.customerCommerceService.markNotificationRead(notificationId));
    }

    @PatchMapping("/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> updateAddress(
            @PathVariable UUID addressId,
            @RequestBody CustomerAddressRequest request
    ) {
        return ResponseEntity.ok(this.customerCommerceService.updateAddress(addressId, request));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable UUID addressId) {
        this.customerCommerceService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted");
    }

    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable UUID productId) {
        this.customerCommerceService.deleteFavorite(productId);
        return ResponseEntity.ok("Favorite deleted");
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponse> addItemToCart(@RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(this.customerCommerceService.addItemToCart(request));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> viewCart() {
        return ResponseEntity.ok(this.customerCommerceService.viewCart());
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> clearCart() {
        this.customerCommerceService.clearCart();
        return ResponseEntity.ok("Cart cleared");
    }

    @DeleteMapping("/cart/items/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable UUID cartItemId) {
        this.customerCommerceService.deleteCartItem(cartItemId);
        return ResponseEntity.ok("Cart item deleted");
    }

    @PatchMapping("/cart/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @RequestBody UpdateCartItemQuantityRequest request
    ) {
        return ResponseEntity.ok(this.customerCommerceService.updateCartItemQuantity(cartItemId, request));
    }

    @PostMapping("/orders/checkout")
    public ResponseEntity<CheckoutResponse> checkoutBrand(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(this.customerCommerceService.checkoutBrand(request));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<CustomerOrderResponse>> viewOrders() {
        return ResponseEntity.ok(this.customerCommerceService.viewOrders());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CustomerOrderResponse> viewOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.customerCommerceService.viewOrderDetails(orderId));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<CustomerOrderResponse> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.customerCommerceService.cancelOrder(orderId));
    }

    @PatchMapping("/orders/{orderId}/payment/retry")
    public ResponseEntity<CustomerOrderResponse> retryPayment(
            @PathVariable UUID orderId,
            @RequestBody PaymentRetryRequest request
    ) {
        return ResponseEntity.ok(this.customerCommerceService.retryPayment(orderId, request));
    }

    @PostMapping("/orders/{orderId}/returns")
    public ResponseEntity<ReturnRequestResponse> createReturnRequest(
            @PathVariable UUID orderId,
            @RequestBody ReturnRequestCreateRequest request
    ) {
        return ResponseEntity.ok(this.customerCommerceService.createReturnRequest(orderId, request));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnRequestResponse>> viewReturnRequests() {
        return ResponseEntity.ok(this.customerCommerceService.viewReturnRequests());
    }

    @PostMapping("/orders/{orderId}/reorder")
    public ResponseEntity<CartResponse> reorder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.customerCommerceService.reorder(orderId));
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ProductReviewResponse> addReview(
            @PathVariable UUID productId,
            @RequestBody ProductReviewRequest request
    ) {
        return ResponseEntity.ok(this.customerCommerceService.addReview(productId, request));
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ProductReviewResponse>> viewProductReviews(@PathVariable UUID productId) {
        return ResponseEntity.ok(this.customerCommerceService.viewProductReviews(productId));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ProductReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @RequestBody ProductReviewRequest request
    ) {
        return ResponseEntity.ok(this.customerCommerceService.updateReview(reviewId, request));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId) {
        this.customerCommerceService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted");
    }
}
