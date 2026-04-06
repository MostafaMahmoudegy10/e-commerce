drop table if exists order_item;

create table order_item(
    id binary(16) not null primary key ,
    order_price decimal not null ,
    order_quantity int not null ,
    total_price decimal not null ,
    product_id binary(16) not null ,
    order_id binary(16) not null ,
    constraint FK_order_item_order_id
                       foreign key (order_id) references orders(id),
    constraint FK_order_item_product_id
                       foreign key (product_id) references product(id)
);


