drop table if exists payment;

create table payment(
    id uuid not null primary key ,
    payment_status varchar(255) not null ,
    payment_method varchar(255) not null ,
    amount decimal not null ,
    provider varchar(255),
    provider_payment_id varchar(255),
    transaction_reference varchar(255),
    failure_reason varchar(255),
    created_at timestamp default now(),
    updated_at timestamp default now(),
    paid_at timestamp default now(),
    order_id uuid not null ,
    constraint fk_payment_order foreign key (order_id)
                    references orders(id)
);