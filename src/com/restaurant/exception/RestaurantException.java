package com.restaurant.exception;

/**
 * Base exception class for all restaurant application exceptions.
 * Provides a common ancestor for all custom exceptions in the system.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class RestaurantException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final String errorCode;

    /**
     * Constructs a new RestaurantException with the specified message.
     *
     * @param message the detail message
     */
    public RestaurantException(String message) {
        super(message);
        this.errorCode = "REST-000";
    }

    /**
     * Constructs a new RestaurantException with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public RestaurantException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new RestaurantException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public RestaurantException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "REST-000";
    }

    /**
     * Constructs a new RestaurantException with the specified message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause of this exception
     */
    public RestaurantException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", errorCode, getClass().getSimpleName(), getMessage());
    }
}
