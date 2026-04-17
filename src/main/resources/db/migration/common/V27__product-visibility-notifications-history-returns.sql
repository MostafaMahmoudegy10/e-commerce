alter table product add column is_active boolean default true not null;
alter table product add column is_archived boolean default false not null;

drop table if exists recently_viewed_product;
create table recently_viewed_product(
    id uuid primary key not null,
    user_id uuid not null,
    product_id uuid not null,
    created_at timestamp not null,
    viewed_at timestamp not null,
    constraint fk_recent_user_id foreign key (user_id) references users(id),
    constraint fk_recent_product_id foreign key (product_id) references product(id),
    constraint uk_recently_viewed_user_product unique(user_id, product_id)
);

drop table if exists notification;
create table notification(
    id uuid primary key not null,
    user_id uuid not null,
    title varchar(255) not null,
    message text not null,
    notification_type varchar(100) not null,
    reference_id varchar(255),
    is_read boolean not null,
    created_at timestamp not null,
    constraint fk_notification_user_id foreign key (user_id) references users(id)
);

drop table if exists return_request;
create table return_request(
    id uuid primary key not null,
    order_id uuid not null,
    user_id uuid not null,
    reason text not null,
    status varchar(50) not null,
    brand_response text,
    created_at timestamp not null,
    resolved_at timestamp,
    constraint fk_return_request_order_id foreign key (order_id) references orders(id),
    constraint fk_return_request_user_id foreign key (user_id) references users(id)
);
