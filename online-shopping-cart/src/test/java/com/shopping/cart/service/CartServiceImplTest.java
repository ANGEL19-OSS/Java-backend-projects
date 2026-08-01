package com.shopping.cart.service;

import com.shopping.cart.dto.AddToCartRequestDTO;
import com.shopping.cart.dto.CartResponseDTO;
import com.shopping.cart.dto.UpdateQuantityRequestDTO;
import com.shopping.cart.entity.CartItem;
import com.shopping.cart.entity.Product;
import com.shopping.cart.entity.User;
import com.shopping.cart.exception.InsufficientStockException;
import com.shopping.cart.exception.ResourceNotFoundException;
import com.shopping.cart.repository.CartItemRepository;
import com.shopping.cart.repository.ProductRepository;
import com.shopping.cart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Rahul Verma").email("rahul@example.com").build();
        product = Product.builder().id(2L).name("Headphones").price(new BigDecimal("100.00")).stockQuantity(10).build();
        cartItem = CartItem.builder().id(3L).user(user).product(product).quantity(2).build();
    }

    @Test
    @DisplayName("Should successfully add product to cart")
    void addToCart_Success() {
        AddToCartRequestDTO request = AddToCartRequestDTO.builder().userId(1L).productId(2L).quantity(2).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 2L)).thenReturn(Optional.empty());
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        CartResponseDTO response = cartService.addToCart(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("200.00"));
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when quantity exceeds stock")
    void addToCart_InsufficientStock_ThrowsException() {
        AddToCartRequestDTO request = AddToCartRequestDTO.builder().userId(1L).productId(2L).quantity(15).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addToCart(request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user ID not found")
    void addToCart_UserNotFound_ThrowsException() {
        AddToCartRequestDTO request = AddToCartRequestDTO.builder().userId(99L).productId(2L).quantity(1).build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should calculate total amount correctly for multiple cart items")
    void getCartByUserId_TotalAmountCalculation() {
        Product p2 = Product.builder().id(3L).name("Mouse").price(new BigDecimal("50.00")).stockQuantity(20).build();
        CartItem item2 = CartItem.builder().id(4L).user(user).product(p2).quantity(3).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem, item2));

        CartResponseDTO response = cartService.getCartByUserId(1L);

        assertThat(response.getTotalItems()).isEqualTo(2);
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("350.00")); // (2*100) + (3*50)
    }

    @Test
    @DisplayName("Should successfully update cart item quantity")
    void updateCartItemQuantity_Success() {
        UpdateQuantityRequestDTO request = UpdateQuantityRequestDTO.builder().quantity(5).build();

        when(cartItemRepository.findById(3L)).thenReturn(Optional.of(cartItem));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        CartResponseDTO response = cartService.updateCartItemQuantity(3L, request);

        assertThat(response).isNotNull();
        verify(cartItemRepository, times(1)).save(cartItem);
    }
}
