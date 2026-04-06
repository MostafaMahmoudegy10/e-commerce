drop table if exists city;

create table city(
    id UUID  primary key ,
    country_id uuid,
    city_code varchar(10),
    city_name varchar(50),
    constraint FK_city_country_id foreign key (country_id)
                 references country(id)
);