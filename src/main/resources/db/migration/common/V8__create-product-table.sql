drop table if exists product;

create table product(
    id binary(16) not null primary key ,
    product_name varchar(255) not null ,
    product_description text not null ,
    thumbnail text not null
);