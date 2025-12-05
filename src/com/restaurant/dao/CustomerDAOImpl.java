package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.Customer;
import com.restaurant.model.Customer.MembershipTier;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CustomerDAO interface.
 * Provides database operations for customer management using JDBC.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public class CustomerDAOImpl implements CustomerDAO {

    /** Database configuration instance for connection management. */
    private final DatabaseConfiguration dbConfig;

    /**
     * Default constructor that initializes the database configuration.
     */
    public CustomerDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }

    /**
     * Constructor with injected database configuration for testing.
     *
     * @param dbConfig the database configuration to use
     */
    public CustomerDAOImpl(DatabaseConfiguration dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer createCustomer(Customer customer) throws SQLException {
        String sql = """
            INSERT INTO customers (full_name, email, phone, date_of_birth, address,
                membership_tier, loyalty_points, total_spent, visit_count, last_visit_date,
                registration_date, is_active, notes, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setDate(4, customer.getDateOfBirth() != null
                    ? Date.valueOf(customer.getDateOfBirth()) : null);
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getMembershipTier() != null
                    ? customer.getMembershipTier().name() : MembershipTier.BRONZE.name());
            stmt.setInt(7, customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0);
            stmt.setBigDecimal(8, customer.getTotalSpent() != null
                    ? customer.getTotalSpent() : BigDecimal.ZERO);
            stmt.setInt(9, customer.getVisitCount() != null ? customer.getVisitCount() : 0);
            stmt.setDate(10, customer.getLastVisitDate() != null
                    ? Date.valueOf(customer.getLastVisitDate()) : null);
            stmt.setTimestamp(11, Timestamp.valueOf(now));
            stmt.setBoolean(12, true);
            stmt.setString(13, customer.getNotes());
            stmt.setTimestamp(14, Timestamp.valueOf(now));
            stmt.setTimestamp(15, Timestamp.valueOf(now));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                    customer.setRegistrationDate(now);
                    customer.setCreatedAt(now);
                    customer.setUpdatedAt(now);
                    customer.setActive(true);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findCustomerById(Integer customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findCustomerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ? AND is_active = true";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM customers WHERE phone = ? AND is_active = true";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = """
            UPDATE customers SET
                full_name = ?, email = ?, phone = ?, date_of_birth = ?, address = ?,
                membership_tier = ?, loyalty_points = ?, total_spent = ?, visit_count = ?,
                last_visit_date = ?, notes = ?, updated_at = ?
            WHERE customer_id = ?
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setDate(4, customer.getDateOfBirth() != null
                    ? Date.valueOf(customer.getDateOfBirth()) : null);
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getMembershipTier().name());
            stmt.setInt(7, customer.getLoyaltyPoints());
            stmt.setBigDecimal(8, customer.getTotalSpent());
            stmt.setInt(9, customer.getVisitCount());
            stmt.setDate(10, customer.getLastVisitDate() != null
                    ? Date.valueOf(customer.getLastVisitDate()) : null);
            stmt.setString(11, customer.getNotes());
            stmt.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(13, customer.getCustomerId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteCustomer(Integer customerId) throws SQLException {
        String sql = "UPDATE customers SET is_active = false, updated_at = ? WHERE customer_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customers WHERE is_active = true ORDER BY full_name";
        return executeCustomerListQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getAllCustomersIncludingInactive() throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY full_name";
        return executeCustomerListQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> searchCustomersByName(String nameQuery) throws SQLException {
        String sql = "SELECT * FROM customers WHERE is_active = true AND LOWER(full_name) LIKE ? ORDER BY full_name";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nameQuery.toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getCustomersByMembershipTier(MembershipTier tier) throws SQLException {
        String sql = "SELECT * FROM customers WHERE is_active = true AND membership_tier = ? ORDER BY full_name";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tier.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getTopCustomersBySpending(int limit) throws SQLException {
        String sql = "SELECT * FROM customers WHERE is_active = true ORDER BY total_spent DESC LIMIT ?";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getInactiveCustomersSince(LocalDate since) throws SQLException {
        String sql = """
            SELECT * FROM customers
            WHERE is_active = true
            AND (last_visit_date IS NULL OR last_visit_date < ?)
            ORDER BY last_visit_date ASC
            """;
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(since));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getCustomersByBirthdayMonth(int month) throws SQLException {
        String sql = """
            SELECT * FROM customers
            WHERE is_active = true AND MONTH(date_of_birth) = ?
            ORDER BY DAY(date_of_birth)
            """;
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, month);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLoyaltyPoints(Integer customerId, int pointsToAdd) throws SQLException {
        String sql = "UPDATE customers SET loyalty_points = loyalty_points + ?, updated_at = ? WHERE customer_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pointsToAdd);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, customerId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean recordCustomerVisit(Integer customerId, BigDecimal spendingAmount) throws SQLException {
        String sql = """
            UPDATE customers SET
                total_spent = total_spent + ?,
                visit_count = visit_count + 1,
                last_visit_date = ?,
                loyalty_points = loyalty_points + ?,
                updated_at = ?
            WHERE customer_id = ?
            """;

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Calculate loyalty points (1 point per Rs. 100 spent)
            int pointsEarned = spendingAmount.divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.DOWN).intValue();

            stmt.setBigDecimal(1, spendingAmount);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setInt(3, pointsEarned);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(5, customerId);

            boolean updated = stmt.executeUpdate() > 0;

            if (updated) {
                updateMembershipTierBasedOnSpending(customerId);
            }

            return updated;
        }
    }

    /**
     * Updates the membership tier based on total spending.
     *
     * @param customerId the customer ID to update
     * @throws SQLException if a database access error occurs
     */
    private void updateMembershipTierBasedOnSpending(Integer customerId) throws SQLException {
        String selectSql = "SELECT total_spent FROM customers WHERE customer_id = ?";
        String updateSql = "UPDATE customers SET membership_tier = ?, updated_at = ? WHERE customer_id = ?";

        try (Connection conn = dbConfig.getConnection()) {
            BigDecimal totalSpent;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, customerId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        totalSpent = rs.getBigDecimal("total_spent");
                    } else {
                        return;
                    }
                }
            }

            MembershipTier newTier = MembershipTier.fromSpending(totalSpent);

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, newTier.name());
                updateStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                updateStmt.setInt(3, customerId);
                updateStmt.executeUpdate();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActiveCustomerCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE is_active = true";
        return executeCountQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCustomerCountByTier(MembershipTier tier) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE is_active = true AND membership_tier = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tier.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getAverageCustomerSpending() throws SQLException {
        String sql = "SELECT AVG(total_spent) FROM customers WHERE is_active = true";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal avg = rs.getBigDecimal(1);
                return avg != null ? avg : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getCustomersRegisteredBetween(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT * FROM customers
            WHERE DATE(registration_date) BETWEEN ? AND ?
            ORDER BY registration_date DESC
            """;
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmailRegistered(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPhoneRegistered(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE phone = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Customer object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return the mapped Customer object
     * @throws SQLException if a database access error occurs
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));

        Date dateOfBirth = rs.getDate("date_of_birth");
        if (dateOfBirth != null) {
            customer.setDateOfBirth(dateOfBirth.toLocalDate());
        }

        customer.setAddress(rs.getString("address"));
        customer.setMembershipTier(MembershipTier.fromString(rs.getString("membership_tier")));
        customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
        customer.setTotalSpent(rs.getBigDecimal("total_spent"));
        customer.setVisitCount(rs.getInt("visit_count"));

        Date lastVisitDate = rs.getDate("last_visit_date");
        if (lastVisitDate != null) {
            customer.setLastVisitDate(lastVisitDate.toLocalDate());
        }

        Timestamp registrationDate = rs.getTimestamp("registration_date");
        if (registrationDate != null) {
            customer.setRegistrationDate(registrationDate.toLocalDateTime());
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
     * Executes a query that returns a list of customers.
     *
     * @param sql the SQL query to execute
     * @return the list of customers
     * @throws SQLException if a database access error occurs
     */
    private List<Customer> executeCustomerListQuery(String sql) throws SQLException {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }
}
