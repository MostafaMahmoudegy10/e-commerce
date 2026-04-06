drop table if exists  product_item;

create table product_item(
  id binary(16) primary key not null ,
  color varchar(20) not null ,
  size varchar(3) not null,
  product_id binary(16) not null ,
  stock integer not null ,
  price decimal(10,2) not null ,
  sku varchar(30) not null ,
  constraint UK_product_variant unique(color,size,product_id),
  constraint FK_product_item_product_id
      foreign key (product_id) references product(id)
);