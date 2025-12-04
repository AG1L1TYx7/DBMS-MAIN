package com.restaurant.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Reservation Model Class
 * Represents a table reservation
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class Reservation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Integer reservationId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Integer tableId;
    private String tableNumber; // For display purposes
    private String tableName; // For display purposes
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer numberOfGuests;
    private String specialRequests;
    private ReservationStatus status;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Reservation Status Enumeration
     */
    public enum ReservationStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed"),
        NO_SHOW("No Show");
        
        private final String displayName;
        
        ReservationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static ReservationStatus fromString(String status) {
            for (ReservationStatus reservationStatus : ReservationStatus.values()) {
                if (reservationStatus.name().equalsIgnoreCase(status) || 
                    reservationStatus.displayName.equalsIgnoreCase(status)) {
                    return reservationStatus;
                }
            }
            return PENDING;
        }
    }
    
    /**
     * Default constructor
     */
    public Reservation() {
        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor
     */
    public Reservation(String customerName, String customerPhone, Integer tableId,
                      LocalDate reservationDate, LocalTime reservationTime, Integer numberOfGuests) {
        this();
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.tableId = tableId;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.numberOfGuests = numberOfGuests;
    }
    
    // Getters and Setters
    
    public Integer getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
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
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
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
    
    public LocalDate getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public LocalTime getReservationTime() {
        return reservationTime;
    }
    
    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }
    
    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }
    
    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public Integer getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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
     * Check if reservation is active (pending or confirmed)
     */
    public boolean isActive() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }
    
    /**
     * Check if reservation is for today
     */
    public boolean isToday() {
        return reservationDate != null && reservationDate.equals(LocalDate.now());
    }
    
    /**
     * Check if reservation is upcoming
     */
    public boolean isUpcoming() {
        return reservationDate != null && reservationDate.isAfter(LocalDate.now());
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", tableNumber='" + tableNumber + '\'' +
                ", reservationDate=" + reservationDate +
                ", reservationTime=" + reservationTime +
                ", numberOfGuests=" + numberOfGuests +
                ", status=" + status +
                '}';
    }
}
