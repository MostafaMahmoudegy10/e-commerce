package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.repository;

import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BrandDashboardHomeQueryRepository {

    List<Order> findRecentOrders(String externalId, int limit);

    List<Order> findOrdersCreatedSince(String externalId, Instant fromInclusive);

    List<Payment> findPaidPaymentsSince(String externalId, Instant fromInclusive);

    List<Product> findRecentProducts(String externalId, int limit);

    List<DashboardNotification> findRecentUnreadNotifications(UUID userId, int limit);
}
