drop table if exists size;

create table size(
  id uuid not null primary key,
  size_name varchar(255),
  stock bigint,
  product_item_id uuid ,
  constraint fk_product_item_id_size
                 foreign key (product_item_id) references product_item(id)
);