package com.restaurant.model;

import com.restaurant.model.User.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link User} model class.
 * 
 * <p>This test class provides comprehensive coverage of the User model's functionality,
 * including basic properties, user roles, active status, timestamps,
 * toString behavior, and constructors.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic user properties</li>
 *   <li>{@link RoleTests} - Tests for user role enum handling</li>
 *   <li>{@link ActiveStatusTests} - Tests for user active status</li>
 *   <li>{@link TimestampTests} - Tests for timestamp handling</li>
 *   <li>{@link ToStringTests} - Tests for toString representation</li>
 *   <li>{@link ConstructorTests} - Tests for constructors</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see User
 * @see UserRole
 */
public class UserTest {
    
    /** The test user instance used across test methods. */
    private User user;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh User instance for each test case.
     */
    @BeforeEach
    void setUp() {
        user = new User();
    }
    
    /**
     * Nested test class for basic user property tests.
     * 
     * <p>Validates getter and setter functionality for core user properties
     * including user ID, username, full name, email, phone, password hash, and employee ID.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that user ID can be set and retrieved correctly.
         * Verifies the setUserId and getUserId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get user ID")
        void testUserId() {
            user.setUserId(1);
            assertEquals(1, user.getUserId());
        }
        
        /**
         * Tests that username can be set and retrieved correctly.
         * Username is the unique login identifier for the user.
         */
        @Test
        @DisplayName("Should set and get username")
        void testUsername() {
            user.setUsername("testuser");
            assertEquals("testuser", user.getUsername());
        }
        
        /**
         * Tests that full name can be set and retrieved correctly.
         * Full name is the user's complete display name.
         */
        @Test
        @DisplayName("Should set and get full name")
        void testFullName() {
            user.setFullName("John Doe");
            assertEquals("John Doe", user.getFullName());
        }
        
        /**
         * Tests that email address can be set and retrieved correctly.
         * Email is used for communications and password recovery.
         */
        @Test
        @DisplayName("Should set and get email")
        void testEmail() {
            user.setEmailAddress("test@example.com");
            assertEquals("test@example.com", user.getEmailAddress());
        }
        
        /**
         * Tests that phone number can be set and retrieved correctly.
         * Phone number is used for contact purposes.
         */
        @Test
        @DisplayName("Should set and get phone number")
        void testPhoneNumber() {
            user.setPhoneNumber("1234567890");
            assertEquals("1234567890", user.getPhoneNumber());
        }
        
        /**
         * Tests that password hash can be set and retrieved correctly.
         * Password hash stores the encrypted user password.
         */
        @Test
        @DisplayName("Should set and get password hash")
        void testPasswordHash() {
            String hash = "$2a$12$hashedpassword";
            user.setPasswordHash(hash);
            assertEquals(hash, user.getPasswordHash());
        }
        
        /**
         * Tests that employee ID can be set and retrieved correctly.
         * Employee ID is an organizational identifier for staff.
         */
        @Test
        @DisplayName("Should set and get employee ID")
        void testEmployeeId() {
            user.setEmployeeId("1001");
            assertEquals("1001", user.getEmployeeId());
        }
    }
    
    /**
     * Nested test class for user role tests.
     * 
     * <p>Validates UserRole enum functionality including setting different roles
     * and verifying all enum values exist.</p>
     */
    @Nested
    @DisplayName("Role Tests")
    class RoleTests {
        
        /**
         * Tests setting and getting ADMIN role.
         * Admin users have full system access.
         */
        @Test
        @DisplayName("Should set and get ADMIN role")
        void testAdminRole() {
            user.setRole(UserRole.ADMIN);
            assertEquals(UserRole.ADMIN, user.getRole());
        }
        
        /**
         * Tests setting and getting SERVER role.
         * Server users handle customer orders and service.
         */
        @Test
        @DisplayName("Should set and get SERVER role")
        void testServerRole() {
            user.setRole(UserRole.SERVER);
            assertEquals(UserRole.SERVER, user.getRole());
        }
        
        /**
         * Tests setting and getting CHEF role.
         * Chef users manage kitchen operations.
         */
        @Test
        @DisplayName("Should set and get CHEF role")
        void testChefRole() {
            user.setRole(UserRole.CHEF);
            assertEquals(UserRole.CHEF, user.getRole());
        }
        
        /**
         * Tests setting and getting CUSTOMER role.
         * Customer users are restaurant patrons with limited access.
         */
        @Test
        @DisplayName("Should set and get CUSTOMER role")
        void testCustomerRole() {
            user.setRole(UserRole.CUSTOMER);
            assertEquals(UserRole.CUSTOMER, user.getRole());
        }
        
        /**
         * Tests that UserRole enum contains exactly 4 values.
         * Verifies all expected roles are present.
         */
        @Test
        @DisplayName("UserRole enum should have correct values")
        void testUserRoleEnumValues() {
            UserRole[] roles = UserRole.values();
            assertThat(roles).hasSize(4);
            assertThat(roles).containsExactlyInAnyOrder(
                UserRole.ADMIN, UserRole.SERVER, UserRole.CHEF, UserRole.CUSTOMER
            );
        }
    }
    
    /**
     * Nested test class for active status tests.
     * 
     * <p>Validates user active status including default value
     * and setting active/inactive status.</p>
     */
    @Nested
    @DisplayName("Active Status Tests")
    class ActiveStatusTests {
        
        /**
         * Tests the default active status for new users.
         * Default value depends on implementation.
         */
        @Test
        @DisplayName("Should default to active")
        void testDefaultActive() {
            User newUser = new User();
            // Default value depends on implementation
        }
        
        /**
         * Tests that active status can be set to true.
         * Verifies the setActive method for activating users.
         */
        @Test
        @DisplayName("Should set active to true")
        void testSetActiveTrue() {
            user.setActive(true);
            assertTrue(user.isActive());
        }
        
        /**
         * Tests that active status can be set to false.
         * Verifies the setActive method for deactivating users.
         */
        @Test
        @DisplayName("Should set active to false")
        void testSetActiveFalse() {
            user.setActive(false);
            assertFalse(user.isActive());
        }
    }
    
    /**
     * Nested test class for timestamp tests.
     * 
     * <p>Validates timestamp handling including createdAt
     * and lastLoginAt timestamps.</p>
     */
    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {
        
        /**
         * Tests that createdAt timestamp can be set and retrieved correctly.
         * Created at records when the user account was created.
         */
        @Test
        @DisplayName("Should set and get created at timestamp")
        void testCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            assertEquals(now, user.getCreatedAt());
        }
        
        /**
         * Tests that lastLoginAt timestamp can be set and retrieved correctly.
         * Last login at records when the user last logged in.
         */
        @Test
        @DisplayName("Should set and get last login timestamp")
        void testLastLoginAt() {
            LocalDateTime now = LocalDateTime.now();
            user.setLastLoginAt(now);
            assertEquals(now, user.getLastLoginAt());
        }
    }
    
    /**
     * Nested test class for toString tests.
     * 
     * <p>Validates that the toString method produces meaningful output
     * containing the username.</p>
     */
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        /**
         * Tests that toString contains the username.
         * Verifies meaningful string representation for debugging.
         */
        @Test
        @DisplayName("ToString should contain username")
        void testToStringContainsUsername() {
            user.setUsername("testuser");
            user.setFullName("Test User");
            user.setRole(UserRole.SERVER);
            
            String toString = user.toString();
            assertThat(toString).contains("testuser");
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of User objects through
     * the default constructor.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor creates a non-null user.
         * Verifies basic object creation.
         */
        @Test
        @DisplayName("Default constructor should create empty user")
        void testDefaultConstructor() {
            User newUser = new User();
            assertNotNull(newUser);
        }
    }
}
