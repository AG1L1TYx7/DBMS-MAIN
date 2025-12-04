package com.restaurant.controller;

import com.restaurant.dao.ProductDAO;
import com.restaurant.dao.ProductDAOImpl;
import com.restaurant.model.Product;
import com.restaurant.model.Product.ProductCategory;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Product Controller
 * Handles product management business logic
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class ProductController {
    
    private final ProductDAO productDAO;
    
    public ProductController() {
        this.productDAO = new ProductDAOImpl();
    }
    
    /**
     * Create a new product
     */
    public boolean createProduct(String name, String category, BigDecimal price, String description) {
        try {
            // Validation
            if (name == null || name.trim().isEmpty()) {
                showError("Product name is required");
                return false;
            }
            
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Price must be greater than zero");
                return false;
            }
            
            // Check if product exists
            if (productDAO.findProductByName(name).isPresent()) {
                showError("Product with this name already exists");
                return false;
            }
            
            // Create product
            Product product = new Product();
            product.setProductName(name.trim());
            product.setCategory(ProductCategory.valueOf(category));
            product.setPrice(price);
            product.setDescription(description != null ? description.trim() : "");
            product.setAvailable(true);
            
            Product created = productDAO.createProduct(product);
            return created != null && created.getProductId() != null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            showError("Invalid category selected");
            return false;
        }
    }
    
    /**
     * Update existing product
     */
    public boolean updateProduct(Integer productId, String name, String category, 
                                 BigDecimal price, String description, boolean available) {
        try {
            // Validation
            if (productId == null) {
                showError("Product ID is required");
                return false;
            }
            
            Optional<Product> existingOpt = productDAO.findProductById(productId);
            if (existingOpt.isEmpty()) {
                showError("Product not found");
                return false;
            }
            
            // Check if name changed and if new name exists
            Product existing = existingOpt.get();
            if (!existing.getProductName().equalsIgnoreCase(name)) {
                if (productDAO.findProductByName(name).isPresent()) {
                    showError("Another product with this name already exists");
                    return false;
                }
            }
            
            // Update product
            existing.setProductName(name.trim());
            existing.setCategory(ProductCategory.valueOf(category));
            existing.setPrice(price);
            existing.setDescription(description != null ? description.trim() : "");
            existing.setAvailable(available);
            
            return productDAO.updateProduct(existing);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            showError("Invalid category selected");
            return false;
        }
    }
    
    /**
     * Delete product
     */
    public boolean deleteProduct(Integer productId) {
        try {
            if (productId == null) {
                showError("Product ID is required");
                return false;
            }
            
            int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                return productDAO.deleteProduct(productId);
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get available products only
     */
    public List<Product> getAvailableProducts() {
        try {
            return productDAO.getAvailableProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        try {
            return productDAO.getProductsByCategory(category);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Search products
     */
    public List<Product> searchProducts(String searchTerm) {
        try {
            ProductDAOImpl impl = (ProductDAOImpl) productDAO;
            return impl.searchProducts(searchTerm);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Search failed: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get all categories
     */
    public List<String> getAllCategories() {
        try {
            return productDAO.getAllCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, 
            "Product Error", JOptionPane.ERROR_MESSAGE);
    }
}
