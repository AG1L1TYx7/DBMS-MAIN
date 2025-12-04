package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.Product;
import com.restaurant.model.Product.ProductCategory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Product DAO Implementation
 * Handles all database operations for products
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class ProductDAOImpl implements ProductDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public ProductDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public Product createProduct(Product product) throws SQLException {
        // First, get category_id from category name
        Integer categoryId = getCategoryIdByName(product.getCategory().name());
        if (categoryId == null) {
            throw new SQLException("Invalid category: " + product.getCategory().name());
        }
        
        String sql = "INSERT INTO products (product_name, category_id, price, description, " +
                    "stock_quantity, reorder_level, max_stock_level, unit, is_available) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, categoryId);
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setString(4, product.getDescription());
            pstmt.setInt(5, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            pstmt.setInt(6, product.getReorderLevel() != null ? product.getReorderLevel() : 10);
            pstmt.setInt(7, product.getMaxStockLevel() != null ? product.getMaxStockLevel() : 100);
            pstmt.setString(8, product.getUnit() != null ? product.getUnit() : "pcs");
            pstmt.setBoolean(9, product.isAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                        product.setCreatedAt(LocalDateTime.now());
                        return product;
                    }
                }
            }
            
            throw new SQLException("Creating product failed, no ID obtained.");
        }
    }
    
    @Override
    public Optional<Product> findProductById(Integer productId) throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                    "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                    "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                    "FROM products p " +
                    "JOIN product_categories pc ON p.category_id = pc.category_id " +
                    "WHERE p.product_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                    "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                    "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                    "FROM products p " +
                    "JOIN product_categories pc ON p.category_id = pc.category_id " +
                    "ORDER BY p.product_name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        
        return products;
    }
    
    @Override
    public List<Product> getAvailableProducts() throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                    "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                    "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                    "FROM products p " +
                    "JOIN product_categories pc ON p.category_id = pc.category_id " +
                    "WHERE p.is_available = true ORDER BY pc.category_name, p.product_name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        
        return products;
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                    "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                    "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                    "FROM products p " +
                    "JOIN product_categories pc ON p.category_id = pc.category_id " +
                    "WHERE pc.category_name = ? ORDER BY p.product_name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        
        return products;
    }
    
    @Override
    public boolean updateProduct(Product product) throws SQLException {
        // First, get category_id from category name
        Integer categoryId = getCategoryIdByName(product.getCategory().name());
        if (categoryId == null) {
            throw new SQLException("Invalid category: " + product.getCategory().name());
        }
        
        String sql = "UPDATE products SET product_name = ?, category_id = ?, price = ?, " +
                    "description = ?, stock_quantity = ?, reorder_level = ?, " +
                    "max_stock_level = ?, unit = ?, is_available = ? WHERE product_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, categoryId);
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setString(4, product.getDescription());
            pstmt.setInt(5, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            pstmt.setInt(6, product.getReorderLevel() != null ? product.getReorderLevel() : 10);
            pstmt.setInt(7, product.getMaxStockLevel() != null ? product.getMaxStockLevel() : 100);
            pstmt.setString(8, product.getUnit() != null ? product.getUnit() : "pcs");
            pstmt.setBoolean(9, product.isAvailable());
            pstmt.setInt(10, product.getProductId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                product.setUpdatedAt(LocalDateTime.now());
                return true;
            }
            
            return false;
        }
    }
    
    @Override
    public boolean deleteProduct(Integer productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public List<String> getAllCategories() throws SQLException {
        String sql = "SELECT category_name FROM product_categories ORDER BY category_name";
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        }
        
        return categories;
    }
    
    @Override
    public Optional<Product> findProductByName(String productName) throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                    "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                    "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                    "FROM products p " +
                    "JOIN product_categories pc ON p.category_id = pc.category_id " +
                    "WHERE LOWER(p.product_name) = LOWER(?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    public boolean productNameExists(String productName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE LOWER(product_name) = LOWER(?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    public List<Product> searchProducts(String searchTerm) throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                    "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                    "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                    "FROM products p " +
                    "JOIN product_categories pc ON p.category_id = pc.category_id " +
                    "WHERE LOWER(p.product_name) LIKE LOWER(?) " +
                    "OR LOWER(p.description) LIKE LOWER(?) ORDER BY p.product_name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        
        return products;
    }
    
    /**
     * Helper method to get category_id from category name
     */
    private Integer getCategoryIdByName(String categoryName) throws SQLException {
        String sql = "SELECT category_id FROM product_categories WHERE category_name = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoryName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        
        // Get category name from joined table and convert to enum
        String categoryName = rs.getString("category_name");
        try {
            product.setCategory(ProductCategory.valueOf(categoryName));
        } catch (IllegalArgumentException e) {
            // If category doesn't match enum, default to SPECIAL
            product.setCategory(ProductCategory.SPECIAL);
        }
        
        product.setPrice(rs.getBigDecimal("price"));
        product.setDescription(rs.getString("description"));
        
        // Set inventory fields
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setReorderLevel(rs.getInt("reorder_level"));
        product.setMaxStockLevel(rs.getInt("max_stock_level"));
        product.setUnit(rs.getString("unit"));
        
        product.setAvailable(rs.getBoolean("is_available"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return product;
    }
}
