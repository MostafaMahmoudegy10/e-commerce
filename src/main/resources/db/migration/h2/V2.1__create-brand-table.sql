create table brand(
      id uuid not null primary key,
      brand_name varchar(255) not null,
      brand_email varchar(255),
      description varchar(255) not null,
      user_id uuid,
      brand_image_url text,
      public_id text,
      constraint FK_user_own_brand foreign key(user_id)
          references users(id)
);

create index idx_brand_email on brand(brand_email);