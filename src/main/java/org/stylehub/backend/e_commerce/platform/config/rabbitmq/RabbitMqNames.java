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

    public static final String MODEL_GIG_REQUEST_CREATED_ROUTING_KEY =
            "ecommerce.model.gig.request.created";

    public static final String MODEL_GIG_REQUEST_ACCEPTED_ROUTING_KEY =
            "ecommerce.model.gig.request.accepted";

    public static final String MODEL_GIG_REQUEST_REJECTED_ROUTING_KEY =
            "ecommerce.model.gig.request.rejected";

    public static final String MODEL_GIG_REQUEST_CREATED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.created.queue";

    public static final String MODEL_GIG_REQUEST_ACCEPTED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.accepted.queue";

    public static final String MODEL_GIG_REQUEST_REJECTED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.rejected.queue";

}
