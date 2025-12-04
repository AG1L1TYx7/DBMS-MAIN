package com.restaurant.controller;

import com.restaurant.dao.ReservationDAO;
import com.restaurant.dao.ReservationDAOImpl;
import com.restaurant.model.Reservation;
import com.restaurant.model.Reservation.ReservationStatus;
import com.restaurant.model.RestaurantTable;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Reservation Controller
 * Handles business logic for table reservations
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class ReservationController {
    
    private final ReservationDAO reservationDAO;
    
    public ReservationController() {
        this.reservationDAO = new ReservationDAOImpl();
    }
    
    /**
     * Create a new reservation
     */
    public Reservation createReservation(String customerName, String customerPhone, String customerEmail,
                                         Integer tableId, LocalDate date, LocalTime time, 
                                         int partySize, String specialRequests) {
        try {
            // Validate inputs
            if (customerName == null || customerName.trim().isEmpty()) {
                showError("Customer name is required");
                return null;
            }
            
            if (customerPhone == null || customerPhone.trim().isEmpty()) {
                showError("Customer phone is required");
                return null;
            }
            
            if (tableId == null) {
                showError("Please select a table");
                return null;
            }
            
            if (date == null || date.isBefore(LocalDate.now())) {
                showError("Please select a valid future date");
                return null;
            }
            
            if (time == null) {
                showError("Please select a reservation time");
                return null;
            }
            
            if (partySize <= 0) {
                showError("Party size must be at least 1");
                return null;
            }
            
            // Check if table is available
            if (!reservationDAO.isTableAvailable(tableId, date, time)) {
                showError("This table is not available at the selected date and time.\n" +
                         "Please choose another table or time.");
                return null;
            }
            
            // Create reservation
            Reservation reservation = new Reservation();
            reservation.setCustomerName(customerName.trim());
            reservation.setCustomerPhone(customerPhone.trim());
            reservation.setCustomerEmail(customerEmail != null ? customerEmail.trim() : null);
            reservation.setTableId(tableId);
            reservation.setReservationDate(date);
            reservation.setReservationTime(time);
            reservation.setNumberOfGuests(partySize);
            reservation.setSpecialRequests(specialRequests);
            reservation.setStatus(ReservationStatus.CONFIRMED);
            
            Reservation savedReservation = reservationDAO.createReservation(reservation);
            
            if (savedReservation != null) {
                JOptionPane.showMessageDialog(null,
                    "Reservation confirmed!\n\n" +
                    "Confirmation #: " + savedReservation.getReservationId() + "\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n" +
                    "Party Size: " + partySize + "\n\n" +
                    "We look forward to seeing you!",
                    "Reservation Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            return savedReservation;
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to create reservation: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get available tables for a given date, time, and party size
     */
    public List<RestaurantTable> getAvailableTables(LocalDate date, LocalTime time, int partySize) {
        try {
            return reservationDAO.getAvailableTables(date, time, partySize);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get all tables
     */
    public List<RestaurantTable> getAllTables() {
        try {
            return reservationDAO.getAllTables();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get reservations by customer phone
     */
    public List<Reservation> getReservationsByPhone(String phone) {
        try {
            return reservationDAO.getReservationsByCustomerPhone(phone);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get reservations for a specific date
     */
    public List<Reservation> getReservationsByDate(LocalDate date) {
        try {
            return reservationDAO.getReservationsByDate(date);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get today's reservations
     */
    public List<Reservation> getTodaysReservations() {
        try {
            return reservationDAO.getTodaysReservations();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get upcoming reservations
     */
    public List<Reservation> getUpcomingReservations() {
        try {
            return reservationDAO.getUpcomingReservations();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Cancel a reservation
     */
    public boolean cancelReservation(Integer reservationId) {
        try {
            return reservationDAO.cancelReservation(reservationId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to cancel reservation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Confirm a reservation
     */
    public boolean confirmReservation(Integer reservationId) {
        try {
            return reservationDAO.updateReservationStatus(reservationId, ReservationStatus.CONFIRMED);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mark reservation as completed
     */
    public boolean completeReservation(Integer reservationId) {
        try {
            return reservationDAO.updateReservationStatus(reservationId, ReservationStatus.COMPLETED);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mark reservation as no-show
     */
    public boolean markAsNoShow(Integer reservationId) {
        try {
            return reservationDAO.updateReservationStatus(reservationId, ReservationStatus.NO_SHOW);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if table is available
     */
    public boolean isTableAvailable(Integer tableId, LocalDate date, LocalTime time) {
        try {
            return reservationDAO.isTableAvailable(tableId, date, time);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message,
            "Reservation Error", JOptionPane.ERROR_MESSAGE);
    }
}
