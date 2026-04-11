alter table category add column if not exists category_name_ar varchar(255);
alter table category add column if not exists category_name_en varchar(255);

alter table product add column if not exists product_name_ar varchar(255);
alter table product add column if not exists product_name_en varchar(255);
alter table product add column if not exists product_description_ar text;
alter table product add column if not exists product_description_en text;
