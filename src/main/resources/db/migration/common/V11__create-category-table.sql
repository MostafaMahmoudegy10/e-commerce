drop table if exists category;

create table category(
    id bigint primary key not null auto_increment,
    category_name varchar(255) not null ,
    parent_id bigint,
    constraint FK_category_id
                     foreign key (parent_id)
                     references category(id)
);