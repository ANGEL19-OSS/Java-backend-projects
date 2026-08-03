package com.hibernate.inventory.service;

import com.hibernate.inventory.dao.ProductDao;
import com.hibernate.inventory.dao.ProductDaoImpl;
import com.hibernate.inventory.entity.Product;
import com.hibernate.inventory.exception.DuplicateProductException;
import com.hibernate.inventory.exception.InvalidProductIdException;
import com.hibernate.inventory.exception.NegativeStockException;
import com.hibernate.inventory.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class InventoryServiceImpl implements InventoryService {

    private final ProductDao productDao = new ProductDaoImpl();

    @Override
    public Product addProduct(Product product) {
        if (product.getQuantity() != null && product.getQuantity() < 0) {
            throw new NegativeStockException("Stock quantity cannot be negative");
        }
        if (productDao.findByName(product.getName()).isPresent()) {
            throw new DuplicateProductException("Product with name '" + product.getName() + "' already exists.");
        }
        return productDao.save(product);
    }

    @Override
    public Product updateStock(Long productId, Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            throw new NegativeStockException("Stock quantity cannot be negative");
        }
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new InvalidProductIdException("Invalid Product ID: " + productId));

        product.setQuantity(newQuantity);
        return productDao.update(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new InvalidProductIdException("Invalid Product ID: " + productId));
        productDao.delete(product.getId());
    }

    @Override
    public Product getProductById(Long productId) {
        return productDao.findById(productId)
                .orElseThrow(() -> new InvalidProductIdException("Invalid Product ID: " + productId));
    }

    @Override
    public List<Product> searchByCategory(String category) {
        return productDao.findByCategory(category);
    }

    @Override
    public List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productDao.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productDao.findLowStock(threshold);
    }

    @Override
    public void batchUpdateStock(List<Long> productIds, List<Integer> newQuantities) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (int i = 0; i < productIds.size(); i++) {
                Long id = productIds.get(i);
                Integer qty = newQuantities.get(i);
                if (qty < 0) {
                    throw new NegativeStockException("Negative stock is invalid for product ID: " + id);
                }
                Product product = session.get(Product.class, id);
                if (product == null) {
                    throw new InvalidProductIdException("Invalid Product ID: " + id);
                }
                product.setQuantity(qty);
                session.merge(product);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
