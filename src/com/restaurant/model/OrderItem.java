package com.restaurant.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * OrderItem Model Class
 * Represents an individual item in an order
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class OrderItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    
    /**
     * Default constructor
     */
    public OrderItem() {
        this.quantity = 1;
    }
    
    /**
     * Parameterized constructor
     */
    public OrderItem(Integer productId, String productName, Integer quantity, 
                     BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    
    public Integer getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
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
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    /**
     * Calculate subtotal based on quantity and unit price
     */
    private void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}
