package com.restaurant.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Bill Model Class
 * Represents a bill/invoice for an order in the restaurant management system
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class Bill implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Integer billId;
    private String billNumber;
    private BigDecimal netAmount;
    private Integer totalItems;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    private PaymentMethod paymentMethod;
    private LocalDateTime billedAt;
    private Integer userId;
    private String billedByUser;
    private List<OrderItem> orderItems;
    
    /**
     * Payment Method Enumeration
     */
    public enum PaymentMethod {
        CASH("Cash"),
        CARD("Card"),
        DIGITAL_WALLET("Digital Wallet"),
        UPI("UPI");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Default constructor
     */
    public Bill() {
        this.billedAt = LocalDateTime.now();
        this.paymentMethod = PaymentMethod.CASH;
        this.orderItems = new ArrayList<>();
        this.taxAmount = BigDecimal.ZERO;
        generateBillNumber();
    }
    
    /**
     * Generate unique bill number
     */
    private void generateBillNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.billNumber = "BILL-" + billedAt.format(formatter);
    }
    
    /**
     * Calculate total from order items
     */
    public void calculateTotals() {
        this.netAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalItems = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        
        // Calculate tax (assuming 10% tax rate)
        this.taxAmount = netAmount.multiply(BigDecimal.valueOf(0.10));
        
        // Calculate total amount
        this.totalAmount = netAmount.add(taxAmount);
    }
    
    // Getters and Setters
    
    public Integer getBillId() {
        return billId;
    }
    
    public void setBillId(Integer billId) {
        this.billId = billId;
    }
    
    public String getBillNumber() {
        return billNumber;
    }
    
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
    
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
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
    
    public BigDecimal getCashReceived() {
        return cashReceived;
    }
    
    public void setCashReceived(BigDecimal cashReceived) {
        this.cashReceived = cashReceived;
        if (totalAmount != null) {
            this.changeAmount = cashReceived.subtract(totalAmount);
        }
    }
    
    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
    
    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDateTime getBilledAt() {
        return billedAt;
    }
    
    public void setBilledAt(LocalDateTime billedAt) {
        this.billedAt = billedAt;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getBilledByUser() {
        return billedByUser;
    }
    
    public void setBilledByUser(String billedByUser) {
        this.billedByUser = billedByUser;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculateTotals();
    }
    
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        calculateTotals();
    }
    
    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", billNumber='" + billNumber + '\'' +
                ", netAmount=" + netAmount +
                ", totalItems=" + totalItems +
                ", taxAmount=" + taxAmount +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", billedAt=" + billedAt +
                '}';
    }
}
