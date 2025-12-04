package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.Reservation;
import com.restaurant.model.Reservation.ReservationStatus;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.RestaurantTable.TableStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reservation Data Access Object Implementation
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class ReservationDAOImpl implements ReservationDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public ReservationDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public Reservation createReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (table_id, customer_name, customer_phone, customer_email, " +
                    "party_size, reservation_date, reservation_time, status, special_requests) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reservation.getTableId());
            stmt.setString(2, reservation.getCustomerName());
            stmt.setString(3, reservation.getCustomerPhone());
            stmt.setString(4, reservation.getCustomerEmail());
            stmt.setInt(5, reservation.getNumberOfGuests());
            stmt.setDate(6, Date.valueOf(reservation.getReservationDate()));
            stmt.setTime(7, Time.valueOf(reservation.getReservationTime()));
            stmt.setString(8, reservation.getStatus().name());
            stmt.setString(9, reservation.getSpecialRequests());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setReservationId(generatedKeys.getInt(1));
                    }
                }
                
                // Update table status to RESERVED
                updateTableStatus(reservation.getTableId(), TableStatus.RESERVED);
                
                return reservation;
            }
        }
        return null;
    }
    
    @Override
    public Optional<Reservation> findById(Integer reservationId) throws SQLException {
        String sql = "SELECT r.*, t.table_number FROM reservations r " +
                    "LEFT JOIN restaurant_tables t ON r.table_id = t.table_id " +
                    "WHERE r.reservation_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Reservation> getReservationsByDate(LocalDate date) throws SQLException {
        String sql = "SELECT r.*, t.table_number FROM reservations r " +
                    "LEFT JOIN restaurant_tables t ON r.table_id = t.table_id " +
                    "WHERE r.reservation_date = ? ORDER BY r.reservation_time";
        
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> getReservationsByCustomerPhone(String phone) throws SQLException {
        String sql = "SELECT r.*, t.table_number FROM reservations r " +
                    "LEFT JOIN restaurant_tables t ON r.table_id = t.table_id " +
                    "WHERE r.customer_phone = ? ORDER BY r.reservation_date DESC, r.reservation_time DESC";
        
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        return reservations;
    }
    
    @Override
    public boolean updateReservationStatus(Integer reservationId, ReservationStatus status) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, reservationId);
            
            boolean updated = stmt.executeUpdate() > 0;
            
            // If cancelled or completed, update table status back to AVAILABLE
            if (updated && (status == ReservationStatus.CANCELLED || 
                           status == ReservationStatus.COMPLETED ||
                           status == ReservationStatus.NO_SHOW)) {
                Optional<Reservation> reservation = findById(reservationId);
                if (reservation.isPresent()) {
                    updateTableStatus(reservation.get().getTableId(), TableStatus.AVAILABLE);
                }
            }
            
            return updated;
        }
    }
    
    @Override
    public boolean cancelReservation(Integer reservationId) throws SQLException {
        return updateReservationStatus(reservationId, ReservationStatus.CANCELLED);
    }
    
    @Override
    public List<RestaurantTable> getAvailableTables(LocalDate date, LocalTime time, int partySize) throws SQLException {
        // Get tables that are not reserved at the specified date/time and have sufficient capacity
        String sql = "SELECT t.* FROM restaurant_tables t " +
                    "WHERE t.capacity >= ? " +
                    "AND t.status != 'MAINTENANCE' " +
                    "AND t.table_id NOT IN (" +
                    "    SELECT r.table_id FROM reservations r " +
                    "    WHERE r.reservation_date = ? " +
                    "    AND r.status IN ('PENDING', 'CONFIRMED') " +
                    "    AND ABS(TIMESTAMPDIFF(MINUTE, r.reservation_time, ?)) < 120" + // 2 hour window
                    ") ORDER BY t.capacity ASC";
        
        List<RestaurantTable> tables = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, partySize);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tables.add(mapResultSetToTable(rs));
                }
            }
        }
        return tables;
    }
    
    @Override
    public List<RestaurantTable> getAllTables() throws SQLException {
        String sql = "SELECT * FROM restaurant_tables ORDER BY table_number";
        
        List<RestaurantTable> tables = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tables.add(mapResultSetToTable(rs));
            }
        }
        return tables;
    }
    
    @Override
    public boolean isTableAvailable(Integer tableId, LocalDate date, LocalTime time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations " +
                    "WHERE table_id = ? " +
                    "AND reservation_date = ? " +
                    "AND status IN ('PENDING', 'CONFIRMED') " +
                    "AND ABS(TIMESTAMPDIFF(MINUTE, reservation_time, ?)) < 120";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tableId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public List<Reservation> getUpcomingReservations() throws SQLException {
        String sql = "SELECT r.*, t.table_number FROM reservations r " +
                    "LEFT JOIN restaurant_tables t ON r.table_id = t.table_id " +
                    "WHERE r.reservation_date >= CURDATE() " +
                    "AND r.status IN ('PENDING', 'CONFIRMED') " +
                    "ORDER BY r.reservation_date, r.reservation_time";
        
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> getTodaysReservations() throws SQLException {
        return getReservationsByDate(LocalDate.now());
    }
    
    private void updateTableStatus(Integer tableId, TableStatus status) throws SQLException {
        String sql = "UPDATE restaurant_tables SET status = ? WHERE table_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, tableId);
            stmt.executeUpdate();
        }
    }
    
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setTableId(rs.getInt("table_id"));
        reservation.setCustomerName(rs.getString("customer_name"));
        reservation.setCustomerPhone(rs.getString("customer_phone"));
        reservation.setCustomerEmail(rs.getString("customer_email"));
        reservation.setNumberOfGuests(rs.getInt("party_size"));
        
        Date date = rs.getDate("reservation_date");
        if (date != null) {
            reservation.setReservationDate(date.toLocalDate());
        }
        
        Time time = rs.getTime("reservation_time");
        if (time != null) {
            reservation.setReservationTime(time.toLocalTime());
        }
        
        String status = rs.getString("status");
        if (status != null) {
            reservation.setStatus(ReservationStatus.fromString(status));
        }
        
        reservation.setSpecialRequests(rs.getString("special_requests"));
        
        // Try to get table_number from join
        try {
            reservation.setTableNumber(rs.getString("table_number"));
        } catch (SQLException e) {
            // Column not in result set
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            reservation.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return reservation;
    }
    
    private RestaurantTable mapResultSetToTable(ResultSet rs) throws SQLException {
        RestaurantTable table = new RestaurantTable();
        table.setTableId(rs.getInt("table_id"));
        table.setTableNumber(rs.getString("table_number"));
        table.setCapacity(rs.getInt("capacity"));
        table.setLocation(rs.getString("location"));
        
        String status = rs.getString("status");
        if (status != null) {
            table.setStatus(TableStatus.fromString(status));
        }
        
        try {
            table.setDescription(rs.getString("notes"));
        } catch (SQLException e) {
            // Column might not exist
        }
        
        return table;
    }
}
