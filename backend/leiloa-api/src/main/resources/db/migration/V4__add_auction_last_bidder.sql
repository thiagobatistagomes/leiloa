ALTER TABLE auctions
    ADD COLUMN last_bidder_id UUID;

ALTER TABLE auctions
    ADD CONSTRAINT fk_auctions_last_bidder
    FOREIGN KEY (last_bidder_id)
    REFERENCES users(id);
