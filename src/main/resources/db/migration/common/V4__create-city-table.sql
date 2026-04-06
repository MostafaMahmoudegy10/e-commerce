drop table if exists city;

create table city(
    id bigint primary key  auto_increment,
    country_id bigint,
    city_code varchar(10),
    city_name varchar(50),
    constraint FK_city_country_id foreign key (country_id)
                 references country(id)
);