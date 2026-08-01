package com.shopping.cart.service;

import com.shopping.cart.dto.AddToCartRequestDTO;
import com.shopping.cart.dto.CartResponseDTO;
import com.shopping.cart.dto.UpdateQuantityRequestDTO;

public interface CartService {

    CartResponseDTO addToCart(AddToCartRequestDTO requestDTO);

    CartResponseDTO getCartByUserId(Long userId);

    CartResponseDTO updateCartItemQuantity(Long itemId, UpdateQuantityRequestDTO requestDTO);

    CartResponseDTO removeCartItem(Long itemId);

    void clearCart(Long userId);
}
