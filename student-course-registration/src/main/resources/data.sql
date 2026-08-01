-- Initial seed data for Courses
INSERT INTO courses (course_code, title, credits, created_at) VALUES
('CS101', 'Data Structures and Algorithms', 4, CURRENT_TIMESTAMP),
('CS102', 'Database Management Systems', 4, CURRENT_TIMESTAMP),
('CS103', 'Web Development with Spring Boot', 3, CURRENT_TIMESTAMP),
('MA201', 'Linear Algebra and Calculus', 3, CURRENT_TIMESTAMP);

-- Initial seed data for Students
INSERT INTO students (name, email, department, created_at) VALUES
('Aarav Sharma', 'aarav.sharma@college.edu', 'Computer Science', CURRENT_TIMESTAMP),
('Diya Patel', 'diya.patel@college.edu', 'Computer Science', CURRENT_TIMESTAMP),
('Rohan Kapoor', 'rohan.kapoor@college.edu', 'Information Technology', CURRENT_TIMESTAMP);

-- Initial seed data for Join Table student_courses
-- Aarav (1) is enrolled in CS101 (1) and CS102 (2)
INSERT INTO student_courses (student_id, course_id) VALUES
(1, 1),
(1, 2);

-- Diya (2) is enrolled in CS101 (1) and CS103 (3)
INSERT INTO student_courses (student_id, course_id) VALUES
(2, 1),
(2, 3);
