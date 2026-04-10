drop table if exists users;

CREATE TABLE users (
    id UUID PRIMARY KEY ,
    external_user_id varchar(100) NOT NULL unique ,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_email ON users(email);

CREATE UNIQUE INDEX idx_external_id ON users(external_user_id);
