drop table if exists country;

create table country(
    id bigint primary key auto_increment,
    country_name varchar(30) unique,
    country_code varchar(3) unique,
    phone_code VARCHAR(5) unique ,
    currency_code CHAR(3)
);