package com.restaurant.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * LeaveRequest Model - Represents employee leave requests
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class LeaveRequest {
    
    private int leaveId;
    private int userId;
    private String username;      // For display
    private String fullName;      // For display
    private String userRole;      // For display
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private int reviewedBy;
    private String reviewedByName; // For display
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Leave Type Enumeration
     */
    public enum LeaveType {
        ANNUAL("Annual Leave"),
        SICK("Sick Leave"),
        PERSONAL("Personal Leave"),
        EMERGENCY("Emergency Leave"),
        UNPAID("Unpaid Leave");
        
        private final String displayName;
        
        LeaveType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Leave Status Enumeration
     */
    public enum LeaveStatus {
        PENDING("Pending", new java.awt.Color(243, 156, 18)),
        APPROVED("Approved", new java.awt.Color(46, 204, 113)),
        REJECTED("Rejected", new java.awt.Color(231, 76, 60)),
        CANCELLED("Cancelled", new java.awt.Color(149, 165, 166));
        
        private final String displayName;
        private final java.awt.Color color;
        
        LeaveStatus(String displayName, java.awt.Color color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public java.awt.Color getColor() {
            return color;
        }
    }
    
    // Constructors
    public LeaveRequest() {
        this.status = LeaveStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public LeaveRequest(int userId, LeaveType leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this();
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }
    
    // Calculate total days
    public long getTotalDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0;
    }
    
    // Getters and Setters
    public int getLeaveId() {
        return leaveId;
    }
    
    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }
    
    // Alias for database field name
    public int getRequestId() {
        return leaveId;
    }
    
    public void setRequestId(int requestId) {
        this.leaveId = requestId;
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
    
    public LeaveType getLeaveType() {
        return leaveType;
    }
    
    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LeaveStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
    
    public int getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(int reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    // Alias for database field name
    public int getApprovedBy() {
        return reviewedBy;
    }
    
    public void setApprovedBy(int approvedBy) {
        this.reviewedBy = approvedBy;
    }
    
    public String getReviewedByName() {
        return reviewedByName;
    }
    
    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }
    
    // Alias for database field name
    public String getApprovedByName() {
        return reviewedByName;
    }
    
    public void setApprovedByName(String approvedByName) {
        this.reviewedByName = approvedByName;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public String getReviewNotes() {
        return reviewNotes;
    }
    
    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
    
    // Alias for database field name
    public String getApprovalNotes() {
        return reviewNotes;
    }
    
    public void setApprovalNotes(String approvalNotes) {
        this.reviewNotes = approvalNotes;
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
        return String.format("LeaveRequest[%d] - %s: %s to %s (%s)", 
            leaveId, fullName, startDate, endDate, status.getDisplayName());
    }
}
