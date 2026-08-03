package com.hibernate.inventory.dao;

import com.hibernate.inventory.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductDao {

    Product save(Product product);

    Product update(Product product);

    void delete(Long id);

    Optional<Product> findById(Long id);

    Optional<Product> findByName(String name);

    List<Product> findAll();

    List<Product> findByCategory(String category);

    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findLowStock(Integer threshold);
}
