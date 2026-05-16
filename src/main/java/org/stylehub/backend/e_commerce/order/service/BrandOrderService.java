package org.stylehub.backend.e_commerce.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderDetailsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderItemDetailsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderListItemResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderShippingAddressResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderStatsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderStatusUpdateResponse;
import org.stylehub.backend.e_commerce.order.address.entity.ShippingAddress;
import org.stylehub.backend.e_commerce.order.address.repository.ShippingAddressRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.event.OrderLifecycleEvent;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.order.item.repoistory.OrderItemRepository;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.order.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.order.publisher.OrderPublisherEvents;
import org.stylehub.backend.e_commerce.order.repository.BrandOrderQueryRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.mail.dto.ProductReviewEmailItem;
import org.stylehub.backend.e_commerce.platform.mail.events.ProductReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.publisher.EmailEventPublisher;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandOrderService {

    private final OrderRepository orderRepository;
    private final BrandOrderQueryRepository brandOrderQueryRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final CurrentUserProvider currentUserProvider;
    private final BrandService brandService;
    private final OrderPublisherEvents orderPublisherEvents;
    private final EmailEventPublisher emailEventPublisher;

    public PageResponse<BrandOrderListItemResponse> findBrandOrders(
            BrandOrderFilterRequest filter,
            Pageable pageable
    ) {
        String externalId = currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);

        Page<Order> page = this.brandOrderQueryRepository.findBrandOrders(
                externalId,
                filter,
                pageable
        );

        Map<UUID, Payment> paymentsByOrderId = page.getContent().isEmpty()
                ? Map.of()
                : this.paymentRepository.findByOrder_IdIn(
                                page.getContent().stream().map(Order::getId).toList()
                        ).stream()
                        .collect(Collectors.toMap(payment -> payment.getOrder().getId(), Function.identity()));

        List<BrandOrderListItemResponse> items = page.getContent().stream()
                .map(order -> mapToListItem(order, paymentsByOrderId.get(order.getId())))
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public BrandOrderStatsResponse getBrandOrderStats() {
        String externalId = currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);

        return new BrandOrderStatsResponse(
                defaultIfNull(this.paymentRepository.sumRevenueByBrandExternalIdAndPaymentStatus(externalId, PaymentStatus.PAID)),
                this.orderRepository.countByBrand_User_ExternalUserId(externalId),
                this.orderRepository.countByBrand_User_ExternalUserIdAndOrderStatus(externalId, OrderStatus.PENDING),
                this.orderRepository.countByBrand_User_ExternalUserIdAndOrderStatus(externalId, OrderStatus.DELIVERED)
        );
    }

    public BrandOrderDetailsResponse findBrandOrderDetails(UUID orderId) {
        String externalId = currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);

        Order order = this.orderRepository.findByIdAndBrandExternalId(orderId, externalId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Payment payment = this.paymentRepository.findByOrder_Id(orderId).orElse(null);
        ShippingAddress shippingAddress = this.shippingAddressRepository.findByOrder_Id(order.getId())
                .orElse(null);
        List<OrderItem> orderItems = this.orderItemRepository.findAllWithDetailsByOrderId(orderId);

        return mapToDetails(order, payment, shippingAddress, orderItems);
    }

    public BrandOrderStatusUpdateResponse markOrderAsShipped(UUID orderId) {
        Order order = loadOwnedOrder(orderId);

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be marked as shipped");
        }

        order.setOrderStatus(OrderStatus.SHIPPED);
        order.setShippedAt(Instant.now());
        this.orderRepository.save(order);
        this.orderPublisherEvents.publishOrderShipped(buildOrderLifecycleEvent(order, this.paymentRepository.findByOrder_Id(orderId).orElse(null)));

        return new BrandOrderStatusUpdateResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderStatus(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                "Order marked as shipped successfully"
        );
    }

    public BrandOrderStatusUpdateResponse markOrderAsDelivered(UUID orderId) {
        Order order = loadOwnedOrder(orderId);

        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be marked as delivered");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(Instant.now());
        this.orderRepository.save(order);
        this.orderPublisherEvents.publishOrderDelivered(buildOrderLifecycleEvent(order, this.paymentRepository.findByOrder_Id(orderId).orElse(null)));
        publishProductReviewRequest(order);

        return new BrandOrderStatusUpdateResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderStatus(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                "Order marked as delivered successfully"
        );
    }

    private void publishProductReviewRequest(Order order) {
        List<OrderItem> orderItems = this.orderItemRepository.findAllWithDetailsByOrderId(order.getId());
        List<ProductReviewEmailItem> products = orderItems.stream()
                .map(orderItem -> orderItem.getVariant().getProductColor().getProduct())
                .filter(Objects::nonNull)
                .map(product -> new ProductReviewEmailItem(
                        product.getId(),
                        product.getProductNameEn()
                ))
                .distinct()
                .toList();

        this.emailEventPublisher.publishProductReviewRequested(
                new ProductReviewRequestedEmailEvent(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getCustomer().getUser().getExternalUserId(),
                        order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                        order.getCustomer().getCustomerEmail(),
                        products,
                        order.getDeliveredAt()
                )
        );
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Order loadOwnedOrder(UUID orderId) {
        String externalId = currentUserProvider.externalId();
        this.brandService.isBrandExists(externalId);

        return this.orderRepository.findByIdAndBrandExternalId(orderId, externalId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    private OrderLifecycleEvent buildOrderLifecycleEvent(Order order, Payment payment) {
        return new OrderLifecycleEvent(
                order.getId(),
                order.getOrderNumber(),
                order.getBrand().getUser().getId(),
                order.getCustomer().getUsername(),
                order.getCustomer().getCustomerEmail(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                payment == null ? null : payment.getPaymentStatus(),
                Instant.now()
        );
    }

    private BrandOrderListItemResponse mapToListItem(Order order, Payment payment) {
        CustomerProfile customer = order.getCustomer();
        return new BrandOrderListItemResponse(
                order.getId(),
                order.getOrderNumber(),
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getCustomerEmail(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                payment == null ? null : payment.getPaymentStatus(),
                order.getCreatedAt()
        );
    }

    private BrandOrderDetailsResponse mapToDetails(
            Order order,
            Payment payment,
            ShippingAddress shippingAddress,
            List<OrderItem> orderItems
    ) {
        CustomerProfile customer = order.getCustomer();

        return new BrandOrderDetailsResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getBrand().getId(),
                order.getBrand().getBrandName(),
                order.getOrderStatus(),
                payment == null ? null : payment.getPaymentStatus(),
                payment == null ? null : payment.getPaymentMethod(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                customer.getId(),
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getCustomerEmail(),
                customer.getPhoneNumber(),
                customer.getProfileImageUrl(),
                toShippingAddressResponse(shippingAddress),
                orderItems.stream().map(this::mapOrderItem).toList(),
                payment == null ? null : payment.getPaidAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt()
        );
    }

    private BrandOrderShippingAddressResponse toShippingAddressResponse(ShippingAddress shippingAddress) {
        if (shippingAddress == null) {
            return null;
        }

        return new BrandOrderShippingAddressResponse(
                shippingAddress.getCityEn(),
                shippingAddress.getCityAr(),
                shippingAddress.getStreetEn(),
                shippingAddress.getStreetAr(),
                shippingAddress.getBuildingNumber(),
                shippingAddress.addressEn(
                        shippingAddress.getStreetEn(),
                        shippingAddress.getCityEn(),
                        shippingAddress.getBuildingNumber()
                ),
                shippingAddress.addressAr(
                        shippingAddress.getStreetAr(),
                        shippingAddress.getCityAr(),
                        shippingAddress.getBuildingNumber()
                )
        );
    }

    private BrandOrderItemDetailsResponse mapOrderItem(OrderItem orderItem) {
        var productColor = orderItem.getVariant().getProductColor();
        var product = productColor.getProduct();

        return new BrandOrderItemDetailsResponse(
                orderItem.getId(),
                product.getId(),
                product.getProductNameEn(),
                product.getProductNameAr(),
                product.getThumbnail(),
                productColor.getColorCode(),
                orderItem.getVariant().getSize(),
                orderItem.getVariant().getSku(),
                orderItem.getOrderPrice(),
                orderItem.getOrderQuantity(),
                orderItem.getTotalPrice()
        );
    }
}
