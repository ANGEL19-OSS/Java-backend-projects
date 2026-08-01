package com.shopping.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.cart.dto.*;
import com.shopping.cart.exception.GlobalExceptionHandler;
import com.shopping.cart.exception.InsufficientStockException;
import com.shopping.cart.exception.ResourceNotFoundException;
import com.shopping.cart.service.CartService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(GlobalExceptionHandler.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddToCartRequestDTO addRequest;
    private CartResponseDTO cartResponse;

    @BeforeEach
    void setUp() {
        addRequest = AddToCartRequestDTO.builder()
                .userId(1L)
                .productId(2L)
                .quantity(2)
                .build();

        CartItemResponseDTO itemDTO = CartItemResponseDTO.builder()
                .id(10L)
                .productId(2L)
                .productName("Wireless Headphones")
                .unitPrice(new BigDecimal("149.99"))
                .quantity(2)
                .subtotal(new BigDecimal("299.98"))
                .build();

        cartResponse = CartResponseDTO.builder()
                .userId(1L)
                .userName("Rahul Verma")
                .items(List.of(itemDTO))
                .totalItems(1)
                .totalAmount(new BigDecimal("299.98"))
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/cart/items - Success 201")
    void addToCart_Success() throws Exception {
        when(cartService.addToCart(any(AddToCartRequestDTO.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.totalAmount").value(299.98));
    }

    @Test
    @DisplayName("POST /api/v1/cart/items - Quantity Zero Error 400")
    void addToCart_ZeroQuantity_ValidationError() throws Exception {
        AddToCartRequestDTO invalidRequest = AddToCartRequestDTO.builder()
                .userId(1L)
                .productId(2L)
                .quantity(0)
                .build();

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    @DisplayName("POST /api/v1/cart/items - Insufficient Stock Error 400")
    void addToCart_InsufficientStock() throws Exception {
        when(cartService.addToCart(any(AddToCartRequestDTO.class)))
                .thenThrow(new InsufficientStockException("Insufficient stock for product 'Wireless Headphones'. Requested: 20, Available in stock: 5"));

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Insufficient stock for product 'Wireless Headphones'. Requested: 20, Available in stock: 5"));
    }

    @Test
    @DisplayName("GET /api/v1/cart/user/{userId} - Found 200")
    void getCartByUserId_Found() throws Exception {
        when(cartService.getCartByUserId(1L)).thenReturn(cartResponse);

        mockMvc.perform(get("/api/v1/cart/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/cart/user/{userId} - User Not Found 404")
    void getCartByUserId_NotFound() throws Exception {
        when(cartService.getCartByUserId(999L))
                .thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(get("/api/v1/cart/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found with ID: 999"));
    }

    @Test
    @DisplayName("DELETE /api/v1/cart/user/{userId}/clear - Success 200")
    void clearCart_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/cart/user/1/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
