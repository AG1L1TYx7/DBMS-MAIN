package com.restaurant.controller;

import com.restaurant.dao.ScheduleDAO;
import com.restaurant.dao.ScheduleDAOImpl;
import com.restaurant.dao.LeaveRequestDAO;
import com.restaurant.dao.LeaveRequestDAOImpl;
import com.restaurant.model.Schedule;
import com.restaurant.model.Schedule.ShiftType;
import com.restaurant.model.LeaveRequest;
import com.restaurant.model.LeaveRequest.LeaveStatus;
import com.restaurant.model.LeaveRequest.LeaveType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Schedule Controller - Handles scheduling and leave request operations
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class ScheduleController {
    
    private final ScheduleDAO scheduleDAO;
    private final LeaveRequestDAO leaveRequestDAO;
    
    public ScheduleController() {
        this.scheduleDAO = new ScheduleDAOImpl();
        this.leaveRequestDAO = new LeaveRequestDAOImpl();
    }
    
    // ==================== Schedule Operations ====================
    
    /**
     * Create a new schedule
     */
    public Schedule createSchedule(int userId, LocalDate date, LocalTime startTime, 
                                   LocalTime endTime, ShiftType shiftType, String notes, int createdBy) {
        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setScheduleDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setShiftType(shiftType);
        schedule.setNotes(notes);
        schedule.setCreatedBy(createdBy);
        
        return scheduleDAO.createSchedule(schedule);
    }
    
    /**
     * Update an existing schedule
     */
    public boolean updateSchedule(Schedule schedule) {
        return scheduleDAO.updateSchedule(schedule);
    }
    
    /**
     * Delete a schedule
     */
    public boolean deleteSchedule(int scheduleId) {
        return scheduleDAO.deleteSchedule(scheduleId);
    }
    
    /**
     * Get schedule by ID
     */
    public Optional<Schedule> getScheduleById(int scheduleId) {
        return scheduleDAO.getScheduleById(scheduleId);
    }
    
    /**
     * Get all schedules
     */
    public List<Schedule> getAllSchedules() {
        return scheduleDAO.getAllSchedules();
    }
    
    /**
     * Get schedules for a specific user
     */
    public List<Schedule> getSchedulesByUserId(int userId) {
        return scheduleDAO.getSchedulesByUserId(userId);
    }
    
    /**
     * Get schedules for a specific date
     */
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleDAO.getSchedulesByDate(date);
    }
    
    /**
     * Get schedules for a date range
     */
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleDAO.getSchedulesByDateRange(startDate, endDate);
    }
    
    /**
     * Get schedules for a user within a date range
     */
    public List<Schedule> getSchedulesByUserAndDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        return scheduleDAO.getSchedulesByUserAndDateRange(userId, startDate, endDate);
    }
    
    /**
     * Get schedules for current week
     */
    public List<Schedule> getCurrentWeekSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return scheduleDAO.getWeekSchedules(weekStart);
    }
    
    /**
     * Get schedules for a specific week
     */
    public List<Schedule> getWeekSchedules(LocalDate weekStart) {
        return scheduleDAO.getWeekSchedules(weekStart);
    }
    
    /**
     * Get schedules for current month
     */
    public List<Schedule> getCurrentMonthSchedules() {
        LocalDate today = LocalDate.now();
        return scheduleDAO.getMonthSchedules(today.getYear(), today.getMonthValue());
    }
    
    /**
     * Get schedules for a specific month
     */
    public List<Schedule> getMonthSchedules(int year, int month) {
        return scheduleDAO.getMonthSchedules(year, month);
    }
    
    /**
     * Check if user has schedule on date
     */
    public boolean hasScheduleOnDate(int userId, LocalDate date) {
        return scheduleDAO.hasScheduleOnDate(userId, date);
    }
    
    /**
     * Check if a proposed schedule overlaps with existing schedules
     * @param userId the employee's user ID
     * @param date the schedule date
     * @param startTime the proposed start time
     * @param endTime the proposed end time
     * @param excludeScheduleId schedule ID to exclude (for edits), pass 0 for new schedules
     * @return true if there's an overlap
     */
    public boolean hasOverlappingSchedule(int userId, LocalDate date, LocalTime startTime, LocalTime endTime, int excludeScheduleId) {
        return scheduleDAO.hasOverlappingSchedule(userId, date, startTime, endTime, excludeScheduleId);
    }
    
    /**
     * Create schedule with overlap validation
     * @return the created schedule, or null if validation fails or creation fails
     */
    public Schedule createScheduleWithValidation(int userId, LocalDate date, LocalTime startTime, 
                                                  LocalTime endTime, ShiftType shiftType, String notes, int createdBy) {
        // Check for overlapping schedules
        if (hasOverlappingSchedule(userId, date, startTime, endTime, 0)) {
            return null;
        }
        return createSchedule(userId, date, startTime, endTime, shiftType, notes, createdBy);
    }
    
    // ==================== Leave Request Operations ====================
    
    /**
     * Submit a leave request
     */
    public LeaveRequest submitLeaveRequest(int userId, LeaveType leaveType, 
                                           LocalDate startDate, LocalDate endDate, String reason) {
        // Check for overlapping leaves
        if (leaveRequestDAO.hasOverlappingLeave(userId, startDate, endDate)) {
            return null; // Overlapping leave exists
        }
        
        LeaveRequest request = new LeaveRequest();
        request.setUserId(userId);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReason(reason);
        request.setStatus(LeaveStatus.PENDING);
        
        return leaveRequestDAO.createLeaveRequest(request);
    }
    
    /**
     * Get all leave requests
     */
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestDAO.getAllLeaveRequests();
    }
    
    /**
     * Get leave requests for a user
     */
    public List<LeaveRequest> getLeaveRequestsByUserId(int userId) {
        return leaveRequestDAO.getLeaveRequestsByUserId(userId);
    }
    
    /**
     * Get pending leave requests
     */
    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestDAO.getPendingLeaveRequests();
    }
    
    /**
     * Approve a leave request
     */
    public boolean approveLeaveRequest(int leaveId, int reviewedBy, String reviewNotes) {
        return leaveRequestDAO.approveLeaveRequest(leaveId, reviewedBy, reviewNotes);
    }
    
    /**
     * Reject a leave request
     */
    public boolean rejectLeaveRequest(int leaveId, int reviewedBy, String reviewNotes) {
        return leaveRequestDAO.rejectLeaveRequest(leaveId, reviewedBy, reviewNotes);
    }
    
    /**
     * Cancel a leave request
     */
    public boolean cancelLeaveRequest(int leaveId) {
        return leaveRequestDAO.cancelLeaveRequest(leaveId);
    }
    
    /**
     * Get leave request by ID
     */
    public Optional<LeaveRequest> getLeaveRequestById(int leaveId) {
        return leaveRequestDAO.getLeaveRequestById(leaveId);
    }
    
    /**
     * Check if user has overlapping leave
     */
    public boolean hasOverlappingLeave(int userId, LocalDate startDate, LocalDate endDate) {
        return leaveRequestDAO.hasOverlappingLeave(userId, startDate, endDate);
    }
    
    /**
     * Get approved leaves for date range (useful for calendar display)
     */
    public List<LeaveRequest> getApprovedLeavesInRange(LocalDate startDate, LocalDate endDate) {
        return leaveRequestDAO.getLeaveRequestsByDateRange(startDate, endDate)
                .stream()
                .filter(lr -> lr.getStatus() == LeaveStatus.APPROVED)
                .toList();
    }
}
