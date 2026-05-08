alter table orders add column cart_id uuid not null ,
    add constraint fk_cart_order foreign key (cart_id)
    references cart(id);