# Product Inventory Management — Assignment Documentation

---

## 2. Problem Statement

The objective of this assignment is to design, develop, and deploy an enterprise-grade RESTful backend application using **Spring Boot 3** to manage product records in a warehouse inventory.

Warehouse and supply chain operations require an efficient system to monitor stock levels, prevent stockouts, search products by category, filter items within specific price bands, and track low-stock items requiring reordering.

The application exposes clean, standardized RESTful APIs to:
1. Add new products with validations for price, quantity, and unique product names.
2. Retrieve all products or specific products by ID with built-in Pagination and Sorting.
3. Update existing product details while enforcing product name uniqueness.
4. Delete obsolete products from the inventory.
5. Search products by category with case-insensitive matching.
6. Filter products dynamically by price range (`minPrice` to `maxPrice`).
7. Retrieve products with quantity strictly less than a specified threshold (`low stock alerts`).
8. Return consistent JSON response envelopes and appropriate HTTP status codes (`201 Created`, `200 OK`, `400 Bad Request`, `404 Not Found`, `409 Conflict`).

---

## 3. Project Architecture

### 3.1 Package Structure
- Uses domain-driven packaging under `com.warehouse.pim`:
  - `controller`: `ProductController.java` handling REST requests under `/api/v1/products`.
  - `service`: `ProductService.java` interface & `ProductServiceImpl.java` class handling business rules, transactions, and DTO conversions.
  - `repository`: `ProductRepository.java` extending `JpaRepository` for data access with pagination.
  - `entity`: `Product.java` mapped to `products` relational database table.
  - `dto`: `ProductRequestDTO.java`, `ProductResponseDTO.java`, `PageResponse.java`, and generic `ApiResponse.java`.
  - `exception`: `GlobalExceptionHandler.java`, `ResourceNotFoundException.java`, and `DuplicateProductNameException.java`.
  - `config`: `OpenApiConfig.java` for Swagger UI settings.

### 3.2 Controller Layer
- Implemented in `ProductController.java` using `@RestController` and `@RequestMapping("/api/v1/products")`.
- Delegates execution to `ProductService` after validating payloads using `@Valid`.
- Endpoints return `ResponseEntity<ApiResponse<T>>` with status codes (`201 Created`, `200 OK`).

### 3.3 Service Layer
- Implemented in `ProductServiceImpl.java` using `@Service` and `@Transactional`.
- Enforces duplicate name checks (`existsByName` and `existsByNameAndIdNot`).
- Converts JPA entities to DTOs via `ModelMapper`.
- Constructs `PageResponse<ProductResponseDTO>` metadata (`pageNo`, `pageSize`, `totalElements`, `totalPages`).

### 3.4 Repository/DAO Layer
- Implemented in `ProductRepository.java` extending `JpaRepository<Product, Long>`.
- Derived query methods: `existsByName()`, `existsByNameAndIdNot()`, `findByCategoryIgnoreCase()`, `findByPriceBetween()`, and `findByQuantityLessThan()`.

### 3.5 Entity Classes
- Defined in `Product.java` mapped to `products` table using `@Entity` and `@Table`.
- Attributes: `id` (Auto-increment PK), `name` (Unique), `category`, `price`, `quantity`, `supplierName`, `createdAt`, `updatedAt`.
- Auditing callbacks: `@PrePersist` and `@PreUpdate` handle timestamps automatically.

### 3.6 DTO Classes
- `ProductRequestDTO`: Holds Bean Validation rules for input payloads.
- `ProductResponseDTO`: Exposes safe fields for output responses.
- `PageResponse<T>`: Encapsulates paginated list content and metadata.
- `ApiResponse<T>`: Generic response envelope with `success`, `message`, `data`, `errors`, and `timestamp`.

### 3.7 Exception Handling
- Centralized in `GlobalExceptionHandler.java` using `@RestControllerAdvice`.
- `ResourceNotFoundException` ➔ `404 Not Found`.
- `DuplicateProductNameException` ➔ `409 Conflict`.
- Validation errors (`MethodArgumentNotValidException`) ➔ `400 Bad Request` with field error map.

### 3.8 Validation
- `name`: `@NotBlank`, `@Size(min = 2, max = 150)`.
- `category`: `@NotBlank`.
- `price`: `@NotNull`, `@DecimalMin(value = "0.01", message = "Price must be greater than 0")`.
- `quantity`: `@NotNull`, `@Min(value = 0, message = "Quantity cannot be negative")`.
- `supplierName`: `@NotBlank`.

### 3.9 Configuration Classes
- `OpenApiConfig.java`: Configures Swagger UI metadata (`/swagger-ui.html`).
- `ProductInventoryApplication.java`: Configures `ModelMapper` singleton bean.
- Profiles: H2 in-memory DB for development (`dev`) and MySQL 8 for production (`prod`).

---

## 4. Database Design

- **Database Name**: `product_inventory_db`
- **Tables Created**: `products`
- **Primary Key**: `id` (`BIGINT`, `AUTO_INCREMENT`, `PRIMARY KEY`)
- **Unique Constraints**: `name` (`VARCHAR(150)`, `UNIQUE`, `NOT NULL`)
- **Check Constraints**: `price > 0.00`, `quantity >= 0`
- **ER Diagram Description**: Entity `PRODUCTS` with attributes `id` (PK), `name` (UK), `category`, `price`, `quantity`, `supplier_name`, `created_at`, `updated_at`.

---

## 5. API Documentation

| # | HTTP Method | Endpoint | Purpose | Status Code |
|---|---|---|---|---|
| 1 | `POST` | `/api/v1/products` | Add new product | `201 Created` |
| 2 | `GET` | `/api/v1/products` | Retrieve all products (Paginated & Sorted) | `200 OK` |
| 3 | `GET` | `/api/v1/products/{id}` | Retrieve product by ID | `200 OK` / `404` |
| 4 | `PUT` | `/api/v1/products/{id}` | Update product by ID | `200 OK` / `404` / `409` |
| 5 | `DELETE` | `/api/v1/products/{id}` | Delete product by ID | `200 OK` / `404` |
| 6 | `GET` | `/api/v1/products/category/{category}` | Search by category | `200 OK` |
| 7 | `GET` | `/api/v1/products/price-range` | Search by min & max price | `200 OK` |
| 8 | `GET` | `/api/v1/products/low-stock` | Retrieve products with stock < threshold | `200 OK` |

---

## 6. Test Cases Covered (Mandatory)

| Test Case | Expected Result | Actual Result | Status |
|---|---|---|---|
| **Add valid product** | Product created (`201 Created`) | Product created (`201 Created`) | Pass |
| **Add duplicate product name** | `409 Conflict` error message | `409 Conflict` error message | Pass |
| **Add product with negative price** | `400 Bad Request` ("Price > 0") | `400 Bad Request` ("Price > 0") | Pass |
| **Add product with negative quantity** | `400 Bad Request` ("Quantity >= 0") | `400 Bad Request` ("Quantity >= 0") | Pass |
| **Retrieve product by invalid ID (999)** | `404 Not Found` | `404 Not Found` | Pass |
| **Search by non-existing category** | `200 OK` with empty paginated list | `200 OK` with empty paginated list | Pass |
| **Pagination support (`page=0&size=5`)** | 5 products returned with page metadata | 5 products returned with page metadata | Pass |
| **Sorting by price ascending** | Products ordered by price ASC | Products ordered by price ASC | Pass |
