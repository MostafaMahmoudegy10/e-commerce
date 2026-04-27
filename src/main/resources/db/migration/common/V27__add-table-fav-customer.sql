drop table if exists favourite;
create table favourite(
    id uuid primary key not null ,
    user_id uuid not null ,
    product_id uuid not null ,
    created_at timestamp default now(),
    constraint fk_fav_user_id foreign key (user_id)
        references users(id),
    constraint fk_fav_product_id foreign key (product_id)
                      references product(id),
    constraint uq_fav_id_user_id unique(product_id,user_id)
);