package com.restaurant.dao;

import com.restaurant.model.TimeClock;
import com.restaurant.model.TimeClock.ClockStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TimeClock Data Access Object Interface
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public interface TimeClockDAO {
    
    /**
     * Clock in an employee
     */
    TimeClock clockIn(Integer userId, String employeeId) throws SQLException;
    
    /**
     * Clock out an employee
     */
    boolean clockOut(Integer clockId) throws SQLException;
    
    /**
     * Get active clock-in record for user
     */
    Optional<TimeClock> getActiveClockIn(Integer userId) throws SQLException;
    
    /**
     * Check if employee is currently clocked in
     */
    boolean isClockedIn(Integer userId) throws SQLException;
    
    /**
     * Get all time records for a specific date
     */
    List<TimeClock> getRecordsByDate(LocalDate date) throws SQLException;
    
    /**
     * Get all time records for a specific employee
     */
    List<TimeClock> getRecordsByEmployee(Integer userId) throws SQLException;
    
    /**
     * Get all time records for a date range
     */
    List<TimeClock> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    
    /**
     * Get today's clock records
     */
    List<TimeClock> getTodaysRecords() throws SQLException;
    
    /**
     * Get all currently clocked-in employees
     */
    List<TimeClock> getCurrentlyClockedIn() throws SQLException;
    
    /**
     * Find clock record by ID
     */
    Optional<TimeClock> findById(Integer clockId) throws SQLException;
}
