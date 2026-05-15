package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardGlanceResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardHomeResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardNotificationPreviewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardOrderStatusCountResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardProductStockRow;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardRecentOrderResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardRecentProductResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardRevenuePointResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardStoreMoodResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto.BrandDashboardSummaryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.repository.BrandDashboardHomeQueryRepository;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.repository.DashboardNotificationRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.order.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandDashboardHomeService {

    private static final int RECENT_PRODUCTS_LIMIT = 5;
    private static final int RECENT_ORDERS_LIMIT = 5;
    private static final int RECENT_NOTIFICATIONS_LIMIT = 5;
    private static final int LOW_STOCK_THRESHOLD = 5;

    private final CurrentUserProvider currentUserProvider;
    private final BrandService brandService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductVariantRepository productVariantRepository;
    private final BrandDashboardHomeQueryRepository brandDashboardHomeQueryRepository;
    private final DashboardNotificationRepository dashboardNotificationRepository;

    public BrandDashboardHomeResponse getHomeDashboard(String rangeRaw) {
        String externalId = this.currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);
        Brand brand = this.brandService.findBrandByExternalId(externalId);

        DashboardRange range = DashboardRange.from(rangeRaw);
        Instant fromInclusive = range.fromInclusive();
        UUID userId = this.currentUserProvider.getUserId();

        List<Order> recentOrders = this.brandDashboardHomeQueryRepository.findRecentOrders(externalId, RECENT_ORDERS_LIMIT);
        List<Order> rangeOrders = this.brandDashboardHomeQueryRepository.findOrdersCreatedSince(externalId, fromInclusive);
        List<Payment> rangePayments = this.brandDashboardHomeQueryRepository.findPaidPaymentsSince(externalId, fromInclusive);
        List<Product> recentProducts = this.brandDashboardHomeQueryRepository.findRecentProducts(externalId, RECENT_PRODUCTS_LIMIT);
        List<DashboardNotification> recentUnreadNotifications =
                this.brandDashboardHomeQueryRepository.findRecentUnreadNotifications(userId, RECENT_NOTIFICATIONS_LIMIT);

        Map<UUID, Long> productStockById = recentProducts.isEmpty()
                ? Map.of()
                : this.productVariantRepository.sumStockByProductIds(
                        recentProducts.stream().map(Product::getId).toList()
                ).stream().collect(Collectors.toMap(
                        BrandDashboardProductStockRow::productId,
                        BrandDashboardProductStockRow::totalStock
                ));

        return new BrandDashboardHomeResponse(
                brand.getBrandName(),
                range.label(),
                new BrandDashboardSummaryResponse(
                        this.productRepository.countByBrand_User_ExternalUserId(externalId),
                        this.categoryRepository.countByBrand_User_ExternalUserId(externalId),
                        this.orderRepository.countByBrand_User_ExternalUserId(externalId),
                        defaultIfNull(this.paymentRepository.sumRevenueByBrandExternalIdAndPaymentStatus(externalId, PaymentStatus.PAID))
                ),
                buildRevenueSeries(range, rangePayments),
                buildOrderStatusDistribution(rangeOrders),
                recentProducts.stream().map(product -> mapRecentProduct(product, productStockById)).toList(),
                new BrandDashboardGlanceResponse(
                        defaultIfNull(this.productRepository.averagePriceByBrandExternalId(externalId)),
                        defaultIfNull(this.productVariantRepository.sumStockByBrandExternalId(externalId)),
                        this.orderRepository.countByBrand_User_ExternalUserIdAndOrderStatus(externalId, OrderStatus.PENDING)
                ),
                new BrandDashboardStoreMoodResponse(
                        this.orderRepository.countByBrand_User_ExternalUserIdAndOrderStatus(externalId, OrderStatus.DELIVERED),
                        this.productVariantRepository.countLowStockByBrandExternalId(externalId, LOW_STOCK_THRESHOLD),
                        this.dashboardNotificationRepository.countByRecipientUser_IdAndReadAtIsNull(userId)
                ),
                recentOrders.stream().map(this::mapRecentOrder).toList(),
                recentUnreadNotifications.stream().map(this::mapNotificationPreview).toList()
        );
    }

    private List<BrandDashboardRevenuePointResponse> buildRevenueSeries(
            DashboardRange range,
            List<Payment> payments
    ) {
        ZoneId zoneId = ZoneId.systemDefault();
        Map<LocalDate, BigDecimal> revenueByDay = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getPaidAt().atZone(zoneId).toLocalDate(),
                        Collectors.mapping(
                                Payment::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        LocalDate start = LocalDate.now(zoneId).minusDays(range.days() - 1L);
        return start.datesUntil(LocalDate.now(zoneId).plusDays(1L))
                .map(day -> new BrandDashboardRevenuePointResponse(
                        day,
                        revenueByDay.getOrDefault(day, BigDecimal.ZERO)
                ))
                .toList();
    }

    private List<BrandDashboardOrderStatusCountResponse> buildOrderStatusDistribution(List<Order> orders) {
        Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0L);
        }
        orders.forEach(order -> counts.computeIfPresent(order.getOrderStatus(), (status, count) -> count + 1L));

        return counts.entrySet().stream()
                .map(entry -> new BrandDashboardOrderStatusCountResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private BrandDashboardRecentProductResponse mapRecentProduct(Product product, Map<UUID, Long> productStockById) {
        var category = product.getCategory();
        return new BrandDashboardRecentProductResponse(
                product.getId(),
                product.getProductNameEn(),
                product.getProductNameAr(),
                category.getCategoryNameEn(),
                category.getCategoryNameAr(),
                product.getPrice(),
                productStockById.getOrDefault(product.getId(), 0L),
                product.getCreationDate(),
                product.getThumbnail()
        );
    }

    private BrandDashboardRecentOrderResponse mapRecentOrder(Order order) {
        return new BrandDashboardRecentOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getCustomer().getCustomerEmail(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getCreatedAt()
        );
    }

    private BrandDashboardNotificationPreviewResponse mapNotificationPreview(DashboardNotification notification) {
        return new BrandDashboardNotificationPreviewResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceCode(),
                notification.getCreatedAt()
        );
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal defaultIfNull(Double value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    private long defaultIfNull(Long value) {
        return value == null ? 0L : value;
    }

    private enum DashboardRange {
        DAYS_7("7D", 7),
        DAYS_30("30D", 30);

        private final String label;
        private final int days;

        DashboardRange(String label, int days) {
            this.label = label;
            this.days = days;
        }

        public String label() {
            return label;
        }

        public int days() {
            return days;
        }

        public Instant fromInclusive() {
            ZoneId zoneId = ZoneId.systemDefault();
            return LocalDate.now(zoneId)
                    .minusDays(days - 1L)
                    .atStartOfDay(zoneId)
                    .toInstant();
        }

        public static DashboardRange from(String raw) {
            if (raw == null || raw.isBlank()) {
                return DAYS_7;
            }

            String normalized = raw.trim().toUpperCase();
            return switch (normalized) {
                case "7D" -> DAYS_7;
                case "30D" -> DAYS_30;
                default -> throw new IllegalArgumentException("Unsupported dashboard range. Use 7D or 30D");
            };
        }
    }
}
