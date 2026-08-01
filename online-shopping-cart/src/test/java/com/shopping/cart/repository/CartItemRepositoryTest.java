package com.shopping.cart.repository;

import com.shopping.cart.entity.CartItem;
import com.shopping.cart.entity.Product;
import com.shopping.cart.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User user;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder().name("Amit Kumar").email("amit@example.com").build());
        product = productRepository.save(Product.builder().name("Laptop Stand").price(new BigDecimal("45.00")).stockQuantity(20).build());

        cartItem = cartItemRepository.save(CartItem.builder().user(user).product(product).quantity(2).build());
    }

    @Test
    @DisplayName("Should find cart items by user ID")
    void findByUserId() {
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find cart item by user ID and product ID")
    void findByUserIdAndProductId() {
        Optional<CartItem> item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());
        assertThat(item).isPresent();
        assertThat(item.get().getProduct().getName()).isEqualTo("Laptop Stand");
    }

    @Test
    @DisplayName("Should delete cart items by user ID")
    void deleteByUserId() {
        cartItemRepository.deleteByUserId(user.getId());
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        assertThat(items).isEmpty();
    }
}
