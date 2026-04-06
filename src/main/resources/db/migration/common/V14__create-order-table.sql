drop table if exists orders;

create table orders(
    id uuid primary key not null ,
    user_id uuid not null ,
    order_status varchar(30) default 'PENDING',
    total_price decimal not null ,
    created_at timestamp not null,
    constraint FK_order_user_id
                    foreign key (user_id)
                    references users(id)
);