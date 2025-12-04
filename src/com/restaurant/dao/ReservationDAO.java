package com.restaurant.dao;

import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Reservation Data Access Object Interface
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public interface ReservationDAO {
    
    /**
     * Create a new reservation
     */
    Reservation createReservation(Reservation reservation) throws SQLException;
    
    /**
     * Find reservation by ID
     */
    Optional<Reservation> findById(Integer reservationId) throws SQLException;
    
    /**
     * Get all reservations for a specific date
     */
    List<Reservation> getReservationsByDate(LocalDate date) throws SQLException;
    
    /**
     * Get reservations by customer phone
     */
    List<Reservation> getReservationsByCustomerPhone(String phone) throws SQLException;
    
    /**
     * Update reservation status
     */
    boolean updateReservationStatus(Integer reservationId, Reservation.ReservationStatus status) throws SQLException;
    
    /**
     * Cancel reservation
     */
    boolean cancelReservation(Integer reservationId) throws SQLException;
    
    /**
     * Get all available tables for a specific date and time
     */
    List<RestaurantTable> getAvailableTables(LocalDate date, LocalTime time, int partySize) throws SQLException;
    
    /**
     * Get all tables
     */
    List<RestaurantTable> getAllTables() throws SQLException;
    
    /**
     * Check if table is available at specific date/time
     */
    boolean isTableAvailable(Integer tableId, LocalDate date, LocalTime time) throws SQLException;
    
    /**
     * Get upcoming reservations (today and future)
     */
    List<Reservation> getUpcomingReservations() throws SQLException;
    
    /**
     * Get today's reservations
     */
    List<Reservation> getTodaysReservations() throws SQLException;
}
