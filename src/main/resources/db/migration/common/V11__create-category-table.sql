drop table if exists category;

create table category(
    id uuid primary key not null ,
    category_name_en varchar(255) not null ,
    category_name_ar varchar(255) not null ,
    parent_id uuid,
    category_gender varchar(255),
    brand_id uuid ,
    category_description_en text ,
    category_description_ar text,
    image_url text not null ,
    public_id varchar(255) not null ,
    constraint FK_category_id
                     foreign key (parent_id)
                     references category(id),
    foreign key (brand_id) references brand(id)
);