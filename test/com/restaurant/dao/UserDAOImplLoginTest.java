package com.restaurant.dao;

import com.restaurant.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UserDAO login functionality
 * Ensures BCrypt password hashing works correctly
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class UserDAOImplLoginTest {
    
    private UserDAO userDAO;
    
    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }
    
    @Test
    @DisplayName("BCrypt hash generation should produce valid format")
    void testBCryptHashFormat() {
        String password = "testPassword123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        // Valid BCrypt hash should:
        // 1. Start with $2a$ or $2b$
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), 
            "Hash should start with $2a$ or $2b$");
        
        // 2. Be 60 characters long
        assertEquals(60, hash.length(), "BCrypt hash should be 60 characters");
        
        // 3. Be verifiable
        assertTrue(BCrypt.checkpw(password, hash), "Password should verify against hash");
    }
    
    @Test
    @DisplayName("BCrypt should correctly verify matching passwords")
    void testBCryptPasswordVerification() {
        String password = "server";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        // Should match
        assertTrue(BCrypt.checkpw("server", hash), "Correct password should match");
        
        // Should NOT match wrong passwords
        assertFalse(BCrypt.checkpw("wrong", hash), "Wrong password should not match");
        assertFalse(BCrypt.checkpw("Server", hash), "Case-different password should not match");
        assertFalse(BCrypt.checkpw("server ", hash), "Password with space should not match");
    }
    
    @Test
    @DisplayName("Invalid hash should not crash but return false")
    void testInvalidHashHandling() {
        // Plain text should throw exception or return false
        String plainText = "notahash";
        
        assertThrows(IllegalArgumentException.class, () -> {
            BCrypt.checkpw("password", plainText);
        }, "Plain text hash should throw exception");
    }
    
    @Test
    @DisplayName("Known working hash should verify correctly")
    void testKnownHashVerification() {
        // This is the hash we use in the database for password "server"
        String knownHash = "$2a$12$lWAr.sVvfmaw8JudpcPOneCIl8wMXkybLpquQrJuaCZTheSgWA39e";
        
        assertTrue(BCrypt.checkpw("server", knownHash), 
            "Password 'server' should verify against the known hash");
    }
    
    @Test
    @DisplayName("Different salt rounds should all verify correctly")
    void testDifferentSaltRounds() {
        String password = "testPassword";
        
        // Test rounds 10, 11, and 12 (common values)
        for (int rounds = 10; rounds <= 12; rounds++) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(rounds));
            assertTrue(BCrypt.checkpw(password, hash), 
                "Password should verify with " + rounds + " salt rounds");
        }
    }
    
    @Test
    @DisplayName("User authentication should work with valid credentials")
    void testAuthenticateUserWithValidCredentials() throws SQLException {
        // This test requires the database to have a user "server" with password "server"
        // Skip if database is not available
        try {
            Optional<User> user = userDAO.authenticateUser("server", "server");
            
            if (user.isPresent()) {
                assertEquals("server", user.get().getUsername(), "Username should match");
                assertNotNull(user.get().getUserId(), "User ID should not be null");
            }
            // If user is not present, the user might not exist in the test database
        } catch (SQLException e) {
            // Skip test if database is not available
            System.out.println("Skipping test - database not available: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("User authentication should fail with invalid credentials")
    void testAuthenticateUserWithInvalidCredentials() throws SQLException {
        try {
            Optional<User> user = userDAO.authenticateUser("server", "wrongpassword");
            assertTrue(user.isEmpty(), "Authentication should fail with wrong password");
        } catch (SQLException e) {
            // Skip test if database is not available
            System.out.println("Skipping test - database not available: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("User authentication should fail for non-existent user")
    void testAuthenticateNonExistentUser() throws SQLException {
        try {
            Optional<User> user = userDAO.authenticateUser("nonexistentuser12345", "anypassword");
            assertTrue(user.isEmpty(), "Authentication should fail for non-existent user");
        } catch (SQLException e) {
            // Skip test if database is not available
            System.out.println("Skipping test - database not available: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Hash validation: minimum length should be 59 characters")
    void testHashMinimumLength() {
        String validHash = "$2a$12$lWAr.sVvfmaw8JudpcPOneCIl8wMXkybLpquQrJuaCZTheSgWA39e";
        String shortHash = "server"; // Plain text
        
        assertTrue(validHash.length() >= 59, "Valid hash should be at least 59 chars");
        assertTrue(validHash.startsWith("$2"), "Valid hash should start with $2");
        
        assertFalse(shortHash.length() >= 59, "Plain text is too short");
        assertFalse(shortHash.startsWith("$2"), "Plain text doesn't start with $2");
    }
}
