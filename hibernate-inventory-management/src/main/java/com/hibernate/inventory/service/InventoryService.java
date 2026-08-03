package com.hibernate.inventory.service;

import com.hibernate.inventory.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {

    Product addProduct(Product product);

    Product updateStock(Long productId, Integer newQuantity);

    void deleteProduct(Long productId);

    Product getProductById(Long productId);

    List<Product> searchByCategory(String category);

    List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> getLowStockProducts(Integer threshold);

    void batchUpdateStock(List<Long> productIds, List<Integer> newQuantities);
}
