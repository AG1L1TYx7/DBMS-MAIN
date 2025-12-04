package com.restaurant.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Schedule Model - Represents employee work schedules
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class Schedule {
    
    private int scheduleId;
    private int userId;
    private String username;      // For display
    private String fullName;      // For display
    private String userRole;      // For display
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftType shiftType;
    private int breakMinutes;
    private String notes;
    private int createdBy;
    private String createdByName; // For display
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Shift Type Enumeration
     */
    public enum ShiftType {
        MORNING("Morning Shift", "06:00", "14:00"),
        AFTERNOON("Afternoon Shift", "14:00", "22:00"),
        EVENING("Evening Shift", "16:00", "00:00"),
        NIGHT("Night Shift", "22:00", "06:00"),
        FULL_DAY("Full Day", "09:00", "21:00");
        
        private final String displayName;
        private final String defaultStart;
        private final String defaultEnd;
        
        ShiftType(String displayName, String defaultStart, String defaultEnd) {
            this.displayName = displayName;
            this.defaultStart = defaultStart;
            this.defaultEnd = defaultEnd;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDefaultStart() {
            return defaultStart;
        }
        
        public String getDefaultEnd() {
            return defaultEnd;
        }
    }
    
    // Constructors
    public Schedule() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.breakMinutes = 0;
    }
    
    public Schedule(int userId, LocalDate scheduleDate, LocalTime startTime, LocalTime endTime, ShiftType shiftType) {
        this();
        this.userId = userId;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftType = shiftType;
    }
    
    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public LocalDate getScheduleDate() {
        return scheduleDate;
    }
    
    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public ShiftType getShiftType() {
        return shiftType;
    }
    
    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
    
    public int getBreakMinutes() {
        return breakMinutes;
    }
    
    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
    
    @Override
    public String toString() {
        return String.format("Schedule[%d] - %s: %s (%s - %s)", 
            scheduleId, fullName, scheduleDate, startTime, endTime);
    }
}
