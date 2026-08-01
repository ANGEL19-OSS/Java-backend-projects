-- =========================================================
-- Online Shopping Cart Database Schema Script (MySQL 8)
-- =========================================================

CREATE DATABASE IF NOT EXISTS online_shopping_cart_db;
USE online_shopping_cart_db;

DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- Products Table
CREATE TABLE products (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(150)  NOT NULL,
    price          DECIMAL(10,2) NOT NULL,
    stock_quantity INT           NOT NULL,
    created_at     DATETIME      DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_product_price CHECK (price > 0.00),
    CONSTRAINT chk_product_stock CHECK (stock_quantity >= 0)
);

-- Cart Items Table (Relational entity joining User and Product)
CREATE TABLE cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cart_items_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_product UNIQUE (user_id, product_id),
    CONSTRAINT chk_item_quantity CHECK (quantity >= 1)
);

-- Indexes for performance optimization
CREATE INDEX idx_cart_user ON cart_items(user_id);
CREATE INDEX idx_cart_product ON cart_items(product_id);

-- Initial Seed Data
INSERT INTO users (name, email) VALUES
('Rahul Verma', 'rahul.verma@example.com'),
('Priya Sharma', 'priya.sharma@example.com');

INSERT INTO products (name, price, stock_quantity) VALUES
('Wireless Headphones', 149.99, 15),
('Smartphone Pro 128GB', 799.00, 5),
('USB-C Fast Charger', 24.50, 50),
('Bluetooth Speaker', 59.99, 0),
('Smart Fitness Band', 39.99, 10);

INSERT INTO cart_items (user_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 3, 2);
