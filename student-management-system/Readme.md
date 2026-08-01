# Student Management System - Spring Boot RESTful API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A production-grade, enterprise-ready RESTful web application built using **Spring Boot 3** to manage student records for a college. The application provides complete CRUD functionality, custom search filters, request validation, centralized exception handling, OpenAPI/Swagger documentation, and multi-database support (H2 & MySQL).

---

## ⚡ Quick Start Guide (For Evaluators / Reviewers)

To evaluate and test this application immediately without installing or configuring external databases, follow these simple steps:

### Prerequisites
- **JDK 17** or higher installed.

### Step 1: Clone & Run (Zero-Setup using Embedded H2 Database)

Open your terminal inside the project directory and run:

```bash
# On Windows (PowerShell / Command Prompt)
.\mvnw spring-boot:run

# On Linux / macOS
./mvnw spring-boot:run
```

That's it! The application will start locally on **Port 8080**.

---

### Step 2: Test APIs Interactively via Browser (Swagger UI)

Once started, open your browser and navigate to:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

From the interactive Swagger UI, you can:
1. Click any API endpoint (e.g. `POST /api/v1/students`, `GET /api/v1/students`).
2. Click **"Try it out"**.
3. Edit the request payload and click **"Execute"** to see live responses.

---

### Step 3: Run Automated Unit & Integration Tests

To execute the test suite (18 automated unit and integration tests covering positive, negative, and validation scenarios):

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
| **Postman Collection** | [postman/StudentManagementSystem.postman_collection.json](postman/StudentManagementSystem.postman_collection.json) | Complete pre-configured API test collection |
| **H2 Web Console** | `http://localhost:8080/h2-console` | In-memory DB viewer (`JDBC URL: jdbc:h2:mem:studentdb`, User: `sa`, Password: *empty*) |
| **Database Schema** | [docs/schema.sql](docs/schema.sql) | DDL creation script for MySQL production deployment |

---

## 🛠️ Architecture & Enterprise Design Highlights

- **Layered Architecture**: Decoupled `Controller` ➔ `Service` (`StudentService` interface & `StudentServiceImpl`) ➔ `Repository` (`StudentRepository`) ➔ `Database`.
- **DTO Protection**: `StudentRequestDTO` (with Jakarta Bean Validation) and `StudentResponseDTO` ensure domain entities are never directly exposed.
- **Unified JSON Envelope**: All endpoints return a standardized `ApiResponse<T>` envelope containing `success`, `message`, `data`, `errors`, and `timestamp`.
- **Global Exception Handling**: `@RestControllerAdvice` translates runtime errors into clean HTTP status codes (`400 Bad Request`, `404 Not Found`, `409 Conflict`).

---

## 🗄️ Optional: Running with Production MySQL Database

If you wish to test against a live MySQL instance:

1. Execute [docs/schema.sql](docs/schema.sql) in your MySQL server to create `student_management_db`.
2. Run the application with the `prod` profile and pass your database credentials via environment variables or command-line flags:

```bash
# PowerShell
$env:DB_PASSWORD="your_mysql_password"
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"

# Or via command-line arguments:
.\mvnw spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=prod --spring.datasource.username=root --spring.datasource.password=your_password"
```

---

## 💻 Tech Stack Summary

| Component | Technology |
|---|---|
| Framework | Spring Boot 3.2.3 |
| Java Version | Java 17 |
| Persistence | Spring Data JPA / Hibernate |
| Database | H2 (Dev) / MySQL 8 (Prod) |
| Validation | Jakarta Bean Validation |
| API Docs | Springdoc OpenAPI 2.3.0 |
| Testing | JUnit 5, Mockito, Spring Boot Test |
