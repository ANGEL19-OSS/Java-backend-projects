-- Initial seed data for H2 development database
INSERT INTO products (name, category, price, quantity, supplier_name, created_at, updated_at) VALUES
('Wireless Mechanical Keyboard', 'Electronics', 89.99, 25, 'TechGear Supplies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ergonomic Gaming Mouse', 'Electronics', 49.50, 8, 'TechGear Supplies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UltraWide 34-inch Monitor', 'Electronics', 499.00, 5, 'VisionDisplay Inc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Standing Desk Frame', 'Furniture', 299.99, 15, 'OfficeStyle Co', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ergonomic Mesh Chair', 'Furniture', 199.50, 4, 'OfficeStyle Co', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USB-C Multiport Hub', 'Electronics', 35.00, 50, 'ConnectPlus Supplies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Noise Cancelling Headphones', 'Audio', 150.00, 3, 'SoundWave Audio', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
