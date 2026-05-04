CREATE TABLE product_colors (
      id UUID PRIMARY KEY,
      color_code varchar(255) not null ,
      product_id uuid not null ,
      constraint fk_product_product_color_id
                              foreign key (product_id) references product(id),
    constraint uq_color_code_product_id unique (color_code,product_id)
);
