package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.model.Customer.MembershipTier;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customer Controller
 * Handles customer management and loyalty program operations
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class CustomerController {
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/restaurant_db", 
            "root", 
            ""
        );
    }
    
    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY full_name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerById(Integer customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find customer by phone
     */
    public Customer findCustomerByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Create new customer
     */
    public boolean createCustomer(String fullName, String email, String phone, 
                                  LocalDate dateOfBirth, String address, String notes) {
        // Validation
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Check for duplicate phone
        if (findCustomerByPhone(phone) != null) {
            return false;
        }
        
        String sql = "INSERT INTO customers (full_name, email, phone, date_of_birth, address, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fullName.trim());
            pstmt.setString(2, email != null ? email.trim() : null);
            pstmt.setString(3, phone.trim());
            pstmt.setDate(4, dateOfBirth != null ? Date.valueOf(dateOfBirth) : null);
            pstmt.setString(5, address);
            pstmt.setString(6, notes);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update customer
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET full_name = ?, email = ?, phone = ?, " +
                    "date_of_birth = ?, address = ?, notes = ?, is_active = ? " +
                    "WHERE customer_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setDate(4, customer.getDateOfBirth() != null ? 
                Date.valueOf(customer.getDateOfBirth()) : null);
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getNotes());
            pstmt.setBoolean(7, customer.isActive());
            pstmt.setInt(8, customer.getCustomerId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete customer
     */
    public boolean deleteCustomer(Integer customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Add loyalty points after purchase
     */
    public boolean addLoyaltyPoints(Integer customerId, Integer billId, BigDecimal billAmount) {
        String sql = "{CALL sp_add_loyalty_points(?, ?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, billId);
            cstmt.setBigDecimal(3, billAmount);
            
            cstmt.execute();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Redeem loyalty points for discount
     */
    public BigDecimal redeemLoyaltyPoints(Integer customerId, Integer points) {
        String sql = "{CALL sp_redeem_loyalty_points(?, ?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, points);
            cstmt.registerOutParameter(3, Types.DECIMAL);
            
            cstmt.execute();
            return cstmt.getBigDecimal(3);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get customer statistics
     */
    public Map<String, Integer> getCustomerStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active, " +
                    "SUM(CASE WHEN membership_tier = 'BRONZE' THEN 1 ELSE 0 END) as bronze, " +
                    "SUM(CASE WHEN membership_tier = 'SILVER' THEN 1 ELSE 0 END) as silver, " +
                    "SUM(CASE WHEN membership_tier = 'GOLD' THEN 1 ELSE 0 END) as gold, " +
                    "SUM(CASE WHEN membership_tier = 'PLATINUM' THEN 1 ELSE 0 END) as platinum " +
                    "FROM customers";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("active", rs.getInt("active"));
                stats.put("bronze", rs.getInt("bronze"));
                stats.put("silver", rs.getInt("silver"));
                stats.put("gold", rs.getInt("gold"));
                stats.put("platinum", rs.getInt("platinum"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Get top customers
     */
    public List<Customer> getTopCustomers(int limit) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE is_active = TRUE " +
                    "ORDER BY total_spent DESC LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Search customers
     */
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE " +
                    "full_name LIKE ? OR phone LIKE ? OR email LIKE ? " +
                    "ORDER BY full_name";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Map ResultSet to Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(dob.toLocalDate());
        }
        
        customer.setAddress(rs.getString("address"));
        customer.setMembershipTier(MembershipTier.fromString(rs.getString("membership_tier")));
        customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
        customer.setTotalSpent(rs.getBigDecimal("total_spent"));
        customer.setVisitCount(rs.getInt("visit_count"));
        
        Date lastVisit = rs.getDate("last_visit_date");
        if (lastVisit != null) {
            customer.setLastVisitDate(lastVisit.toLocalDate());
        }
        
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            customer.setRegistrationDate(regDate.toLocalDateTime());
        }
        
        customer.setActive(rs.getBoolean("is_active"));
        customer.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            customer.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            customer.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return customer;
    }
}
