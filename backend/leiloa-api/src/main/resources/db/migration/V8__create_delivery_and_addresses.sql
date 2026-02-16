
-- Migration: V8 - Create addresses, payment_addresses, deliveries
-- Author: Thiago


-- 1) TABLE: addresses
CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    label VARCHAR(255),
    street VARCHAR(255) NOT NULL,
    number VARCHAR(50),
    complement VARCHAR(255),
    district VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(50) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Index para otimizar consultas por user_id
CREATE INDEX idx_addresses_user_id ON addresses(user_id);



-- 2) TABLE: payment_addresses
CREATE TABLE payment_addresses (
    id UUID PRIMARY KEY,
    payment_id UUID UNIQUE NOT NULL,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(50),
    complement VARCHAR(255),
    district VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(50) NOT NULL,
    reference_note VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payment_addresses_payment FOREIGN KEY (payment_id)
        REFERENCES payments(id)
        ON DELETE CASCADE
);

-- Index para otimizar consultas por payment_id
CREATE INDEX idx_payment_addresses_payment_id ON payment_addresses(payment_id);



-- 3) TABLE: deliveries
CREATE TABLE deliveries (
    id UUID PRIMARY KEY,
    payment_id UUID UNIQUE NOT NULL,
    address_id UUID NOT NULL,
    delivery_method VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    tracking_code VARCHAR(255),
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    return_requested_at TIMESTAMP,
    returned_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_deliveries_payment FOREIGN KEY (payment_id)
        REFERENCES payments(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_deliveries_address FOREIGN KEY (address_id)
        REFERENCES payment_addresses(id)
        ON DELETE CASCADE
);

-- Indexes para otimizar consultas por payment_id e address_id
CREATE INDEX idx_deliveries_payment_id ON deliveries(payment_id);
CREATE INDEX idx_deliveries_address_id ON deliveries(address_id);
