CREATE TABLE model_agreement_submission (
    id UUID PRIMARY KEY,
    agreement_id UUID NOT NULL,
    note VARCHAR(3000),
    review_status VARCHAR(50) NOT NULL,
    review_feedback VARCHAR(3000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    CONSTRAINT fk_model_agreement_submission_agreement
        FOREIGN KEY (agreement_id) REFERENCES model_agreement(id)
);

CREATE INDEX idx_model_agreement_submission_agreement_id
    ON model_agreement_submission(agreement_id);

CREATE INDEX idx_model_agreement_submission_review_status
    ON model_agreement_submission(review_status);

CREATE TABLE model_agreement_submission_asset (
    id UUID PRIMARY KEY,
    submission_id UUID NOT NULL,
    asset_url VARCHAR(1000) NOT NULL,
    public_id VARCHAR(500) NOT NULL,
    mime_type VARCHAR(255),
    asset_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_model_agreement_submission_asset_submission
        FOREIGN KEY (submission_id) REFERENCES model_agreement_submission(id)
);

CREATE INDEX idx_model_agreement_submission_asset_submission_id
    ON model_agreement_submission_asset(submission_id);
