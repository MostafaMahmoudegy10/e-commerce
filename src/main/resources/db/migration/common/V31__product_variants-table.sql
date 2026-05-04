CREATE TABLE product_variants (
      id UUID PRIMARY KEY,
      product_color_id UUID NOT NULL,
      size VARCHAR(30) NOT NULL,
      stock INTEGER NOT NULL,
      sku varchar(255) not null unique ,
      price_override DECIMAL(19,2),
      CONSTRAINT fk_product_variants_product FOREIGN KEY (product_color_id) REFERENCES product_colors(id) ON DELETE CASCADE,
      CONSTRAINT uk_product_variant_product_color_id_size UNIQUE (product_color_id,size)
);
