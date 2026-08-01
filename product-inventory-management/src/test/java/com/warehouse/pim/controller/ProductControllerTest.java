package com.warehouse.pim.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.pim.dto.PageResponse;
import com.warehouse.pim.dto.ProductRequestDTO;
import com.warehouse.pim.dto.ProductResponseDTO;
import com.warehouse.pim.exception.DuplicateProductNameException;
import com.warehouse.pim.exception.GlobalExceptionHandler;
import com.warehouse.pim.exception.ResourceNotFoundException;
import com.warehouse.pim.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequestDTO validRequest;
    private ProductResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        validRequest = ProductRequestDTO.builder()
                .name("Wireless Keyboard")
                .category("Electronics")
                .price(new BigDecimal("79.99"))
                .quantity(30)
                .supplierName("TechGear")
                .build();

        validResponse = ProductResponseDTO.builder()
                .id(1L)
                .name("Wireless Keyboard")
                .category("Electronics")
                .price(new BigDecimal("79.99"))
                .quantity(30)
                .supplierName("TechGear")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/products - Success 201")
    void createProduct_Success() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Wireless Keyboard"));
    }

    @Test
    @DisplayName("POST /api/v1/products - Validation Error (Negative Price) 400")
    void createProduct_NegativePrice_ValidationError() throws Exception {
        ProductRequestDTO invalidRequest = ProductRequestDTO.builder()
                .name("Wireless Keyboard")
                .category("Electronics")
                .price(new BigDecimal("-10.00"))
                .quantity(5)
                .supplierName("TechGear")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @DisplayName("POST /api/v1/products - Duplicate Name 409")
    void createProduct_DuplicateName() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenThrow(new DuplicateProductNameException("Product with name 'Wireless Keyboard' already exists."));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product with name 'Wireless Keyboard' already exists."));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Found 200")
    void getProductById_Found() throws Exception {
        when(productService.getProductById(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Not Found 404")
    void getProductById_NotFound() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found with ID: 999"));

        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with ID: 999"));
    }

    @Test
    @DisplayName("GET /api/v1/products/low-stock - Success 200")
    void getProductsWithLowStock_Success() throws Exception {
        PageResponse<ProductResponseDTO> pageResponse = PageResponse.<ProductResponseDTO>builder()
                .content(List.of(validResponse))
                .pageNo(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(productService.getProductsWithLowStock(eq(10), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/products/low-stock").param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }
}
