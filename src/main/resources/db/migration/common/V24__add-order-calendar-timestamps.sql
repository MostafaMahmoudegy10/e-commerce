alter table orders
    add column paid_at timestamp;

alter table orders
    add column shipped_at timestamp;

alter table orders
    add column delivered_at timestamp;

alter table orders
    add column cancelled_at timestamp;
