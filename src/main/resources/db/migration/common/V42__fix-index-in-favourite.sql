drop index idx_customer_brand_fav;
create  index idx_customer_brand_fav on favourite(customer_id,brand_id);