alter table orders add column  brand_id uuid not null ,
                   add constraint  fk_brand_order foreign key (brand_id)
                       references brand(id);