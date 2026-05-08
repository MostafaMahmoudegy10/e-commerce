drop table  if exists shipping_address;

create table shipping_address(
    id uuid primary key not null ,
    city_en varchar(255) not null ,
    city_ar varchar(255) not null ,
    street_en varchar(255) not null ,
    street_ar varchar(255) not null ,
    building_number int not null ,
    customer_id uuid not null ,
    constraint fk_customer_address
                             foreign key (customer_id) references customer_profiles(id)
);