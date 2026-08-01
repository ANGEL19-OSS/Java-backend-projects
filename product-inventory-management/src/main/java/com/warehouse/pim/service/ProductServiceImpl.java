package com.warehouse.pim.service;

import com.warehouse.pim.dto.PageResponse;
import com.warehouse.pim.dto.ProductRequestDTO;
import com.warehouse.pim.dto.ProductResponseDTO;
import com.warehouse.pim.entity.Product;
import com.warehouse.pim.exception.DuplicateProductNameException;
import com.warehouse.pim.exception.ResourceNotFoundException;
import com.warehouse.pim.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        if (productRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateProductNameException("Product with name '" + requestDTO.getName() + "' already exists.");
        }

        Product product = modelMapper.map(requestDTO, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDTO> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findAll(pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = findProductEntityById(id);
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product existingProduct = findProductEntityById(id);

        if (productRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateProductNameException("Product name '" + requestDTO.getName() + "' is already in use by another product.");
        }

        existingProduct.setName(requestDTO.getName());
        existingProduct.setCategory(requestDTO.getCategory());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setQuantity(requestDTO.getQuantity());
        existingProduct.setSupplierName(requestDTO.getSupplierName());

        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findProductEntityById(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDTO> getProductsByCategory(String category, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByCategoryIgnoreCase(category, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize, String sortBy, String sortDir) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return buildPageResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDTO> getProductsWithLowStock(Integer threshold, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByQuantityLessThan(threshold, pageable);
        return buildPageResponse(productPage);
    }

    private Product findProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

    private PageResponse<ProductResponseDTO> buildPageResponse(Page<Product> productPage) {
        List<ProductResponseDTO> content = productPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());

        return PageResponse.<ProductResponseDTO>builder()
                .content(content)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }
}
