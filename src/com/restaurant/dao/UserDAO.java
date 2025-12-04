package com.restaurant.dao;

import com.restaurant.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * User Data Access Object Interface
 * Defines CRUD operations for User entity
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public interface UserDAO {
    
    /**
     * Create a new user
     * 
     * @param user User object to create
     * @return created user with generated ID
     * @throws SQLException if database operation fails
     */
    User createUser(User user) throws SQLException;
    
    /**
     * Find user by ID
     * 
     * @param userId user ID
     * @return Optional containing user if found
     * @throws SQLException if database operation fails
     */
    Optional<User> findUserById(Integer userId) throws SQLException;
    
    /**
     * Find user by username
     * 
     * @param username username
     * @return Optional containing user if found
     * @throws SQLException if database operation fails
     */
    Optional<User> findUserByUsername(String username) throws SQLException;
    
    /**
     * Find user by email
     * 
     * @param email email address
     * @return Optional containing user if found
     * @throws SQLException if database operation fails
     */
    Optional<User> findUserByEmail(String email) throws SQLException;
    
    /**
     * Authenticate user
     * 
     * @param username username
     * @param password password
     * @return Optional containing user if authentication successful
     * @throws SQLException if database operation fails
     */
    Optional<User> authenticateUser(String username, String password) throws SQLException;
    
    /**
     * Update user information
     * 
     * @param user User object with updated information
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updateUser(User user) throws SQLException;
    
    /**
     * Delete user by ID
     * 
     * @param userId user ID
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    boolean deleteUser(Integer userId) throws SQLException;
    
    /**
     * Get all users
     * 
     * @return list of all users
     * @throws SQLException if database operation fails
     */
    List<User> getAllUsers() throws SQLException;
    
    /**
     * Check if email exists
     * 
     * @param email email address
     * @return true if email exists
     * @throws SQLException if database operation fails
     */
    boolean emailExists(String email) throws SQLException;
    
    /**
     * Check if username exists
     * 
     * @param username username
     * @return true if username exists
     * @throws SQLException if database operation fails
     */
    boolean usernameExists(String username) throws SQLException;
    
    /**
     * Check if phone number exists
     * 
     * @param phoneNumber phone number
     * @return true if phone number exists
     * @throws SQLException if database operation fails
     */
    boolean phoneNumberExists(String phoneNumber) throws SQLException;
}
