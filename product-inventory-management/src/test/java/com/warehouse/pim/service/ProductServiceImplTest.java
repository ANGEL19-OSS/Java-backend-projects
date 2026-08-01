package com.warehouse.pim.service;

import com.warehouse.pim.dto.PageResponse;
import com.warehouse.pim.dto.ProductRequestDTO;
import com.warehouse.pim.dto.ProductResponseDTO;
import com.warehouse.pim.entity.Product;
import com.warehouse.pim.exception.DuplicateProductNameException;
import com.warehouse.pim.exception.ResourceNotFoundException;
import com.warehouse.pim.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Wireless Mouse")
                .category("Electronics")
                .price(new BigDecimal("29.99"))
                .quantity(50)
                .supplierName("LogiTech")
                .build();

        requestDTO = ProductRequestDTO.builder()
                .name("Wireless Mouse")
                .category("Electronics")
                .price(new BigDecimal("29.99"))
                .quantity(50)
                .supplierName("LogiTech")
                .build();
    }

    @Test
    @DisplayName("Should successfully create product")
    void createProduct_Success() {
        when(productRepository.existsByName(requestDTO.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.createProduct(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Wireless Mouse");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw DuplicateProductNameException when product name exists")
    void createProduct_DuplicateName_ThrowsException() {
        when(productRepository.existsByName(requestDTO.getName())).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(requestDTO))
                .isInstanceOf(DuplicateProductNameException.class)
                .hasMessageContaining("already exists");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return product when valid ID is provided")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.getProductById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Wireless Mouse");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product ID not found")
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with ID: 99");
    }

    @Test
    @DisplayName("Should return paginated products")
    void getAllProducts_Success() {
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<ProductResponseDTO> response = productService.getAllProducts(0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }
}
