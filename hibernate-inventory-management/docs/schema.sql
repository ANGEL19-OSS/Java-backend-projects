-- Database Schema DDL for Hibernate Assignment 7 - Inventory Management Database
-- Target RDBMS: MySQL 8.0 / H2

CREATE DATABASE IF NOT EXISTS hibernate_inventory_db;
USE hibernate_inventory_db;

DROP TABLE IF EXISTS products;

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    supplier_name VARCHAR(150) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_positive_price CHECK (price > 0),
    CONSTRAINT chk_non_negative_quantity CHECK (quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample Seed Data
INSERT INTO products (name, category, price, quantity, supplier_name, created_at, updated_at) VALUES
('Logitech MX Master 3S', 'Electronics', 99.99, 45, 'Logitech Inc.', NOW(), NOW()),
('Dell UltraSharp 27 4K Monitor', 'Electronics', 549.50, 8, 'Dell Technologies', NOW(), NOW()),
('Keychron K2 Wireless Mechanical Keyboard', 'Electronics', 89.00, 30, 'Keychron Accessories', NOW(), NOW()),
('Ergonomic Mesh Executive Chair', 'Furniture', 249.99, 5, 'OfficeStyle Ergonomics', NOW(), NOW()),
('Standing Desk Dual-Motor Frame', 'Furniture', 320.00, 12, 'FlexiSpot Furniture', NOW(), NOW()),
('Heavy-Duty Steel Storage Rack', 'Warehouse Equipment', 175.50, 3, 'StoragePro Corp', NOW(), NOW());
