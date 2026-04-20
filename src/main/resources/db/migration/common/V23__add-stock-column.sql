alter table product_item drop column if exists stock ;
alter table product_item add column stock integer not null;