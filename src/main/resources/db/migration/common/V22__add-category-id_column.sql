-- add the brand id column
alter table product add category_id uuid not null ;

-- add the relation
alter table  product add constraint fk_category_id_product
    foreign key(category_id) references  category(id);
