package org.stylehub.backend.e_commerce.platform.config.rabbitmq;

public final class RabbitMqNames {

    private RabbitMqNames() {}

    public static final String ECOMMERCE_EVENTS_EXCHANGE = "ecommerce-events";

    public static final String STOCK_INSUFFICIENT_ROUTING_KEY =
            "ecommerce.cart.stock.insufficient";

    public static final String STOCK_INSUFFICIENT_QUEUE =
            "stylehub.email.stock.insufficient.queue";

    //queues for order
    public static final String ORDER_CREATED_QUEUE=
            "ecommerce.order.created.queue";

    //routes for order
    public static final String ORDER_CREATED_ROUTING_KEY =
            "ecommerce.order.created";


}
