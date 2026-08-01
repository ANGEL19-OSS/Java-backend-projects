-- =========================================================
-- Student Course Registration Schema Script (MySQL 8)
-- =========================================================

CREATE DATABASE IF NOT EXISTS student_course_db;
USE student_course_db;

DROP TABLE IF EXISTS student_courses;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;

-- Students Table
CREATE TABLE students (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Courses Table
CREATE TABLE courses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(50)  NOT NULL UNIQUE,
    title       VARCHAR(150) NOT NULL,
    credits     INT          NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_credits CHECK (credits >= 1)
);

-- Join Table for Many-to-Many Relationship
CREATE TABLE student_courses (
    student_id BIGINT NOT NULL,
    course_id  BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    
    CONSTRAINT fk_student_courses_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_courses_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Performance Indexes
CREATE INDEX idx_student_courses_student ON student_courses(student_id);
CREATE INDEX idx_student_courses_course ON student_courses(course_id);

-- Initial Seed Data
INSERT INTO courses (course_code, title, credits) VALUES
('CS101', 'Data Structures and Algorithms', 4),
('CS102', 'Database Management Systems', 4),
('CS103', 'Web Development with Spring Boot', 3),
('MA201', 'Linear Algebra and Calculus', 3);

INSERT INTO students (name, email, department) VALUES
('Aarav Sharma', 'aarav.sharma@college.edu', 'Computer Science'),
('Diya Patel', 'diya.patel@college.edu', 'Computer Science'),
('Rohan Kapoor', 'rohan.kapoor@college.edu', 'Information Technology');

INSERT INTO student_courses (student_id, course_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 3);
