package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.LeaveRequest;
import com.restaurant.model.LeaveRequest.LeaveStatus;
import com.restaurant.model.LeaveRequest.LeaveType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Leave Request DAO Implementation
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class LeaveRequestDAOImpl implements LeaveRequestDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public LeaveRequestDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public LeaveRequest createLeaveRequest(LeaveRequest request) {
        String sql = """
            INSERT INTO leave_requests (user_id, leave_type, start_date, end_date, reason, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, request.getUserId());
            stmt.setString(2, request.getLeaveType().name());
            stmt.setDate(3, Date.valueOf(request.getStartDate()));
            stmt.setDate(4, Date.valueOf(request.getEndDate()));
            stmt.setString(5, request.getReason());
            stmt.setString(6, request.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        request.setRequestId(rs.getInt(1));
                        return request;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean updateLeaveRequest(LeaveRequest request) {
        String sql = """
            UPDATE leave_requests SET leave_type = ?, start_date = ?, end_date = ?, reason = ?, status = ?
            WHERE request_id = ?
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getLeaveType().name());
            stmt.setDate(2, Date.valueOf(request.getStartDate()));
            stmt.setDate(3, Date.valueOf(request.getEndDate()));
            stmt.setString(4, request.getReason());
            stmt.setString(5, request.getStatus().name());
            stmt.setInt(6, request.getRequestId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean approveLeaveRequest(int requestId, int approvedBy, String notes) {
        String sql = """
            UPDATE leave_requests SET status = 'APPROVED', approved_by = ?, approval_notes = ?, updated_at = NOW()
            WHERE request_id = ?
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, approvedBy);
            stmt.setString(2, notes);
            stmt.setInt(3, requestId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean rejectLeaveRequest(int requestId, int rejectedBy, String notes) {
        String sql = """
            UPDATE leave_requests SET status = 'REJECTED', approved_by = ?, approval_notes = ?, updated_at = NOW()
            WHERE request_id = ?
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rejectedBy);
            stmt.setString(2, notes);
            stmt.setInt(3, requestId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean cancelLeaveRequest(int requestId) {
        String sql = "UPDATE leave_requests SET status = 'CANCELLED', updated_at = NOW() WHERE request_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deleteLeaveRequest(int requestId) {
        String sql = "DELETE FROM leave_requests WHERE request_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Optional<LeaveRequest> getLeaveRequestById(int requestId) {
        String sql = """
            SELECT lr.*, u.username, u.full_name, u.role as user_role,
                   a.full_name as approved_by_name
            FROM leave_requests lr
            JOIN users u ON lr.user_id = u.user_id
            LEFT JOIN users a ON lr.approved_by = a.user_id
            WHERE lr.request_id = ?
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLeaveRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = """
            SELECT lr.*, u.username, u.full_name, u.role as user_role,
                   a.full_name as approved_by_name
            FROM leave_requests lr
            JOIN users u ON lr.user_id = u.user_id
            LEFT JOIN users a ON lr.approved_by = a.user_id
            ORDER BY lr.created_at DESC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                requests.add(mapResultSetToLeaveRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    @Override
    public List<LeaveRequest> getLeaveRequestsByUserId(int userId) {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = """
            SELECT lr.*, u.username, u.full_name, u.role as user_role,
                   a.full_name as approved_by_name
            FROM leave_requests lr
            JOIN users u ON lr.user_id = u.user_id
            LEFT JOIN users a ON lr.approved_by = a.user_id
            WHERE lr.user_id = ?
            ORDER BY lr.created_at DESC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToLeaveRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    @Override
    public List<LeaveRequest> getPendingLeaveRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = """
            SELECT lr.*, u.username, u.full_name, u.role as user_role,
                   a.full_name as approved_by_name
            FROM leave_requests lr
            JOIN users u ON lr.user_id = u.user_id
            LEFT JOIN users a ON lr.approved_by = a.user_id
            WHERE lr.status = 'PENDING'
            ORDER BY lr.created_at ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                requests.add(mapResultSetToLeaveRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    @Override
    public List<LeaveRequest> getLeaveRequestsByStatus(LeaveStatus status) {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = """
            SELECT lr.*, u.username, u.full_name, u.role as user_role,
                   a.full_name as approved_by_name
            FROM leave_requests lr
            JOIN users u ON lr.user_id = u.user_id
            LEFT JOIN users a ON lr.approved_by = a.user_id
            WHERE lr.status = ?
            ORDER BY lr.created_at DESC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToLeaveRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    @Override
    public List<LeaveRequest> getLeaveRequestsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = """
            SELECT lr.*, u.username, u.full_name, u.role as user_role,
                   a.full_name as approved_by_name
            FROM leave_requests lr
            JOIN users u ON lr.user_id = u.user_id
            LEFT JOIN users a ON lr.approved_by = a.user_id
            WHERE (lr.start_date BETWEEN ? AND ?) OR (lr.end_date BETWEEN ? AND ?)
                  OR (lr.start_date <= ? AND lr.end_date >= ?)
            ORDER BY lr.start_date ASC
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setDate(5, Date.valueOf(startDate));
            stmt.setDate(6, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToLeaveRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    @Override
    public boolean hasOverlappingLeave(int userId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT COUNT(*) FROM leave_requests 
            WHERE user_id = ? AND status IN ('PENDING', 'APPROVED')
            AND ((start_date BETWEEN ? AND ?) OR (end_date BETWEEN ? AND ?)
                 OR (start_date <= ? AND end_date >= ?))
            """;
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            stmt.setDate(4, Date.valueOf(startDate));
            stmt.setDate(5, Date.valueOf(endDate));
            stmt.setDate(6, Date.valueOf(startDate));
            stmt.setDate(7, Date.valueOf(endDate));
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
    
    private LeaveRequest mapResultSetToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setUsername(rs.getString("username"));
        request.setFullName(rs.getString("full_name"));
        request.setUserRole(rs.getString("user_role"));
        request.setLeaveType(LeaveType.valueOf(rs.getString("leave_type")));
        request.setStartDate(rs.getDate("start_date").toLocalDate());
        request.setEndDate(rs.getDate("end_date").toLocalDate());
        request.setReason(rs.getString("reason"));
        request.setStatus(LeaveStatus.valueOf(rs.getString("status")));
        
        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            request.setApprovedBy(approvedBy);
            request.setApprovedByName(rs.getString("approved_by_name"));
        }
        
        request.setApprovalNotes(rs.getString("approval_notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            request.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            request.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return request;
    }
}
