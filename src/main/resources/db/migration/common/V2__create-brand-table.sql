drop table if exists brand;

create table brand(
   id binary(16) not null primary key ,
   brand_name varchar(255) not null ,
   brand_email varchar(255) unique ,
   description text not null ,
   user_id  binary(16) unique ,
   brand_image_url text,
   constraint FK_user_own_brand foreign key(user_id)
                  references users(id),
    index idx_brand_email(brand_email)
);
