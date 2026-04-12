-- add the brand id column
alter table product add brand_id uuid not null ;

-- add the relation
alter table  product add constraint fk_brand_id_product
    foreign key(brand_id) references brand(id);

-- add unique constaint
alter table product add constraint UQ_brand_id_product_name_en
    unique(brand_id,product_name_en);