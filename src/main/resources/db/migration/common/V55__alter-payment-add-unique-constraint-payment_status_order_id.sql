alter table payment add constraint  uq_order_payment_status
    unique (payment_status,order_id);