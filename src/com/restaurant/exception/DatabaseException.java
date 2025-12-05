package com.restaurant.exception;

import java.sql.SQLException;

/**
 * Exception thrown when a database operation fails.
 * Wraps SQL exceptions with more context.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class DatabaseException extends RestaurantException {

    private static final long serialVersionUID = 1L;
    
    private final String sqlState;
    private final int errorCode;
    private final String operation;

    /**
     * Constructs a new DatabaseException from a SQLException.
     *
     * @param message the error message
     * @param cause the underlying SQLException
     */
    public DatabaseException(String message, SQLException cause) {
        super(message, "REST-500", cause);
        this.sqlState = cause.getSQLState();
        this.errorCode = cause.getErrorCode();
        this.operation = null;
    }

    /**
     * Constructs a new DatabaseException with operation context.
     *
     * @param operation the database operation that failed
     * @param cause the underlying SQLException
     * @param includeOperation flag to differentiate from simple message constructor
     */
    public DatabaseException(String operation, SQLException cause, boolean includeOperation) {
        super(String.format("Database operation '%s' failed: %s", operation, cause.getMessage()), 
                "REST-500", cause);
        this.sqlState = cause.getSQLState();
        this.errorCode = cause.getErrorCode();
        this.operation = includeOperation ? operation : null;
    }

    /**
     * Constructs a new DatabaseException with a custom message.
     *
     * @param message the error message
     */
    public DatabaseException(String message) {
        super(message, "REST-500");
        this.sqlState = null;
        this.errorCode = 0;
        this.operation = null;
    }

    /**
     * Gets the SQL state code.
     *
     * @return the SQL state, or null if not available
     */
    public String getSqlState() {
        return sqlState;
    }

    /**
     * Gets the vendor-specific error code.
     *
     * @return the error code
     */
    public int getDbErrorCode() {
        return errorCode;
    }

    /**
     * Gets the operation that failed.
     *
     * @return the operation name, or null if not specified
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Checks if this is a connection error.
     *
     * @return true if this is a connection-related error
     */
    public boolean isConnectionError() {
        return sqlState != null && (sqlState.startsWith("08") || sqlState.equals("HY000"));
    }

    /**
     * Checks if this is a constraint violation.
     *
     * @return true if this is a constraint violation
     */
    public boolean isConstraintViolation() {
        return sqlState != null && sqlState.startsWith("23");
    }

    /**
     * Checks if this is a duplicate key error.
     *
     * @return true if this is a duplicate key error
     */
    public boolean isDuplicateKeyError() {
        return errorCode == 1062 || (sqlState != null && sqlState.equals("23000"));
    }
}
