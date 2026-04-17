package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerCalendarEventResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerDashboardSummaryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerInventoryItemResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderDetailsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerProductReviewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderStatusPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerReturnDecisionRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerReviewSummaryResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.NotificationResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.ReturnRequestResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.service.BrandOwnerOrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class BrandOwnerOrderController {

    private final BrandOwnerOrderService brandOwnerOrderService;

    @GetMapping("dashboard/summary")
    public ResponseEntity<BrandOwnerDashboardSummaryResponse> viewDashboardSummary() {
        return ResponseEntity.ok(this.brandOwnerOrderService.findDashboardSummary());
    }

    @GetMapping
    public ResponseEntity<List<BrandOwnerOrderResponse>> viewOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(this.brandOwnerOrderService.findOrders(status, query));
    }

    @GetMapping("{orderId}")
    public ResponseEntity<BrandOwnerOrderDetailsResponse> viewOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(this.brandOwnerOrderService.findOrderDetails(orderId));
    }

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

    @GetMapping("reviews")
    public ResponseEntity<List<BrandOwnerProductReviewResponse>> viewReviews(
            @RequestParam(required = false) Integer minRating
    ) {
        return ResponseEntity.ok(this.brandOwnerOrderService.findReviews(minRating));
    }

    @GetMapping("inventory")
    public ResponseEntity<List<BrandOwnerInventoryItemResponse>> viewInventory(
            @RequestParam(required = false) Integer lowStockThreshold,
            @RequestParam(required = false, defaultValue = "false") Boolean lowStockOnly
    ) {
        return ResponseEntity.ok(this.brandOwnerOrderService.findInventory(lowStockThreshold, lowStockOnly));
    }

    @GetMapping("returns")
    public ResponseEntity<List<ReturnRequestResponse>> viewReturnRequests() {
        return ResponseEntity.ok(this.brandOwnerOrderService.findReturnRequests());
    }

    @PatchMapping("returns/{returnRequestId}")
    public ResponseEntity<ReturnRequestResponse> decideReturnRequest(
            @PathVariable UUID returnRequestId,
            @RequestBody BrandOwnerReturnDecisionRequest request
    ) {
        return ResponseEntity.ok(this.brandOwnerOrderService.decideReturnRequest(
                returnRequestId,
                request.status(),
                request.brandResponse()
        ));
    }

    @GetMapping("notifications")
    public ResponseEntity<List<NotificationResponse>> viewNotifications() {
        return ResponseEntity.ok(this.brandOwnerOrderService.viewNotifications());
    }

    @PatchMapping("notifications/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markNotificationRead(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(this.brandOwnerOrderService.markNotificationRead(notificationId));
    }

    @GetMapping(value = "export/orders", produces = "text/csv")
    public ResponseEntity<String> exportOrders() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(this.brandOwnerOrderService.exportOrdersCsv());
    }

    @GetMapping(value = "export/sales", produces = "text/csv")
    public ResponseEntity<String> exportSales() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(this.brandOwnerOrderService.exportSalesCsv());
    }
}
