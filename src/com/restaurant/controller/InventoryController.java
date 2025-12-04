package com.restaurant.controller;

import com.restaurant.dao.ProductDAO;
import com.restaurant.dao.ProductDAOImpl;
import com.restaurant.model.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Inventory Controller
 * Handles inventory management operations including stock tracking, 
 * reorder alerts, and inventory adjustments
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class InventoryController {
    
    private final ProductDAO productDAO;
    
    /**
     * Constructor
     */
    public InventoryController() {
        this.productDAO = new ProductDAOImpl();
    }
    
    /**
     * Get all products with inventory information
     */
    public List<Product> getAllInventoryItems() {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get products with low stock (below reorder level)
     */
    public List<Product> getLowStockProducts() {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            List<Product> lowStockProducts = new ArrayList<>();
            
            for (Product product : allProducts) {
                if (product.isLowStock() && !product.isOutOfStock()) {
                    lowStockProducts.add(product);
                }
            }
            
            return lowStockProducts;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get out of stock products
     */
    public List<Product> getOutOfStockProducts() {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            List<Product> outOfStockProducts = new ArrayList<>();
            
            for (Product product : allProducts) {
                if (product.isOutOfStock()) {
                    outOfStockProducts.add(product);
                }
            }
            
            return outOfStockProducts;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Update stock quantity for a product
     * 
     * @param productId The product ID
     * @param newQuantity The new stock quantity
     * @param adjustmentType Type of adjustment: "ADD", "REMOVE", "SET"
     * @param reason Reason for adjustment
     * @return true if successful
     */
    public boolean updateStock(Integer productId, Integer newQuantity, 
                               String adjustmentType, String reason) {
        try {
            Optional<Product> productOpt = productDAO.findProductById(productId);
            if (productOpt.isEmpty()) {
                return false;
            }
            Product product = productOpt.get();
            
            Integer currentQuantity = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            Integer finalQuantity;
            
            switch (adjustmentType.toUpperCase()) {
                case "ADD":
                    finalQuantity = currentQuantity + newQuantity;
                    break;
                case "REMOVE":
                    finalQuantity = Math.max(0, currentQuantity - newQuantity);
                    break;
                case "SET":
                    finalQuantity = newQuantity;
                    break;
                default:
                    return false;
            }
            
            product.setStockQuantity(finalQuantity);
            
            // Update availability based on stock
            if (finalQuantity <= 0) {
                product.setAvailable(false);
            } else if (!product.isAvailable() && finalQuantity > 0) {
                product.setAvailable(true);
            }
            
            return productDAO.updateProduct(product);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update reorder level for a product
     */
    public boolean updateReorderLevel(Integer productId, Integer reorderLevel) {
        try {
            Optional<Product> productOpt = productDAO.findProductById(productId);
            if (productOpt.isEmpty()) {
                return false;
            }
            Product product = productOpt.get();
            
            product.setReorderLevel(reorderLevel);
            return productDAO.updateProduct(product);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update max stock level for a product
     */
    public boolean updateMaxStockLevel(Integer productId, Integer maxStockLevel) {
        try {
            Optional<Product> productOpt = productDAO.findProductById(productId);
            if (productOpt.isEmpty()) {
                return false;
            }
            Product product = productOpt.get();
            
            product.setMaxStockLevel(maxStockLevel);
            return productDAO.updateProduct(product);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update unit for a product
     */
    public boolean updateUnit(Integer productId, String unit) {
        try {
            Optional<Product> productOpt = productDAO.findProductById(productId);
            if (productOpt.isEmpty()) {
                return false;
            }
            Product product = productOpt.get();
            
            product.setUnit(unit);
            return productDAO.updateProduct(product);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get inventory statistics
     */
    public Map<String, Integer> getInventoryStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        try {
            List<Product> allProducts = productDAO.getAllProducts();
        
        int totalItems = allProducts.size();
        int lowStockCount = 0;
        int outOfStockCount = 0;
        int inStockCount = 0;
        
        for (Product product : allProducts) {
            if (product.isOutOfStock()) {
                outOfStockCount++;
            } else if (product.isLowStock()) {
                lowStockCount++;
            } else {
                inStockCount++;
            }
        }
        
            stats.put("total", totalItems);
            stats.put("lowStock", lowStockCount);
            stats.put("outOfStock", outOfStockCount);
            stats.put("inStock", inStockCount);
        } catch (SQLException e) {
            e.printStackTrace();
            stats.put("total", 0);
            stats.put("lowStock", 0);
            stats.put("outOfStock", 0);
            stats.put("inStock", 0);
        }
        
        return stats;
    }
    
    /**
     * Get products that need restocking (low or out of stock)
     */
    public List<Product> getProductsNeedingRestock() {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            List<Product> needRestock = new ArrayList<>();
            
            for (Product product : allProducts) {
                if (product.isLowStock() || product.isOutOfStock()) {
                    needRestock.add(product);
                }
            }
            
            return needRestock;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Initialize inventory for a product (set default values)
     */
    public boolean initializeInventory(Integer productId, Integer initialStock, 
                                       Integer reorderLevel, Integer maxStock, String unit) {
        try {
            Optional<Product> productOpt = productDAO.findProductById(productId);
            if (productOpt.isEmpty()) {
                return false;
            }
            Product product = productOpt.get();
            
            product.setStockQuantity(initialStock != null ? initialStock : 0);
            product.setReorderLevel(reorderLevel != null ? reorderLevel : 10);
            product.setMaxStockLevel(maxStock != null ? maxStock : 100);
            product.setUnit(unit != null ? unit : "pcs");
            
            // Set availability based on stock
            product.setAvailable(initialStock != null && initialStock > 0);
            
            return productDAO.updateProduct(product);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deduct stock when an order is placed
     */
    public boolean deductStock(Integer productId, Integer quantity) {
        return updateStock(productId, quantity, "REMOVE", "Order placed");
    }
    
    /**
     * Add stock when receiving inventory
     */
    public boolean addStock(Integer productId, Integer quantity) {
        return updateStock(productId, quantity, "ADD", "Stock received");
    }
}
