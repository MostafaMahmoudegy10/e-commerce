drop table if exists product;

create table product(
    id uuid not null primary key ,
    product_name_en varchar(255) not null ,
    product_name_ar varchar(255) not null ,
    product_description_en text not null ,
    product_description_ar text not null ,
    price decimal(10,2) not null ,
    thumbnail text not null,
    public_id text not null
);