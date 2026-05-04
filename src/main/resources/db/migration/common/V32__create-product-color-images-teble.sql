drop table if exists  product_color_images;

create table product_color_images(
  id uuid primary key not null ,
  image_url text not null ,
  public_id varchar(255) not null ,
  product_color_id uuid not null ,
   constraint FK_product_item_image_id
                   foreign key (product_color_id) references product_colors(id)
);