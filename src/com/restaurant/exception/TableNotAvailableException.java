package com.restaurant.exception;

import java.time.LocalDateTime;

/**
 * Exception thrown when a table is not available for reservation or seating.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class TableNotAvailableException extends RestaurantException {

    private static final long serialVersionUID = 1L;
    
    private final Integer tableId;
    private final String tableNumber;
    private final LocalDateTime requestedTime;

    /**
     * Constructs a new TableNotAvailableException.
     *
     * @param tableId the ID of the table
     * @param tableNumber the table number
     */
    public TableNotAvailableException(Integer tableId, String tableNumber) {
        super(String.format("Table '%s' (ID: %d) is not available", tableNumber, tableId), "REST-409");
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.requestedTime = null;
    }

    /**
     * Constructs a new TableNotAvailableException with a requested time.
     *
     * @param tableId the ID of the table
     * @param tableNumber the table number
     * @param requestedTime the time when the table was requested
     */
    public TableNotAvailableException(Integer tableId, String tableNumber, LocalDateTime requestedTime) {
        super(String.format("Table '%s' (ID: %d) is not available at %s", 
                tableNumber, tableId, requestedTime), "REST-409");
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.requestedTime = requestedTime;
    }

    /**
     * Constructs a new TableNotAvailableException with a custom message.
     *
     * @param message custom error message
     */
    public TableNotAvailableException(String message) {
        super(message, "REST-409");
        this.tableId = null;
        this.tableNumber = null;
        this.requestedTime = null;
    }

    /**
     * Gets the table ID.
     *
     * @return the table ID
     */
    public Integer getTableId() {
        return tableId;
    }

    /**
     * Gets the table number.
     *
     * @return the table number
     */
    public String getTableNumber() {
        return tableNumber;
    }

    /**
     * Gets the requested time.
     *
     * @return the requested time, or null if not specified
     */
    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }
}
