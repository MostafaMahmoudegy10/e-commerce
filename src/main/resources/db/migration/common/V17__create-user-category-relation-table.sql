drop table if exists users_category;

create table users_category(
    user_id uuid not null ,
    category_id uuid not null ,
    primary key (user_id,category_id),
    foreign key (user_id) references users(id),
    foreign key (category_id) references category(id)
)