package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.repository;

import org.stylehub.backend.e_commerce.order.entity.Order;

import java.time.Instant;
import java.util.List;

public interface BrandCalendarQueryRepository {

    List<Order> findOrdersTouchingWindow(String externalId, Instant fromInclusive, Instant toExclusive);

    List<Order> findUpcomingOrders(String externalId, int limit);
}
