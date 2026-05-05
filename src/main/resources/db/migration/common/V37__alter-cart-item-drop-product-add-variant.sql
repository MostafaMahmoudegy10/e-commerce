alter table cart_item
    add column product_variant_id uuid not null ,
    add column total_price decimal,
    add constraint fk_variant_item foreign key (product_variant_id)
references product_variants(id)