drop table if exists  product_item;

create table product_item(
  id uuid primary key ,
  color varchar(20) not null ,
  product_id uuid not null ,
  stock integer not null ,
  sku varchar(30) not null ,
  constraint UK_product_variant unique(color,product_id),
  constraint FK_product_item_product_id
      foreign key (product_id) references product(id)
);