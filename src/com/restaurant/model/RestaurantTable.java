package com.restaurant.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Restaurant Table Model Class
 * Represents a table in the restaurant
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class RestaurantTable implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer tableId;
    private String tableNumber;
    private String tableName;
    private Integer capacity;
    private TableStatus status;
    private String location;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Table Status Enumeration
     */
    public enum TableStatus {
        AVAILABLE("Available"),
        OCCUPIED("Occupied"),
        RESERVED("Reserved"),
        MAINTENANCE("Maintenance");
        
        private final String displayName;
        
        TableStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static TableStatus fromString(String status) {
            for (TableStatus tableStatus : TableStatus.values()) {
                if (tableStatus.name().equalsIgnoreCase(status) || 
                    tableStatus.displayName.equalsIgnoreCase(status)) {
                    return tableStatus;
                }
            }
            return AVAILABLE;
        }
    }
    
    /**
     * Default constructor
     */
    public RestaurantTable() {
        this.status = TableStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor
     */
    public RestaurantTable(String tableNumber, String tableName, Integer capacity, String location) {
        this();
        this.tableNumber = tableNumber;
        this.tableName = tableName;
        this.capacity = capacity;
        this.location = location;
    }
    
    // Getters and Setters
    
    public Integer getTableId() {
        return tableId;
    }
    
    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public TableStatus getStatus() {
        return status;
    }
    
    public void setStatus(TableStatus status) {
        this.status = status;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    /**
     * Check if table is available
     */
    public boolean isAvailable() {
        return status == TableStatus.AVAILABLE;
    }
    
    /**
     * Check if table is occupied
     */
    public boolean isOccupied() {
        return status == TableStatus.OCCUPIED;
    }
    
    /**
     * Check if table is reserved
     */
    public boolean isReserved() {
        return status == TableStatus.RESERVED;
    }
    
    @Override
    public String toString() {
        return "RestaurantTable{" +
                "tableId=" + tableId +
                ", tableNumber='" + tableNumber + '\'' +
                ", tableName='" + tableName + '\'' +
                ", capacity=" + capacity +
                ", status=" + status +
                ", location='" + location + '\'' +
                '}';
    }
}
