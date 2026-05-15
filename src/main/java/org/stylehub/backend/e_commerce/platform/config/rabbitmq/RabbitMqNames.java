package org.stylehub.backend.e_commerce.platform.config.rabbitmq;

public final class RabbitMqNames {

    private RabbitMqNames() {}

    public static final String ECOMMERCE_EVENTS_EXCHANGE = "ecommerce-events";

    public static final String STOCK_INSUFFICIENT_ROUTING_KEY =
            "ecommerce.cart.stock.insufficient";

    public static final String STOCK_INSUFFICIENT_QUEUE =
            "stylehub.email.stock.insufficient.queue";

    public static final String MODEL_REVIEW_REQUESTED_EMAIL_ROUTING_KEY =
            "ecommerce.model.review.requested.email";

    public static final String MODEL_REVIEW_REQUESTED_EMAIL_QUEUE =
            "stylehub.email.model.review.requested.queue";

    public static final String PRODUCT_REVIEW_REQUESTED_EMAIL_ROUTING_KEY =
            "ecommerce.product.review.requested.email";

    public static final String PRODUCT_REVIEW_REQUESTED_EMAIL_QUEUE =
            "stylehub.email.product.review.requested.queue";

    //queues for order
    public static final String ORDER_CREATED_QUEUE=
            "ecommerce.order.created.queue";

    //routes for order
    public static final String ORDER_CREATED_ROUTING_KEY =
            "ecommerce.order.created";
    public static final String ORDER_PAID_ROUTING_KEY =
            "ecommerce.order.paid";
    public static final String ORDER_SHIPPED_ROUTING_KEY =
            "ecommerce.order.shipped";
    public static final String ORDER_DELIVERED_ROUTING_KEY =
            "ecommerce.order.delivered";
    public static final String INVENTORY_LOW_STOCK_ROUTING_KEY =
            "ecommerce.inventory.low.stock";

    public static final String MODEL_GIG_REQUEST_CREATED_ROUTING_KEY =
            "ecommerce.model.gig.request.created";

    public static final String MODEL_GIG_REQUEST_ACCEPTED_ROUTING_KEY =
            "ecommerce.model.gig.request.accepted";

    public static final String MODEL_GIG_REQUEST_REJECTED_ROUTING_KEY =
            "ecommerce.model.gig.request.rejected";

    public static final String MODEL_GIG_REQUEST_CANCELLED_ROUTING_KEY =
            "ecommerce.model.gig.request.cancelled";

    public static final String MODEL_AGREEMENT_SUBMITTED_ROUTING_KEY =
            "ecommerce.model.agreement.submitted";

    public static final String MODEL_AGREEMENT_REVISION_REQUESTED_ROUTING_KEY =
            "ecommerce.model.agreement.revision.requested";

    public static final String MODEL_AGREEMENT_APPROVED_ROUTING_KEY =
            "ecommerce.model.agreement.approved";

    public static final String MODEL_AGREEMENT_PAYMENT_SUCCEEDED_ROUTING_KEY =
            "ecommerce.model.agreement.payment.succeeded";

    public static final String MODEL_AGREEMENT_PAYMENT_FAILED_ROUTING_KEY =
            "ecommerce.model.agreement.payment.failed";

    public static final String MODEL_GIG_REQUEST_CREATED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.created.queue";
    public static final String ORDER_CREATED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.order.created.queue";
    public static final String ORDER_PAID_NOTIFICATION_QUEUE =
            "stylehub.dashboard.order.paid.queue";
    public static final String ORDER_SHIPPED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.order.shipped.queue";
    public static final String ORDER_DELIVERED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.order.delivered.queue";
    public static final String INVENTORY_LOW_STOCK_NOTIFICATION_QUEUE =
            "stylehub.dashboard.inventory.low.stock.queue";

    public static final String MODEL_GIG_REQUEST_ACCEPTED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.accepted.queue";

    public static final String MODEL_GIG_REQUEST_REJECTED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.rejected.queue";

    public static final String MODEL_GIG_REQUEST_CANCELLED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.gig.request.cancelled.queue";

    public static final String MODEL_AGREEMENT_SUBMITTED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.agreement.submitted.queue";

    public static final String MODEL_AGREEMENT_REVISION_REQUESTED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.agreement.revision.requested.queue";

    public static final String MODEL_AGREEMENT_APPROVED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.agreement.approved.queue";

    public static final String MODEL_AGREEMENT_PAYMENT_SUCCEEDED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.agreement.payment.succeeded.queue";

    public static final String MODEL_AGREEMENT_PAYMENT_FAILED_NOTIFICATION_QUEUE =
            "stylehub.dashboard.model.agreement.payment.failed.queue";

}
