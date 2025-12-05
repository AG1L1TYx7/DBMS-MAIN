package com.restaurant.controller;

import com.restaurant.dao.TableDAO;
import com.restaurant.dao.TableDAOImpl;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.RestaurantTable.TableStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Table Controller Class.
 * Handles business logic for restaurant table management operations.
 * Acts as an intermediary between the view layer and the data access layer.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public class TableController {

    /** Data access object for table operations. */
    private final TableDAO tableDAO;

    /**
     * Default constructor that initializes the TableDAO implementation.
     */
    public TableController() {
        this.tableDAO = new TableDAOImpl();
    }

    /**
     * Constructor with injected TableDAO for testing purposes.
     *
     * @param tableDAO the TableDAO implementation to use
     */
    public TableController(TableDAO tableDAO) {
        this.tableDAO = tableDAO;
    }

    // ==================== Table CRUD Operations ====================

    /**
     * Creates a new restaurant table.
     *
     * @param tableNumber the table number/identifier
     * @param capacity the seating capacity
     * @param location the location/section in the restaurant
     * @return the created RestaurantTable object
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public RestaurantTable createTable(String tableNumber, int capacity, String location)
            throws SQLException {
        // Validate inputs
        validateTableNumber(tableNumber);
        validateCapacity(capacity);

        // Check if table number already exists
        if (isTableNumberExists(tableNumber)) {
            throw new IllegalArgumentException("Table number '" + tableNumber + "' already exists");
        }

        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(tableNumber.trim().toUpperCase());
        table.setCapacity(capacity);
        table.setLocation(location != null ? location.trim() : null);
        table.setStatus(TableStatus.AVAILABLE);
        table.setReservable(true);

        return tableDAO.createTable(table);
    }

    /**
     * Creates a new restaurant table with additional options.
     *
     * @param tableNumber the table number/identifier
     * @param capacity the seating capacity
     * @param location the location/section in the restaurant
     * @param isReservable whether the table can be reserved
     * @param notes additional notes about the table
     * @return the created RestaurantTable object
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public RestaurantTable createTable(String tableNumber, int capacity, String location,
                                       boolean isReservable, String notes) throws SQLException {
        validateTableNumber(tableNumber);
        validateCapacity(capacity);

        if (isTableNumberExists(tableNumber)) {
            throw new IllegalArgumentException("Table number '" + tableNumber + "' already exists");
        }

        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(tableNumber.trim().toUpperCase());
        table.setCapacity(capacity);
        table.setLocation(location != null ? location.trim() : null);
        table.setStatus(TableStatus.AVAILABLE);
        table.setReservable(isReservable);
        table.setNotes(notes != null ? notes.trim() : null);

        return tableDAO.createTable(table);
    }

    /**
     * Retrieves a table by its ID.
     *
     * @param tableId the table ID
     * @return Optional containing the table if found
     * @throws SQLException if a database error occurs
     */
    public Optional<RestaurantTable> getTableById(Integer tableId) throws SQLException {
        if (tableId == null || tableId <= 0) {
            return Optional.empty();
        }
        return tableDAO.findTableById(tableId);
    }

    /**
     * Retrieves a table by its table number.
     *
     * @param tableNumber the table number
     * @return Optional containing the table if found
     * @throws SQLException if a database error occurs
     */
    public Optional<RestaurantTable> getTableByNumber(String tableNumber) throws SQLException {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return tableDAO.findTableByNumber(tableNumber.trim().toUpperCase());
    }

    /**
     * Updates an existing table's information.
     *
     * @param table the table with updated information
     * @return true if update was successful
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if a database error occurs
     */
    public boolean updateTable(RestaurantTable table) throws SQLException {
        if (table == null || table.getTableId() == null) {
            throw new IllegalArgumentException("Table and table ID are required");
        }

        validateTableNumber(table.getTableNumber());
        validateCapacity(table.getCapacity());

        // Check if table exists
        Optional<RestaurantTable> existing = tableDAO.findTableById(table.getTableId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Table not found with ID: " + table.getTableId());
        }

        // If table number changed, check for duplicates
        if (!existing.get().getTableNumber().equals(table.getTableNumber())) {
            if (isTableNumberExists(table.getTableNumber())) {
                throw new IllegalArgumentException("Table number '" + table.getTableNumber() + "' already exists");
            }
        }

        return tableDAO.updateTable(table);
    }

    /**
     * Deletes a table from the system.
     *
     * @param tableId the table ID to delete
     * @return true if deletion was successful
     * @throws IllegalArgumentException if table is occupied
     * @throws SQLException if a database error occurs
     */
    public boolean deleteTable(Integer tableId) throws SQLException {
        if (tableId == null || tableId <= 0) {
            throw new IllegalArgumentException("Invalid table ID");
        }

        Optional<RestaurantTable> table = tableDAO.findTableById(tableId);
        if (table.isEmpty()) {
            throw new IllegalArgumentException("Table not found with ID: " + tableId);
        }

        // Don't allow deletion of occupied tables
        if (table.get().getStatus() == TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Cannot delete an occupied table. Release the table first.");
        }

        return tableDAO.deleteTable(tableId);
    }

    // ==================== Table Listing Operations ====================

    /**
     * Retrieves all tables in the restaurant.
     *
     * @return list of all tables
     * @throws SQLException if a database error occurs
     */
    public List<RestaurantTable> getAllTables() throws SQLException {
        return tableDAO.getAllTables();
    }

    /**
     * Retrieves all available tables.
     *
     * @return list of available tables
     * @throws SQLException if a database error occurs
     */
    public List<RestaurantTable> getAvailableTables() throws SQLException {
        return tableDAO.getAvailableTables();
    }

    /**
     * Retrieves all occupied tables.
     *
     * @return list of occupied tables
     * @throws SQLException if a database error occurs
     */
    public List<RestaurantTable> getOccupiedTables() throws SQLException {
        return tableDAO.getOccupiedTables();
    }

    /**
     * Retrieves available tables with a minimum capacity.
     *
     * @param minCapacity the minimum required seating capacity
     * @return list of available tables meeting the capacity requirement
     * @throws IllegalArgumentException if capacity is invalid
     * @throws SQLException if a database error occurs
     */
    public List<RestaurantTable> findAvailableTablesForParty(int minCapacity) throws SQLException {
        if (minCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        return tableDAO.getAvailableTablesByCapacity(minCapacity);
    }

    /**
     * Retrieves tables by location/section.
     *
     * @param location the location to filter by
     * @return list of tables in the specified location
     * @throws SQLException if a database error occurs
     */
    public List<RestaurantTable> getTablesByLocation(String location) throws SQLException {
        if (location == null || location.trim().isEmpty()) {
            return getAllTables();
        }
        return tableDAO.getTablesByLocation(location.trim());
    }

    /**
     * Retrieves all distinct locations/sections in the restaurant.
     *
     * @return list of location names
     * @throws SQLException if a database error occurs
     */
    public List<String> getAllLocations() throws SQLException {
        return tableDAO.getAllLocations();
    }

    // ==================== Table Status Operations ====================

    /**
     * Occupies a table and optionally assigns a server.
     *
     * @param tableId the table ID to occupy
     * @param serverId the ID of the server to assign (can be null)
     * @return true if the operation was successful
     * @throws IllegalArgumentException if the table is not available
     * @throws SQLException if a database error occurs
     */
    public boolean occupyTable(Integer tableId, Integer serverId) throws SQLException {
        validateTableId(tableId);

        Optional<RestaurantTable> table = tableDAO.findTableById(tableId);
        if (table.isEmpty()) {
            throw new IllegalArgumentException("Table not found with ID: " + tableId);
        }

        if (table.get().getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalArgumentException("Table is not available. Current status: " + table.get().getStatus());
        }

        return tableDAO.occupyTable(tableId, serverId);
    }

    /**
     * Releases an occupied table (marks it as available).
     *
     * @param tableId the table ID to release
     * @return true if the operation was successful
     * @throws SQLException if a database error occurs
     */
    public boolean releaseTable(Integer tableId) throws SQLException {
        validateTableId(tableId);
        return tableDAO.releaseTable(tableId);
    }

    /**
     * Updates the status of a table.
     *
     * @param tableId the table ID
     * @param status the new status
     * @return true if the update was successful
     * @throws SQLException if a database error occurs
     */
    public boolean updateTableStatus(Integer tableId, TableStatus status) throws SQLException {
        validateTableId(tableId);
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        return tableDAO.updateTableStatus(tableId, status);
    }

    /**
     * Marks a table as reserved.
     *
     * @param tableId the table ID to reserve
     * @return true if the operation was successful
     * @throws SQLException if a database error occurs
     */
    public boolean reserveTable(Integer tableId) throws SQLException {
        validateTableId(tableId);

        Optional<RestaurantTable> table = tableDAO.findTableById(tableId);
        if (table.isEmpty()) {
            throw new IllegalArgumentException("Table not found with ID: " + tableId);
        }

        if (table.get().getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalArgumentException("Table is not available for reservation");
        }

        return tableDAO.updateTableStatus(tableId, TableStatus.RESERVED);
    }

    /**
     * Marks a table as under maintenance.
     *
     * @param tableId the table ID
     * @return true if the operation was successful
     * @throws SQLException if a database error occurs
     */
    public boolean setTableMaintenance(Integer tableId) throws SQLException {
        validateTableId(tableId);

        Optional<RestaurantTable> table = tableDAO.findTableById(tableId);
        if (table.isEmpty()) {
            throw new IllegalArgumentException("Table not found with ID: " + tableId);
        }

        if (table.get().getStatus() == TableStatus.OCCUPIED) {
            throw new IllegalArgumentException("Cannot set maintenance on an occupied table");
        }

        return tableDAO.updateTableStatus(tableId, TableStatus.MAINTENANCE);
    }

    // ==================== Availability Checking ====================

    /**
     * Checks if a table is available for a reservation at a specific time.
     *
     * @param tableId the table ID
     * @param dateTime the desired date and time
     * @param durationMinutes the expected duration in minutes
     * @return true if the table is available
     * @throws SQLException if a database error occurs
     */
    public boolean isTableAvailableForReservation(Integer tableId, LocalDateTime dateTime,
                                                  int durationMinutes) throws SQLException {
        validateTableId(tableId);
        if (dateTime == null) {
            throw new IllegalArgumentException("Date and time are required");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        // First check if table is reservable
        Optional<RestaurantTable> table = tableDAO.findTableById(tableId);
        if (table.isEmpty() || !table.get().isReservable()) {
            return false;
        }

        return tableDAO.isTableAvailableAt(tableId, dateTime, durationMinutes);
    }

    /**
     * Finds the best available table for a party size.
     * Returns the smallest available table that can accommodate the party.
     *
     * @param partySize the number of guests
     * @return Optional containing the best table if found
     * @throws SQLException if a database error occurs
     */
    public Optional<RestaurantTable> findBestTableForParty(int partySize) throws SQLException {
        if (partySize <= 0) {
            return Optional.empty();
        }

        List<RestaurantTable> availableTables = tableDAO.getAvailableTablesByCapacity(partySize);

        // Find the smallest table that fits
        return availableTables.stream()
                .filter(t -> t.getCapacity() >= partySize)
                .min((t1, t2) -> Integer.compare(t1.getCapacity(), t2.getCapacity()));
    }

    // ==================== Statistics ====================

    /**
     * Gets the total count of tables.
     *
     * @return total table count
     * @throws SQLException if a database error occurs
     */
    public int getTotalTableCount() throws SQLException {
        return tableDAO.getTotalTableCount();
    }

    /**
     * Gets the count of available tables.
     *
     * @return available table count
     * @throws SQLException if a database error occurs
     */
    public int getAvailableTableCount() throws SQLException {
        return tableDAO.getAvailableTableCount();
    }

    /**
     * Gets the count of occupied tables.
     *
     * @return occupied table count
     * @throws SQLException if a database error occurs
     */
    public int getOccupiedTableCount() throws SQLException {
        return tableDAO.getOccupiedTableCount();
    }

    /**
     * Gets the total seating capacity of all tables.
     *
     * @return total seating capacity
     * @throws SQLException if a database error occurs
     */
    public int getTotalSeatingCapacity() throws SQLException {
        return tableDAO.getTotalSeatingCapacity();
    }

    /**
     * Gets the available seating capacity.
     *
     * @return available seating capacity
     * @throws SQLException if a database error occurs
     */
    public int getAvailableSeatingCapacity() throws SQLException {
        return tableDAO.getAvailableSeatingCapacity();
    }

    /**
     * Calculates the current occupancy rate as a percentage.
     *
     * @return occupancy rate (0-100)
     * @throws SQLException if a database error occurs
     */
    public double getOccupancyRate() throws SQLException {
        int total = getTotalTableCount();
        if (total == 0) {
            return 0.0;
        }
        int occupied = getOccupiedTableCount();
        return (occupied * 100.0) / total;
    }

    /**
     * Gets a summary of table statuses.
     *
     * @return TableStatusSummary containing status counts
     * @throws SQLException if a database error occurs
     */
    public TableStatusSummary getTableStatusSummary() throws SQLException {
        TableStatusSummary summary = new TableStatusSummary();
        summary.setTotal(getTotalTableCount());
        summary.setAvailable(getAvailableTableCount());
        summary.setOccupied(getOccupiedTableCount());
        summary.setTotalCapacity(getTotalSeatingCapacity());
        summary.setAvailableCapacity(getAvailableSeatingCapacity());
        summary.setOccupancyRate(getOccupancyRate());
        return summary;
    }

    // ==================== Validation Methods ====================

    /**
     * Validates a table number.
     *
     * @param tableNumber the table number to validate
     * @throws IllegalArgumentException if invalid
     */
    private void validateTableNumber(String tableNumber) {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number is required");
        }
        if (tableNumber.length() > 10) {
            throw new IllegalArgumentException("Table number must be at most 10 characters");
        }
        if (!tableNumber.trim().matches("^[A-Za-z0-9\\-]+$")) {
            throw new IllegalArgumentException("Table number can only contain letters, numbers, and hyphens");
        }
    }

    /**
     * Validates table capacity.
     *
     * @param capacity the capacity to validate
     * @throws IllegalArgumentException if invalid
     */
    private void validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (capacity > 20) {
            throw new IllegalArgumentException("Capacity cannot exceed 20 seats");
        }
    }

    /**
     * Validates table ID.
     *
     * @param tableId the table ID to validate
     * @throws IllegalArgumentException if invalid
     */
    private void validateTableId(Integer tableId) {
        if (tableId == null || tableId <= 0) {
            throw new IllegalArgumentException("Invalid table ID");
        }
    }

    /**
     * Checks if a table number already exists.
     *
     * @param tableNumber the table number to check
     * @return true if exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isTableNumberExists(String tableNumber) throws SQLException {
        return tableDAO.isTableNumberExists(tableNumber.trim().toUpperCase());
    }

    // ==================== Inner Classes ====================

    /**
     * Summary class for table status information.
     */
    public static class TableStatusSummary {
        private int total;
        private int available;
        private int occupied;
        private int reserved;
        private int maintenance;
        private int totalCapacity;
        private int availableCapacity;
        private double occupancyRate;

        // Getters and Setters
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public int getAvailable() { return available; }
        public void setAvailable(int available) { this.available = available; }

        public int getOccupied() { return occupied; }
        public void setOccupied(int occupied) { this.occupied = occupied; }

        public int getReserved() { return reserved; }
        public void setReserved(int reserved) { this.reserved = reserved; }

        public int getMaintenance() { return maintenance; }
        public void setMaintenance(int maintenance) { this.maintenance = maintenance; }

        public int getTotalCapacity() { return totalCapacity; }
        public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

        public int getAvailableCapacity() { return availableCapacity; }
        public void setAvailableCapacity(int availableCapacity) { this.availableCapacity = availableCapacity; }

        public double getOccupancyRate() { return occupancyRate; }
        public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }

        @Override
        public String toString() {
            return String.format(
                "TableStatusSummary{total=%d, available=%d, occupied=%d, " +
                "capacity=%d/%d, occupancy=%.1f%%}",
                total, available, occupied, availableCapacity, totalCapacity, occupancyRate
            );
        }
    }
}
