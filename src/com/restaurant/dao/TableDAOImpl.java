package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.RestaurantTable.TableStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TableDAO interface.
 * Provides database operations for restaurant table management using JDBC.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public class TableDAOImpl implements TableDAO {

    /** Database configuration instance for connection management. */
    private final DatabaseConfiguration dbConfig;

    /**
     * Default constructor that initializes the database configuration.
     */
    public TableDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }

    /**
     * Constructor with injected database configuration for testing.
     *
     * @param dbConfig the database configuration to use
     */
    public TableDAOImpl(DatabaseConfiguration dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RestaurantTable createTable(RestaurantTable table) throws SQLException {
        String sql = """
            INSERT INTO restaurant_tables (table_number, capacity, location, status,
                is_reservable, assigned_server_id, notes, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getLocation());
            stmt.setString(4, table.getStatus() != null ? table.getStatus().name() : TableStatus.AVAILABLE.name());
            stmt.setBoolean(5, table.isReservable());
            if (table.getAssignedServerId() != null) {
                stmt.setInt(6, table.getAssignedServerId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, table.getNotes());
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            stmt.setTimestamp(9, Timestamp.valueOf(now));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating table failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    table.setTableId(generatedKeys.getInt(1));
                    table.setCreatedAt(now);
                    table.setUpdatedAt(now);
                } else {
                    throw new SQLException("Creating table failed, no ID obtained.");
                }
            }
        }
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RestaurantTable> findTableById(Integer tableId) throws SQLException {
        String sql = "SELECT * FROM restaurant_tables WHERE table_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTable(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RestaurantTable> findTableByNumber(String tableNumber) throws SQLException {
        String sql = "SELECT * FROM restaurant_tables WHERE table_number = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTable(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateTable(RestaurantTable table) throws SQLException {
        String sql = """
            UPDATE restaurant_tables SET
                table_number = ?, capacity = ?, location = ?, status = ?,
                is_reservable = ?, assigned_server_id = ?, notes = ?, updated_at = ?
            WHERE table_id = ?
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getLocation());
            stmt.setString(4, table.getStatus().name());
            stmt.setBoolean(5, table.isReservable());
            if (table.getAssignedServerId() != null) {
                stmt.setInt(6, table.getAssignedServerId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, table.getNotes());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(9, table.getTableId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteTable(Integer tableId) throws SQLException {
        String sql = "DELETE FROM restaurant_tables WHERE table_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RestaurantTable> getAllTables() throws SQLException {
        String sql = "SELECT * FROM restaurant_tables ORDER BY table_number";
        return executeTableListQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RestaurantTable> getAvailableTables() throws SQLException {
        String sql = "SELECT * FROM restaurant_tables WHERE status = 'AVAILABLE' ORDER BY table_number";
        return executeTableListQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RestaurantTable> getOccupiedTables() throws SQLException {
        String sql = "SELECT * FROM restaurant_tables WHERE status = 'OCCUPIED' ORDER BY table_number";
        return executeTableListQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RestaurantTable> getTablesByMinCapacity(int minCapacity) throws SQLException {
        String sql = "SELECT * FROM restaurant_tables WHERE capacity >= ? ORDER BY capacity, table_number";
        List<RestaurantTable> tables = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minCapacity);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tables.add(mapResultSetToTable(rs));
                }
            }
        }
        return tables;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RestaurantTable> getAvailableTablesByCapacity(int minCapacity) throws SQLException {
        String sql = """
            SELECT * FROM restaurant_tables
            WHERE status = 'AVAILABLE' AND capacity >= ?
            ORDER BY capacity, table_number
            """;
        List<RestaurantTable> tables = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minCapacity);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tables.add(mapResultSetToTable(rs));
                }
            }
        }
        return tables;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RestaurantTable> getTablesByLocation(String location) throws SQLException {
        String sql = "SELECT * FROM restaurant_tables WHERE location = ? ORDER BY table_number";
        List<RestaurantTable> tables = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tables.add(mapResultSetToTable(rs));
                }
            }
        }
        return tables;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateTableStatus(Integer tableId, TableStatus status) throws SQLException {
        String sql = "UPDATE restaurant_tables SET status = ?, updated_at = ? WHERE table_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, tableId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean occupyTable(Integer tableId, Integer serverId) throws SQLException {
        String sql = """
            UPDATE restaurant_tables SET
                status = 'OCCUPIED', assigned_server_id = ?, updated_at = ?
            WHERE table_id = ? AND status = 'AVAILABLE'
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (serverId != null) {
                stmt.setInt(1, serverId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, tableId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean releaseTable(Integer tableId) throws SQLException {
        String sql = """
            UPDATE restaurant_tables SET
                status = 'AVAILABLE', assigned_server_id = NULL, updated_at = ?
            WHERE table_id = ?
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, tableId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalTableCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM restaurant_tables";
        return executeCountQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAvailableTableCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM restaurant_tables WHERE status = 'AVAILABLE'";
        return executeCountQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOccupiedTableCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM restaurant_tables WHERE status = 'OCCUPIED'";
        return executeCountQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalSeatingCapacity() throws SQLException {
        String sql = "SELECT COALESCE(SUM(capacity), 0) FROM restaurant_tables";
        return executeCountQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAvailableSeatingCapacity() throws SQLException {
        String sql = "SELECT COALESCE(SUM(capacity), 0) FROM restaurant_tables WHERE status = 'AVAILABLE'";
        return executeCountQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTableAvailableAt(Integer tableId, LocalDateTime dateTime, int durationMinutes) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM reservations
            WHERE table_id = ? AND status = 'CONFIRMED'
            AND (
                (reservation_datetime <= ? AND DATE_ADD(reservation_datetime, INTERVAL duration_minutes MINUTE) > ?)
                OR (reservation_datetime < DATE_ADD(?, INTERVAL ? MINUTE) AND reservation_datetime >= ?)
            )
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Timestamp ts = Timestamp.valueOf(dateTime);
            stmt.setInt(1, tableId);
            stmt.setTimestamp(2, ts);
            stmt.setTimestamp(3, ts);
            stmt.setTimestamp(4, ts);
            stmt.setInt(5, durationMinutes);
            stmt.setTimestamp(6, ts);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllLocations() throws SQLException {
        String sql = "SELECT DISTINCT location FROM restaurant_tables WHERE location IS NOT NULL ORDER BY location";
        List<String> locations = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                locations.add(rs.getString("location"));
            }
        }
        return locations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTableNumberExists(String tableNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM restaurant_tables WHERE table_number = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a RestaurantTable object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return the mapped RestaurantTable object
     * @throws SQLException if a database access error occurs
     */
    private RestaurantTable mapResultSetToTable(ResultSet rs) throws SQLException {
        RestaurantTable table = new RestaurantTable();
        table.setTableId(rs.getInt("table_id"));
        table.setTableNumber(rs.getString("table_number"));
        table.setCapacity(rs.getInt("capacity"));
        table.setLocation(rs.getString("location"));

        String status = rs.getString("status");
        if (status != null) {
            table.setStatus(TableStatus.valueOf(status));
        }

        table.setReservable(rs.getBoolean("is_reservable"));

        int serverId = rs.getInt("assigned_server_id");
        if (!rs.wasNull()) {
            table.setAssignedServerId(serverId);
        }

        table.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            table.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            table.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return table;
    }

    /**
     * Executes a simple count query and returns the result.
     *
     * @param sql the SQL count query to execute
     * @return the count result
     * @throws SQLException if a database access error occurs
     */
    private int executeCountQuery(String sql) throws SQLException {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Executes a query that returns a list of tables.
     *
     * @param sql the SQL query to execute
     * @return the list of tables
     * @throws SQLException if a database access error occurs
     */
    private List<RestaurantTable> executeTableListQuery(String sql) throws SQLException {
        List<RestaurantTable> tables = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tables.add(mapResultSetToTable(rs));
            }
        }
        return tables;
    }
}
