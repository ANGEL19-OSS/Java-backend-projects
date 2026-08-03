package com.hibernate.inventory.service;

import com.hibernate.inventory.dto.PageResponse;
import com.hibernate.inventory.dto.ProductRequestDTO;
import com.hibernate.inventory.dto.ProductResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);

    PageResponse<ProductResponseDTO> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);

    ProductResponseDTO updateStock(Long id, Integer newQuantity);

    void batchUpdateStock(List<Long> productIds, List<Integer> newQuantities);

    void deleteProduct(Long id);

    PageResponse<ProductResponseDTO> getProductsByCategory(String category, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductResponseDTO> getProductsWithLowStock(Integer threshold, int pageNo, int pageSize, String sortBy, String sortDir);
}
