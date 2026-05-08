package org.stylehub.backend.e_commerce.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.customer.dto.order.CheckoutResponse;
import org.stylehub.backend.e_commerce.customer.dto.order.OrderCreationRequest;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.order.address.repository.ShippingAddressRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.item.OrderItemService;
import org.stylehub.backend.e_commerce.order.payment.PaymentService;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.order.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.order.publisher.OrderPublisherEvents;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;
import org.stylehub.backend.e_commerce.platform.mail.publisher.EmailEventPublisher;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final ShippingAddressRepository  shippingAddressRepository;
    private final CurrentUserProvider  currentUserProvider;
    private final CustomerProfileService  customerProfileService;
    private final BrandService  brandService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderPublisherEvents  orderPublisherEvents;
    private final EmailEventPublisher  emailEventPublisher;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final PaymentRepository paymentRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerOrderService.class);

    @Transactional
    public CheckoutResponse createOrder(OrderCreationRequest orderCreationRequest, String externalBrandId){
        LOGGER.info("OrderCreationRequest:{}",orderCreationRequest);
        // first we will check if this cart is active and belong
           // get the customer profile
            CustomerProfile customerProfile =this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());
           // get the brand
            Brand brand=this.brandService.findBrandByExternalId(externalBrandId);
           // get active cart with this brand and customer
           Cart cart=getValidActiveCart(customerProfile,brand);

           // validate stock for every cart item
           validateStock(this.cartItemRepository.findCartItemsByCart_Id(cart.getId()),brand,customerProfile);

           // get total price for subTotalCartItemsPrice
            BigDecimal totalPrice= this.cartItemRepository.findTotalPriceByCartId(cart.getId());

            Optional<Order> orderExisted=this.orderRepository.findOrderByCart_IdAndOrderStatus(cart.getId(),OrderStatus.PENDING);
            if (orderExisted.isPresent()){
                Order oldOrder=orderExisted.get();
                oldOrder.setOrderStatus(OrderStatus.CANCELLED);
                Order cancelled=orderRepository.save(oldOrder);
                Payment payment = paymentRepository.findTopByOrder_IdAndPaymentStatusOrderByCreatedAtDesc(cancelled.getId(),PaymentStatus.PENDING)
                        .orElseThrow(()->new RuntimeException("Payment not found"));
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }

            Order newOrder=new Order();
            newOrder.setCustomer(customerProfile);
            newOrder.setBrand(brand);
            newOrder.setCart(cart);
            newOrder.setTotalPrice(totalPrice);
            newOrder.setOrderStatus(OrderStatus.PENDING);
            newOrder.setOrderNumber(newOrder.generateOrderNumber());
            Order order= orderRepository.save(newOrder);
            // now convert to order items
            this.orderItemService.saveAllOrderItems(cart.getId(),order);
            // now you can add an event
            this.addCreationEvent(order,customerProfile,brand);


        return new CheckoutResponse(
                order.getId(),
                order.getOrderNumber(),
                brand.getBrandName(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                PaymentStatus.PENDING,
                "Order created successfully. Complete payment to confirm your order."
        );

    }
    private void validateStock(List<CartItem> cartItems,Brand brand,CustomerProfile customerProfile){
        cartItems.stream().forEach(cartItem->{
            // we get the variant
            ProductVariant productVariant =cartItem.getProductVariant();
            LOGGER.info("starting checking between quantity and requested in cart_item in this snapshot{}", Instant.now());
            if(cartItem.getQuantity()>productVariant.getStock()){
                InsufficientStockRequestedEvent event=new InsufficientStockRequestedEvent(
                        brand.getBrandName(),
                        brand.getBrandEmail(),
                        productVariant.getProductColor().getProduct().getProductNameEn(),
                        productVariant.getSku(),
                        cartItem.getQuantity(),
                        productVariant.getStock(),
                        customerProfile.getUsername(),
                        Instant.now()
                );
                emailEventPublisher.publishInsufficientStockRequested(event);
                throw new IllegalArgumentException("Requested Quantity of "+productVariant.getSku()+" exceeds stock "+productVariant.getStock());
            }
        });
    }
    private Cart getValidActiveCart(CustomerProfile customerProfile, Brand brand) {
        Cart cart = cartRepository
                .findCartByCartStatusAndCustomer_IdAndBrand_Id(
                        CartStatus.ACTIVE,
                        customerProfile.getId(),
                        brand.getId()
                )
                .orElseThrow(() -> new IllegalStateException("Active cart not found"));

        if (!cartItemRepository.existsByCart_Id(cart.getId())) {
            throw new IllegalStateException("Cart is empty. Please add items first.");
        }

        return cart;
    }
    private void addCreationEvent(Order order,CustomerProfile customerProfile,Brand brand){
        OrderCreationEvent orderCreationEvent=new OrderCreationEvent(
                order.getId(),
                order.getOrderNumber(),
                customerProfile.getId(),
                customerProfile.getUsername(),
                customerProfile.getCustomerEmail(),
                brand.getId(),
                brand.getBrandName(),
                brand.getBrandEmail(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                PaymentStatus.PENDING,
                Instant.now()

        );
        this.orderPublisherEvents.orderCreated(orderCreationEvent);
    }
}
