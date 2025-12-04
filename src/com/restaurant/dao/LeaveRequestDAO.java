package com.restaurant.dao;

import com.restaurant.model.LeaveRequest;
import com.restaurant.model.LeaveRequest.LeaveStatus;
import com.restaurant.model.LeaveRequest.LeaveType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * LeaveRequest DAO Interface
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public interface LeaveRequestDAO {
    
    /**
     * Create a new leave request
     */
    LeaveRequest createLeaveRequest(LeaveRequest leaveRequest);
    
    /**
     * Update an existing leave request
     */
    boolean updateLeaveRequest(LeaveRequest leaveRequest);
    
    /**
     * Delete a leave request
     */
    boolean deleteLeaveRequest(int leaveId);
    
    /**
     * Get leave request by ID
     */
    Optional<LeaveRequest> getLeaveRequestById(int leaveId);
    
    /**
     * Get all leave requests
     */
    List<LeaveRequest> getAllLeaveRequests();
    
    /**
     * Get leave requests by user ID
     */
    List<LeaveRequest> getLeaveRequestsByUserId(int userId);
    
    /**
     * Get leave requests by status
     */
    List<LeaveRequest> getLeaveRequestsByStatus(LeaveStatus status);
    
    /**
     * Get pending leave requests (for admin review)
     */
    List<LeaveRequest> getPendingLeaveRequests();
    
    /**
     * Approve a leave request
     */
    boolean approveLeaveRequest(int leaveId, int reviewedBy, String reviewNotes);
    
    /**
     * Reject a leave request
     */
    boolean rejectLeaveRequest(int leaveId, int reviewedBy, String reviewNotes);
    
    /**
     * Cancel a leave request
     */
    boolean cancelLeaveRequest(int leaveId);
    
    /**
     * Check if user has overlapping leave requests
     */
    boolean hasOverlappingLeave(int userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get leave requests by date range
     */
    List<LeaveRequest> getLeaveRequestsByDateRange(LocalDate startDate, LocalDate endDate);
}
