package com.restaurant.controller;

import com.restaurant.dao.UserDAO;
import com.restaurant.dao.UserDAOImpl;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * User Controller
 * Handles user management business logic
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class UserController {
    
    private final UserDAO userDAO;
    
    public UserController() {
        this.userDAO = new UserDAOImpl();
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            showError("Failed to load users: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Create new user
     */
    public boolean createUser(String fullName, String email, String username, 
                             String phoneNumber, String password, String address, 
                             String role) {
        try {
            // Validation
            if (fullName == null || fullName.trim().isEmpty()) {
                showError("Full name is required");
                return false;
            }
            
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showError("Valid email is required");
                return false;
            }
            
            if (username == null || username.length() < 3) {
                showError("Username must be at least 3 characters");
                return false;
            }
            
            if (phoneNumber == null || !phoneNumber.matches("^[0-9]{10}$")) {
                showError("Valid 10-digit phone number is required");
                return false;
            }
            
            if (password == null || password.length() < 6) {
                showError("Password must be at least 6 characters");
                return false;
            }
            
            // Check duplicates
            if (userDAO.emailExists(email)) {
                showError("Email already exists");
                return false;
            }
            
            if (userDAO.usernameExists(username)) {
                showError("Username already exists");
                return false;
            }
            
            if (userDAO.phoneNumberExists(phoneNumber)) {
                showError("Phone number already exists");
                return false;
            }
            
            // Create user
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmailAddress(email.trim().toLowerCase());
            user.setUsername(username.trim().toLowerCase());
            user.setPhoneNumber(phoneNumber.trim());
            user.setPasswordHash(password); // In production, hash the password
            user.setAddress(address != null ? address.trim() : "");
            user.setRole(UserRole.valueOf(role));
            user.setActive(true);
            
            userDAO.createUser(user);
            return true;
            
        } catch (SQLException e) {
            showError("Failed to create user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user
     */
    public boolean updateUser(Integer userId, String fullName, String email, 
                             String phoneNumber, String address, String role, boolean active) {
        try {
            Optional<User> optionalUser = userDAO.findUserById(userId);
            if (optionalUser.isEmpty()) {
                showError("User not found");
                return false;
            }
            
            User user = optionalUser.get();
            
            // Validation
            if (fullName == null || fullName.trim().isEmpty()) {
                showError("Full name is required");
                return false;
            }
            
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showError("Valid email is required");
                return false;
            }
            
            if (phoneNumber == null || !phoneNumber.matches("^[0-9]{10}$")) {
                showError("Valid 10-digit phone number is required");
                return false;
            }
            
            // Check duplicates (excluding current user)
            Optional<User> emailUser = userDAO.findUserByEmail(email.trim().toLowerCase());
            if (emailUser.isPresent() && !emailUser.get().getUserId().equals(userId)) {
                showError("Email already exists");
                return false;
            }
            
            // Update user
            user.setFullName(fullName.trim());
            user.setEmailAddress(email.trim().toLowerCase());
            user.setPhoneNumber(phoneNumber.trim());
            user.setAddress(address != null ? address.trim() : "");
            user.setRole(UserRole.valueOf(role));
            user.setActive(active);
            
            return userDAO.updateUser(user);
            
        } catch (SQLException e) {
            showError("Failed to update user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(Integer userId) {
        try {
            return userDAO.deleteUser(userId);
        } catch (SQLException e) {
            showError("Failed to delete user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Integer userId) {
        try {
            return userDAO.findUserById(userId);
        } catch (SQLException e) {
            showError("Failed to find user: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    /**
     * Toggle user active status
     */
    public boolean toggleUserStatus(Integer userId) {
        try {
            Optional<User> optionalUser = userDAO.findUserById(userId);
            if (optionalUser.isEmpty()) {
                showError("User not found");
                return false;
            }
            
            User user = optionalUser.get();
            user.setActive(!user.isActive());
            return userDAO.updateUser(user);
            
        } catch (SQLException e) {
            showError("Failed to update user status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Change password
     */
    public boolean changePassword(Integer userId, String newPassword) {
        try {
            if (newPassword == null || newPassword.length() < 6) {
                showError("Password must be at least 6 characters");
                return false;
            }
            
            Optional<User> optionalUser = userDAO.findUserById(userId);
            if (optionalUser.isEmpty()) {
                showError("User not found");
                return false;
            }
            
            User user = optionalUser.get();
            user.setPasswordHash(newPassword); // In production, hash the password
            return userDAO.updateUser(user);
            
        } catch (SQLException e) {
            showError("Failed to change password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
