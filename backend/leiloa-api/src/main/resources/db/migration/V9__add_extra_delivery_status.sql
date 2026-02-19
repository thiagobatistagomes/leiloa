-- Migration: V9 - Add extra delivery status
-- Author: Thiago



ALTER TABLE deliveries 
    ADD COLUMN pending_at TIMESTAMP NULL,
    ADD COLUMN processing_at TIMESTAMP NULL,
    ADD COLUMN in_transit_at TIMESTAMP NULL,
    ADD COLUMN out_for_delivery_at TIMESTAMP NULL
