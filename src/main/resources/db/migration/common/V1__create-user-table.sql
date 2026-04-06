CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,

    user_id binary(16) unique  not null ,

    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    age INT,

    is_model BOOLEAN NOT NULL DEFAULT FALSE,

    role VARCHAR(20) NOT NULL,

    gender CHAR(1),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

CREATE INDEX idx_email ON users(email);