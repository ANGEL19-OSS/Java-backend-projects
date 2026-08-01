-- =========================================================
-- Product Inventory Management Database Schema Script (MySQL 8)
-- =========================================================

CREATE DATABASE IF NOT EXISTS product_inventory_db;
USE product_inventory_db;

DROP TABLE IF EXISTS products;

CREATE TABLE products (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150)   NOT NULL UNIQUE,
    category      VARCHAR(100)   NOT NULL,
    price         DECIMAL(10,2)  NOT NULL,
    quantity      INT            NOT NULL,
    supplier_name VARCHAR(150)   NOT NULL,
    created_at    DATETIME       DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_price CHECK (price > 0.00),
    CONSTRAINT chk_quantity CHECK (quantity >= 0)
);

-- Search optimization indexes
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_quantity ON products(quantity);

-- Initial Sample Data
INSERT INTO products (name, category, price, quantity, supplier_name) VALUES
('Wireless Mechanical Keyboard', 'Electronics', 89.99, 25, 'TechGear Supplies'),
('Ergonomic Gaming Mouse', 'Electronics', 49.50, 8, 'TechGear Supplies'),
('UltraWide 34-inch Monitor', 'Electronics', 499.00, 5, 'VisionDisplay Inc'),
('Standing Desk Frame', 'Furniture', 299.99, 15, 'OfficeStyle Co'),
('Ergonomic Mesh Chair', 'Furniture', 199.50, 4, 'OfficeStyle Co'),
('USB-C Multiport Hub', 'Electronics', 35.00, 50, 'ConnectPlus Supplies'),
('Noise Cancelling Headphones', 'Audio', 150.00, 3, 'SoundWave Audio');
