drop table if exists address;

create table address(
    id binary(16) primary key not null ,
    city_id bigint,
    street_name varchar(50),
    user_id binary(16),
    constraint FK_address_user_id
                    foreign key (user_id) references users(id),
    constraint FK_address_city_id
                    foreign key (city_id)
                    references city(id)
);