alter table cart add column brand_id uuid not null ,
    add constraint fk_brand_cart foreign key (brand_id)
    references brand(id);