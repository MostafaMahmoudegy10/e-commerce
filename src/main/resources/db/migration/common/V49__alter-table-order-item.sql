alter table  order_item drop column product_id,
    add column product_variant_id uuid not null ,
    add constraint fk_variant_order_item foreign key (product_variant_id)
        references product_variants(id);