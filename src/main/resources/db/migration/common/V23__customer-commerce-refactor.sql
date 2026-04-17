drop table if exists favorite;

create table favorite(
    id uuid primary key not null,
    user_id uuid not null,
    product_id uuid not null,
    constraint fk_favorite_user_id
        foreign key (user_id) references users(id),
    constraint fk_favorite_product_id
        foreign key (product_id) references product(id),
    constraint uk_favorite_user_product unique(user_id, product_id)
);

alter table orders
    add column brand_id uuid;

alter table orders
    add constraint fk_order_brand_id
        foreign key (brand_id) references brand(id);

alter table cart_item
    drop constraint fk_cart_item_product;

alter table cart_item
    rename column product_id to product_item_id;

alter table cart_item
    add column size_name varchar(255);

alter table cart_item
    add constraint fk_cart_item_product_item
        foreign key (product_item_id) references product_item(id);

alter table order_item
    drop constraint fk_order_item_product_id;

alter table order_item
    rename column product_id to product_item_id;

alter table order_item
    add column size_name varchar(255);

alter table order_item
    add constraint fk_order_item_product_item_id
        foreign key (product_item_id) references product_item(id);
