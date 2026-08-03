# Hibernate Assignment 7 – Inventory Management System

A standalone Java application built using **Hibernate ORM 6** to manage product records in an inventory database.

---

## ⚡ Quick Start & Running Tests

To build and execute all unit and integration tests:

```bash
# On Windows
.\mvnw.cmd test

# On Linux / macOS
./mvnw test
```

---

## 📋 Submission Test Cases Verified

1. **Negative Stock**: Validates that stock quantities cannot be negative (`NegativeStockException`).
2. **Duplicate Product**: Validates product name uniqueness (`DuplicateProductException`).
3. **Invalid Product ID**: Handles requests for missing product IDs (`InvalidProductIdException`).
4. **Transaction Rollback**: Ensures multi-step batch operations roll back completely via `tx.rollback()` if an error occurs.

---

## 📁 Evaluation Artifacts
- **Documentation**: [docs/Assignment_Documentation.md](docs/Assignment_Documentation.md)
- **Configuration**: [src/main/resources/hibernate.cfg.xml](src/main/resources/hibernate.cfg.xml)
- **Test Suite**: [src/test/java/com/hibernate/inventory/HibernateInventoryTest.java](src/test/java/com/hibernate/inventory/HibernateInventoryTest.java)
