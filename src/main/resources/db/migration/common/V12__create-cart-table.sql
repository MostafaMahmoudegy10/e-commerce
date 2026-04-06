drop table if exists cart;

create table cart(
    id uuid primary key not null ,
    user_id uuid not null ,
    cart_status varchar(20) default 'active',
    created_at timestamp ,
    updated_at timestamp,
    constraint FK_cart_user_id
                 foreign key (user_id) references users(id)
);