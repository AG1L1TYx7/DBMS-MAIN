package com.restaurant.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Model Class
 * Represents a customer order in the restaurant management system
 * Supports kitchen workflow with status tracking
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Integer orderId;
    private String orderNumber;
    private OrderType orderType;
    private String tableNumber;
    private OrderStatus status;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private Integer serverId;
    private String serverName;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime preparedAt;
    private LocalDateTime servedAt;
    private Integer billId;
    private List<OrderItem> orderItems;
    
    /**
     * Order Type Enumeration
     */
    public enum OrderType {
        DINE_IN("Dine In"),
        TAKEOUT("Takeout"),
        DELIVERY("Delivery");
        
        private final String displayName;
        
        OrderType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Order Status Enumeration - Kitchen Workflow
     */
    public enum OrderStatus {
        PENDING("Pending", "🕐"),           // Order placed, waiting for kitchen
        CONFIRMED("Confirmed", "✓"),        // Order confirmed by kitchen
        PREPARING("Preparing", "👨‍🍳"),       // Chef is preparing
        READY("Ready", "✅"),                // Food ready for pickup/serving
        SERVED("Served", "🍽️"),              // Food delivered to customer
        COMPLETED("Completed", "💰"),        // Bill paid
        CANCELLED("Cancelled", "❌");        // Order cancelled
        
        private final String displayName;
        private final String icon;
        
        OrderStatus(String displayName, String icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getIcon() {
            return icon;
        }
    }
    
    /**
     * Default constructor
     */
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.orderType = OrderType.DINE_IN;
        this.orderItems = new ArrayList<>();
        this.taxAmount = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        generateOrderNumber();
    }
    
    /**
     * Generate unique order number
     */
    private void generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.orderNumber = "ORD-" + createdAt.format(formatter);
    }
    
    /**
     * Calculate totals from order items
     */
    public void calculateTotals() {
        this.subtotal = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate tax (10% tax rate)
        this.taxAmount = subtotal.multiply(BigDecimal.valueOf(0.10));
        
        // Calculate total amount
        this.totalAmount = subtotal.add(taxAmount);
    }
    
    /**
     * Get total number of items
     */
    public int getTotalItems() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    /**
     * Add item to order
     */
    public void addItem(OrderItem item) {
        this.orderItems.add(item);
        calculateTotals();
    }
    
    /**
     * Remove item from order
     */
    public void removeItem(OrderItem item) {
        this.orderItems.remove(item);
        calculateTotals();
    }
    
    /**
     * Get display string for table/takeout
     */
    public String getLocationDisplay() {
        if (orderType == OrderType.TAKEOUT) {
            return "TAKEOUT";
        } else if (orderType == OrderType.DELIVERY) {
            return "DELIVERY";
        } else {
            return tableNumber != null ? "Table " + tableNumber : "No Table";
        }
    }
    
    /**
     * Check if order can be sent to kitchen
     */
    public boolean canSendToKitchen() {
        return status == OrderStatus.PENDING && !orderItems.isEmpty();
    }
    
    /**
     * Check if order can be marked as ready
     */
    public boolean canMarkReady() {
        return status == OrderStatus.PREPARING;
    }
    
    /**
     * Check if order can be served
     */
    public boolean canServe() {
        return status == OrderStatus.READY;
    }
    
    /**
     * Format created time for display
     */
    public String getFormattedCreatedTime() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }
    
    /**
     * Get time elapsed since order was created (for kitchen display)
     */
    public String getWaitTime() {
        if (createdAt != null) {
            long minutes = java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
            if (minutes < 60) {
                return minutes + " min";
            } else {
                return (minutes / 60) + "h " + (minutes % 60) + "m";
            }
        }
        return "";
    }

    // Getters and Setters
    
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        if (status == OrderStatus.READY && this.preparedAt == null) {
            this.preparedAt = LocalDateTime.now();
        }
        if (status == OrderStatus.SERVED && this.servedAt == null) {
            this.servedAt = LocalDateTime.now();
        }
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(LocalDateTime preparedAt) {
        this.preparedAt = preparedAt;
    }

    public LocalDateTime getServedAt() {
        return servedAt;
    }

    public void setServedAt(LocalDateTime servedAt) {
        this.servedAt = servedAt;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculateTotals();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber='" + orderNumber + '\'' +
                ", orderType=" + orderType +
                ", tableNumber='" + tableNumber + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
