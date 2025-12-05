package com.restaurant.exception;

/**
 * Exception thrown when a customer cannot be found in the database.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class CustomerNotFoundException extends EntityNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CustomerNotFoundException with the customer ID.
     *
     * @param customerId the ID of the customer that was not found
     */
    public CustomerNotFoundException(Integer customerId) {
        super("Customer", customerId);
    }

    /**
     * Constructs a new CustomerNotFoundException with a custom message.
     *
     * @param customerId the ID of the customer that was not found
     * @param message custom error message
     */
    public CustomerNotFoundException(Integer customerId, String message) {
        super("Customer", customerId, message);
    }

    /**
     * Constructs a new CustomerNotFoundException for email lookup.
     *
     * @param email the email that was not found
     * @return a new CustomerNotFoundException
     */
    public static CustomerNotFoundException byEmail(String email) {
        return new CustomerNotFoundException(null, 
            String.format("Customer with email '%s' not found", email));
    }

    /**
     * Constructs a new CustomerNotFoundException for phone lookup.
     *
     * @param phone the phone number that was not found
     * @return a new CustomerNotFoundException
     */
    public static CustomerNotFoundException byPhone(String phone) {
        return new CustomerNotFoundException(null, 
            String.format("Customer with phone '%s' not found", phone));
    }
}
