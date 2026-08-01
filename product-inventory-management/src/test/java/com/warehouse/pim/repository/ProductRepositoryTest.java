package com.warehouse.pim.repository;

import com.warehouse.pim.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        product1 = Product.builder()
                .name("Smart Watch Series 5")
                .category("Electronics")
                .price(new BigDecimal("199.99"))
                .quantity(15)
                .supplierName("TechGear Supplies")
                .build();

        product2 = Product.builder()
                .name("Ergonomic Office Chair")
                .category("Furniture")
                .price(new BigDecimal("249.50"))
                .quantity(4)
                .supplierName("OfficeStyle Co")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("Should return true when product name exists")
    void existsByName_True() {
        boolean exists = productRepository.existsByName("Smart Watch Series 5");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when product name does not exist")
    void existsByName_False() {
        boolean exists = productRepository.existsByName("Unknown Product");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find products by category with pagination")
    void findByCategoryIgnoreCase() {
        Page<Product> page = productRepository.findByCategoryIgnoreCase("electronics", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Smart Watch Series 5");
    }

    @Test
    @DisplayName("Should find products by price range")
    void findByPriceBetween() {
        Page<Product> page = productRepository.findByPriceBetween(new BigDecimal("150.00"), new BigDecimal("220.00"), PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Smart Watch Series 5");
    }

    @Test
    @DisplayName("Should find products with low stock quantity")
    void findByQuantityLessThan() {
        Page<Product> page = productRepository.findByQuantityLessThan(10, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Ergonomic Office Chair");
    }
}
