CREATE TABLE model_review (
    id UUID PRIMARY KEY,
    agreement_id UUID NOT NULL UNIQUE,
    brand_id UUID NOT NULL,
    model_profile_id UUID NOT NULL,
    stars INTEGER NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_model_review_agreement
        FOREIGN KEY (agreement_id) REFERENCES model_agreement(id),
    CONSTRAINT fk_model_review_brand
        FOREIGN KEY (brand_id) REFERENCES brand(id),
    CONSTRAINT fk_model_review_model_profile
        FOREIGN KEY (model_profile_id) REFERENCES model_profiles(id),
    CONSTRAINT ck_model_review_stars
        CHECK (stars BETWEEN 1 AND 5)
);

CREATE INDEX idx_model_review_brand_id
    ON model_review(brand_id);

CREATE INDEX idx_model_review_model_profile_id
    ON model_review(model_profile_id);
