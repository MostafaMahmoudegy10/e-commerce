drop table if exists product_review;

create table product_review(
    id uuid primary key not null,
    rating int not null,
    comment text,
    created_at timestamp not null,
    user_id uuid not null,
    product_id uuid not null,
    constraint fk_product_review_user_id
        foreign key (user_id) references users(id),
    constraint fk_product_review_product_id
        foreign key (product_id) references product(id),
    constraint uk_product_review_user_product unique(user_id, product_id)
);
