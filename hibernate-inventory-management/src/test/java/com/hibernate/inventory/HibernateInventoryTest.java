package com.hibernate.inventory;

import com.hibernate.inventory.dao.ProductDao;
import com.hibernate.inventory.dao.ProductDaoImpl;
import com.hibernate.inventory.entity.Product;
import com.hibernate.inventory.exception.DuplicateProductException;
import com.hibernate.inventory.exception.InvalidProductIdException;
import com.hibernate.inventory.exception.NegativeStockException;
import com.hibernate.inventory.service.InventoryService;
import com.hibernate.inventory.service.InventoryServiceImpl;
import com.hibernate.inventory.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HibernateInventoryTest {

    private InventoryService inventoryService;
    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl();
        productDao = new ProductDaoImpl();

        // Clean database before each test
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Product").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @AfterAll
    static void tearDownAll() {
        HibernateUtil.shutdown();
    }

    // ==========================================
    // FUNCTIONAL REQUIREMENTS
    // ==========================================

    @Test
    @DisplayName("Functional Requirement 1: Add product")
    void testAddProduct() {
        Product product = Product.builder()
                .name("Hibernate Wireless Mouse")
                .category("Electronics")
                .price(new BigDecimal("29.99"))
                .quantity(50)
                .supplierName("Logitech")
                .build();

        Product saved = inventoryService.addProduct(product);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Hibernate Wireless Mouse");
        assertThat(saved.getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Functional Requirement 2: Update stock")
    void testUpdateStock() {
        Product product = Product.builder()
                .name("Gaming Keyboard")
                .category("Electronics")
                .price(new BigDecimal("79.99"))
                .quantity(15)
                .supplierName("Corsair")
                .build();

        Product saved = inventoryService.addProduct(product);
        Product updated = inventoryService.updateStock(saved.getId(), 80);

        assertThat(updated.getQuantity()).isEqualTo(80);

        Product fromDb = inventoryService.getProductById(saved.getId());
        assertThat(fromDb.getQuantity()).isEqualTo(80);
    }

    @Test
    @DisplayName("Functional Requirement 3: Delete product")
    void testDeleteProduct() {
        Product product = Product.builder()
                .name("Desk Mat")
                .category("Accessories")
                .price(new BigDecimal("19.99"))
                .quantity(30)
                .supplierName("DeskCo")
                .build();

        Product saved = inventoryService.addProduct(product);
        Long id = saved.getId();

        inventoryService.deleteProduct(id);

        assertThatThrownBy(() -> inventoryService.getProductById(id))
                .isInstanceOf(InvalidProductIdException.class);
    }

    @Test
    @DisplayName("Functional Requirement 4: Search products")
    void testSearchProducts() {
        Product p1 = Product.builder()
                .name("4K Monitor")
                .category("Electronics")
                .price(new BigDecimal("399.99"))
                .quantity(8)
                .supplierName("Dell")
                .build();

        Product p2 = Product.builder()
                .name("USB-C Hub")
                .category("Electronics")
                .price(new BigDecimal("49.99"))
                .quantity(25)
                .supplierName("Anker")
                .build();

        inventoryService.addProduct(p1);
        inventoryService.addProduct(p2);

        List<Product> electronics = inventoryService.searchByCategory("Electronics");
        assertThat(electronics).hasSize(2);

        List<Product> lowStock = inventoryService.getLowStockProducts(10);
        assertThat(lowStock).hasSize(1);
        assertThat(lowStock.get(0).getName()).isEqualTo("4K Monitor");
    }

    // ==========================================
    // MANDATORY SUBMISSION TEST CASES
    // ==========================================

    @Test
    @DisplayName("Submission Test Case 1: Negative stock throws NegativeStockException")
    void testSubmissionTestCase_NegativeStock() {
        Product product = Product.builder()
                .name("Valid Item")
                .category("General")
                .price(new BigDecimal("10.00"))
                .quantity(20)
                .supplierName("Supplier Alpha")
                .build();

        Product saved = inventoryService.addProduct(product);

        // Attempt to create product with negative stock
        Product negativeProduct = Product.builder()
                .name("Invalid Item")
                .category("General")
                .price(new BigDecimal("10.00"))
                .quantity(-5)
                .supplierName("Supplier Alpha")
                .build();

        assertThatThrownBy(() -> inventoryService.addProduct(negativeProduct))
                .isInstanceOf(NegativeStockException.class)
                .hasMessageContaining("cannot be negative");

        // Attempt to update existing stock to negative value
        assertThatThrownBy(() -> inventoryService.updateStock(saved.getId(), -10))
                .isInstanceOf(NegativeStockException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    @DisplayName("Submission Test Case 2: Duplicate product throws DuplicateProductException")
    void testSubmissionTestCase_DuplicateProduct() {
        Product p1 = Product.builder()
                .name("Unique Laptop Stand")
                .category("Furniture")
                .price(new BigDecimal("35.00"))
                .quantity(10)
                .supplierName("ErgoSupplies")
                .build();

        inventoryService.addProduct(p1);

        Product duplicateP2 = Product.builder()
                .name("Unique Laptop Stand")
                .category("Furniture")
                .price(new BigDecimal("45.00"))
                .quantity(5)
                .supplierName("ErgoSupplies")
                .build();

        assertThatThrownBy(() -> inventoryService.addProduct(duplicateP2))
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Submission Test Case 3: Invalid product ID throws InvalidProductIdException")
    void testSubmissionTestCase_InvalidProductId() {
        Long invalidId = 99999L;

        assertThatThrownBy(() -> inventoryService.getProductById(invalidId))
                .isInstanceOf(InvalidProductIdException.class)
                .hasMessageContaining("Invalid Product ID: " + invalidId);

        assertThatThrownBy(() -> inventoryService.updateStock(invalidId, 50))
                .isInstanceOf(InvalidProductIdException.class)
                .hasMessageContaining("Invalid Product ID: " + invalidId);

        assertThatThrownBy(() -> inventoryService.deleteProduct(invalidId))
                .isInstanceOf(InvalidProductIdException.class)
                .hasMessageContaining("Invalid Product ID: " + invalidId);
    }

    @Test
    @DisplayName("Submission Test Case 4: Transaction rollback on mid-operation failure")
    void testSubmissionTestCase_TransactionRollback() {
        Product p1 = Product.builder()
                .name("Batch Item 1")
                .category("Batch")
                .price(new BigDecimal("100.00"))
                .quantity(50)
                .supplierName("BatchCorp")
                .build();

        Product p2 = Product.builder()
                .name("Batch Item 2")
                .category("Batch")
                .price(new BigDecimal("200.00"))
                .quantity(60)
                .supplierName("BatchCorp")
                .build();

        Product saved1 = inventoryService.addProduct(p1);
        Product saved2 = inventoryService.addProduct(p2);

        List<Long> ids = List.of(saved1.getId(), saved2.getId());
        List<Integer> newQuantities = List.of(500, -10); // Item 2 has negative stock (-10)

        // batchUpdateStock should throw NegativeStockException and ROLL BACK transaction
        assertThatThrownBy(() -> inventoryService.batchUpdateStock(ids, newQuantities))
                .isInstanceOf(NegativeStockException.class);

        // Verify transaction rollback: saved1 quantity remains 50 in database
        Product item1InDb = inventoryService.getProductById(saved1.getId());
        assertThat(item1InDb.getQuantity()).isEqualTo(50);
    }
}
