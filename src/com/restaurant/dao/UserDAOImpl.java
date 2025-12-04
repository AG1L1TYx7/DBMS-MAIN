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
        String sql = "INSERT INTO users (full_name, email_address, username, phone_number, " +
                    "password_hash, address, role, is_active, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmailAddress());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getPasswordHash());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getRole().name());
            stmt.setBoolean(8, user.isActive());
            stmt.setTimestamp(9, Timestamp.valueOf(user.getCreatedAt()));
            
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
                    
                    // Verify password using BCrypt
                    if (BCrypt.checkpw(password, storedHash)) {
                        User user = mapResultSetToUser(rs);
                        // Update last login time
                        updateLastLogin(user.getUserId());
                        return Optional.of(user);
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
}
