drop table if exists  product_item_image;

create table product_item_image(
  id binary(16) primary key not null ,
  image_url text not null ,
  public_id varchar(255) not null ,
  product_item_id binary(16),
   constraint FK_product_item_image_id
                   foreign key (product_item_id) references product_item(id)
);