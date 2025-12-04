package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User DAO Implementation
 * Implements data access operations for User entity
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class UserDAOImpl implements UserDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public UserDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public User createUser(User user) throws SQLException {
        // Generate employee ID for SERVER and CHEF roles
        if (user.getRole() == UserRole.SERVER || user.getRole() == UserRole.CHEF) {
            String employeeId = generateEmployeeId();
            user.setEmployeeId(employeeId);
        }
        
        String sql = "INSERT INTO users (employee_id, full_name, email_address, username, phone_number, " +
                    "password_hash, address, role, is_active, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getEmployeeId());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmailAddress());
            stmt.setString(4, user.getUsername());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getPasswordHash());
            stmt.setString(7, user.getAddress());
            stmt.setString(8, user.getRole().name());
            stmt.setBoolean(9, user.isActive());
            stmt.setTimestamp(10, Timestamp.valueOf(user.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return user;
        }
    }
    
    @Override
    public Optional<User> findUserById(Integer userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<User> findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<User> findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email_address = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<User> authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = true";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    // Validate hash format before attempting BCrypt check
                    if (storedHash == null || storedHash.length() < 59 || !storedHash.startsWith("$2")) {
                        System.err.println("Invalid password hash for user: " + username + 
                                         " (length: " + (storedHash != null ? storedHash.length() : 0) + 
                                         "). Please use signup form to create users.");
                        return Optional.empty();
                    }
                    
                    // Verify password using BCrypt
                    try {
                        if (BCrypt.checkpw(password, storedHash)) {
                            User user = mapResultSetToUser(rs);
                            // Update last login time
                            updateLastLogin(user.getUserId());
                            return Optional.of(user);
                        }
                    } catch (Exception e) {
                        System.err.println("BCrypt verification failed for user: " + username + 
                                         " - " + e.getMessage());
                        return Optional.empty();
                    }
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email_address = ?, phone_number = ?, " +
                    "address = ?, role = ?, is_active = ? WHERE user_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmailAddress());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getUserId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean deleteUser(Integer userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    @Override
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email_address = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    @Override
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    @Override
    public boolean phoneNumberExists(String phoneNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE phone_number = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phoneNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    /**
     * Update last login timestamp
     */
    private void updateLastLogin(Integer userId) throws SQLException {
        String sql = "UPDATE users SET last_login_at = ? WHERE user_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmployeeId(rs.getString("employee_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmailAddress(rs.getString("email_address"));
        user.setUsername(rs.getString("username"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAddress(rs.getString("address"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            user.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }
        
        return user;
    }
    
    @Override
    public String generateEmployeeId() throws SQLException {
        String sql = "SELECT MAX(CAST(employee_id AS UNSIGNED)) FROM users WHERE employee_id IS NOT NULL";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int nextId = 1001; // Start from 1001
            
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (maxId >= 1001) {
                    nextId = maxId + 1;
                }
            }
            
            // Ensure 4 digits
            if (nextId > 9999) {
                throw new SQLException("Employee ID limit reached");
            }
            
            return String.format("%04d", nextId);
        }
    }
    
    @Override
    public Optional<User> findUserByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT * FROM users WHERE employee_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<User> getAllEmployees() throws SQLException {
        String sql = "SELECT * FROM users WHERE role IN ('SERVER', 'CHEF') ORDER BY employee_id";
        List<User> employees = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(mapResultSetToUser(rs));
            }
        }
        
        return employees;
    }
}
