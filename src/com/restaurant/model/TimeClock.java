package com.restaurant.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * TimeClock Model Class
 * Represents an employee clock-in/clock-out record
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class TimeClock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Integer clockId;
    private Integer userId;
    private String employeeId;
    private String employeeName;  // For display purposes
    private String role;          // For display purposes
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private BigDecimal totalHours;
    private ClockStatus status;
    private String notes;
    private LocalDateTime createdAt;
    
    /**
     * Clock Status Enumeration
     */
    public enum ClockStatus {
        CLOCKED_IN("Clocked In"),
        CLOCKED_OUT("Clocked Out");
        
        private final String displayName;
        
        ClockStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static ClockStatus fromString(String status) {
            for (ClockStatus clockStatus : ClockStatus.values()) {
                if (clockStatus.name().equalsIgnoreCase(status) || 
                    clockStatus.displayName.equalsIgnoreCase(status)) {
                    return clockStatus;
                }
            }
            return CLOCKED_OUT;
        }
    }
    
    /**
     * Default constructor
     */
    public TimeClock() {
        this.status = ClockStatus.CLOCKED_IN;
        this.clockInTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor for clock-in
     */
    public TimeClock(Integer userId, String employeeId) {
        this();
        this.userId = userId;
        this.employeeId = employeeId;
    }
    
    /**
     * Calculate total hours worked
     */
    public void calculateTotalHours() {
        if (clockInTime != null && clockOutTime != null) {
            Duration duration = Duration.between(clockInTime, clockOutTime);
            long minutes = duration.toMinutes();
            this.totalHours = BigDecimal.valueOf(minutes / 60.0).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
    
    /**
     * Get formatted duration string
     */
    public String getFormattedDuration() {
        if (clockInTime == null) return "N/A";
        
        LocalDateTime endTime = clockOutTime != null ? clockOutTime : LocalDateTime.now();
        Duration duration = Duration.between(clockInTime, endTime);
        
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        
        return String.format("%d hrs %d min", hours, minutes);
    }
    
    // Getters and Setters
    
    public Integer getClockId() {
        return clockId;
    }
    
    public void setClockId(Integer clockId) {
        this.clockId = clockId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getClockInTime() {
        return clockInTime;
    }
    
    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }
    
    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }
    
    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }
    
    public BigDecimal getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }
    
    public ClockStatus getStatus() {
        return status;
    }
    
    public void setStatus(ClockStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "TimeClock{" +
                "clockId=" + clockId +
                ", userId=" + userId +
                ", employeeId='" + employeeId + '\'' +
                ", clockInTime=" + clockInTime +
                ", clockOutTime=" + clockOutTime +
                ", totalHours=" + totalHours +
                ", status=" + status +
                '}';
    }
}
