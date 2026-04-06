drop table if exists review;

create table review(
  id binary(16) primary key not null ,
  user_id binary(16),
  rating decimal(5,2) check(rating between 0 and 100),
  review_comment text,
  constraint FK_review_user_id foreign key (user_id)
                   references users(id)
);