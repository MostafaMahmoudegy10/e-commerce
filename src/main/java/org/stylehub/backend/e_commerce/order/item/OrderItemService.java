package org.stylehub.backend.e_commerce.order.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.order.item.repoistory.OrderItemRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final static Logger logger = LoggerFactory.getLogger(OrderItemService.class);

    @Transactional
    public void saveAllOrderItems(UUID cartId, Order order) {
        List<CartItem>cartItems=this.cartItemRepository.findCartItemsByCart_Id(cartId);
        List<OrderItem>orderItems=
                cartItems.stream().map((item)->{
                    OrderItem orderItem=new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setOrderPrice(item.getPrice());
                    orderItem.setOrderQuantity(item.getQuantity());
                    orderItem.setVariant(item.getProductVariant());
                    orderItem.setTotalPrice(item.getTotalPrice());
                    return orderItem;
                }).toList();
        this.orderItemRepository.saveAll(orderItems);
    }
}
