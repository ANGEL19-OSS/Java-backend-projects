-- =========================================================
-- Employee and Passport Mapping Schema Script (MySQL 8)
-- =========================================================

CREATE DATABASE IF NOT EXISTS employee_passport_db;
USE employee_passport_db;

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS passports;

-- Passports Table (Child Entity)
CREATE TABLE passports (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    passport_number VARCHAR(50)  NOT NULL UNIQUE,
    country         VARCHAR(100) NOT NULL,
    expiry_date     DATE         NOT NULL,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- Employees Table (Parent / Owning side entity containing FK passport_id)
CREATE TABLE employees (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    department  VARCHAR(100) NOT NULL,
    passport_id BIGINT UNIQUE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_employees_passport FOREIGN KEY (passport_id) REFERENCES passports(id) ON DELETE CASCADE
);

-- Optimization Index
CREATE INDEX idx_employee_email ON employees(email);

-- Initial Seed Data
INSERT INTO passports (passport_number, country, expiry_date) VALUES
('IND98765432', 'India', '2030-12-31'),
('USA12345678', 'USA', '2032-06-15');

INSERT INTO employees (name, email, department, passport_id) VALUES
('Vikramaditya Roy', 'vikram.roy@company.com', 'Engineering', 1),
('Sarah Jenkins', 'sarah.j@company.com', 'Product Management', 2),
('Ananya Gupta', 'ananya.g@company.com', 'Human Resources', NULL);
