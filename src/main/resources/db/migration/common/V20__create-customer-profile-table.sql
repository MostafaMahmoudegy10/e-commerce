CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    bio VARCHAR(1000),
    gender VARCHAR(10) NOT NULL,
    profile_image_url VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_profiles_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_customer_profiles_username ON customer_profiles(username);
CREATE UNIQUE INDEX idx_customer_profiles_user_id ON customer_profiles(user_id);
