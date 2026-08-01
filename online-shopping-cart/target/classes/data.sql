-- Initial seed data for Users
INSERT INTO users (name, email, created_at) VALUES
('Rahul Verma', 'rahul.verma@example.com', CURRENT_TIMESTAMP),
('Priya Sharma', 'priya.sharma@example.com', CURRENT_TIMESTAMP);

-- Initial seed data for Products (with varying stock quantities)
INSERT INTO products (name, price, stock_quantity, created_at) VALUES
('Wireless Headphones', 149.99, 15, CURRENT_TIMESTAMP),
('Smartphone Pro 128GB', 799.00, 5, CURRENT_TIMESTAMP),
('USB-C Fast Charger', 24.50, 50, CURRENT_TIMESTAMP),
('Bluetooth Speaker', 59.99, 0, CURRENT_TIMESTAMP),
('Smart Fitness Band', 39.99, 10, CURRENT_TIMESTAMP);

-- Initial seed data for Cart Items (Rahul Verma's cart)
INSERT INTO cart_items (user_id, product_id, quantity, created_at, updated_at) VALUES
(1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
