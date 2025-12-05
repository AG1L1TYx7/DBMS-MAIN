package com.restaurant.exception;

/**
 * Exception thrown when authentication fails.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class AuthenticationException extends RestaurantException {

    private static final long serialVersionUID = 1L;
    
    private final String username;
    private final AuthFailureReason reason;

    /**
     * Reasons for authentication failure.
     */
    public enum AuthFailureReason {
        INVALID_CREDENTIALS("Invalid username or password"),
        USER_NOT_FOUND("User not found"),
        ACCOUNT_LOCKED("Account is locked"),
        ACCOUNT_DISABLED("Account is disabled"),
        PASSWORD_EXPIRED("Password has expired"),
        SESSION_EXPIRED("Session has expired"),
        INSUFFICIENT_PERMISSIONS("Insufficient permissions");

        private final String description;

        AuthFailureReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Constructs a new AuthenticationException.
     *
     * @param reason the reason for authentication failure
     */
    public AuthenticationException(AuthFailureReason reason) {
        super(reason.getDescription(), "REST-401");
        this.username = null;
        this.reason = reason;
    }

    /**
     * Constructs a new AuthenticationException with username.
     *
     * @param username the username that failed authentication
     * @param reason the reason for authentication failure
     */
    public AuthenticationException(String username, AuthFailureReason reason) {
        super(String.format("Authentication failed for user '%s': %s", username, reason.getDescription()), 
                "REST-401");
        this.username = username;
        this.reason = reason;
    }

    /**
     * Constructs a new AuthenticationException with a custom message.
     *
     * @param message custom error message
     */
    public AuthenticationException(String message) {
        super(message, "REST-401");
        this.username = null;
        this.reason = AuthFailureReason.INVALID_CREDENTIALS;
    }

    /**
     * Gets the username that failed authentication.
     *
     * @return the username, or null if not specified
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the reason for authentication failure.
     *
     * @return the failure reason
     */
    public AuthFailureReason getReason() {
        return reason;
    }
}
