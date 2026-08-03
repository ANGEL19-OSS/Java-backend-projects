package com.hibernate.inventory.service;

import com.hibernate.inventory.dto.PageResponse;
import com.hibernate.inventory.dto.ProductRequestDTO;
import com.hibernate.inventory.dto.ProductResponseDTO;
import com.hibernate.inventory.entity.Product;
import com.hibernate.inventory.exception.DuplicateProductException;
import com.hibernate.inventory.exception.InvalidProductIdException;
import com.hibernate.inventory.exception.NegativeStockException;
import com.hibernate.inventory.repository.ProductRepository;
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
        if (requestDTO.getQuantity() != null && requestDTO.getQuantity() < 0) {
            throw new NegativeStockException("Quantity cannot be negative");
        }
        if (productRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateProductException("Product with name '" + requestDTO.getName() + "' already exists.");
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

        if (requestDTO.getQuantity() != null && requestDTO.getQuantity() < 0) {
            throw new NegativeStockException("Quantity cannot be negative");
        }

        if (productRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateProductException("Product name '" + requestDTO.getName() + "' is already in use by another product.");
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
    public ProductResponseDTO updateStock(Long id, Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            throw new NegativeStockException("Quantity cannot be negative");
        }
        Product existingProduct = findProductEntityById(id);
        existingProduct.setQuantity(newQuantity);
        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    @Override
    @Transactional
    public void batchUpdateStock(List<Long> productIds, List<Integer> newQuantities) {
        for (int i = 0; i < productIds.size(); i++) {
            Long id = productIds.get(i);
            Integer newQty = newQuantities.get(i);
            if (newQty < 0) {
                throw new NegativeStockException("Negative stock is invalid for product ID: " + id);
            }
            Product product = findProductEntityById(id);
            product.setQuantity(newQty);
            productRepository.save(product);
        }
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
                .orElseThrow(() -> new InvalidProductIdException("Invalid Product ID: " + id));
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
