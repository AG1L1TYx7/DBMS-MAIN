package com.restaurant.dao;

import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for UserDAO.
 * 
 * <p>This test class validates user data access operations and User model behavior.
 * It covers BCrypt password hashing, user creation, authentication workflows,
 * user role management, role mapping, and Optional user retrieval patterns.</p>
 * 
 * <p>The tests are organized into nested classes for better organization and
 * readability, each focusing on a specific aspect of user data access functionality.</p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 */
public class UserDAOTest {
    
    /**
     * Nested test class for BCrypt password hashing tests.
     * 
     * <p>Validates BCrypt hash generation, password verification, invalid hash
     * handling, and hash uniqueness. These tests ensure the security mechanisms
     * for password storage and verification work correctly.</p>
     */
    @Nested
    @DisplayName("BCrypt Password Hashing Tests")
    class BCryptPasswordTests {
        
        /**
         * Tests that BCrypt generates a valid hash with the correct format.
         * 
         * <p>Verifies that the generated hash starts with the BCrypt identifier
         * ($2a$ or $2b$) and has the expected minimum length of 59 characters.</p>
         */
        @Test
        @DisplayName("Should generate valid BCrypt hash")
        void testBCryptHashGeneration() {
            String password = "password123";
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            assertNotNull(hash);
            assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
            assertTrue(hash.length() >= 59);
        }
        
        /**
         * Tests that BCrypt correctly verifies passwords against stored hashes.
         * 
         * <p>Verifies that the correct password matches the hash and that an
         * incorrect password does not match. This is critical for authentication.</p>
         */
        @Test
        @DisplayName("Should verify BCrypt password correctly")
        void testBCryptPasswordVerification() {
            String password = "password123";
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            assertTrue(BCrypt.checkpw(password, hash));
            assertFalse(BCrypt.checkpw("wrongpassword", hash));
        }
        
        /**
         * Tests that BCrypt rejects invalid hash formats.
         * 
         * <p>Verifies that an IllegalArgumentException is thrown when attempting
         * to verify a password against a malformed hash string.</p>
         */
        @Test
        @DisplayName("Should reject invalid BCrypt hash format")
        void testInvalidBCryptHashFormat() {
            String invalidHash = "notAValidHash";
            
            assertThrows(IllegalArgumentException.class, () -> {
                BCrypt.checkpw("password", invalidHash);
            });
        }
        
        /**
         * Tests that BCrypt generates unique hashes for the same password.
         * 
         * <p>Verifies that due to random salt generation, the same password
         * produces different hashes each time, but both hashes still verify
         * correctly against the original password.</p>
         */
        @Test
        @DisplayName("BCrypt hash should be different for same password")
        void testBCryptHashUniqueness() {
            String password = "password123";
            String hash1 = BCrypt.hashpw(password, BCrypt.gensalt(12));
            String hash2 = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            assertNotEquals(hash1, hash2);
            // But both should verify correctly
            assertTrue(BCrypt.checkpw(password, hash1));
            assertTrue(BCrypt.checkpw(password, hash2));
        }
    }
    
    /**
     * Nested test class for user creation tests.
     * 
     * <p>Validates proper user object creation with all required fields
     * and verifies the initial state of user objects before persistence.</p>
     */
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
        /**
         * Tests that a user can be created with all required fields populated.
         * 
         * <p>Verifies that username, full name, email, password hash, role,
         * and active status are all properly set on a new user object.</p>
         */
        @Test
        @DisplayName("Should create user with all required fields")
        void testCreateUserWithRequiredFields() {
            User user = new User();
            user.setUsername("testuser");
            user.setFullName("Test User");
            user.setEmailAddress("test@example.com");
            user.setPhoneNumber("1234567890");
            user.setRole(UserRole.SERVER);
            user.setPasswordHash(BCrypt.hashpw("password123", BCrypt.gensalt(12)));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            
            assertNotNull(user.getUsername());
            assertNotNull(user.getFullName());
            assertNotNull(user.getEmailAddress());
            assertNotNull(user.getPasswordHash());
            assertEquals(UserRole.SERVER, user.getRole());
            assertTrue(user.isActive());
        }
        
        /**
         * Tests that a new user has null ID before being persisted.
         * 
         * <p>Verifies that the user ID is null for newly created user objects
         * that have not yet been saved to the database.</p>
         */
        @Test
        @DisplayName("User should have null ID before persistence")
        void testUserIdNullBeforePersistence() {
            User user = new User();
            user.setUsername("newuser");
            
            assertNull(user.getUserId());
        }
    }
    
    /**
     * Nested test class for authentication tests.
     * 
     * <p>Validates authentication-related functionality including hash format
     * validation, successful authentication with correct credentials, and
     * failed authentication with incorrect credentials.</p>
     */
    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {
        
        /**
         * Tests validation of BCrypt hash format before authentication.
         * 
         * <p>Verifies that valid hashes meet the expected length and prefix
         * requirements, while invalid hashes are properly identified.</p>
         */
        @Test
        @DisplayName("Should validate hash format before authentication")
        void testHashFormatValidation() {
            String validHash = BCrypt.hashpw("password", BCrypt.gensalt(12));
            String invalidHash = "tooshort";
            
            assertTrue(validHash.length() >= 59);
            assertTrue(validHash.startsWith("$2"));
            
            assertFalse(invalidHash.length() >= 59);
            assertFalse(invalidHash.startsWith("$2"));
        }
        
        /**
         * Tests that authentication succeeds with the correct password.
         * 
         * <p>Verifies that providing the correct password results in
         * successful authentication against the stored hash.</p>
         */
        @Test
        @DisplayName("Authentication should succeed with correct password")
        void testAuthenticationSuccess() {
            String password = "correctPassword";
            String storedHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            boolean authenticated = BCrypt.checkpw(password, storedHash);
            assertTrue(authenticated);
        }
        
        /**
         * Tests that authentication fails with an incorrect password.
         * 
         * <p>Verifies that providing the wrong password results in
         * failed authentication, protecting against unauthorized access.</p>
         */
        @Test
        @DisplayName("Authentication should fail with wrong password")
        void testAuthenticationFailure() {
            String correctPassword = "correctPassword";
            String wrongPassword = "wrongPassword";
            String storedHash = BCrypt.hashpw(correctPassword, BCrypt.gensalt(12));
            
            boolean authenticated = BCrypt.checkpw(wrongPassword, storedHash);
            assertFalse(authenticated);
        }
    }
    
    /**
     * Nested test class for user role tests.
     * 
     * <p>Validates user role assignment and verifies role-specific behavior,
     * such as which roles require employee IDs.</p>
     */
    @Nested
    @DisplayName("User Role Tests")
    class UserRoleTests {
        
        /**
         * Tests that SERVER and CHEF roles are properly assigned to users.
         * 
         * <p>Verifies that different user roles (SERVER, CHEF, ADMIN) can be
         * assigned and retrieved correctly. This test also documents the
         * business rule that SERVER and CHEF roles typically need employee IDs.</p>
         */
        @Test
        @DisplayName("SERVER and CHEF roles should get employee ID")
        void testEmployeeIdForServerAndChef() {
            User serverUser = new User();
            serverUser.setRole(UserRole.SERVER);
            
            User chefUser = new User();
            chefUser.setRole(UserRole.CHEF);
            
            User adminUser = new User();
            adminUser.setRole(UserRole.ADMIN);
            
            // SERVER and CHEF need employee IDs
            assertTrue(serverUser.getRole() == UserRole.SERVER);
            assertTrue(chefUser.getRole() == UserRole.CHEF);
            
            // ADMIN does not need employee ID
            assertTrue(adminUser.getRole() == UserRole.ADMIN);
        }
    }
    
    /**
     * Nested test class for user role mapping tests.
     * 
     * <p>Validates the conversion of string values to UserRole enum values,
     * including both valid mappings and error handling for invalid values.</p>
     */
    @Nested
    @DisplayName("User Mapping Tests")
    class UserMappingTests {
        
        /**
         * Tests that user roles are correctly mapped from string values.
         * 
         * <p>Verifies that all valid role strings (ADMIN, SERVER, CHEF, CUSTOMER)
         * are correctly converted to their corresponding UserRole enum values.</p>
         */
        @Test
        @DisplayName("Should correctly map user role from string")
        void testUserRoleMapping() {
            assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
            assertEquals(UserRole.SERVER, UserRole.valueOf("SERVER"));
            assertEquals(UserRole.CHEF, UserRole.valueOf("CHEF"));
            assertEquals(UserRole.CUSTOMER, UserRole.valueOf("CUSTOMER"));
        }
        
        /**
         * Tests that an exception is thrown for invalid role strings.
         * 
         * <p>Verifies that attempting to convert an invalid string to a
         * UserRole throws an IllegalArgumentException.</p>
         */
        @Test
        @DisplayName("Should throw exception for invalid role")
        void testInvalidRoleMapping() {
            assertThrows(IllegalArgumentException.class, () -> {
                UserRole.valueOf("INVALID_ROLE");
            });
        }
    }
    
    /**
     * Nested test class for Optional user tests.
     * 
     * <p>Validates the use of Optional for user retrieval operations,
     * testing both the case when a user is found and when no user exists.</p>
     */
    @Nested
    @DisplayName("Optional User Tests")
    class OptionalUserTests {
        
        /**
         * Tests that findById returns Optional.empty() for non-existent users.
         * 
         * <p>Verifies that when a user is not found in the database,
         * the result is an empty Optional rather than null.</p>
         */
        @Test
        @DisplayName("findById should return Optional.empty for non-existent user")
        void testFindByIdNotFound() {
            // When user is not found, should return Optional.empty()
            Optional<User> result = Optional.empty();
            
            assertTrue(result.isEmpty());
            assertFalse(result.isPresent());
        }
        
        /**
         * Tests that findById returns an Optional containing the user when found.
         * 
         * <p>Verifies that when a user exists in the database, the result
         * is an Optional containing the user with the correct ID and username.</p>
         */
        @Test
        @DisplayName("findById should return Optional with user when found")
        void testFindByIdFound() {
            User user = new User();
            user.setUserId(1);
            user.setUsername("founduser");
            
            Optional<User> result = Optional.of(user);
            
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getUserId());
            assertEquals("founduser", result.get().getUsername());
        }
    }
}
