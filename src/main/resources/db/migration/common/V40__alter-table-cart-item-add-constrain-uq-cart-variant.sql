alter table cart_item
    add constraint uq_cart_variant unique(cart_id,product_variant_id);

CREATE INDEX idx_cart_item_cart
    ON cart_item(cart_id);

CREATE INDEX idx_cart_item_variant
    ON cart_item(product_variant_id);