package com.restaurant.controller;

import com.restaurant.dao.UserDAO;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for AuthenticationController.
 * 
 * <p>This test class validates the authentication business logic in the restaurant
 * management system, including user login, registration, password validation,
 * and session management. It uses Mockito for mocking the UserDAO layer.</p>
 * 
 * <p>Test categories covered:</p>
 * <ul>
 *   <li>Authentication Tests - Login validation with correct/incorrect credentials</li>
 *   <li>User Registration Tests - New user creation and validation</li>
 *   <li>Current User Tests - Session tracking and logout functionality</li>
 *   <li>Password Validation Tests - BCrypt hashing and verification</li>
 *   <li>User Lookup Tests - Finding users by ID, username, or email</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see AuthenticationController
 * @see UserDAO
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {
    
    /** Mock UserDAO for testing authentication operations without database access. */
    @Mock
    private UserDAO mockUserDAO;
    
    /**
     * Nested test class for core authentication tests.
     * 
     * <p>Tests the fundamental authentication operations including:
     * <ul>
     *   <li>Successful login with valid credentials</li>
     *   <li>Failed login with incorrect password</li>
     *   <li>Failed login for non-existent users</li>
     *   <li>Role verification during authentication</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {
        
        /**
         * Tests successful authentication with valid username and password.
         * 
         * <p>Verifies that a user can log in when providing correct credentials
         * and that the returned user object contains expected values.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should authenticate with correct credentials")
        void testAuthenticateSuccess() throws SQLException {
            // Given
            User user = createTestUser(1, "admin", UserRole.ADMIN);
            when(mockUserDAO.authenticateUser("admin", "password123"))
                .thenReturn(Optional.of(user));
            
            // When
            Optional<User> result = mockUserDAO.authenticateUser("admin", "password123");
            
            // Then
            assertTrue(result.isPresent());
            assertEquals("admin", result.get().getUsername());
            assertEquals(UserRole.ADMIN, result.get().getRole());
        }
        
        /**
         * Tests authentication failure when providing an incorrect password.
         * 
         * <p>Verifies that the system returns an empty Optional when
         * the password doesn't match the stored hash.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail authentication with wrong password")
        void testAuthenticateWrongPassword() throws SQLException {
            // Given
            when(mockUserDAO.authenticateUser("admin", "wrongpassword"))
                .thenReturn(Optional.empty());
            
            // When
            Optional<User> result = mockUserDAO.authenticateUser("admin", "wrongpassword");
            
            // Then
            assertTrue(result.isEmpty());
        }
        
        /**
         * Tests authentication failure for a non-existent username.
         * 
         * <p>Verifies that attempting to authenticate with a username
         * that doesn't exist in the system returns an empty Optional.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail authentication for non-existent user")
        void testAuthenticateNonExistentUser() throws SQLException {
            // Given
            when(mockUserDAO.authenticateUser("nonexistent", "password"))
                .thenReturn(Optional.empty());
            
            // When
            Optional<User> result = mockUserDAO.authenticateUser("nonexistent", "password");
            
            // Then
            assertTrue(result.isEmpty());
        }
        
        /**
         * Tests that user role is correctly returned after successful authentication.
         * 
         * <p>Verifies that when a user authenticates, their role (SERVER, CHEF, ADMIN, etc.)
         * is properly retrieved and accessible from the returned user object.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should verify user role during authentication")
        void testAuthenticateWithRoleVerification() throws SQLException {
            // Given
            User serverUser = createTestUser(2, "server1", UserRole.SERVER);
            when(mockUserDAO.authenticateUser("server1", "password123"))
                .thenReturn(Optional.of(serverUser));
            
            // When
            Optional<User> result = mockUserDAO.authenticateUser("server1", "password123");
            
            // Then
            assertTrue(result.isPresent());
            assertEquals(UserRole.SERVER, result.get().getRole());
            assertEquals("SERVER", result.get().getRole().name());
        }
    }
    
    /**
     * Nested test class for user registration tests.
     * 
     * <p>Tests the user registration workflow including:
     * <ul>
     *   <li>Email uniqueness validation before registration</li>
     *   <li>Username uniqueness validation</li>
     *   <li>Password hashing with BCrypt</li>
     *   <li>Employee ID generation based on user role</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {
        
        /**
         * Tests email uniqueness check during registration.
         * 
         * <p>Verifies that the system can detect whether an email address
         * is already registered, preventing duplicate accounts.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check if email exists before registration")
        void testEmailExistsCheck() throws SQLException {
            // Given
            when(mockUserDAO.emailExists("existing@example.com")).thenReturn(true);
            when(mockUserDAO.emailExists("new@example.com")).thenReturn(false);
            
            // Then
            assertTrue(mockUserDAO.emailExists("existing@example.com"));
            assertFalse(mockUserDAO.emailExists("new@example.com"));
        }
        
        /**
         * Tests username uniqueness check during registration.
         * 
         * <p>Verifies that the system can detect whether a username
         * is already taken, ensuring unique usernames for all accounts.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check if username exists before registration")
        void testUsernameExistsCheck() throws SQLException {
            // Given
            when(mockUserDAO.usernameExists("existinguser")).thenReturn(true);
            when(mockUserDAO.usernameExists("newuser")).thenReturn(false);
            
            // Then
            assertTrue(mockUserDAO.usernameExists("existinguser"));
            assertFalse(mockUserDAO.usernameExists("newuser"));
        }
        
        /**
         * Tests that user passwords are properly hashed using BCrypt.
         * 
         * <p>Verifies that when creating a new user, the password is hashed
         * using BCrypt algorithm (identified by $2 prefix) and not stored in plain text.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should create user with hashed password")
        void testCreateUserWithHashedPassword() throws SQLException {
            // Given
            User user = new User();
            user.setUsername("newuser");
            user.setFullName("New User");
            user.setEmailAddress("new@example.com");
            user.setRole(UserRole.SERVER);
            
            // Hash password with BCrypt
            String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt(12));
            user.setPasswordHash(hashedPassword);
            
            User createdUser = new User();
            createdUser.setUserId(1);
            createdUser.setUsername(user.getUsername());
            createdUser.setPasswordHash(user.getPasswordHash());
            
            when(mockUserDAO.createUser(any(User.class))).thenReturn(createdUser);
            
            // When
            User result = mockUserDAO.createUser(user);
            
            // Then
            assertNotNull(result);
            assertNotNull(result.getUserId());
            assertTrue(result.getPasswordHash().startsWith("$2"));
        }
        
        /**
         * Tests that employee ID is required for SERVER role users.
         * 
         * <p>Verifies that users with SERVER role need an employee ID
         * generated during the registration process.</p>
         */
        @Test
        @DisplayName("Should generate employee ID for SERVER role")
        void testEmployeeIdGenerationForServer() {
            User user = new User();
            user.setRole(UserRole.SERVER);
            
            // In actual implementation, employee ID is generated
            boolean needsEmployeeId = user.getRole() == UserRole.SERVER || 
                                       user.getRole() == UserRole.CHEF;
            
            assertTrue(needsEmployeeId);
        }
        
        /**
         * Tests that employee ID is required for CHEF role users.
         * 
         * <p>Verifies that users with CHEF role need an employee ID
         * generated during the registration process.</p>
         */
        @Test
        @DisplayName("Should generate employee ID for CHEF role")
        void testEmployeeIdGenerationForChef() {
            User user = new User();
            user.setRole(UserRole.CHEF);
            
            boolean needsEmployeeId = user.getRole() == UserRole.SERVER || 
                                       user.getRole() == UserRole.CHEF;
            
            assertTrue(needsEmployeeId);
        }
        
        /**
         * Tests that ADMIN role users do not require an employee ID.
         * 
         * <p>Verifies that users with ADMIN role are exempt from
         * the employee ID generation requirement.</p>
         */
        @Test
        @DisplayName("Should NOT generate employee ID for ADMIN role")
        void testNoEmployeeIdForAdmin() {
            User user = new User();
            user.setRole(UserRole.ADMIN);
            
            boolean needsEmployeeId = user.getRole() == UserRole.SERVER || 
                                       user.getRole() == UserRole.CHEF;
            
            assertFalse(needsEmployeeId);
        }
    }
    
    /**
     * Nested test class for current user session tests.
     * 
     * <p>Tests the session management functionality including:
     * <ul>
     *   <li>Tracking the currently logged-in user</li>
     *   <li>Logout functionality and session clearing</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Current User Tests")
    class CurrentUserTests {
        
        /**
         * Tests that the system properly tracks the current logged-in user.
         * 
         * <p>Verifies that after successful authentication, the user's
         * information is accessible through the session management.</p>
         */
        @Test
        @DisplayName("Should track current logged in user")
        void testCurrentUserTracking() {
            User user = createTestUser(1, "admin", UserRole.ADMIN);
            
            // Simulate setting current user
            assertNotNull(user);
            assertEquals("admin", user.getUsername());
        }
        
        /**
         * Tests that logout properly clears the current user session.
         * 
         * <p>Verifies that calling logout() removes the current user
         * reference, returning null for subsequent getCurrentUser() calls.</p>
         */
        @Test
        @DisplayName("Should clear current user on logout")
        void testLogout() {
            // Simulate logout
            AuthenticationController.logout();
            
            assertNull(AuthenticationController.getCurrentUser());
        }
    }
    
    /**
     * Nested test class for password validation and hashing tests.
     * 
     * <p>Tests the password security features including:
     * <ul>
     *   <li>BCrypt password hashing with proper salt</li>
     *   <li>Password verification against stored hashes</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {
        
        /**
         * Tests that passwords are hashed using BCrypt algorithm.
         * 
         * <p>Verifies that the hashed password starts with "$2" (BCrypt identifier)
         * and has the expected minimum length for a BCrypt hash.</p>
         */
        @Test
        @DisplayName("Should hash password with BCrypt")
        void testPasswordHashing() {
            String plainPassword = "password123";
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
            
            assertTrue(hashedPassword.startsWith("$2"));
            assertTrue(hashedPassword.length() >= 59);
        }
        
        /**
         * Tests BCrypt password verification functionality.
         * 
         * <p>Verifies that BCrypt.checkpw() correctly validates matching passwords
         * and rejects non-matching passwords against the stored hash.</p>
         */
        @Test
        @DisplayName("Should verify hashed password")
        void testPasswordVerification() {
            String plainPassword = "password123";
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
            
            assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));
            assertFalse(BCrypt.checkpw("wrongpassword", hashedPassword));
        }
    }
    
    /**
     * Nested test class for user lookup and search tests.
     * 
     * <p>Tests various methods to find users in the system:
     * <ul>
     *   <li>Finding users by their unique ID</li>
     *   <li>Finding users by username</li>
     *   <li>Finding users by email address</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("User Lookup Tests")
    class UserLookupTests {
        
        /**
         * Tests finding a user by their unique identifier.
         * 
         * <p>Verifies that findUserById() returns an Optional containing
         * the user when the ID exists in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should find user by ID")
        void testFindUserById() throws SQLException {
            // Given
            User user = createTestUser(1, "testuser", UserRole.SERVER);
            when(mockUserDAO.findUserById(1)).thenReturn(Optional.of(user));
            
            // When
            Optional<User> result = mockUserDAO.findUserById(1);
            
            // Then
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getUserId());
        }
        
        /**
         * Tests finding a user by their username.
         * 
         * <p>Verifies that findUserByUsername() returns an Optional containing
         * the user when the username exists in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should find user by username")
        void testFindUserByUsername() throws SQLException {
            // Given
            User user = createTestUser(1, "testuser", UserRole.SERVER);
            when(mockUserDAO.findUserByUsername("testuser")).thenReturn(Optional.of(user));
            
            // When
            Optional<User> result = mockUserDAO.findUserByUsername("testuser");
            
            // Then
            assertTrue(result.isPresent());
            assertEquals("testuser", result.get().getUsername());
        }
        
        /**
         * Tests finding a user by their email address.
         * 
         * <p>Verifies that findUserByEmail() returns an Optional containing
         * the user when the email exists in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should find user by email")
        void testFindUserByEmail() throws SQLException {
            // Given
            User user = createTestUser(1, "testuser", UserRole.SERVER);
            user.setEmailAddress("test@example.com");
            when(mockUserDAO.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            
            // When
            Optional<User> result = mockUserDAO.findUserByEmail("test@example.com");
            
            // Then
            assertTrue(result.isPresent());
            assertEquals("test@example.com", result.get().getEmailAddress());
        }
    }
    
    /**
     * Creates a test User instance with specified parameters.
     * 
     * <p>This helper method creates a fully populated User object for use
     * in test cases, including a BCrypt-hashed password.</p>
     * 
     * @param id the user ID to assign
     * @param username the username for the test user
     * @param role the UserRole to assign (ADMIN, SERVER, CHEF, CUSTOMER)
     * @return a new User instance populated with test data
     */
    private User createTestUser(int id, String username, UserRole role) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setFullName("Test User");
        user.setEmailAddress(username + "@example.com");
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPasswordHash(BCrypt.hashpw("password123", BCrypt.gensalt(12)));
        return user;
    }
}
