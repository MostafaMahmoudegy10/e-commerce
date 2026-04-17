package org.stylehub.backend.e_commerce.modules.customer.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.ProductDetailsResponse;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.ProductRecommendationsResponse;
import org.stylehub.backend.e_commerce.modules.customer.catalog.service.CustomerProductCatalogService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerProductCatalogController {

    private final CustomerProductCatalogService customerProductCatalogService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findProducts(
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.customerProductCatalogService.findProducts(
                brandId,
                categoryId,
                query,
                minPrice,
                maxPrice,
                minRating,
                sortBy,
                sortDirection,
                pageable
        ));
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductDetailsResponse> findProductDetails(@PathVariable UUID productId) {
        return ResponseEntity.ok(this.customerProductCatalogService.findProductDetails(productId));
    }

    @GetMapping("discover")
    public ResponseEntity<ProductRecommendationsResponse> discover() {
        return ResponseEntity.ok(this.customerProductCatalogService.findRecommendations());
    }
}
