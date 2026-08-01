package com.shopping.cart.controller;

import com.shopping.cart.dto.*;
import com.shopping.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Online Shopping Cart API", description = "Endpoints for managing online shopping cart items, stock verification, and total amount calculation")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    @Operation(summary = "Add product to cart", description = "Adds a product to the user's cart or updates quantity if already present after verifying stock")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(@Valid @RequestBody AddToCartRequestDTO requestDTO) {
        CartResponseDTO cartResponse = cartService.addToCart(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to cart successfully", cartResponse));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "View user's cart", description = "Retrieves all items in the user's cart along with calculated total amount")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCartByUserId(@PathVariable Long userId) {
        CartResponseDTO cartResponse = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cartResponse));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity in cart", description = "Updates the quantity of a specific cart item after stock verification")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequestDTO requestDTO) {
        CartResponseDTO cartResponse = cartService.updateCartItemQuantity(itemId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated successfully", cartResponse));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the user's cart")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeCartItem(@PathVariable Long itemId) {
        CartResponseDTO cartResponse = cartService.removeCartItem(itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", cartResponse));
    }

    @DeleteMapping("/user/{userId}/clear")
    @Operation(summary = "Clear user's cart", description = "Removes all items from the user's cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}
