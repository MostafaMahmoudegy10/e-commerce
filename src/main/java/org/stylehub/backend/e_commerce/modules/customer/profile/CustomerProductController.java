package org.stylehub.backend.e_commerce.modules.customer.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.CustomerShowProductDetailsDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterRequestDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.service.CustomerProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brand/{brandId}/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BRAND_OWNER','CUSTOMER')")
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @GetMapping
    public ResponseEntity<?>getAllProducts(@PathVariable("brandId") UUID brandId,
        @ModelAttribute FindAllProductFilterRequestDto dtoRequest,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.customerProductService.findAllProductWithFilter(dtoRequest,pageable,brandId));
    }
    @GetMapping("/{productId}")
    public ResponseEntity<CustomerShowProductDetailsDto>showProductDetails(
            @PathVariable("brandId") UUID brandId,
            @PathVariable("productId") UUID productId,
            @RequestParam(name ="itemId",required = false) UUID itemId
    ){
      return ResponseEntity.ok(this.customerProductService.showProductDetails(String.valueOf(brandId),
                productId,
                itemId
                ));
    }

}
