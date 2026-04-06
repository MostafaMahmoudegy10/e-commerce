drop table if exists cart_item;

create table cart_item(
    id uuid primary key not null ,
    price decimal(10,2) not null ,
    quantity int not null ,
    product_id uuid not null ,
    cart_id uuid not null ,
    constraint FK_cart_item_product
                      foreign key (product_id) references product(id),
    constraint FK_cart_item_cart_id
        foreign key (cart_id) references cart(id)
);