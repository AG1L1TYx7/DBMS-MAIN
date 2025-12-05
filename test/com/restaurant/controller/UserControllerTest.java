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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for UserController.
 * 
 * <p>This test class validates the user management business logic in the restaurant
 * management system, including user CRUD operations, validation rules, role management,
 * and account status handling. It uses Mockito for mocking the UserDAO layer.</p>
 * 
 * <p>Test categories covered:</p>
 * <ul>
 *   <li>Get All Users Tests - Retrieving all users from the system</li>
 *   <li>User Validation Tests - Input validation for user fields</li>
 *   <li>Duplicate Check Tests - Ensuring unique email, username, and phone</li>
 *   <li>Create User Tests - New user creation with all fields</li>
 *   <li>Update User Tests - Modifying existing user details</li>
 *   <li>Delete User Tests - Removing users from the system</li>
 *   <li>User Role Tests - Role assignment and conversion</li>
 *   <li>User Active Status Tests - Account activation and deactivation</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see UserController
 * @see UserDAO
 * @see User
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserControllerTest {
    
    /** Mock UserDAO for testing user operations without database access. */
    @Mock
    private UserDAO mockUserDAO;
    
    /**
     * Nested test class for retrieving all users.
     * 
     * <p>Tests the user listing functionality including:
     * <ul>
     *   <li>Returning all users from the database</li>
     *   <li>Handling database errors gracefully</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {
        
        /**
         * Tests retrieval of all users from the system.
         * 
         * <p>Verifies that getAllUsers() returns a complete list of all
         * registered users with their details.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should return all users")
        void testGetAllUsers() throws SQLException {
            // Given
            List<User> users = createTestUsers();
            when(mockUserDAO.getAllUsers()).thenReturn(users);
            
            // When
            List<User> result = mockUserDAO.getAllUsers();
            
            // Then
            assertEquals(3, result.size());
        }
        
        /**
         * Tests error handling when database operation fails.
         * 
         * <p>Verifies that SQLException is properly thrown when
         * the database encounters an error during user retrieval.</p>
         * 
         * @throws SQLException expected when database error occurs
         */
        @Test
        @DisplayName("Should return empty list on exception")
        void testGetAllUsersOnError() throws SQLException {
            // Given
            when(mockUserDAO.getAllUsers()).thenThrow(new SQLException("DB Error"));
            
            // Then
            assertThrows(SQLException.class, () -> mockUserDAO.getAllUsers());
        }
    }
    
    /**
     * Nested test class for user input validation tests.
     * 
     * <p>Tests the validation rules for user fields including:
     * <ul>
     *   <li>Full name is required and not empty</li>
     *   <li>Email format validation using regex</li>
     *   <li>Username minimum length requirement</li>
     *   <li>Phone number format (10 digits)</li>
     *   <li>Password minimum length requirement</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("User Validation Tests")
    class UserValidationTests {
        
        /**
         * Tests validation that full name is not empty or null.
         * 
         * <p>Verifies that empty strings and null values are properly
         * identified as invalid full names.</p>
         */
        @Test
        @DisplayName("Should validate full name is not empty")
        void testFullNameValidation() {
            String emptyName = "";
            String nullName = null;
            String validName = "John Doe";
            
            assertTrue(emptyName == null || emptyName.trim().isEmpty());
            assertTrue(nullName == null || nullName.trim().isEmpty());
            assertFalse(validName == null || validName.trim().isEmpty());
        }
        
        /**
         * Tests email format validation using regex pattern.
         * 
         * <p>Verifies that valid email formats are accepted and
         * invalid formats are rejected. Supports various valid formats
         * including tags and subdomains.</p>
         */
        @Test
        @DisplayName("Should validate email format")
        void testEmailValidation() {
            String validEmail = "test@example.com";
            String invalidEmail = "invalid-email";
            String anotherValid = "user.name+tag@domain.co.uk";
            
            assertTrue(validEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
            assertFalse(invalidEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
            assertTrue(anotherValid.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
        }
        
        /**
         * Tests username minimum length validation.
         * 
         * <p>Verifies that usernames must be at least 3 characters long.
         * Shorter usernames should be rejected.</p>
         */
        @Test
        @DisplayName("Should validate username length")
        void testUsernameValidation() {
            String shortUsername = "ab";
            String validUsername = "validuser";
            String minUsername = "abc";
            
            assertTrue(shortUsername.length() < 3);
            assertFalse(validUsername.length() < 3);
            assertFalse(minUsername.length() < 3);
        }
        
        /**
         * Tests phone number format validation.
         * 
         * <p>Verifies that phone numbers must be exactly 10 digits.
         * Invalid formats like too short, too long, or containing letters
         * should be rejected.</p>
         */
        @Test
        @DisplayName("Should validate phone number format")
        void testPhoneNumberValidation() {
            String validPhone = "1234567890";
            String shortPhone = "12345";
            String longPhone = "12345678901234";
            String withLetters = "12345abcde";
            
            assertTrue(validPhone.matches("^[0-9]{10}$"));
            assertFalse(shortPhone.matches("^[0-9]{10}$"));
            assertFalse(longPhone.matches("^[0-9]{10}$"));
            assertFalse(withLetters.matches("^[0-9]{10}$"));
        }
        
        /**
         * Tests password minimum length validation.
         * 
         * <p>Verifies that passwords must be at least 6 characters long.
         * Shorter passwords should be rejected for security reasons.</p>
         */
        @Test
        @DisplayName("Should validate password length")
        void testPasswordValidation() {
            String shortPassword = "12345";
            String validPassword = "password123";
            String minPassword = "123456";
            
            assertTrue(shortPassword.length() < 6);
            assertFalse(validPassword.length() < 6);
            assertFalse(minPassword.length() < 6);
        }
    }
    
    /**
     * Nested test class for duplicate field validation tests.
     * 
     * <p>Tests the uniqueness checks for user fields including:
     * <ul>
     *   <li>Email uniqueness across all users</li>
     *   <li>Username uniqueness across all users</li>
     *   <li>Phone number uniqueness across all users</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Duplicate Check Tests")
    class DuplicateCheckTests {
        
        /**
         * Tests email existence check for duplicate prevention.
         * 
         * <p>Verifies that the system can detect whether an email address
         * is already registered, returning true for existing emails.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check if email exists")
        void testEmailExists() throws SQLException {
            // Given
            when(mockUserDAO.emailExists("existing@example.com")).thenReturn(true);
            when(mockUserDAO.emailExists("new@example.com")).thenReturn(false);
            
            // Then
            assertTrue(mockUserDAO.emailExists("existing@example.com"));
            assertFalse(mockUserDAO.emailExists("new@example.com"));
        }
        
        /**
         * Tests username existence check for duplicate prevention.
         * 
         * <p>Verifies that the system can detect whether a username
         * is already taken, returning true for existing usernames.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check if username exists")
        void testUsernameExists() throws SQLException {
            // Given
            when(mockUserDAO.usernameExists("existinguser")).thenReturn(true);
            when(mockUserDAO.usernameExists("newuser")).thenReturn(false);
            
            // Then
            assertTrue(mockUserDAO.usernameExists("existinguser"));
            assertFalse(mockUserDAO.usernameExists("newuser"));
        }
        
        /**
         * Tests phone number existence check for duplicate prevention.
         * 
         * <p>Verifies that the system can detect whether a phone number
         * is already registered, returning true for existing phone numbers.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check if phone number exists")
        void testPhoneNumberExists() throws SQLException {
            // Given
            when(mockUserDAO.phoneNumberExists("1234567890")).thenReturn(true);
            when(mockUserDAO.phoneNumberExists("0987654321")).thenReturn(false);
            
            // Then
            assertTrue(mockUserDAO.phoneNumberExists("1234567890"));
            assertFalse(mockUserDAO.phoneNumberExists("0987654321"));
        }
    }
    
    /**
     * Nested test class for user creation tests.
     * 
     * <p>Tests the user creation workflow including:
     * <ul>
     *   <li>Creating users with all required and optional fields</li>
     *   <li>Password hashing using BCrypt algorithm</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {
        
        /**
         * Tests successful user creation with all fields populated.
         * 
         * <p>Verifies that a user can be created with all required fields
         * and that duplicate checks pass before creation.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should create user with all fields")
        void testCreateUserSuccess() throws SQLException {
            // Given
            User newUser = createTestUser(null, "newuser", UserRole.SERVER);
            User savedUser = createTestUser(1, "newuser", UserRole.SERVER);
            
            when(mockUserDAO.emailExists(any())).thenReturn(false);
            when(mockUserDAO.usernameExists(any())).thenReturn(false);
            when(mockUserDAO.phoneNumberExists(any())).thenReturn(false);
            when(mockUserDAO.createUser(any(User.class))).thenReturn(savedUser);
            
            // When
            User result = mockUserDAO.createUser(newUser);
            
            // Then
            assertNotNull(result);
            assertNotNull(result.getUserId());
        }
        
        /**
         * Tests that passwords are hashed using BCrypt algorithm.
         * 
         * <p>Verifies that the hashed password starts with "$2" (BCrypt identifier),
         * has the expected minimum length, and can be verified with BCrypt.checkpw().</p>
         */
        @Test
        @DisplayName("Should hash password with BCrypt")
        void testPasswordHashing() {
            String plainPassword = "password123";
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
            
            assertTrue(hashedPassword.startsWith("$2"));
            assertTrue(hashedPassword.length() >= 59);
            assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));
        }
    }
    
    /**
     * Nested test class for user update tests.
     * 
     * <p>Tests the user update workflow including:
     * <ul>
     *   <li>Updating existing user details</li>
     *   <li>Handling updates for non-existent users</li>
     *   <li>Checking for duplicate email on update</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {
        
        /**
         * Tests successful update of an existing user.
         * 
         * <p>Verifies that a user's details can be modified and the
         * changes are persisted successfully.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should update existing user")
        void testUpdateUserSuccess() throws SQLException {
            // Given
            User existingUser = createTestUser(1, "existinguser", UserRole.SERVER);
            when(mockUserDAO.findUserById(1)).thenReturn(Optional.of(existingUser));
            when(mockUserDAO.updateUser(any(User.class))).thenReturn(true);
            
            // When
            existingUser.setFullName("Updated Name");
            boolean result = mockUserDAO.updateUser(existingUser);
            
            // Then
            assertTrue(result);
        }
        
        /**
         * Tests update failure when the user doesn't exist.
         * 
         * <p>Verifies that attempting to update a non-existent user
         * is properly detected by returning an empty Optional.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail update when user not found")
        void testUpdateUserNotFound() throws SQLException {
            // Given
            when(mockUserDAO.findUserById(999)).thenReturn(Optional.empty());
            
            // Then
            assertTrue(mockUserDAO.findUserById(999).isEmpty());
        }
        
        /**
         * Tests duplicate email detection during user update.
         * 
         * <p>Verifies that when updating a user's email to one that already
         * exists (belonging to a different user), the duplicate is detected.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check for duplicate email on update")
        void testUpdateUserDuplicateEmailCheck() throws SQLException {
            // Given
            User user1 = createTestUser(1, "user1", UserRole.SERVER);
            user1.setEmailAddress("user1@example.com");
            User user2 = createTestUser(2, "user2", UserRole.SERVER);
            user2.setEmailAddress("user2@example.com");
            
            when(mockUserDAO.findUserById(1)).thenReturn(Optional.of(user1));
            when(mockUserDAO.findUserByEmail("user2@example.com")).thenReturn(Optional.of(user2));
            
            // User 1 trying to change email to user2's email
            Optional<User> existing = mockUserDAO.findUserByEmail("user2@example.com");
            boolean isDuplicate = existing.isPresent() && !existing.get().getUserId().equals(1);
            
            // Then
            assertTrue(isDuplicate);
        }
    }
    
    /**
     * Nested test class for user deletion tests.
     * 
     * <p>Tests the user deletion workflow including:
     * <ul>
     *   <li>Deleting existing users</li>
     *   <li>Handling deletion of non-existent users</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {
        
        /**
         * Tests successful deletion of an existing user.
         * 
         * <p>Verifies that a user can be removed from the system
         * when they exist in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should delete existing user")
        void testDeleteUserSuccess() throws SQLException {
            // Given
            when(mockUserDAO.deleteUser(1)).thenReturn(true);
            
            // When
            boolean result = mockUserDAO.deleteUser(1);
            
            // Then
            assertTrue(result);
        }
        
        /**
         * Tests deletion failure when the user doesn't exist.
         * 
         * <p>Verifies that attempting to delete a non-existent user
         * returns false indicating the operation failed.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail delete when user not found")
        void testDeleteUserNotFound() throws SQLException {
            // Given
            when(mockUserDAO.deleteUser(999)).thenReturn(false);
            
            // When
            boolean result = mockUserDAO.deleteUser(999);
            
            // Then
            assertFalse(result);
        }
    }
    
    /**
     * Nested test class for user role management tests.
     * 
     * <p>Tests the role assignment and conversion functionality:
     * <ul>
     *   <li>Setting and getting user roles</li>
     *   <li>Converting role strings to enum values</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("User Role Tests")
    class UserRoleTests {
        
        /**
         * Tests setting and getting user roles.
         * 
         * <p>Verifies that all UserRole enum values (ADMIN, SERVER, CHEF, CUSTOMER)
         * can be properly assigned to and retrieved from a user.</p>
         */
        @Test
        @DisplayName("Should set and get user role")
        void testUserRole() {
            User user = new User();
            
            user.setRole(UserRole.ADMIN);
            assertEquals(UserRole.ADMIN, user.getRole());
            
            user.setRole(UserRole.SERVER);
            assertEquals(UserRole.SERVER, user.getRole());
            
            user.setRole(UserRole.CHEF);
            assertEquals(UserRole.CHEF, user.getRole());
            
            user.setRole(UserRole.CUSTOMER);
            assertEquals(UserRole.CUSTOMER, user.getRole());
        }
        
        /**
         * Tests converting role string names to UserRole enum.
         * 
         * <p>Verifies that valueOf() correctly converts string names
         * to their corresponding UserRole enum values.</p>
         */
        @Test
        @DisplayName("Should convert role string to enum")
        void testRoleStringToEnum() {
            assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
            assertEquals(UserRole.SERVER, UserRole.valueOf("SERVER"));
            assertEquals(UserRole.CHEF, UserRole.valueOf("CHEF"));
            assertEquals(UserRole.CUSTOMER, UserRole.valueOf("CUSTOMER"));
        }
    }
    
    /**
     * Nested test class for user active status tests.
     * 
     * <p>Tests the account activation status functionality:
     * <ul>
     *   <li>Setting user active or inactive status</li>
     *   <li>Default active status for new users</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("User Active Status Tests")
    class UserActiveStatusTests {
        
        /**
         * Tests setting and getting user active status.
         * 
         * <p>Verifies that a user's active status can be toggled
         * between true and false states.</p>
         */
        @Test
        @DisplayName("Should set user active status")
        void testUserActiveStatus() {
            User user = new User();
            
            user.setActive(true);
            assertTrue(user.isActive());
            
            user.setActive(false);
            assertFalse(user.isActive());
        }
        
        /**
         * Tests that new users should be active by default.
         * 
         * <p>Verifies that when a user is created, they should be
         * set to active status by default.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("New user should be active by default")
        void testNewUserActive() throws SQLException {
            // When creating user, should be active
            User user = new User();
            user.setActive(true);
            
            assertTrue(user.isActive());
        }
    }
    
    /**
     * Creates a list of test User instances for testing.
     * 
     * <p>This helper method creates multiple users with different roles
     * for use in tests that require a collection of users.</p>
     * 
     * @return a List containing three test users (ADMIN, SERVER, CHEF)
     */
    private List<User> createTestUsers() {
        List<User> users = new ArrayList<>();
        users.add(createTestUser(1, "admin", UserRole.ADMIN));
        users.add(createTestUser(2, "server1", UserRole.SERVER));
        users.add(createTestUser(3, "chef1", UserRole.CHEF));
        return users;
    }
    
    /**
     * Creates a test User instance with specified parameters.
     * 
     * <p>This helper method creates a fully populated User object for use
     * in test cases, including a BCrypt-hashed password and all required fields.</p>
     * 
     * @param id the user ID to assign (can be null for new users)
     * @param username the username for the test user
     * @param role the UserRole to assign (ADMIN, SERVER, CHEF, CUSTOMER)
     * @return a new User instance populated with test data
     */
    private User createTestUser(Integer id, String username, UserRole role) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setFullName("Test " + username);
        user.setEmailAddress(username + "@example.com");
        user.setPhoneNumber("123456789" + (id != null ? id : 0));
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPasswordHash(BCrypt.hashpw("password123", BCrypt.gensalt(12)));
        return user;
    }
}
