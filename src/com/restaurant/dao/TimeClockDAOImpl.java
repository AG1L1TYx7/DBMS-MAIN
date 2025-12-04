package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.TimeClock;
import com.restaurant.model.TimeClock.ClockStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TimeClock Data Access Object Implementation
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class TimeClockDAOImpl implements TimeClockDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public TimeClockDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public TimeClock clockIn(Integer userId, String employeeId) throws SQLException {
        String sql = "INSERT INTO time_clock (user_id, employee_id, clock_in_time, status) VALUES (?, ?, NOW(), 'CLOCKED_IN')";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, employeeId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Integer clockId = generatedKeys.getInt(1);
                        return findById(clockId).orElse(null);
                    }
                }
            }
            return null;
        }
    }
    
    @Override
    public boolean clockOut(Integer clockId) throws SQLException {
        String sql = "UPDATE time_clock SET clock_out_time = NOW(), status = 'CLOCKED_OUT', " +
                     "total_hours = TIMESTAMPDIFF(MINUTE, clock_in_time, NOW()) / 60.0 " +
                     "WHERE clock_id = ? AND status = 'CLOCKED_IN'";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clockId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public Optional<TimeClock> getActiveClockIn(Integer userId) throws SQLException {
        String sql = "SELECT tc.*, u.full_name, u.role FROM time_clock tc " +
                     "JOIN users u ON tc.user_id = u.user_id " +
                     "WHERE tc.user_id = ? AND tc.status = 'CLOCKED_IN' " +
                     "ORDER BY tc.clock_in_time DESC LIMIT 1";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeClock(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public boolean isClockedIn(Integer userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM time_clock WHERE user_id = ? AND status = 'CLOCKED_IN'";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public List<TimeClock> getRecordsByDate(LocalDate date) throws SQLException {
        String sql = "SELECT tc.*, u.full_name, u.role FROM time_clock tc " +
                     "JOIN users u ON tc.user_id = u.user_id " +
                     "WHERE DATE(tc.clock_in_time) = ? " +
                     "ORDER BY tc.clock_in_time DESC";
        
        List<TimeClock> records = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToTimeClock(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public List<TimeClock> getRecordsByEmployee(Integer userId) throws SQLException {
        String sql = "SELECT tc.*, u.full_name, u.role FROM time_clock tc " +
                     "JOIN users u ON tc.user_id = u.user_id " +
                     "WHERE tc.user_id = ? " +
                     "ORDER BY tc.clock_in_time DESC LIMIT 50";
        
        List<TimeClock> records = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToTimeClock(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public List<TimeClock> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT tc.*, u.full_name, u.role FROM time_clock tc " +
                     "JOIN users u ON tc.user_id = u.user_id " +
                     "WHERE DATE(tc.clock_in_time) BETWEEN ? AND ? " +
                     "ORDER BY tc.clock_in_time DESC";
        
        List<TimeClock> records = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToTimeClock(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public List<TimeClock> getTodaysRecords() throws SQLException {
        return getRecordsByDate(LocalDate.now());
    }
    
    @Override
    public List<TimeClock> getCurrentlyClockedIn() throws SQLException {
        String sql = "SELECT tc.*, u.full_name, u.role FROM time_clock tc " +
                     "JOIN users u ON tc.user_id = u.user_id " +
                     "WHERE tc.status = 'CLOCKED_IN' " +
                     "ORDER BY tc.clock_in_time ASC";
        
        List<TimeClock> records = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToTimeClock(rs));
                }
            }
        }
        return records;
    }
    
    @Override
    public Optional<TimeClock> findById(Integer clockId) throws SQLException {
        String sql = "SELECT tc.*, u.full_name, u.role FROM time_clock tc " +
                     "JOIN users u ON tc.user_id = u.user_id " +
                     "WHERE tc.clock_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clockId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeClock(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    private TimeClock mapResultSetToTimeClock(ResultSet rs) throws SQLException {
        TimeClock tc = new TimeClock();
        tc.setClockId(rs.getInt("clock_id"));
        tc.setUserId(rs.getInt("user_id"));
        tc.setEmployeeId(rs.getString("employee_id"));
        tc.setEmployeeName(rs.getString("full_name"));
        tc.setRole(rs.getString("role"));
        
        Timestamp clockIn = rs.getTimestamp("clock_in_time");
        if (clockIn != null) {
            tc.setClockInTime(clockIn.toLocalDateTime());
        }
        
        Timestamp clockOut = rs.getTimestamp("clock_out_time");
        if (clockOut != null) {
            tc.setClockOutTime(clockOut.toLocalDateTime());
        }
        
        BigDecimal totalHours = rs.getBigDecimal("total_hours");
        if (totalHours != null) {
            tc.setTotalHours(totalHours);
        }
        
        String status = rs.getString("status");
        if (status != null) {
            tc.setStatus(ClockStatus.fromString(status));
        }
        
        tc.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            tc.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return tc;
    }
}
