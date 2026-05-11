CREATE TABLE model_agreement_payment (
    id UUID PRIMARY KEY,
    agreement_id UUID NOT NULL UNIQUE,
    payment_status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    provider VARCHAR(255) NOT NULL,
    provider_payment_id VARCHAR(255),
    transaction_reference VARCHAR(255),
    failure_reason VARCHAR(2000),
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_model_agreement_payment_agreement
        FOREIGN KEY (agreement_id) REFERENCES model_agreement(id)
);

CREATE INDEX idx_model_agreement_payment_status
    ON model_agreement_payment(payment_status);
