drop table if exists admins;

create table admins(
    brand_id uuid not null ,
    user_id uuid not null ,
    constraint FK_admins_brand_id
                   foreign key (brand_id) references brand(id),
    constraint FK_admins_user_id
                   foreign key (user_id) references users(id)
);