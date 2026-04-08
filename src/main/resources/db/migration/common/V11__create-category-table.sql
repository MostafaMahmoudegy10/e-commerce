drop table if exists category;

create table category(
    id uuid primary key not null ,
    category_name varchar(255) not null ,
    parent_id uuid,
    category_gender varchar(255),
    brand_id uuid ,
    constraint FK_category_id
                     foreign key (parent_id)
                     references category(id),
    foreign key (brand_id) references brand(id)
);