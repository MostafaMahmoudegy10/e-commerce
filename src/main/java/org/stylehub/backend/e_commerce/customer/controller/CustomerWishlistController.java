package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.service.CustomerWishlistService;
import org.stylehub.backend.e_commerce.favourite.dto.WishlistView;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer Wishlist", description = "Save, view, and remove favorite products for a customer.")
public class CustomerWishlistController{

    private final CustomerWishlistService customerWishlistService;

    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestParam("productId")UUID productId, @PathVariable("brandId")String brandId){
        return ResponseEntity.ok(this.customerWishlistService.addToWishlist(productId,brandId));
    }
    @GetMapping
    public ResponseEntity<PageResponse<WishlistView>> getWishlist(@PathVariable("brandId")String brandId,
                                                                  @PageableDefault(size = 10) Pageable pageable){
     return ResponseEntity.ok(this.customerWishlistService.viewWishlist(brandId,pageable));
    }
    @DeleteMapping
    public ResponseEntity<String>deleteProductFromWishlist(@PathVariable("brandId")String brandId,@RequestParam("productId")UUID productId){
      return ResponseEntity.ok(this.customerWishlistService.deleteProductFromWishlist(productId,brandId));
    }

}
