alter table favourite drop constraint  uq_customer_brand_fav,
add constraint uq_customer_brand_fav unique (customer_id,brand_id,product_id);
