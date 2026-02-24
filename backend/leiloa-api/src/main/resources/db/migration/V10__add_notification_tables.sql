-- Migration: V10 - Add notification tables
-- Author: Thiago





CREATE TABLE notification_types (
    id UUID PRIMARY KEY,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    type_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data JSONB,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notifications_type
        FOREIGN KEY (type_id) REFERENCES notification_types(id)
        ON DELETE CASCADE
);


CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_unread ON notifications(user_id, read_at);


CREATE TABLE user_devices (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    device_token VARCHAR(255) NOT NULL,
    device_type VARCHAR(50),
    user_agent VARCHAR(500),
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_user_devices_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);


CREATE INDEX idx_user_devices_user ON user_devices(user_id);
CREATE UNIQUE INDEX uq_user_devices_token ON user_devices(device_token);