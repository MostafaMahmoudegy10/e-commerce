package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.customer.review.entity.ProductReview;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerCalendarEventResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerDashboardSummaryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerInventoryItemResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerInventorySizeResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderDetailsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderItemResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerProductReviewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderStatusPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerReviewSummaryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerSalesPointResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.NotificationResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.OrderTimelineEventResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.ReturnRequestResponse;
import org.stylehub.backend.e_commerce.modules.customer.review.repository.ProductReviewRepository;
import org.stylehub.backend.e_commerce.modules.notification.service.NotificationService;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.entity.ReturnRequest;
import org.stylehub.backend.e_commerce.order.entity.ReturnRequestStatus;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.order.repository.ReturnRequestRepository;
import org.stylehub.backend.e_commerce.payment.entity.Payment;
import org.stylehub.backend.e_commerce.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.product_item.repository.ProductItemRepository;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandOwnerOrderService {

    private final OrderRepository orderRepository;
    private final CurrentUserProvider currentUserProvider;
    private final BrandRepository brandRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductItemRepository productItemRepository;
    private final PaymentRepository paymentRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final NotificationService notificationService;

    @Transactional
    public BrandOwnerOrderResponse patchOrderStatus(UUID orderId, BrandOwnerOrderStatusPatchRequest request) {
        if (request.orderStatus() == null) {
            throw new IllegalArgumentException("Order status is required");
        }

        Order order = findBrandOrder(orderId);
        order.setOrderStatus(request.orderStatus());
        applyStatusTimestamp(order, request.orderStatus());
        notifyCustomerForStatus(order);

        return toOrderResponse(this.orderRepository.save(order));
    }

    @Transactional
    public List<BrandOwnerCalendarEventResponse> findCalendarEvents(int year, int month) {
        validateYearMonth(year, month);

        LocalDateTime monthStart = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);

        List<Order> orders = this.orderRepository.findBrandOrdersForCalendar(
                currentUserProvider.externalId(),
                Timestamp.valueOf(monthStart),
                Timestamp.valueOf(nextMonthStart)
        );

        List<BrandOwnerCalendarEventResponse> events = new ArrayList<>();
        for (Order order : orders) {
            addEventIfInMonth(events, order, order.getCreatedAt(), year, month, "ORDER_CREATED",
                    "New order from " + order.getUser().getEmail());
            addEventIfInMonth(events, order, order.getPaidAt(), year, month, "ORDER_PAID",
                    "Order paid by " + order.getUser().getEmail());
            addEventIfInMonth(events, order, order.getShippedAt(), year, month, "ORDER_SHIPPED",
                    "Order shipped to " + order.getUser().getEmail());
            addEventIfInMonth(events, order, order.getDeliveredAt(), year, month, "ORDER_DELIVERED",
                    "Order delivered to " + order.getUser().getEmail());
            addEventIfInMonth(events, order, order.getCancelledAt(), year, month, "ORDER_CANCELLED",
                    "Order cancelled for " + order.getUser().getEmail());
        }

        return events.stream()
                .sorted(Comparator.comparing(BrandOwnerCalendarEventResponse::eventDate)
                        .thenComparing(BrandOwnerCalendarEventResponse::eventType))
                .toList();
    }

    @Transactional
    public BrandOwnerReviewSummaryResponse findReviewSummary() {
        Brand brand = this.brandRepository.findByUser_ExternalUserId(currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found for current user"));
        double average = normalizeAverage(this.productReviewRepository.findAverageRatingByBrandId(brand.getId()));
        long totalReviews = this.productReviewRepository.countByBrandId(brand.getId());
        return new BrandOwnerReviewSummaryResponse(
                brand.getId(),
                brand.getBrandName(),
                average,
                totalReviews
        );
    }

    @Transactional
    public BrandOwnerDashboardSummaryResponse findDashboardSummary() {
        List<Order> orders = this.orderRepository.findAllByBrand_User_ExternalUserIdOrderByCreatedAtDesc(currentUserProvider.externalId());
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
        long deliveredOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count();
        long cancelledOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED).count();
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.PAID
                        || o.getOrderStatus() == OrderStatus.SHIPPED
                        || o.getOrderStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ProductItem> inventory = this.productItemRepository.findAllByProduct_Brand_User_ExternalUserId(currentUserProvider.externalId());
        long lowStockProductItems = inventory.stream().filter(this::isLowStock).count();

        List<BrandOwnerOrderResponse> recentOrders = orders.stream()
                .limit(5)
                .map(this::toOrderResponse)
                .toList();

        List<BrandOwnerSalesPointResponse> salesTimeline = buildSalesTimeline(orders);

        return new BrandOwnerDashboardSummaryResponse(
                totalOrders,
                pendingOrders,
                deliveredOrders,
                cancelledOrders,
                totalRevenue,
                lowStockProductItems,
                recentOrders,
                salesTimeline
        );
    }

    @Transactional
    public List<BrandOwnerOrderResponse> findOrders(String status, String query) {
        List<Order> orders = this.orderRepository.findAllByBrand_User_ExternalUserIdOrderByCreatedAtDesc(currentUserProvider.externalId());
        return orders.stream()
                .filter(order -> matchesStatus(order, status))
                .filter(order -> matchesQuery(order, query))
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional
    public BrandOwnerOrderDetailsResponse findOrderDetails(UUID orderId) {
        Order order = findBrandOrder(orderId);
        Payment payment = this.paymentRepository.findByOrder_Id(orderId).orElse(null);
        List<BrandOwnerOrderItemResponse> items = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(this::toOrderItemResponse).toList();

        return new BrandOwnerOrderDetailsResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getPaidAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                order.getEstimatedDeliveryAt(),
                order.getShippingRecipientName(),
                order.getShippingPhoneNumber(),
                order.getShippingAddressLine1(),
                order.getShippingAddressLine2(),
                order.getShippingCity(),
                order.getShippingCountry(),
                order.getShippingPostalCode(),
                payment == null ? null : payment.getPaymentMethod(),
                payment == null ? null : payment.getTransactionId(),
                payment == null ? null : payment.getPaymentStatus(),
                buildTimeline(order),
                items
        );
    }

    @Transactional
    public List<BrandOwnerInventoryItemResponse> findInventory(Integer lowStockThreshold, Boolean lowStockOnly) {
        int threshold = lowStockThreshold == null ? 5 : lowStockThreshold;
        return this.productItemRepository.findAllByProduct_Brand_User_ExternalUserId(currentUserProvider.externalId()).stream()
                .map(item -> toInventoryResponse(item, threshold))
                .filter(item -> !Boolean.TRUE.equals(lowStockOnly) || item.lowStock())
                .toList();
    }

    @Transactional
    public List<BrandOwnerProductReviewResponse> findReviews(Integer minRating) {
        return this.productReviewRepository.findAllByBrandOwnerExternalUserId(currentUserProvider.externalId()).stream()
                .filter(review -> minRating == null || review.getRating() >= minRating)
                .map(this::toBrandReviewResponse)
                .toList();
    }

    @Transactional
    public List<ReturnRequestResponse> findReturnRequests() {
        return this.returnRequestRepository.findAllByBrandOwnerExternalUserId(currentUserProvider.externalId())
                .stream().map(this::toReturnRequestResponse).toList();
    }

    @Transactional
    public ReturnRequestResponse decideReturnRequest(UUID returnRequestId, ReturnRequestStatus status, String brandResponse) {
        if (status == null || status == ReturnRequestStatus.PENDING) {
            throw new IllegalArgumentException("Return decision should be APPROVED or REJECTED");
        }
        ReturnRequest request = this.returnRequestRepository.findByIdAndBrandOwnerExternalUserId(returnRequestId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Return request not found"));
        request.setStatus(status);
        request.setBrandResponse(brandResponse);
        request.setResolvedAt(Timestamp.valueOf(LocalDateTime.now()));
        if (status == ReturnRequestStatus.APPROVED) {
            request.getOrder().setOrderStatus(OrderStatus.CANCELLED);
            request.getOrder().setCancelledAt(Timestamp.valueOf(LocalDateTime.now()));
            this.paymentRepository.findByOrder_Id(request.getOrder().getId()).ifPresent(payment -> {
                payment.setPaymentStatus(org.stylehub.backend.e_commerce.payment.entity.PaymentStatus.REFUNDED);
                this.paymentRepository.save(payment);
            });
        }
        ReturnRequest saved = this.returnRequestRepository.save(request);
        this.notificationService.notifyUserByExternalId(
                request.getUser().getExternalUserId(),
                "Return request updated",
                "Your return request for order " + request.getOrder().getId() + " is " + status.name(),
                "RETURN_" + status.name(),
                request.getId().toString()
        );
        return toReturnRequestResponse(saved);
    }

    @Transactional
    public List<NotificationResponse> viewNotifications() {
        return this.notificationService.findNotifications(currentUserProvider.externalId());
    }

    @Transactional
    public NotificationResponse markNotificationRead(UUID notificationId) {
        return this.notificationService.markAsRead(notificationId, currentUserProvider.externalId());
    }

    @Transactional
    public String exportOrdersCsv() {
        List<Order> orders = this.orderRepository.findAllByBrand_User_ExternalUserIdOrderByCreatedAtDesc(currentUserProvider.externalId());
        StringBuilder csv = new StringBuilder("orderId,customerEmail,status,totalPrice,createdAt\n");
        for (Order order : orders) {
            csv.append(order.getId()).append(",")
                    .append(order.getUser().getEmail()).append(",")
                    .append(order.getOrderStatus()).append(",")
                    .append(order.getTotalPrice()).append(",")
                    .append(order.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }

    @Transactional
    public String exportSalesCsv() {
        List<BrandOwnerSalesPointResponse> timeline = buildSalesTimeline(
                this.orderRepository.findAllByBrand_User_ExternalUserIdOrderByCreatedAtDesc(currentUserProvider.externalId())
        );
        StringBuilder csv = new StringBuilder("date,revenue,ordersCount\n");
        for (BrandOwnerSalesPointResponse point : timeline) {
            csv.append(point.date()).append(",")
                    .append(point.revenue()).append(",")
                    .append(point.ordersCount()).append("\n");
        }
        return csv.toString();
    }

    private void addEventIfInMonth(
            List<BrandOwnerCalendarEventResponse> events,
            Order order,
            Timestamp timestamp,
            int year,
            int month,
            String eventType,
            String title
    ) {
        if (timestamp == null) {
            return;
        }
        LocalDate eventDate = timestamp.toLocalDateTime().toLocalDate();
        if (eventDate.getYear() != year || eventDate.getMonthValue() != month) {
            return;
        }
        events.add(new BrandOwnerCalendarEventResponse(
                order.getId(),
                eventDate,
                eventType,
                title,
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getUser().getEmail()
        ));
    }

    private void applyStatusTimestamp(Order order, OrderStatus orderStatus) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        switch (orderStatus) {
            case PAID -> {
                if (order.getPaidAt() == null) {
                    order.setPaidAt(now);
                }
            }
            case SHIPPED -> {
                if (order.getShippedAt() == null) {
                    order.setShippedAt(now);
                }
            }
            case DELIVERED -> {
                if (order.getDeliveredAt() == null) {
                    order.setDeliveredAt(now);
                }
            }
            case CANCELLED -> {
                if (order.getCancelledAt() == null) {
                    order.setCancelledAt(now);
                }
            }
            case PENDING -> {
            }
        }
    }

    private Order findBrandOrder(UUID orderId) {
        return this.orderRepository.findByIdAndBrand_User_ExternalUserId(orderId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found for your brand"));
    }

    private boolean matchesStatus(Order order, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        return order.getOrderStatus().name().equalsIgnoreCase(status);
    }

    private boolean matchesQuery(Order order, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String normalized = query.trim().toLowerCase();
        return order.getUser().getEmail().toLowerCase().contains(normalized)
                || order.getId().toString().toLowerCase().contains(normalized);
    }

    private boolean isLowStock(ProductItem productItem) {
        return totalStock(productItem) <= 5;
    }

    private int totalStock(ProductItem productItem) {
        return productItem.getSizeList() == null
                ? 0
                : productItem.getSizeList().stream().map(Size::getStock).reduce(0, Integer::sum);
    }

    private List<BrandOwnerSalesPointResponse> buildSalesTimeline(List<Order> orders) {
        LocalDate today = LocalDate.now();
        List<BrandOwnerSalesPointResponse> points = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal revenue = orders.stream()
                    .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().toLocalDateTime().toLocalDate().equals(date))
                    .filter(order -> order.getOrderStatus() == OrderStatus.PAID
                            || order.getOrderStatus() == OrderStatus.SHIPPED
                            || order.getOrderStatus() == OrderStatus.DELIVERED)
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long ordersCount = orders.stream()
                    .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().toLocalDateTime().toLocalDate().equals(date))
                    .count();
            points.add(new BrandOwnerSalesPointResponse(date, revenue, ordersCount));
        }
        return points;
    }

    private BrandOwnerOrderResponse toOrderResponse(Order order) {
        return new BrandOwnerOrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getPaidAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt()
        );
    }

    private BrandOwnerOrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        ProductItem productItem = orderItem.getProductItem();
        Product product = productItem.getProduct();
        return new BrandOwnerOrderItemResponse(
                productItem.getId(),
                product.getId(),
                product.getProductNameEn(),
                productItem.getColor(),
                orderItem.getSizeName(),
                orderItem.getOrderQuantity(),
                orderItem.getOrderPrice(),
                orderItem.getTotalPrice()
        );
    }

    private List<OrderTimelineEventResponse> buildTimeline(Order order) {
        List<OrderTimelineEventResponse> timeline = new ArrayList<>();
        timeline.add(new OrderTimelineEventResponse("ORDER_CREATED", order.getCreatedAt()));
        if (order.getPaidAt() != null) {
            timeline.add(new OrderTimelineEventResponse("ORDER_PAID", order.getPaidAt()));
        }
        if (order.getShippedAt() != null) {
            timeline.add(new OrderTimelineEventResponse("ORDER_SHIPPED", order.getShippedAt()));
        }
        if (order.getDeliveredAt() != null) {
            timeline.add(new OrderTimelineEventResponse("ORDER_DELIVERED", order.getDeliveredAt()));
        }
        if (order.getCancelledAt() != null) {
            timeline.add(new OrderTimelineEventResponse("ORDER_CANCELLED", order.getCancelledAt()));
        }
        return timeline;
    }

    private BrandOwnerInventoryItemResponse toInventoryResponse(ProductItem item, int threshold) {
        List<BrandOwnerInventorySizeResponse> sizes = item.getSizeList() == null
                ? List.of()
                : item.getSizeList().stream()
                .map(size -> new BrandOwnerInventorySizeResponse(size.getSizeName(), size.getStock()))
                .toList();
        int totalStock = totalStock(item);
        return new BrandOwnerInventoryItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getProductNameEn(),
                item.getColor(),
                item.getSku(),
                item.getProduct().getPrice(),
                totalStock,
                totalStock <= threshold,
                sizes
        );
    }

    private BrandOwnerProductReviewResponse toBrandReviewResponse(ProductReview review) {
        return new BrandOwnerProductReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getProduct().getProductNameEn(),
                review.getRating(),
                review.getComment(),
                review.getUser().getEmail(),
                review.getCreatedAt()
        );
    }

    private ReturnRequestResponse toReturnRequestResponse(ReturnRequest request) {
        return new ReturnRequestResponse(
                request.getId(),
                request.getOrder().getId(),
                request.getUser().getEmail(),
                request.getStatus(),
                request.getReason(),
                request.getBrandResponse(),
                request.getCreatedAt(),
                request.getResolvedAt()
        );
    }

    private void validateYearMonth(int year, int month) {
        if (year < 2000 || year > 3000) {
            throw new IllegalArgumentException("Year is not valid");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month is not valid");
        }
    }

    private void notifyCustomerForStatus(Order order) {
        if (order.getOrderStatus() == OrderStatus.SHIPPED) {
            this.notificationService.notifyUserByExternalId(
                    order.getUser().getExternalUserId(),
                    "Order shipped",
                    "Your order " + order.getId() + " has been shipped.",
                    "ORDER_SHIPPED",
                    order.getId().toString()
            );
        }
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            this.notificationService.notifyUserByExternalId(
                    order.getUser().getExternalUserId(),
                    "Order delivered",
                    "Your order " + order.getId() + " has been delivered.",
                    "ORDER_DELIVERED",
                    order.getId().toString()
            );
        }
    }

    private double normalizeAverage(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.round(value * 100.0) / 100.0;
    }
}
