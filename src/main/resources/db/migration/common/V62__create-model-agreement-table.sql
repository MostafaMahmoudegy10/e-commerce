CREATE TABLE model_agreement (
    id UUID PRIMARY KEY,
    agreement_number VARCHAR(50) NOT NULL UNIQUE,
    brand_id UUID NOT NULL,
    model_profile_id UUID NOT NULL,
    request_id UUID NOT NULL UNIQUE,
    available_for VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(3000) NOT NULL,
    agreed_price NUMERIC(12, 2) NOT NULL,
    deadline TIMESTAMP,
    location VARCHAR(500),
    agreement_status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,
    delivered_at TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_model_agreement_brand
        FOREIGN KEY (brand_id) REFERENCES brand(id),
    CONSTRAINT fk_model_agreement_model_profile
        FOREIGN KEY (model_profile_id) REFERENCES model_profiles(id),
    CONSTRAINT fk_model_agreement_request
        FOREIGN KEY (request_id) REFERENCES model_gig_request(id)
);

CREATE INDEX idx_model_agreement_brand_id
    ON model_agreement(brand_id);

CREATE INDEX idx_model_agreement_model_profile_id
    ON model_agreement(model_profile_id);

CREATE INDEX idx_model_agreement_status
    ON model_agreement(agreement_status);
