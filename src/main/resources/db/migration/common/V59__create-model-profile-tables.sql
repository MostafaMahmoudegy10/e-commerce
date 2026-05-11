CREATE TABLE model_profiles (
    id UUID PRIMARY KEY,
    model_name VARCHAR(255),
    bio VARCHAR(2000),
    city VARCHAR(255),
    model_email varchar(255),
    age INTEGER,
    height_cm INTEGER,
    weight_kg INTEGER,
    hair_color VARCHAR(100),
    rating_avg NUMERIC(5, 2) DEFAULT 0,
    rating_count INTEGER DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    body_type VARCHAR(50),
    skin_tone VARCHAR(50),
    gender VARCHAR(20),
    user_id UUID UNIQUE,
    CONSTRAINT fk_model_profiles_users
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_model_profiles_user_id
    ON model_profiles(user_id);

CREATE TABLE model_profile_available_for (
    id UUID PRIMARY KEY,
    model_profile_id UUID NOT NULL,
    available_for VARCHAR(100),
    price_per_session NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_model_profile_available_for_profile
        FOREIGN KEY (model_profile_id) REFERENCES model_profiles(id),
    CONSTRAINT uq_model_available_for
        UNIQUE (model_profile_id, available_for, price_per_session)
);

CREATE INDEX idx_model_profile_available_for_profile_id
    ON model_profile_available_for(model_profile_id);

CREATE TABLE model_profile_images (
    id UUID PRIMARY KEY,
    public_id VARCHAR(255),
    profile_image VARCHAR(1000),
    model_profile_id UUID,
    CONSTRAINT fk_model_profile_images_profile
        FOREIGN KEY (model_profile_id) REFERENCES model_profiles(id)
);

CREATE INDEX idx_model_profile_images_profile_id
    ON model_profile_images(model_profile_id);
