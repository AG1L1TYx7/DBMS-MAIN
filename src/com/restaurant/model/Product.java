package com.restaurant.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Model Class
 * Represents a product/menu item in the restaurant management system
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer productId;
    private String productName;
    private ProductCategory category;
    private BigDecimal price;
    private String description;
    private String imagePath;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Inventory fields
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Integer maxStockLevel;
    private String unit; // e.g., "pcs", "kg", "liters"
    
    /**
     * Product Category Enumeration
     */
    public enum ProductCategory {
        BURGER("Burger"),
        CHICKEN_ROLL("Chicken Roll"),
        RICE_MEALS("Rice Meals"),
        BEVERAGE("Beverage"),
        FRIES("Fries"),
        DESSERTS("Desserts"),
        SOFT_COCKTAIL("Soft Cocktail"),
        MILKSHAKE("Milkshake"),
        APPETIZER("Appetizer"),
        SPECIAL("Special");
        
        private final String displayName;
        
        ProductCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static ProductCategory fromString(String categoryName) {
            for (ProductCategory category : ProductCategory.values()) {
                if (category.displayName.equalsIgnoreCase(categoryName) || 
                    category.name().equalsIgnoreCase(categoryName)) {
                    return category;
                }
            }
            return null;
        }
    }
    
    /**
     * Default constructor
     */
    public Product() {
        this.isAvailable = true;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor
     */
    public Product(String productName, ProductCategory category, BigDecimal price, 
                   String description, String imagePath) {
        this();
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
    }
    
    // Getters and Setters
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }
    
    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    /**
     * Check if product stock is low (below or at reorder level, but not out of stock)
     */
    public boolean isLowStock() {
        return stockQuantity != null && reorderLevel != null 
            && stockQuantity > 0 && stockQuantity <= reorderLevel;
    }
    
    /**
     * Check if product is out of stock
     */
    public boolean isOutOfStock() {
        return stockQuantity == null || stockQuantity <= 0;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", isAvailable=" + isAvailable +
                ", stockQuantity=" + stockQuantity +
                ", reorderLevel=" + reorderLevel +
                '}';
    }
}
