drop table if exists payment;

create table payment(
    id binary(16) not null primary key ,
    amount decimal not null ,
    payment_method varchar(255) not null ,
    payment_status varchar(255) not null ,
    transaction_id varchar(255) not null ,
    created_at timestamp not null ,
    order_id binary(16) not null ,
    constraint FK_payment_order_id
                    foreign key (order_id)
                    references orders(id)
);
