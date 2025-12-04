package com.restaurant.controller;

import com.restaurant.dao.UserDAO;
import com.restaurant.dao.UserDAOImpl;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Authentication Controller
 * Handles user authentication and registration business logic
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class AuthenticationController {
    
    private final UserDAO userDAO;
    private static User currentUser;
    
    public AuthenticationController() {
        this.userDAO = new UserDAOImpl();
    }
    
    /**
     * Authenticate user login (simple version without role)
     * 
     * @param username username
     * @param password password
     * @return authenticated user or null
     */
    public User authenticateUser(String username, String password) {
        try {
            // Check database for user
            Optional<User> userOpt = userDAO.authenticateUser(username, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                return currentUser;
            }
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database error during authentication: " + e.getMessage(),
                "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Authenticate user login
     * 
     * @param username username
     * @param password password
     * @param role selected role
     * @return authenticated user or null
     */
    public User authenticateUser(String username, String password, String role) {
        try {
            // Validate for any role login
            Optional<User> userOpt = userDAO.authenticateUser(username, password);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Verify role matches
                if (user.getRole().name().equals(role)) {
                    currentUser = user;
                    return currentUser;
                }
            }
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database error during authentication: " + e.getMessage(),
                "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Register new user
     * 
     * @param user user object to register
     * @return true if registration successful
     */
    public boolean registerUser(User user) {
        try {
            // Validate email
            if (userDAO.emailExists(user.getEmailAddress())) {
                JOptionPane.showMessageDialog(null,
                    "Email address is already registered. Please use a different email.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate username
            if (userDAO.usernameExists(user.getUsername())) {
                JOptionPane.showMessageDialog(null,
                    "Username is already taken. Please choose a different username.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate phone number
            if (userDAO.phoneNumberExists(user.getPhoneNumber())) {
                JOptionPane.showMessageDialog(null,
                    "Phone number is already registered. Please use a different number.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Create user
            User createdUser = userDAO.createUser(user);
            return createdUser != null && createdUser.getUserId() != null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database error during registration: " + e.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Get current logged-in user
     * 
     * @return current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Logout current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Check if user is authenticated
     * 
     * @return true if user is logged in
     */
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * Check if current user is admin
     * 
     * @return true if current user is admin
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }
    
    /**
     * Validate password strength
     * 
     * @param password password to validate
     * @return true if password meets requirements
     */
    public boolean validatePassword(String password) {
        // At least 8 characters, one uppercase, one lowercase, one digit, one special character
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(pattern);
    }
    
    /**
     * Validate email format
     * 
     * @param email email to validate
     * @return true if email is valid
     */
    public boolean validateEmail(String email) {
        String pattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(pattern);
    }
    
    /**
     * Validate username format
     * 
     * @param username username to validate
     * @return true if username is valid
     */
    public boolean validateUsername(String username) {
        // 5-10 alphanumeric characters
        String pattern = "^[a-zA-Z0-9]{5,10}$";
        return username.matches(pattern);
    }
    
    /**
     * Validate phone number format
     * 
     * @param phoneNumber phone number to validate
     * @return true if phone number is valid
     */
    public boolean validatePhoneNumber(String phoneNumber) {
        // 10 digit number
        String pattern = "^[0-9]{10}$";
        return phoneNumber.matches(pattern);
    }
}
