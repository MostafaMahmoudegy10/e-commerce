package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarDayMarkerResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarDayResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarDaySummaryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarEventResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarEventType;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarMonthResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarUpcomingOrderResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.repository.BrandCalendarQueryRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrandCalendarService {

    private static final ZoneId DISPLAY_ZONE = ZoneId.of("Africa/Cairo");
    private static final int UPCOMING_ORDERS_LIMIT = 5;

    private final CurrentUserProvider currentUserProvider;
    private final BrandService brandService;
    private final BrandCalendarQueryRepository brandCalendarQueryRepository;

    public BrandCalendarMonthResponse getMonthCalendar(String monthRaw) {
        String externalId = this.currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);

        YearMonth month = parseMonth(monthRaw);
        Instant fromInclusive = month.atDay(1).atStartOfDay(DISPLAY_ZONE).toInstant();
        Instant toExclusive = month.plusMonths(1).atDay(1).atStartOfDay(DISPLAY_ZONE).toInstant();

        List<Order> monthOrders = this.brandCalendarQueryRepository.findOrdersTouchingWindow(
                externalId,
                fromInclusive,
                toExclusive
        );

        List<BrandCalendarDaySummaryResponse> days = month.atDay(1)
                .datesUntil(month.plusMonths(1).atDay(1))
                .map(date -> buildDaySummary(date, monthOrders))
                .toList();

        List<BrandCalendarUpcomingOrderResponse> upcomingOrders =
                this.brandCalendarQueryRepository.findUpcomingOrders(externalId, UPCOMING_ORDERS_LIMIT).stream()
                        .map(this::mapUpcomingOrder)
                        .toList();

        return new BrandCalendarMonthResponse(month, days, upcomingOrders);
    }

    public BrandCalendarDayResponse getDayCalendar(String dateRaw) {
        String externalId = this.currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);

        LocalDate date = parseDate(dateRaw);
        Instant fromInclusive = date.atStartOfDay(DISPLAY_ZONE).toInstant();
        Instant toExclusive = date.plusDays(1L).atStartOfDay(DISPLAY_ZONE).toInstant();

        List<Order> dayOrders = this.brandCalendarQueryRepository.findOrdersTouchingWindow(
                externalId,
                fromInclusive,
                toExclusive
        );

        List<BrandCalendarEventResponse> events = dayOrders.stream()
                .flatMap(order -> buildEventsForDate(order, date).stream())
                .sorted(Comparator.comparing(BrandCalendarEventResponse::eventTime))
                .toList();

        return new BrandCalendarDayResponse(date, events);
    }

    private BrandCalendarDaySummaryResponse buildDaySummary(LocalDate date, List<Order> orders) {
        Map<BrandCalendarEventType, Long> counts = new EnumMap<>(BrandCalendarEventType.class);
        for (BrandCalendarEventType eventType : BrandCalendarEventType.values()) {
            counts.put(eventType, 0L);
        }

        orders.forEach(order -> buildEventsForDate(order, date).forEach(
                event -> counts.computeIfPresent(event.eventType(), (type, count) -> count + 1L)
        ));

        List<BrandCalendarDayMarkerResponse> markers = counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new BrandCalendarDayMarkerResponse(entry.getKey(), entry.getValue()))
                .toList();

        long totalEvents = markers.stream().mapToLong(BrandCalendarDayMarkerResponse::count).sum();
        return new BrandCalendarDaySummaryResponse(date, totalEvents, markers);
    }

    private List<BrandCalendarEventResponse> buildEventsForDate(Order order, LocalDate date) {
        List<BrandCalendarEventResponse> events = new java.util.ArrayList<>();

        if (toLocalDate(order.getCreatedAt().toInstant()).equals(date)) {
            events.add(mapEvent(order, BrandCalendarEventType.ORDER_CREATED, "Order created", order.getCreatedAt().toInstant()));
        }
        if (order.getShippedAt() != null && toLocalDate(order.getShippedAt()).equals(date)) {
            events.add(mapEvent(order, BrandCalendarEventType.ORDER_SHIPPED, "Shipment left hub", order.getShippedAt()));
        }
        if (order.getDeliveredAt() != null && toLocalDate(order.getDeliveredAt()).equals(date)) {
            events.add(mapEvent(order, BrandCalendarEventType.ORDER_DELIVERED, "Delivered", order.getDeliveredAt()));
        }
        if (order.getCancelledAt() != null && toLocalDate(order.getCancelledAt()).equals(date)) {
            events.add(mapEvent(order, BrandCalendarEventType.ORDER_CANCELLED, "Order cancelled", order.getCancelledAt()));
        }

        return events;
    }

    private BrandCalendarEventResponse mapEvent(
            Order order,
            BrandCalendarEventType eventType,
            String title,
            Instant eventTime
    ) {
        return new BrandCalendarEventResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getCustomer().getCustomerEmail(),
                eventType,
                title,
                eventTime,
                order.getOrderStatus(),
                order.getTotalPrice()
        );
    }

    private BrandCalendarUpcomingOrderResponse mapUpcomingOrder(Order order) {
        return new BrandCalendarUpcomingOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getCustomer().getCustomerEmail(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getCreatedAt()
        );
    }

    private YearMonth parseMonth(String monthRaw) {
        if (monthRaw == null || monthRaw.isBlank()) {
            return YearMonth.now(DISPLAY_ZONE);
        }
        return YearMonth.parse(monthRaw.trim());
    }

    private LocalDate parseDate(String dateRaw) {
        if (dateRaw == null || dateRaw.isBlank()) {
            return LocalDate.now(DISPLAY_ZONE);
        }
        return LocalDate.parse(dateRaw.trim());
    }

    private LocalDate toLocalDate(Instant instant) {
        return instant.atZone(DISPLAY_ZONE).toLocalDate();
    }
}
