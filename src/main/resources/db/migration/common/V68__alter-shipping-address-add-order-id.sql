alter table shipping_address
    add column order_id uuid;

alter table shipping_address
    add constraint fk_shipping_address_order
        foreign key (order_id) references orders(id);

create unique index uq_shipping_address_order_id
    on shipping_address(order_id)
    where order_id is not null;
