package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerCalendarEventResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerOrderStatusPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOwnerReviewSummaryResponse;
import org.stylehub.backend.e_commerce.modules.customer.review.repository.ProductReviewRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;

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

    @Transactional
    public BrandOwnerOrderResponse patchOrderStatus(UUID orderId, BrandOwnerOrderStatusPatchRequest request) {
        if (request.orderStatus() == null) {
            throw new IllegalArgumentException("Order status is required");
        }

        Order order = findBrandOrder(orderId);
        order.setOrderStatus(request.orderStatus());
        applyStatusTimestamp(order, request.orderStatus());

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

    private void validateYearMonth(int year, int month) {
        if (year < 2000 || year > 3000) {
            throw new IllegalArgumentException("Year is not valid");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month is not valid");
        }
    }

    private double normalizeAverage(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.round(value * 100.0) / 100.0;
    }
}
