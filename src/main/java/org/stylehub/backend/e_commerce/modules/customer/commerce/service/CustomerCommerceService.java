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
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.*;
import org.stylehub.backend.e_commerce.modules.customer.favorite.entity.Favorite;
import org.stylehub.backend.e_commerce.modules.customer.favorite.repository.FavoriteRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.order.item.repository.OrderItemRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
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
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BrandRepository brandRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

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
    public CartResponse viewCart() {
        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        return buildCartResponse(cart);
    }

    @Transactional
    public CheckoutResponse checkoutBrand(CheckoutRequest request) {
        validateCheckoutRequest(request);

        User user = ensureCurrentCustomerUser();
        Cart cart = findOrCreateActiveCart(user);
        Brand brand = this.brandRepository.findById(request.brandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));

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
                orderItems.stream().map(this::toOrderItemResponse).toList(),
                toPaymentResponse(savedPayment)
        );
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
}
