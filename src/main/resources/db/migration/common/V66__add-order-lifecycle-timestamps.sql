alter table orders
    add column shipped_at timestamp,
    add column delivered_at timestamp,
    add column cancelled_at timestamp;
