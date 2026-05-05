drop table if exists product_rating;

create table product_rating(
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       product_id UUID NOT NULL,
       customer_id UUID NOT NULL,
       stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
       comment TEXT,
       created_at TIMESTAMP DEFAULT now(),
       updated_at TIMESTAMP DEFAULT now(),
        constraint fk_product_rate
                           foreign key (product_id) references product(id),

        constraint fk_customer_rate
                           foreign key (customer_id) references customer_profiles(id),

        constraint uq_product_customer unique (product_id,customer_id)
);