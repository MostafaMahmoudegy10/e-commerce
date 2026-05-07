alter table favourite
drop column user_id,
    add column  customer_id uuid not null ,
    add constraint fk_customer_fav foreign key (customer_id)
        references customer_profiles(id),
    add column brand_id uuid not null ,
        add constraint fk_brand_fav foreign key (brand_id)
            references brand(id),
    add column update_at timestamp default now(),
    drop column  created_at ,
    add column create_at timestamp default now(),
    add constraint  uq_customer_brand_fav unique(customer_id,brand_id,id);

create index idx_customer_brand_fav on favourite(customer_id,brand_id,id);