-- =========================================================
-- Student Management System Database Schema Script (MySQL 8)
-- =========================================================

CREATE DATABASE IF NOT EXISTS student_management_db;
USE student_management_db;

DROP TABLE IF EXISTS students;

CREATE TABLE students (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(15)  NOT NULL,
    department   VARCHAR(100) NOT NULL,
    year_of_study INT          NOT NULL,
    cgpa         DECIMAL(4,2) NOT NULL,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_year_of_study CHECK (year_of_study >= 1 AND year_of_study <= 5),
    CONSTRAINT chk_cgpa CHECK (cgpa >= 0.0 AND cgpa <= 10.0)
);

-- Index for department search optimization
CREATE INDEX idx_students_department ON students(department);

-- Sample Data Insertion
INSERT INTO students (name, email, phone_number, department, year_of_study, cgpa) VALUES
('Arjun Sharma', 'arjun.sharma@college.edu', '9876543210', 'Computer Science', 3, 8.85),
('Priya Patel', 'priya.patel@college.edu', '9876543211', 'Electrical Engineering', 2, 9.20),
('Rohan Verma', 'rohan.verma@college.edu', '9876543212', 'Computer Science', 4, 7.50),
('Ananya Sen', 'ananya.sen@college.edu', '9876543213', 'Mechanical Engineering', 1, 8.10),
('Karan Gupta', 'karan.gupta@college.edu', '9876543214', 'Civil Engineering', 2, 6.90);
