alter table cart
add constraint uq_customer_brand_active_cart
    unique (customer_id,brand_id,cart_status);

CREATE INDEX idx_cart_customer_brand_status
    ON cart(customer_id, brand_id, cart_status);