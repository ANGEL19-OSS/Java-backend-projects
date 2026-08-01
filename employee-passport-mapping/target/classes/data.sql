-- Initial seed data for Passports
INSERT INTO passports (passport_number, country, expiry_date, created_at) VALUES
('IND98765432', 'India', '2030-12-31', CURRENT_TIMESTAMP),
('USA12345678', 'USA', '2032-06-15', CURRENT_TIMESTAMP);

-- Initial seed data for Employees
-- Employee 1 has a mapped passport (IND98765432)
INSERT INTO employees (name, email, department, passport_id, created_at, updated_at) VALUES
('Vikramaditya Roy', 'vikram.roy@company.com', 'Engineering', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Employee 2 has a mapped passport (USA12345678)
INSERT INTO employees (name, email, department, passport_id, created_at, updated_at) VALUES
('Sarah Jenkins', 'sarah.j@company.com', 'Product Management', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Employee 3 has NO mapped passport (passport_id is NULL)
INSERT INTO employees (name, email, department, passport_id, created_at, updated_at) VALUES
('Ananya Gupta', 'ananya.g@company.com', 'Human Resources', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
