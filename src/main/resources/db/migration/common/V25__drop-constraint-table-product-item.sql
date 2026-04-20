alter table product_item drop constraint if exists UK_product_variant ;
alter table product_item add constraint UK_product_varient unique (id,color);