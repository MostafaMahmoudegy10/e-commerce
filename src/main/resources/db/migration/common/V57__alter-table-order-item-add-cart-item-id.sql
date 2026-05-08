alter table order_item add column cart_item_id uuid not null,
add constraint  fk_cart_item_order_item foreign key (cart_item_id)
references order_item(id);