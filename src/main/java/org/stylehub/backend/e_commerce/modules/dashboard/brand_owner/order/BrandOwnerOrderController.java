package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderDetailsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderListItemResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderStatsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderStatusUpdateResponse;
import org.stylehub.backend.e_commerce.order.service.BrandOrderService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/orders")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
@Tag(name = "Brand Orders Dashboard", description = "Brand dashboard order list, stats, details, and fulfillment actions.")
public class BrandOwnerOrderController {

    private final BrandOrderService brandOrderService;

    @GetMapping
    public ResponseEntity<PageResponse<BrandOrderListItemResponse>> findBrandOrders(
            @ModelAttribute BrandOrderFilterRequest filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.brandOrderService.findBrandOrders(filter, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<BrandOrderStatsResponse> getBrandOrderStats() {
        return ResponseEntity.ok(this.brandOrderService.getBrandOrderStats());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BrandOrderDetailsResponse> findBrandOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.brandOrderService.findBrandOrderDetails(orderId));
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<BrandOrderStatusUpdateResponse> markOrderAsShipped(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.brandOrderService.markOrderAsShipped(orderId));
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<BrandOrderStatusUpdateResponse> markOrderAsDelivered(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.brandOrderService.markOrderAsDelivered(orderId));
    }
}
