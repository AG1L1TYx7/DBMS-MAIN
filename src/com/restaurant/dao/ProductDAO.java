package com.restaurant.dao;

import com.restaurant.model.Product;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Product Data Access Object Interface
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public interface ProductDAO {
    
    Product createProduct(Product product) throws SQLException;
    Optional<Product> findProductById(Integer productId) throws SQLException;
    Optional<Product> findProductByName(String productName) throws SQLException;
    boolean updateProduct(Product product) throws SQLException;
    boolean deleteProduct(Integer productId) throws SQLException;
    List<Product> getAllProducts() throws SQLException;
    List<Product> getProductsByCategory(String category) throws SQLException;
    List<Product> getAvailableProducts() throws SQLException;
    List<String> getAllCategories() throws SQLException;
}
