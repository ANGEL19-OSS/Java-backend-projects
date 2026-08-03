package com.hibernate.inventory.controller;

import com.hibernate.inventory.dto.ApiResponse;
import com.hibernate.inventory.dto.PageResponse;
import com.hibernate.inventory.dto.ProductRequestDTO;
import com.hibernate.inventory.dto.ProductResponseDTO;
import com.hibernate.inventory.dto.StockUpdateRequestDTO;
import com.hibernate.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Hibernate Inventory API", description = "Endpoints for managing inventory records in Hibernate Assignment 7")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Add a new product", description = "Creates a new product record in the inventory")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product record created successfully", createdProduct));
    }

    @GetMapping
    @Operation(summary = "Retrieve all products", description = "Fetches a paginated and sorted list of all products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<ProductResponseDTO> response = productService.getAllProducts(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve product by ID", description = "Fetches details of a specific product by its ID")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details", description = "Updates an existing product's details by ID")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock quantity", description = "Updates stock quantity for a specific product by ID")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequestDTO stockRequest) {
        ProductResponseDTO updatedProduct = productService.updateStock(id, stockRequest.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Product stock updated successfully", updatedProduct));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Removes a product record from the inventory by ID")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Search products by category", description = "Finds products belonging to a specified category with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDTO>>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<ProductResponseDTO> response = productService.getProductsByCategory(category, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Category search completed", response));
    }

    @GetMapping("/price-range")
    @Operation(summary = "Search products by price range", description = "Finds products within a specified min and max price range")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDTO>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<ProductResponseDTO> response = productService.getProductsByPriceRange(minPrice, maxPrice, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Price range search completed", response));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Retrieve low stock products", description = "Fetches products with quantity strictly less than the specified threshold")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDTO>>> getProductsWithLowStock(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "quantity") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<ProductResponseDTO> response = productService.getProductsWithLowStock(threshold, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Low stock search completed", response));
    }
}
