package com.restaurant.dao;

import com.restaurant.model.Customer;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Customer Data Access Object Interface.
 * Provides methods for CRUD operations and advanced queries on customer data.
 * Supports loyalty program management and customer analytics.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public interface CustomerDAO {

    /**
     * Creates a new customer in the database.
     *
     * @param customer the customer object to create
     * @return the created customer with generated ID
     * @throws SQLException if a database access error occurs
     */
    Customer createCustomer(Customer customer) throws SQLException;

    /**
     * Finds a customer by their unique identifier.
     *
     * @param customerId the unique identifier of the customer
     * @return an Optional containing the customer if found, empty otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Customer> findCustomerById(Integer customerId) throws SQLException;

    /**
     * Finds a customer by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the customer if found, empty otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Customer> findCustomerByEmail(String email) throws SQLException;

    /**
     * Finds a customer by their phone number.
     *
     * @param phone the phone number to search for
     * @return an Optional containing the customer if found, empty otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Customer> findCustomerByPhone(String phone) throws SQLException;

    /**
     * Updates an existing customer's information.
     *
     * @param customer the customer object with updated information
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean updateCustomer(Customer customer) throws SQLException;

    /**
     * Deletes a customer from the database (soft delete - marks as inactive).
     *
     * @param customerId the unique identifier of the customer to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean deleteCustomer(Integer customerId) throws SQLException;

    /**
     * Retrieves all active customers from the database.
     *
     * @return a list of all active customers
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getAllCustomers() throws SQLException;

    /**
     * Retrieves all customers including inactive ones.
     *
     * @return a list of all customers
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getAllCustomersIncludingInactive() throws SQLException;

    /**
     * Searches for customers by name (partial match, case-insensitive).
     *
     * @param nameQuery the name pattern to search for
     * @return a list of customers matching the search criteria
     * @throws SQLException if a database access error occurs
     */
    List<Customer> searchCustomersByName(String nameQuery) throws SQLException;

    /**
     * Retrieves customers by their membership tier.
     *
     * @param tier the membership tier to filter by
     * @return a list of customers with the specified tier
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getCustomersByMembershipTier(Customer.MembershipTier tier) throws SQLException;

    /**
     * Retrieves top customers by total spending.
     *
     * @param limit the maximum number of customers to return
     * @return a list of top spending customers
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getTopCustomersBySpending(int limit) throws SQLException;

    /**
     * Retrieves customers who haven't visited since a specific date.
     *
     * @param since the date to check visits against
     * @return a list of inactive customers
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getInactiveCustomersSince(LocalDate since) throws SQLException;

    /**
     * Retrieves customers with birthdays in a specific month.
     *
     * @param month the month number (1-12)
     * @return a list of customers with birthdays in that month
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getCustomersByBirthdayMonth(int month) throws SQLException;

    /**
     * Updates the loyalty points for a customer.
     *
     * @param customerId the customer's unique identifier
     * @param pointsToAdd the number of points to add (can be negative for redemption)
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean updateLoyaltyPoints(Integer customerId, int pointsToAdd) throws SQLException;

    /**
     * Updates the customer's spending and visit statistics after a transaction.
     *
     * @param customerId the customer's unique identifier
     * @param spendingAmount the amount spent in the transaction
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean recordCustomerVisit(Integer customerId, BigDecimal spendingAmount) throws SQLException;

    /**
     * Gets the total count of active customers.
     *
     * @return the number of active customers
     * @throws SQLException if a database access error occurs
     */
    int getActiveCustomerCount() throws SQLException;

    /**
     * Gets the total count of customers by membership tier.
     *
     * @param tier the membership tier to count
     * @return the number of customers in that tier
     * @throws SQLException if a database access error occurs
     */
    int getCustomerCountByTier(Customer.MembershipTier tier) throws SQLException;

    /**
     * Calculates the average spending per customer.
     *
     * @return the average spending amount
     * @throws SQLException if a database access error occurs
     */
    BigDecimal getAverageCustomerSpending() throws SQLException;

    /**
     * Retrieves customers registered within a date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of customers registered in the date range
     * @throws SQLException if a database access error occurs
     */
    List<Customer> getCustomersRegisteredBetween(LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Checks if an email address is already registered.
     *
     * @param email the email to check
     * @return true if the email is already registered, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean isEmailRegistered(String email) throws SQLException;

    /**
     * Checks if a phone number is already registered.
     *
     * @param phone the phone number to check
     * @return true if the phone is already registered, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean isPhoneRegistered(String phone) throws SQLException;
}
