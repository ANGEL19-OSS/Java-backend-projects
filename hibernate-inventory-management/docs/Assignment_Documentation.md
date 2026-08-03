# Hibernate Assignment 7 – Inventory Management System
## Assignment Documentation & Execution Report

---

## 1. Executive Summary

This document provides complete documentation for **Hibernate Assignment 7 – Inventory Management System**. The application is implemented as a standalone **Hibernate ORM** Java backend project using **Hibernate 6.4.4.Final**, **H2 In-Memory Database**, **JUnit 5**, **AssertJ**, and **Lombok**.

The project uses direct **Hibernate Session & Transaction APIs** (`SessionFactory`, `Session`, `Transaction`, `session.beginTransaction()`, `tx.commit()`, `tx.rollback()`, HQL queries) to implement all required functional features and verify all mandatory submission test cases.

---

## 2. Technical Stack

| Category | Technology / Library | Version |
| :--- | :--- | :--- |
| **Language** | Java | 17 |
| **ORM Framework** | Hibernate ORM Core | 6.4.4.Final |
| **Database** | Embedded H2 Database | 2.2.224 |
| **Boilerplate Reduction** | Lombok | 1.18.30 |
| **Logging** | SLF4J / Logback | 2.0.12 / 1.4.14 |
| **Testing** | JUnit 5 & AssertJ | 5.10.2 & 3.25.3 |

---

## 3. Project Architecture

### 3.1 Package Structure
- `com.hibernate.inventory.entity`: `Product.java` (Hibernate Entity with `@Entity`, `@Table`, `@Id`, `@Column(unique=true)`).
- `com.hibernate.inventory.util`: `HibernateUtil.java` (Builds and exposes Hibernate `SessionFactory`).
- `com.hibernate.inventory.exception`: `NegativeStockException.java`, `DuplicateProductException.java`, `InvalidProductIdException.java`.
- `com.hibernate.inventory.dao`: `ProductDao.java` (Interface) and `ProductDaoImpl.java` (Direct Session & HQL implementation).
- `com.hibernate.inventory.service`: `InventoryService.java` (Interface) and `InventoryServiceImpl.java` (Business logic and explicit Transaction Rollback handling).
- `com.hibernate.inventory`: `HibernateInventoryTest.java` (JUnit 5 test suite verifying all submission test cases).

---

## 4. Functional Requirements & Implementation

1. **Add products**: `inventoryService.addProduct(product)` persists new entities using Hibernate `session.persist()`.
2. **Update stock**: `inventoryService.updateStock(id, quantity)` updates stock levels using Hibernate `session.merge()`.
3. **Delete products**: `inventoryService.deleteProduct(id)` removes entities using Hibernate `session.remove()`.
4. **Search products**:
   - Search by category: `session.createQuery("FROM Product p WHERE LOWER(p.category) = LOWER(:category)")`
   - Search by price range: `session.createQuery("FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")`
   - Search low stock: `session.createQuery("FROM Product p WHERE p.quantity < :threshold")`

---

## 5. Submission Test Cases Matrix

| Test Case | Cause / Trigger | Handled Exception | Verification Status |
| :--- | :--- | :--- | :--- |
| **1. Negative Stock** | Attempting to create product or update stock with quantity `< 0` | `NegativeStockException` | **PASS** |
| **2. Duplicate Product** | Attempting to add product with a name that already exists in DB | `DuplicateProductException` | **PASS** |
| **3. Invalid Product ID** | Operating on a non-existent product ID (e.g. `99999`) | `InvalidProductIdException` | **PASS** |
| **4. Transaction Rollback**| Batch stock update failure mid-operation (item #2 has negative stock) | `tx.rollback()` triggered; item #1 stock remains unchanged | **PASS** |

---

## 6. Verification Results

Run Maven test execution:
```bash
.\mvnw.cmd test
```

```text
[INFO] Running com.hibernate.inventory.HibernateInventoryTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.15 s -- in com.hibernate.inventory.HibernateInventoryTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
