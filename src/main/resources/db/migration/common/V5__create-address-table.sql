drop table if exists address;

create table address(
    id uuid primary key ,
    city_id uuid,
    street_name varchar(50),
    user_id uuid,
    constraint FK_address_user_id
                    foreign key (user_id) references users(id),
    constraint FK_address_city_id
                    foreign key (city_id)
                    references city(id)
);