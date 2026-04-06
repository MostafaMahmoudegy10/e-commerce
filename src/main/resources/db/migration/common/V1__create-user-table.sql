drop table if exists users;

CREATE TABLE users (
    id UUID PRIMARY KEY ,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT,
    is_model BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(20) NOT NULL,
    gender CHAR(1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_email ON users(email);