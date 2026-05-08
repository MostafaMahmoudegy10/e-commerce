alter table orders add column  customer_id uuid not null ,
                   add constraint  fk_customer_order foreign key (customer_id)
                       references customer_profiles(id);