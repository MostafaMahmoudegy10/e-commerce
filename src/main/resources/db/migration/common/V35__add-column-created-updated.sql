alter table product
add column creation_date timestamp default now(),
add column update_date timestamp default now();