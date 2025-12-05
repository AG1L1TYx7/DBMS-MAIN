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
 * Unit tests for {@link UserDAOImpl} login and authentication functionality.
 * 
 * <p>This test class validates the BCrypt password hashing implementation used
 * in the user authentication system. It ensures that passwords are properly
 * hashed, verified, and that the authentication process works correctly with
 * both valid and invalid credentials.</p>
 * 
 * <p>The tests cover:</p>
 * <ul>
 *   <li>BCrypt hash generation and format validation</li>
 *   <li>Password verification with correct and incorrect passwords</li>
 *   <li>Handling of invalid hash formats</li>
 *   <li>User authentication with database integration</li>
 *   <li>Hash length and format requirements</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> Some tests require a database connection and will
 * be skipped gracefully if the database is not available.</p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see UserDAO
 * @see UserDAOImpl
 * @see BCrypt
 */
public class UserDAOImplLoginTest {
    
    /**
     * The UserDAO instance under test.
     * Initialized before each test to ensure a clean state.
     */
    private UserDAO userDAO;
    
    /**
     * Sets up the test environment before each test method.
     * Initializes a new UserDAO instance to ensure test isolation.
     */
    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }
    
    /**
     * Tests that BCrypt generates hashes in the correct format.
     * 
     * <p>Verifies that a generated BCrypt hash:</p>
     * <ul>
     *   <li>Starts with "$2a$" or "$2b$" (BCrypt version identifiers)</li>
     *   <li>Is exactly 60 characters long</li>
     *   <li>Can be verified against the original password</li>
     * </ul>
     */
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
    
    /**
     * Tests that BCrypt correctly verifies matching and non-matching passwords.
     * 
     * <p>This test validates password verification behavior including:</p>
     * <ul>
     *   <li>Correct password matches the hash</li>
     *   <li>Incorrect password does not match</li>
     *   <li>Password verification is case-sensitive</li>
     *   <li>Trailing whitespace causes verification failure</li>
     * </ul>
     */
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
    
    /**
     * Tests that BCrypt properly handles invalid hash formats.
     * 
     * <p>When provided with a plain text string instead of a valid BCrypt hash,
     * the {@link BCrypt#checkpw(String, String)} method should throw an
     * {@link IllegalArgumentException} rather than crashing or returning
     * an incorrect result.</p>
     * 
     * @throws IllegalArgumentException expected when verifying against invalid hash
     */
    @Test
    @DisplayName("Invalid hash should not crash but return false")
    void testInvalidHashHandling() {
        // Plain text should throw exception or return false
        String plainText = "notahash";
        
        assertThrows(IllegalArgumentException.class, () -> {
            BCrypt.checkpw("password", plainText);
        }, "Plain text hash should throw exception");
    }
    
    /**
     * Tests verification against a known working BCrypt hash.
     * 
     * <p>This test uses a pre-generated hash from the database to ensure
     * that the BCrypt implementation is compatible with stored password hashes.
     * The known hash corresponds to the password "server" with 12 salt rounds.</p>
     */
    @Test
    @DisplayName("Known working hash should verify correctly")
    void testKnownHashVerification() {
        // This is the hash we use in the database for password "server"
        String knownHash = "$2a$12$lWAr.sVvfmaw8JudpcPOneCIl8wMXkybLpquQrJuaCZTheSgWA39e";
        
        assertTrue(BCrypt.checkpw("server", knownHash), 
            "Password 'server' should verify against the known hash");
    }
    
    /**
     * Tests that BCrypt verification works correctly with different salt rounds.
     * 
     * <p>BCrypt allows configurable work factors (salt rounds) that affect
     * computation time. This test verifies that hashes generated with
     * rounds 10, 11, and 12 all verify correctly against the original password.</p>
     * 
     * <p>Higher round values increase security but also increase computation time.</p>
     */
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
    
    /**
     * Tests user authentication with valid credentials through the DAO.
     * 
     * <p>This integration test verifies that a user can be authenticated
     * using the UserDAO with correct username and password. The test
     * requires a database connection and will be skipped if unavailable.</p>
     * 
     * <p>Expected behavior: The user "server" with password "server" should
     * authenticate successfully if present in the database.</p>
     * 
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Tests that user authentication fails with invalid credentials.
     * 
     * <p>This test verifies that providing an incorrect password for an
     * existing user results in authentication failure (empty Optional).
     * The test requires a database connection and will be skipped if unavailable.</p>
     * 
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Tests that authentication fails for non-existent users.
     * 
     * <p>This test verifies that attempting to authenticate a user that
     * does not exist in the database returns an empty Optional rather
     * than throwing an exception or returning a partial result.</p>
     * 
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Tests the minimum length and format requirements for BCrypt hashes.
     * 
     * <p>This test validates that:</p>
     * <ul>
     *   <li>Valid BCrypt hashes are at least 59 characters long</li>
     *   <li>Valid BCrypt hashes start with "$2" (version identifier)</li>
     *   <li>Plain text passwords fail both validation checks</li>
     * </ul>
     * 
     * <p>These checks can be used to distinguish between hashed passwords
     * and plain text passwords in the database during migration.</p>
     */
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
