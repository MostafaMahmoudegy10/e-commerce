CREATE TABLE model_gig_request (
    id UUID PRIMARY KEY,
    request_number VARCHAR(50) NOT NULL UNIQUE,
    brand_id UUID NOT NULL,
    model_profile_id UUID NOT NULL,
    available_for VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(3000) NOT NULL,
    proposed_price NUMERIC(12, 2) NOT NULL,
    deadline TIMESTAMP,
    location VARCHAR(500),
    request_status VARCHAR(50) NOT NULL,
    rejection_reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    CONSTRAINT fk_model_gig_request_brand
        FOREIGN KEY (brand_id) REFERENCES brand(id),
    CONSTRAINT fk_model_gig_request_model_profile
        FOREIGN KEY (model_profile_id) REFERENCES model_profiles(id)
);

CREATE INDEX idx_model_gig_request_brand_id
    ON model_gig_request(brand_id);

CREATE INDEX idx_model_gig_request_model_profile_id
    ON model_gig_request(model_profile_id);

CREATE INDEX idx_model_gig_request_status
    ON model_gig_request(request_status);
