drop table if exists customer_address;

create table customer_address(
    id uuid primary key not null,
    user_id uuid not null,
    recipient_name varchar(255) not null,
    phone_number varchar(50) not null,
    address_line_1 text not null,
    address_line_2 text,
    city varchar(255) not null,
    country varchar(255) not null,
    postal_code varchar(100),
    is_default boolean not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_customer_address_user_id
        foreign key (user_id) references users(id)
);

alter table orders add column estimated_delivery_at timestamp;
alter table orders add column shipping_recipient_name varchar(255);
alter table orders add column shipping_phone_number varchar(50);
alter table orders add column shipping_address_line_1 text;
alter table orders add column shipping_address_line_2 text;
alter table orders add column shipping_city varchar(255);
alter table orders add column shipping_country varchar(255);
alter table orders add column shipping_postal_code varchar(100);
