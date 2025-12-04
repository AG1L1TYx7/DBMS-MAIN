package com.restaurant.dao;

import com.restaurant.model.Schedule;
import com.restaurant.model.Schedule.ShiftType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Schedule DAO Interface
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public interface ScheduleDAO {
    
    /**
     * Create a new schedule
     */
    Schedule createSchedule(Schedule schedule);
    
    /**
     * Update an existing schedule
     */
    boolean updateSchedule(Schedule schedule);
    
    /**
     * Delete a schedule
     */
    boolean deleteSchedule(int scheduleId);
    
    /**
     * Get schedule by ID
     */
    Optional<Schedule> getScheduleById(int scheduleId);
    
    /**
     * Get all schedules
     */
    List<Schedule> getAllSchedules();
    
    /**
     * Get schedules by user ID
     */
    List<Schedule> getSchedulesByUserId(int userId);
    
    /**
     * Get schedules by date
     */
    List<Schedule> getSchedulesByDate(LocalDate date);
    
    /**
     * Get schedules by date range
     */
    List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get schedules by user and date range
     */
    List<Schedule> getSchedulesByUserAndDateRange(int userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get schedules for a specific week (for calendar view)
     */
    List<Schedule> getWeekSchedules(LocalDate weekStart);
    
    /**
     * Get schedules for a specific month (for calendar view)
     */
    List<Schedule> getMonthSchedules(int year, int month);
    
    /**
     * Check if user has schedule on date
     */
    boolean hasScheduleOnDate(int userId, LocalDate date);
    
    /**
     * Check if user has overlapping schedule on date (for validation)
     * @param userId the employee's user ID
     * @param date the schedule date
     * @param startTime the proposed start time
     * @param endTime the proposed end time
     * @param excludeScheduleId optional schedule ID to exclude (for edits), pass 0 to not exclude
     * @return true if there's an overlapping schedule
     */
    boolean hasOverlappingSchedule(int userId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime, int excludeScheduleId);
}
