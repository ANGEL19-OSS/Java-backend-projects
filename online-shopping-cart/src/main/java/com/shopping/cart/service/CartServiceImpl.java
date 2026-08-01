package com.shopping.cart.service;

import com.shopping.cart.dto.*;
import com.shopping.cart.entity.CartItem;
import com.shopping.cart.entity.Product;
import com.shopping.cart.entity.User;
import com.shopping.cart.exception.InsufficientStockException;
import com.shopping.cart.exception.ResourceNotFoundException;
import com.shopping.cart.repository.CartItemRepository;
import com.shopping.cart.repository.ProductRepository;
import com.shopping.cart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponseDTO addToCart(AddToCartRequestDTO requestDTO) {
        User user = findUserById(requestDTO.getUserId());
        Product product = findProductById(requestDTO.getProductId());

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());

        int targetQuantity = requestDTO.getQuantity();
        if (existingItemOpt.isPresent()) {
            targetQuantity += existingItemOpt.get().getQuantity();
        }

        if (targetQuantity > product.getStockQuantity()) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock for product '%s'. Requested: %d, Available in stock: %d",
                    product.getName(), targetQuantity, product.getStockQuantity()
            ));
        }

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(targetQuantity);
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(requestDTO.getQuantity())
                    .build();
        }

        cartItemRepository.save(cartItem);
        return getCartByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCartByUserId(Long userId) {
        User user = findUserById(userId);
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        List<CartItemResponseDTO> itemDTOs = cartItems.stream()
                .map(this::mapToItemResponseDTO)
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemDTOs.stream()
                .map(CartItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .items(itemDTOs)
                .totalItems(itemDTOs.size())
                .totalAmount(totalAmount)
                .build();
    }

    @Override
    public CartResponseDTO updateCartItemQuantity(Long itemId, UpdateQuantityRequestDTO requestDTO) {
        CartItem cartItem = findCartItemById(itemId);
        Product product = cartItem.getProduct();

        if (requestDTO.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock for product '%s'. Requested: %d, Available in stock: %d",
                    product.getName(), requestDTO.getQuantity(), product.getStockQuantity()
            ));
        }

        cartItem.setQuantity(requestDTO.getQuantity());
        cartItemRepository.save(cartItem);

        return getCartByUserId(cartItem.getUser().getId());
    }

    @Override
    public CartResponseDTO removeCartItem(Long itemId) {
        CartItem cartItem = findCartItemById(itemId);
        Long userId = cartItem.getUser().getId();
        cartItemRepository.delete(cartItem);
        return getCartByUserId(userId);
    }

    @Override
    public void clearCart(Long userId) {
        findUserById(userId);
        cartItemRepository.deleteByUserId(userId);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    private CartItem findCartItemById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + itemId));
    }

    private CartItemResponseDTO mapToItemResponseDTO(CartItem cartItem) {
        return CartItemResponseDTO.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .unitPrice(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}
