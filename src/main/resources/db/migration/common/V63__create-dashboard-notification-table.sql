CREATE TABLE dashboard_notification (
    id UUID PRIMARY KEY,
    recipient_user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    reference_type VARCHAR(100),
    reference_id UUID,
    reference_code VARCHAR(100),
    action_url VARCHAR(500),
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dashboard_notification_recipient_user
        FOREIGN KEY (recipient_user_id) REFERENCES users(id)
);

CREATE INDEX idx_dashboard_notification_recipient_user_id
    ON dashboard_notification(recipient_user_id);

CREATE INDEX idx_dashboard_notification_type
    ON dashboard_notification(type);

CREATE INDEX idx_dashboard_notification_created_at
    ON dashboard_notification(created_at);
