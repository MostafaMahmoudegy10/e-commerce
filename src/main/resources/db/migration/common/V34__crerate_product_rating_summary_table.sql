drop table if exists product_rating_summary;

create table product_rating_summary(
    id uuid primary key not null ,
    product_id uuid not null ,
    avg_rating decimal ,
    rating_count int ,
    rating1_count int,
    rating2_count int,
    rating3_count int,
    rating4_count int,
    rating5_count int,
    constraint fk_product_rating_summary foreign key (product_id)
                                   references product(id),
    constraint uq_product_avg_rate unique (product_id,avg_rating)
);