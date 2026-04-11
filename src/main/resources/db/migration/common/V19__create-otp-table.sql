drop table if exists otp;

create  table otp(
    id uuid primary key not null ,
    user_id uuid not null ,
    otp_hash varchar(255) not null ,
    purpose varchar(255) not null ,
    channel varchar(255) not null ,
    recipient varchar(255) not null ,
    expires_at timestamp with time zone not null ,
    consumed_at timestamp with time zone  ,
    attempt_count integer not null  default 0,
    max_attempts integer default  5,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null  default now()
);

create index idx_otp_user_id on otp(user_id);

create index  idx_otp_recipient_purpose_created_at on otp(recipient,purpose,created_at);