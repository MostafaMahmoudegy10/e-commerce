package org.stylehub.backend.e_commerce.modules.customer.commerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.modules.customer.address.entity.CustomerAddress;
import org.stylehub.backend.e_commerce.modules.customer.address.repository.CustomerAddressRepository;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.ProductReviewRequest;
import org.stylehub.backend.e_commerce.modules.customer.catalog.dto.ProductReviewResponse;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.*;
import org.stylehub.backend.e_commerce.modules.customer.favorite.entity.Favorite;
import org.stylehub.backend.e_commerce.modules.customer.favorite.repository.FavoriteRepository;
import org.stylehub.backend.e_commerce.modules.customer.review.entity.ProductReview;
import org.stylehub.backend.e_commerce.modules.customer.review.repository.ProductReviewRepository;
import org.stylehub.backend.e_commerce.modules.notification.service.NotificationService;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.entity.ReturnRequest;
import org.stylehub.backend.e_commerce.order.entity.ReturnRequestStatus;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.order.item.repository.OrderItemRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.order.repository.ReturnRequestRepository;
import org.stylehub.backend.e_commerce.payment.entity.Payment;
import org.stylehub.backend.e_commerce.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.product_item.repository.ProductItemRepository;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerCommerceService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final UserSyncService userSyncService;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BrandRepository brandRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final NotificationService notificationService;

    @Transactional
    public FavoriteResponse addFavorite(UUID productId) {
        User user = ensureCurrentCustomerUser();
        Product product = findProduct(productId);

        boolean exists = this.favoriteRepository.existsByUser_ExternalUserIdAndProduct_Id(user.getExternalUserId(), productId);
        if (exists) {
            throw new IllegalArgumentException("Product already added to favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        this.favoriteRepository.save(favorite);
        return toFavoriteResponse(product);
    }

    @Transactional
    public void deleteFavorite(UUID productId) {
        User user = ensureCurrentCustomerUser();
        Favorite favorite = this.favoriteRepository.findByUser_ExternalUserIdAndProduct_Id(user.getExternalUserId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Favorite product not found"));
        this.favoriteRepository.delete(favorite);
    }

    @Transactional
    public List<FavoriteResponse> viewFavorites() {
        User user = ensureCurrentCustomerUser();
        return this.favoriteRepository.findAllByUser_ExternalUserId(user.getExternalUserId()).stream()
                .map(Favorite::getProduct)
                .map(this::toFavoriteResponse)
                .toList();
    }

    @Transactional
    public CustomerAddressResponse addAddress(CustomerAddressRequest request) {
        User user = ensureCurrentCustomerUser();
        validateAddressRequest(request);

        if (Boolean.TRUE.equals(request.isDefault())) {
            unsetDefaultAddresses(user.getExternalUserId());
        }

        CustomerAddress address = new CustomerAddress();
        address.setUser(user);
        address.setRecipientName(request.recipientName());
        address.setPhoneNumber(request.phoneNumber());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setCountry(request.country());
        address.setPostalCode(request.postalCode());
        address.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
        return toAddressResponse(this.customerAddressRepository.save(address));
    }

    @Transactional
    public List<CustomerAddressResponse> viewAddresses() {
        User user = ensureCurrentCustomerUser();
        return this.customerAddressRepository.findAllByUser_ExternalUserIdOrderByCreatedAtDesc(user.getExternalUserId())
                .stream().map(this::toAddressResponse).toList();
    }

    @Transactional
    public CustomerAddressResponse updateAddress(UUID addressId, CustomerAddressRequest request) {
        User user = ensureCurrentCustomerUser();
        validateAddressRequest(request);
        CustomerAddress address = findAddress(addressId, user.getExternalUserId());

        if (Boolean.TRUE.equals(request.isDefault())) {
            unsetDefaultAddresses(user.getExternalUserId());
        }

        address.setRecipientName(request.recipientName());
        address.setPhoneNumber(request.phoneNumber());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setCountry(request.country());
        address.setPostalCode(request.postalCode());
        address.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
        return toAddressResponse(this.customerAddressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(UUID addressId) {
        User user = ensureCurrentCustomerUser();
        CustomerAddress address = findAddress(addressId, user.getExternalUserId());
        this.customerAddressRepository.delete(address);
    }

    @Transactional
    public CartResponse addItemToCart(AddCartItemRequest request) {
        validateCartRequest(request);

        User user = ensureCurrentCustomerUser();
        ProductItem productItem = findProductItem(request.productItemId());
        Size size = findSize(productItem, request.sizeName());

        if (size.getStock() < request.quantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }

        Cart cart = findOrCreateActiveCart(user);
        CartItem cartItem = this.cartItemRepository
                .findByCart_IdAndProductItem_IdAndSizeName(cart.getId(), productItem.getId(), request.sizeName())
                .orElseGet(CartItem::new);

        int updatedQuantity = request.quantity();
        if (cartItem.getId() != null) {
            updatedQuantity += cartItem.getQuantity();
        }

        if (size.getStock() < updatedQuantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }

        cartItem.setCart(cart);
        cartItem.setProductItem(productItem);
        cartItem.setSizeName(size.getSizeName());
        cartItem.setPrice(productItem.getProduct().getPrice());
        cartItem.setQuantity(updatedQuantity);
        this.cartItemRepository.save(cartItem);

        return buildCartResponse(cart);
    }

    @Transactional
    public void deleteCartItem(UUID cartItemId) {
        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        CartItem cartItem = this.cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to your active cart");
        }
        this.cartItemRepository.delete(cartItem);
    }

    @Transactional
    public CartResponse updateCartItemQuantity(UUID cartItemId, UpdateCartItemQuantityRequest request) {
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity should be above 0");
        }

        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        CartItem cartItem = this.cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to your active cart");
        }

        Size size = findSize(cartItem.getProductItem(), cartItem.getSizeName());
        if (size.getStock() < request.quantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }

        cartItem.setQuantity(request.quantity());
        this.cartItemRepository.save(cartItem);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse viewCart() {
        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        return buildCartResponse(cart);
    }

    @Transactional
    public void clearCart() {
        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        this.cartItemRepository.deleteAll(this.cartItemRepository.findAllByCart_Id(cart.getId()));
    }

    @Transactional
    public CheckoutResponse checkoutBrand(CheckoutRequest request) {
        validateCheckoutRequest(request);

        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        Brand brand = this.brandRepository.findById(request.brandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        CustomerAddress address = findAddress(request.addressId(), user.getExternalUserId());

        List<CartItem> brandCartItems = this.cartItemRepository.findAllByCart_IdAndBrand_Id(cart.getId(), brand.getId());
        if (brandCartItems.isEmpty()) {
            throw new IllegalArgumentException("No cart items found for this brand");
        }

        for (CartItem cartItem : brandCartItems) {
            Size size = findSize(cartItem.getProductItem(), cartItem.getSizeName());
            if (size.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product item " + cartItem.getProductItem().getId());
            }
        }

        BigDecimal totalPrice = brandCartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setBrand(brand);
        order.setOrderStatus(OrderStatus.PAID);
        order.setTotalPrice(totalPrice);
        order.setPaidAt(Timestamp.valueOf(LocalDateTime.now()));
        order.setEstimatedDeliveryAt(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
        order.setShippingRecipientName(address.getRecipientName());
        order.setShippingPhoneNumber(address.getPhoneNumber());
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingCity(address.getCity());
        order.setShippingCountry(address.getCountry());
        order.setShippingPostalCode(address.getPostalCode());
        Order savedOrder = this.orderRepository.save(order);

        List<OrderItem> orderItems = brandCartItems.stream().map(cartItem -> {
            Size size = findSize(cartItem.getProductItem(), cartItem.getSizeName());
            size.setStock(size.getStock() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProductItem(cartItem.getProductItem());
            orderItem.setSizeName(cartItem.getSizeName());
            orderItem.setOrderPrice(cartItem.getPrice());
            orderItem.setOrderQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            return orderItem;
        }).toList();

        this.orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(totalPrice);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setTransactionId(request.transactionId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment savedPayment = this.paymentRepository.save(payment);
        this.notificationService.notifyUserByExternalId(
                user.getExternalUserId(),
                "Order confirmed",
                "Your order " + savedOrder.getId() + " was created successfully.",
                "ORDER_CREATED",
                savedOrder.getId().toString()
        );

        this.cartItemRepository.deleteAll(brandCartItems);
        if (this.cartItemRepository.findAllByCart_Id(cart.getId()).isEmpty()) {
            cart.setCartStatus(CartStatus.CHECKED_OUT);
            this.cartRepository.save(cart);
        }

        return new CheckoutResponse(
                savedOrder.getId(),
                brand.getId(),
                brand.getBrandName(),
                savedOrder.getOrderStatus(),
                savedOrder.getTotalPrice(),
                toAddressResponse(address),
                savedOrder.getEstimatedDeliveryAt(),
                buildTimeline(savedOrder),
                orderItems.stream().map(this::toOrderItemResponse).toList(),
                toPaymentResponse(savedPayment)
        );
    }

    @Transactional
    public List<CustomerOrderResponse> viewOrders() {
        User user = ensureCurrentCustomerUser();
        return this.orderRepository.findAllByUser_ExternalUserIdOrderByCreatedAtDesc(user.getExternalUserId()).stream()
                .map(this::toCustomerOrderResponse)
                .toList();
    }

    @Transactional
    public CustomerOrderResponse viewOrderDetails(UUID orderId) {
        User user = ensureCurrentCustomerUser();
        Order order = this.orderRepository.findByIdAndUser_ExternalUserId(orderId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toCustomerOrderResponse(order);
    }

    @Transactional
    public CustomerOrderResponse cancelOrder(UUID orderId) {
        User user = ensureCurrentCustomerUser();
        Order order = this.orderRepository.findByIdAndUser_ExternalUserId(orderId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("You cannot cancel an order after shipment");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order already cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Timestamp.valueOf(LocalDateTime.now()));

        List<OrderItem> orderItems = order.getOrderItems() == null ? new ArrayList<>() : order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Size size = findSize(orderItem.getProductItem(), orderItem.getSizeName());
            size.setStock(size.getStock() + orderItem.getOrderQuantity());
        }

        this.paymentRepository.findByOrder_Id(orderId).ifPresent(payment -> {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            this.paymentRepository.save(payment);
        });
        this.notificationService.notifyUserByExternalId(
                user.getExternalUserId(),
                "Refund confirmed",
                "Your order " + order.getId() + " was cancelled and refund confirmed.",
                "REFUND_CONFIRMED",
                order.getId().toString()
        );

        return toCustomerOrderResponse(this.orderRepository.save(order));
    }

    @Transactional
    public CustomerOrderResponse retryPayment(UUID orderId, PaymentRetryRequest request) {
        User user = ensureCurrentCustomerUser();
        validatePaymentRetryRequest(request);
        Order order = this.orderRepository.findByIdAndUser_ExternalUserId(orderId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        Payment payment = this.paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled order cannot be paid again");
        }

        payment.setPaymentMethod(request.paymentMethod());
        payment.setTransactionId(request.transactionId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setOrderStatus(OrderStatus.PAID);
        if (order.getPaidAt() == null) {
            order.setPaidAt(Timestamp.valueOf(LocalDateTime.now()));
        }

        this.paymentRepository.save(payment);
        this.notificationService.notifyUserByExternalId(
                user.getExternalUserId(),
                "Payment confirmed",
                "Payment for order " + order.getId() + " succeeded.",
                "PAYMENT_SUCCESS",
                order.getId().toString()
        );
        return toCustomerOrderResponse(this.orderRepository.save(order));
    }

    @Transactional
    public ReturnRequestResponse createReturnRequest(UUID orderId, ReturnRequestCreateRequest request) {
        User user = ensureCurrentCustomerUser();
        if (request.reason() == null || request.reason().isBlank()) {
            throw new IllegalArgumentException("Return reason is required");
        }
        Order order = this.orderRepository.findByIdAndUser_ExternalUserId(orderId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Return request is allowed only for delivered orders");
        }
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setOrder(order);
        returnRequest.setUser(user);
        returnRequest.setReason(request.reason());
        returnRequest.setStatus(ReturnRequestStatus.PENDING);
        ReturnRequest saved = this.returnRequestRepository.save(returnRequest);
        this.notificationService.notifyUserByExternalId(
                order.getBrand().getUser().getExternalUserId(),
                "New return request",
                "Customer requested return for order " + order.getId(),
                "RETURN_REQUEST",
                saved.getId().toString()
        );
        return toReturnRequestResponse(saved);
    }

    @Transactional
    public List<ReturnRequestResponse> viewReturnRequests() {
        User user = ensureCurrentCustomerUser();
        return this.returnRequestRepository.findAllByUser_ExternalUserIdOrderByCreatedAtDesc(user.getExternalUserId())
                .stream().map(this::toReturnRequestResponse).toList();
    }

    @Transactional
    public List<NotificationResponse> viewNotifications() {
        ensureCurrentCustomerUser();
        return this.notificationService.findNotifications(currentUserProvider.externalId());
    }

    @Transactional
    public NotificationResponse markNotificationRead(UUID notificationId) {
        ensureCurrentCustomerUser();
        return this.notificationService.markAsRead(notificationId, currentUserProvider.externalId());
    }

    @Transactional
    public CartResponse reorder(UUID orderId) {
        User user = ensureCurrentCustomerUser();
        Order order = this.orderRepository.findByIdAndUser_ExternalUserId(orderId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        Cart cart = findOrCreateActiveCart(user);

        List<OrderItem> orderItems = order.getOrderItems() == null ? List.of() : order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            ProductItem productItem = orderItem.getProductItem();
            Size size = findSize(productItem, orderItem.getSizeName());
            if (size.getStock() < orderItem.getOrderQuantity()) {
                throw new IllegalArgumentException("Insufficient stock to reorder product item " + productItem.getId());
            }

            CartItem cartItem = this.cartItemRepository
                    .findByCart_IdAndProductItem_IdAndSizeName(cart.getId(), productItem.getId(), orderItem.getSizeName())
                    .orElseGet(CartItem::new);

            int newQuantity = orderItem.getOrderQuantity();
            if (cartItem.getId() != null) {
                newQuantity += cartItem.getQuantity();
            }
            if (size.getStock() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock to reorder product item " + productItem.getId());
            }

            cartItem.setCart(cart);
            cartItem.setProductItem(productItem);
            cartItem.setSizeName(orderItem.getSizeName());
            cartItem.setPrice(orderItem.getOrderPrice());
            cartItem.setQuantity(newQuantity);
            this.cartItemRepository.save(cartItem);
        }

        return buildCartResponse(cart);
    }

    @Transactional
    public ProductReviewResponse addReview(UUID productId, ProductReviewRequest request) {
        validateReviewRequest(request);

        User user = ensureCurrentCustomerUser();
        Product product = findProduct(productId);
        ensureDeliveredPurchase(user.getExternalUserId(), productId);
        if (this.productReviewRepository.existsByUser_ExternalUserIdAndProduct_Id(user.getExternalUserId(), productId)) {
            throw new IllegalArgumentException("You already reviewed this product");
        }

        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment());
        return toProductReviewResponse(this.productReviewRepository.save(review));
    }

    @Transactional
    public List<ProductReviewResponse> viewProductReviews(UUID productId) {
        findProduct(productId);
        return this.productReviewRepository.findAllByProduct_IdOrderByCreatedAtDesc(productId).stream()
                .map(this::toProductReviewResponse)
                .toList();
    }

    @Transactional
    public ProductReviewResponse updateReview(UUID reviewId, ProductReviewRequest request) {
        validateReviewRequest(request);
        User user = ensureCurrentCustomerUser();
        ProductReview review = this.productReviewRepository.findByIdAndUser_ExternalUserId(reviewId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setRating(request.rating());
        review.setComment(request.comment());
        return toProductReviewResponse(this.productReviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(UUID reviewId) {
        User user = ensureCurrentCustomerUser();
        ProductReview review = this.productReviewRepository.findByIdAndUser_ExternalUserId(reviewId, user.getExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        this.productReviewRepository.delete(review);
    }

    private void validateCartRequest(AddCartItemRequest request) {
        if (request.productItemId() == null) {
            throw new IllegalArgumentException("Product item id is required");
        }
        if (request.sizeName() == null || request.sizeName().isBlank()) {
            throw new IllegalArgumentException("Size name is required");
        }
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity should be above 0");
        }
    }

    private void validateCheckoutRequest(CheckoutRequest request) {
        if (request.brandId() == null) {
            throw new IllegalArgumentException("Brand id is required");
        }
        if (request.addressId() == null) {
            throw new IllegalArgumentException("Address id is required");
        }
        if (request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (request.transactionId() == null || request.transactionId().isBlank()) {
            throw new IllegalArgumentException("Transaction id is required");
        }
    }

    private void validateAddressRequest(CustomerAddressRequest request) {
        if (request.recipientName() == null || request.recipientName().isBlank()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (request.addressLine1() == null || request.addressLine1().isBlank()) {
            throw new IllegalArgumentException("Address line 1 is required");
        }
        if (request.city() == null || request.city().isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (request.country() == null || request.country().isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }
    }

    private void validateReviewRequest(ProductReviewRequest request) {
        if (request.rating() == null || request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating should be between 1 and 5");
        }
    }

    private void validatePaymentRetryRequest(PaymentRetryRequest request) {
        if (request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (request.transactionId() == null || request.transactionId().isBlank()) {
            throw new IllegalArgumentException("Transaction id is required");
        }
    }

    private User ensureCurrentCustomerUser() {
        return this.userRepository.findByExternalUserId(currentUserProvider.externalId())
                .orElseGet(() -> {
                    User createdUser = this.userSyncService.create(currentUserProvider);
                    createdUser.setRole(Role.CUSTOMER);
                    return this.userRepository.save(createdUser);
                });
    }

    private Product findProduct(UUID productId) {
        return this.productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private ProductItem findProductItem(UUID productItemId) {
        return this.productItemRepository.findById(productItemId)
                .orElseThrow(() -> new IllegalArgumentException("Product item not found"));
    }

    private CustomerAddress findAddress(UUID addressId, String externalUserId) {
        return this.customerAddressRepository.findByIdAndUser_ExternalUserId(addressId, externalUserId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    }

    private Size findSize(ProductItem productItem, String sizeName) {
        return productItem.getSizeList().stream()
                .filter(size -> size.getSizeName() != null && size.getSizeName().equalsIgnoreCase(sizeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Size not found for this product item"));
    }

    private Cart findOrCreateActiveCart(User user) {
        return this.cartRepository.findByUser_ExternalUserIdAndCartStatus(user.getExternalUserId(), CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCartStatus(CartStatus.ACTIVE);
                    return this.cartRepository.save(cart);
                });
    }

    private void unsetDefaultAddresses(String externalUserId) {
        List<CustomerAddress> addresses = this.customerAddressRepository.findAllByUser_ExternalUserId(externalUserId);
        for (CustomerAddress address : addresses) {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                address.setIsDefault(false);
            }
        }
        this.customerAddressRepository.saveAll(addresses);
    }

    private void ensureDeliveredPurchase(String externalUserId, UUID productId) {
        boolean delivered = this.orderItemRepository.existsDeliveredOrderItemByUserAndProduct(externalUserId, productId);
        if (!delivered) {
            throw new IllegalArgumentException("You can review only products from delivered orders");
        }
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> cartItems = this.cartItemRepository.findAllByCart_Id(cart.getId());
        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), itemResponses, totalPrice);
    }

    private FavoriteResponse toFavoriteResponse(Product product) {
        return new FavoriteResponse(
                product.getId(),
                product.getBrand().getId(),
                product.getProductNameEn(),
                product.getProductNameAr(),
                product.getThumbnail()
        );
    }

    private CustomerAddressResponse toAddressResponse(CustomerAddress address) {
        return new CustomerAddressResponse(
                address.getId(),
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getCountry(),
                address.getPostalCode(),
                address.getIsDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }

    private CustomerAddressResponse toAddressResponse(Order order) {
        return new CustomerAddressResponse(
                null,
                order.getShippingRecipientName(),
                order.getShippingPhoneNumber(),
                order.getShippingAddressLine1(),
                order.getShippingAddressLine2(),
                order.getShippingCity(),
                order.getShippingCountry(),
                order.getShippingPostalCode(),
                false,
                null,
                null
        );
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        ProductItem productItem = cartItem.getProductItem();
        Product product = productItem.getProduct();
        return new CartItemResponse(
                cartItem.getId(),
                productItem.getId(),
                product.getId(),
                product.getBrand().getId(),
                product.getProductNameEn(),
                productItem.getColor(),
                cartItem.getSizeName(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getTotalPrice(),
                product.getThumbnail()
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        ProductItem productItem = orderItem.getProductItem();
        Product product = productItem.getProduct();
        return new OrderItemResponse(
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

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaymentStatus()
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

    private CustomerOrderResponse toCustomerOrderResponse(Order order) {
        PaymentResponse paymentResponse = this.paymentRepository.findByOrder_Id(order.getId())
                .map(this::toPaymentResponse)
                .orElse(null);

        List<OrderItemResponse> items = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(this::toOrderItemResponse).toList();

        return new CustomerOrderResponse(
                order.getId(),
                order.getBrand().getId(),
                order.getBrand().getBrandName(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getPaidAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                order.getEstimatedDeliveryAt(),
                toAddressResponse(order),
                buildTimeline(order),
                items,
                paymentResponse
        );
    }

    private ProductReviewResponse toProductReviewResponse(ProductReview review) {
        return new ProductReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getEmail(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    private ReturnRequestResponse toReturnRequestResponse(ReturnRequest returnRequest) {
        return new ReturnRequestResponse(
                returnRequest.getId(),
                returnRequest.getOrder().getId(),
                returnRequest.getUser().getEmail(),
                returnRequest.getStatus(),
                returnRequest.getReason(),
                returnRequest.getBrandResponse(),
                returnRequest.getCreatedAt(),
                returnRequest.getResolvedAt()
        );
    }
}
