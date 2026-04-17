package org.stylehub.backend.e_commerce.modules.customer.commerce;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping("/cart/items/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable UUID cartItemId) {
        this.customerCommerceService.deleteCartItem(cartItemId);
        return ResponseEntity.ok("Cart item deleted");
    }

    @PostMapping("/orders/checkout")
    public ResponseEntity<CheckoutResponse> checkoutBrand(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(this.customerCommerceService.checkoutBrand(request));
    }
}
