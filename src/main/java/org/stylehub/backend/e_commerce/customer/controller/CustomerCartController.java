package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.cart.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.dto.cart.CartItemViewResponse;
import org.stylehub.backend.e_commerce.customer.dto.cart.UpdateCartItemQuantityRequest;
import org.stylehub.backend.e_commerce.customer.service.CustomerCartService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/customer/brands/{brandId}/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole({'CUSTOMER'})")
@Tag(name = "Customer Cart", description = "Manage the authenticated customer's shopping cart for a brand.")
public class CustomerCartController {

    private final CustomerCartService cartService;

    @Operation(summary = "Add to cart", description = "Adds a product variant to the customer's cart or updates its quantity if it already exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cart item or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a customer"),
            @ApiResponse(responseCode = "404", description = "Brand or variant was not found")
    })
    @PostMapping()
    public ResponseEntity<Map<String,Integer>>addToCart(
            @PathVariable("brandId") String brandId
            ,@ModelAttribute AddToCartRequest request){
       return ResponseEntity.ok(cartService.upsertToCart(brandId,request));
    }

    @Operation(summary = "View cart", description = "Returns the current customer's paginated cart items for a brand.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart returned successfully"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a customer"),
            @ApiResponse(responseCode = "404", description = "Brand was not found")
    })
    @GetMapping()
    public ResponseEntity<PageResponse<CartItemViewResponse>> viewCart(@PathVariable("brandId")String brandId
    ,@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(this.cartService.viewCart(brandId,pageable));
    }

    @PatchMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<Map<String, Integer>> updateCartItemQuantity(
            @PathVariable("brandId") String brandId,
            @PathVariable("cartId") UUID cartId,
            @PathVariable("cartItemId") UUID cartItemId,
            @ModelAttribute UpdateCartItemQuantityRequest request
    ) {
        return ResponseEntity.ok(this.cartService.changeCartItemQuantity(brandId, cartId, cartItemId, request));
    }

    @Operation(summary = "Remove cart item", description = "Removes a single item from the customer's cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart item removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cart remove request"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a customer"),
            @ApiResponse(responseCode = "404", description = "Cart or cart item was not found")
    })
    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<String> removeFromCart(
            @PathVariable("brandId") String brandId,
            @PathVariable("cartId") UUID cartId,
            @PathVariable("cartItemId") UUID cartItemId
    ){
        return ResponseEntity.ok(this.cartService.removeFromCart(brandId,cartId,cartItemId));
    }
}
