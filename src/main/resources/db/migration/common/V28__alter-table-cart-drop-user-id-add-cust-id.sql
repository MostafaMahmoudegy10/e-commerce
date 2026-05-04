alter table cart drop column user_id;
alter table cart add column customer_id uuid not null ;
alter table cart add constraint fk_customer_id_cart foreign key (customer_id)
references customer_profiles(id);