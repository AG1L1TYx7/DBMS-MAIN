package com.restaurant.dao;

import com.restaurant.model.RestaurantTable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Table Data Access Object Interface.
 * Provides methods for CRUD operations and availability management
 * for restaurant tables.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public interface TableDAO {

    /**
     * Creates a new table in the database.
     *
     * @param table the table object to create
     * @return the created table with generated ID
     * @throws SQLException if a database access error occurs
     */
    RestaurantTable createTable(RestaurantTable table) throws SQLException;

    /**
     * Finds a table by its unique identifier.
     *
     * @param tableId the unique identifier of the table
     * @return an Optional containing the table if found, empty otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<RestaurantTable> findTableById(Integer tableId) throws SQLException;

    /**
     * Finds a table by its table number.
     *
     * @param tableNumber the table number to search for
     * @return an Optional containing the table if found, empty otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<RestaurantTable> findTableByNumber(String tableNumber) throws SQLException;

    /**
     * Updates an existing table's information.
     *
     * @param table the table object with updated information
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean updateTable(RestaurantTable table) throws SQLException;

    /**
     * Deletes a table from the database.
     *
     * @param tableId the unique identifier of the table to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean deleteTable(Integer tableId) throws SQLException;

    /**
     * Retrieves all tables from the database.
     *
     * @return a list of all tables
     * @throws SQLException if a database access error occurs
     */
    List<RestaurantTable> getAllTables() throws SQLException;

    /**
     * Retrieves all available tables (not currently occupied).
     *
     * @return a list of available tables
     * @throws SQLException if a database access error occurs
     */
    List<RestaurantTable> getAvailableTables() throws SQLException;

    /**
     * Retrieves all occupied tables.
     *
     * @return a list of occupied tables
     * @throws SQLException if a database access error occurs
     */
    List<RestaurantTable> getOccupiedTables() throws SQLException;

    /**
     * Retrieves tables by their seating capacity.
     *
     * @param minCapacity the minimum required capacity
     * @return a list of tables with at least the specified capacity
     * @throws SQLException if a database access error occurs
     */
    List<RestaurantTable> getTablesByMinCapacity(int minCapacity) throws SQLException;

    /**
     * Retrieves available tables with a minimum seating capacity.
     *
     * @param minCapacity the minimum required capacity
     * @return a list of available tables with at least the specified capacity
     * @throws SQLException if a database access error occurs
     */
    List<RestaurantTable> getAvailableTablesByCapacity(int minCapacity) throws SQLException;

    /**
     * Retrieves tables by their location/section in the restaurant.
     *
     * @param location the location/section name
     * @return a list of tables in the specified location
     * @throws SQLException if a database access error occurs
     */
    List<RestaurantTable> getTablesByLocation(String location) throws SQLException;

    /**
     * Updates the status of a table.
     *
     * @param tableId the unique identifier of the table
     * @param status the new status for the table
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean updateTableStatus(Integer tableId, RestaurantTable.TableStatus status) throws SQLException;

    /**
     * Marks a table as occupied and assigns a server.
     *
     * @param tableId the unique identifier of the table
     * @param serverId the ID of the server assigned to the table
     * @return true if the operation was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean occupyTable(Integer tableId, Integer serverId) throws SQLException;

    /**
     * Marks a table as available (releases it).
     *
     * @param tableId the unique identifier of the table
     * @return true if the operation was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean releaseTable(Integer tableId) throws SQLException;

    /**
     * Gets the total count of tables.
     *
     * @return the total number of tables
     * @throws SQLException if a database access error occurs
     */
    int getTotalTableCount() throws SQLException;

    /**
     * Gets the count of available tables.
     *
     * @return the number of available tables
     * @throws SQLException if a database access error occurs
     */
    int getAvailableTableCount() throws SQLException;

    /**
     * Gets the count of occupied tables.
     *
     * @return the number of occupied tables
     * @throws SQLException if a database access error occurs
     */
    int getOccupiedTableCount() throws SQLException;

    /**
     * Gets the total seating capacity of all tables.
     *
     * @return the total seating capacity
     * @throws SQLException if a database access error occurs
     */
    int getTotalSeatingCapacity() throws SQLException;

    /**
     * Gets the available seating capacity (from unoccupied tables).
     *
     * @return the available seating capacity
     * @throws SQLException if a database access error occurs
     */
    int getAvailableSeatingCapacity() throws SQLException;

    /**
     * Checks if a table is available at a specific time for reservation.
     *
     * @param tableId the unique identifier of the table
     * @param dateTime the date and time to check
     * @param durationMinutes the expected duration in minutes
     * @return true if the table is available, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean isTableAvailableAt(Integer tableId, LocalDateTime dateTime, int durationMinutes) throws SQLException;

    /**
     * Gets all distinct locations/sections in the restaurant.
     *
     * @return a list of location names
     * @throws SQLException if a database access error occurs
     */
    List<String> getAllLocations() throws SQLException;

    /**
     * Checks if a table number already exists.
     *
     * @param tableNumber the table number to check
     * @return true if the table number exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean isTableNumberExists(String tableNumber) throws SQLException;
}
