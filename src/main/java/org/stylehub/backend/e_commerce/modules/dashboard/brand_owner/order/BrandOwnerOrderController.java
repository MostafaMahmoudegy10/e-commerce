package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerCalendarEventResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderStatusPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerReviewSummaryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.service.BrandOwnerOrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class BrandOwnerOrderController {

    private final BrandOwnerOrderService brandOwnerOrderService;

    @PatchMapping("{orderId}/status")
    public ResponseEntity<BrandOwnerOrderResponse> patchOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody BrandOwnerOrderStatusPatchRequest request
    ) {
        return ResponseEntity.ok(this.brandOwnerOrderService.patchOrderStatus(orderId, request));
    }

    @GetMapping("calendar")
    public ResponseEntity<List<BrandOwnerCalendarEventResponse>> viewCalendarEvents(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(this.brandOwnerOrderService.findCalendarEvents(year, month));
    }

    @GetMapping("reviews/summary")
    public ResponseEntity<BrandOwnerReviewSummaryResponse> viewReviewSummary() {
        return ResponseEntity.ok(this.brandOwnerOrderService.findReviewSummary());
    }
}
