package com.warehouse.pim.service;

import com.warehouse.pim.dto.PageResponse;
import com.warehouse.pim.dto.ProductRequestDTO;
import com.warehouse.pim.dto.ProductResponseDTO;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);

    PageResponse<ProductResponseDTO> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);

    void deleteProduct(Long id);

    PageResponse<ProductResponseDTO> getProductsByCategory(String category, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<ProductResponseDTO> getProductsWithLowStock(Integer threshold, int pageNo, int pageSize, String sortBy, String sortDir);
}
