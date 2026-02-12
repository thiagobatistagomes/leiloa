-- Migration: Add extra fields to users table (updated_at, phone_number, etc.)
-- Author: Thiago

ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN status_changed_by UUID,
    ADD COLUMN last_login_at TIMESTAMP,
    ADD COLUMN phone_number VARCHAR(255);
