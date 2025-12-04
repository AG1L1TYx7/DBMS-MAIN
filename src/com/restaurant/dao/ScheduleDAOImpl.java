package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.Schedule;
import com.restaurant.model.Schedule.ShiftType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Schedule DAO Implementation
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class ScheduleDAOImpl implements ScheduleDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public ScheduleDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public Schedule createSchedule(Schedule schedule) {
        String sql = """
            INSERT INTO employee_schedules (user_id, schedule_date, shift_type, start_time, end_time, break_minutes, notes, created_by, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, schedule.getUserId());
            stmt.setDate(2, Date.valueOf(schedule.getScheduleDate()));
            stmt.setString(3, schedule.getShiftType().name());
            stmt.setTime(4, Time.valueOf(schedule.getStartTime()));
            stmt.setTime(5, Time.valueOf(schedule.getEndTime()));
            stmt.setInt(6, schedule.getBreakMinutes());
            stmt.setString(7, schedule.getNotes());
            stmt.setInt(8, schedule.getCreatedBy());
            stmt.setBoolean(9, schedule.isActive());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        schedule.setScheduleId(rs.getInt(1));
                        return schedule;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean updateSchedule(Schedule schedule) {
        String sql = """
            UPDATE employee_schedules SET user_id = ?, schedule_date = ?, shift_type = ?,
            start_time = ?, end_time = ?, break_minutes = ?, notes = ?, is_active = ?
            WHERE schedule_id = ?
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, schedule.getUserId());
            stmt.setDate(2, Date.valueOf(schedule.getScheduleDate()));
            stmt.setString(3, schedule.getShiftType().name());
            stmt.setTime(4, Time.valueOf(schedule.getStartTime()));
            stmt.setTime(5, Time.valueOf(schedule.getEndTime()));
            stmt.setInt(6, schedule.getBreakMinutes());
            stmt.setString(7, schedule.getNotes());
            stmt.setBoolean(8, schedule.isActive());
            stmt.setInt(9, schedule.getScheduleId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deleteSchedule(int scheduleId) {
        String sql = "DELETE FROM employee_schedules WHERE schedule_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, scheduleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Optional<Schedule> getScheduleById(int scheduleId) {
        String sql = """
            SELECT s.*, u.username, u.full_name, u.role as user_role, 
                   c.full_name as created_by_name
            FROM employee_schedules s
            JOIN users u ON s.user_id = u.user_id
            LEFT JOIN users c ON s.created_by = c.user_id
            WHERE s.schedule_id = ?
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, scheduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        String sql = """
            SELECT s.*, u.username, u.full_name, u.role as user_role,
                   c.full_name as created_by_name
            FROM employee_schedules s
            JOIN users u ON s.user_id = u.user_id
            LEFT JOIN users c ON s.created_by = c.user_id
            WHERE s.is_active = TRUE
            ORDER BY s.schedule_date DESC, s.start_time ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    @Override
    public List<Schedule> getSchedulesByUserId(int userId) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = """
            SELECT s.*, u.username, u.full_name, u.role as user_role,
                   c.full_name as created_by_name
            FROM employee_schedules s
            JOIN users u ON s.user_id = u.user_id
            LEFT JOIN users c ON s.created_by = c.user_id
            WHERE s.user_id = ? AND s.is_active = TRUE
            ORDER BY s.schedule_date DESC, s.start_time ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    @Override
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = """
            SELECT s.*, u.username, u.full_name, u.role as user_role,
                   c.full_name as created_by_name
            FROM employee_schedules s
            JOIN users u ON s.user_id = u.user_id
            LEFT JOIN users c ON s.created_by = c.user_id
            WHERE s.schedule_date = ? AND s.is_active = TRUE
            ORDER BY s.start_time ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    @Override
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = """
            SELECT s.*, u.username, u.full_name, u.role as user_role,
                   c.full_name as created_by_name
            FROM employee_schedules s
            JOIN users u ON s.user_id = u.user_id
            LEFT JOIN users c ON s.created_by = c.user_id
            WHERE s.schedule_date BETWEEN ? AND ? AND s.is_active = TRUE
            ORDER BY s.schedule_date ASC, s.start_time ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    @Override
    public List<Schedule> getSchedulesByUserAndDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = """
            SELECT s.*, u.username, u.full_name, u.role as user_role,
                   c.full_name as created_by_name
            FROM employee_schedules s
            JOIN users u ON s.user_id = u.user_id
            LEFT JOIN users c ON s.created_by = c.user_id
            WHERE s.user_id = ? AND s.schedule_date BETWEEN ? AND ? AND s.is_active = TRUE
            ORDER BY s.schedule_date ASC, s.start_time ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    @Override
    public List<Schedule> getWeekSchedules(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return getSchedulesByDateRange(weekStart, weekEnd);
    }
    
    @Override
    public List<Schedule> getMonthSchedules(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return getSchedulesByDateRange(startDate, endDate);
    }
    
    @Override
    public boolean hasScheduleOnDate(int userId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM employee_schedules WHERE user_id = ? AND schedule_date = ? AND is_active = TRUE";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean hasOverlappingSchedule(int userId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime, int excludeScheduleId) {
        String sql = """
            SELECT COUNT(*) FROM employee_schedules 
            WHERE user_id = ? AND schedule_date = ? AND is_active = TRUE
            AND schedule_id != ?
            AND (
                (start_time < ? AND end_time > ?) OR
                (start_time >= ? AND start_time < ?) OR
                (end_time > ? AND end_time <= ?)
            )
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setInt(3, excludeScheduleId);
            // Check: existing shift fully contains new shift
            stmt.setTime(4, Time.valueOf(startTime));
            stmt.setTime(5, Time.valueOf(endTime));
            // Check: existing start is within new shift
            stmt.setTime(6, Time.valueOf(startTime));
            stmt.setTime(7, Time.valueOf(endTime));
            // Check: existing end is within new shift
            stmt.setTime(8, Time.valueOf(startTime));
            stmt.setTime(9, Time.valueOf(endTime));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setUserId(rs.getInt("user_id"));
        schedule.setUsername(rs.getString("username"));
        schedule.setFullName(rs.getString("full_name"));
        schedule.setUserRole(rs.getString("user_role"));
        schedule.setScheduleDate(rs.getDate("schedule_date").toLocalDate());
        schedule.setShiftType(ShiftType.valueOf(rs.getString("shift_type")));
        schedule.setStartTime(rs.getTime("start_time").toLocalTime());
        schedule.setEndTime(rs.getTime("end_time").toLocalTime());
        schedule.setBreakMinutes(rs.getInt("break_minutes"));
        schedule.setNotes(rs.getString("notes"));
        schedule.setCreatedBy(rs.getInt("created_by"));
        schedule.setCreatedByName(rs.getString("created_by_name"));
        schedule.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            schedule.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            schedule.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return schedule;
    }
}
