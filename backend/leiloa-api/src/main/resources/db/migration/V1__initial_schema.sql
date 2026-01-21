-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- ROLES
-- =========================
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- =========================
-- USER_ROLES (N:N)
-- =========================
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- =========================
-- CATEGORIES
-- =========================
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- =========================
-- ITEMS
-- =========================
CREATE TABLE items (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url TEXT,

    category_id UUID NOT NULL,
    seller_id UUID NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_items_category
        FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_items_seller
        FOREIGN KEY (seller_id) REFERENCES users(id)
);

-- =========================
-- AUCTIONS
-- =========================
CREATE TABLE auctions (
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL,
    user_id UUID NOT NULL,

    start_price NUMERIC(12,2) NOT NULL,
    min_increment NUMERIC(12,2) NOT NULL,

    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,

    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_auctions_item
        FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_auctions_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- BIDS
-- =========================
CREATE TABLE bids (
    id UUID PRIMARY KEY,
    auction_id UUID NOT NULL,
    user_id UUID NOT NULL,

    value NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bids_auction
        FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_bids_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- COMMENTS
-- =========================
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    auction_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_comment_id UUID,

    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_auction
        FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comments_parent
        FOREIGN KEY (parent_comment_id) REFERENCES comments(id)
);

-- =========================
-- PAYMENTS
-- =========================
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    auction_id UUID NOT NULL,
    winner_id UUID NOT NULL,

    value NUMERIC(12,2) NOT NULL,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_payments_auction
        FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT fk_payments_winner
        FOREIGN KEY (winner_id) REFERENCES users(id)
);
