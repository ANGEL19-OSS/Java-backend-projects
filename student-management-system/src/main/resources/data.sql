-- Initial seed data for H2 development database
INSERT INTO students (name, email, phone_number, department, year_of_study, cgpa, created_at, updated_at) VALUES
('Arjun Sharma', 'arjun.sharma@college.edu', '9876543210', 'Computer Science', 3, 8.85, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Priya Patel', 'priya.patel@college.edu', '9876543211', 'Electrical Engineering', 2, 9.20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Rohan Verma', 'rohan.verma@college.edu', '9876543212', 'Computer Science', 4, 7.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ananya Sen', 'ananya.sen@college.edu', '9876543213', 'Mechanical Engineering', 1, 8.10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Karan Gupta', 'karan.gupta@college.edu', '9876543214', 'Civil Engineering', 2, 6.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
