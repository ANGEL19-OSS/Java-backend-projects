# Student Course Registration System - Spring Boot RESTful API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Hibernate](https://img.shields.io/badge/Hibernate-JPA-blue.svg)](https://hibernate.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A production-grade, enterprise-ready RESTful web application built using **Spring Boot 3** and **Hibernate / JPA** demonstrating **Many-to-Many (`@ManyToMany`) relational mapping** between `Student` and `Course` entities via a join table `student_courses`. Features include registering students, creating courses, enrolling students in courses, removing enrollments, duplicate enrollment checks, lazy loading verification, centralized exception handling, and OpenAPI/Swagger documentation.

---

## ⚡ Quick Start Guide (For Evaluators / Reviewers)

To evaluate and test this application immediately without installing or configuring external databases, follow these simple steps:

### Prerequisites
- **JDK 17** or higher installed.

### Step 1: Clone & Run (Zero-Setup using Embedded H2 Database)

Open your terminal inside the `student-course-registration` directory and run:

```bash
# On Windows (PowerShell / Command Prompt)
.\mvnw spring-boot:run

# On Linux / macOS
./mvnw spring-boot:run
```

The application will start locally on **Port 8080** and seed default students, courses, and enrollments automatically.

---

### Step 2: Test APIs Interactively via Browser (Swagger UI)

Once started, open your browser and navigate to:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

From the interactive Swagger UI, you can test all endpoints directly:
- `POST /api/v1/students` — Register a new student
- `POST /api/v1/courses` — Create a new academic course
- `POST /api/v1/students/{studentId}/enroll/{courseId}` — Enroll student in a course (prevents duplicate enrollments)
- `DELETE /api/v1/students/{studentId}/unenroll/{courseId}` — Remove course enrollment
- `GET /api/v1/students/{studentId}/courses` — View set of courses enrolled by student
- `GET /api/v1/courses/{courseId}/students` — View set of students registered in course

---

### Step 3: Run Automated Unit & Integration Tests

To execute the test suite:

```bash
# Windows
.\mvnw test

# Linux / macOS
./mvnw test
```

---

## 📋 Evaluation Artifacts & Testing Resources

| Resource | Path in Repository | Description |
|---|---|---|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | Interactive browser API explorer |
| **OpenAPI Spec** | `http://localhost:8080/api-docs` | Raw OpenAPI 3.0 JSON specification |
| **Postman Collection** | [postman/StudentCourseRegistration.postman_collection.json](postman/StudentCourseRegistration.postman_collection.json) | Pre-configured API test suite |
| **H2 Web Console** | `http://localhost:8080/h2-console` | In-memory DB viewer (`JDBC URL: jdbc:h2:mem:coursedb`, User: `sa`, Password: *empty*) |
| **Database Schema** | [docs/schema.sql](docs/schema.sql) | DDL creation script for MySQL production deployment |
| **Assignment Docs** | [docs/Assignment_Documentation.md](docs/Assignment_Documentation.md) | Full assignment documentation |

---

## 🗄️ Optional: Running with Production MySQL Database

If you wish to test against a live MySQL instance:

1. Execute [docs/schema.sql](docs/schema.sql) in your MySQL server to create `student_course_db`.
2. Run the application with the `prod` profile:

```bash
# PowerShell
$env:DB_PASSWORD="your_mysql_password"
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"
```

---

## 💻 Tech Stack Summary

| Component | Technology |
|---|---|
| Framework | Spring Boot 3.2.3 |
| ORM / Persistence | Hibernate / Spring Data JPA |
| Java Version | Java 17 |
| Database | H2 (Dev) / MySQL 8 (Prod) |
| Validation | Jakarta Bean Validation |
| API Docs | Springdoc OpenAPI 2.3.0 |
| Testing | JUnit 5, Mockito, Spring Boot Test |
