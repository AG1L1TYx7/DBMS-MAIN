package com.restaurant.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when input validation fails.
 * Can contain multiple validation errors.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class ValidationException extends RestaurantException {

    private static final long serialVersionUID = 1L;
    
    private final List<String> validationErrors;
    private final String fieldName;

    /**
     * Constructs a new ValidationException with a single error message.
     *
     * @param message the validation error message
     */
    public ValidationException(String message) {
        super(message, "REST-400");
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
        this.fieldName = null;
    }

    /**
     * Constructs a new ValidationException for a specific field.
     *
     * @param fieldName the name of the field that failed validation
     * @param message the validation error message
     */
    public ValidationException(String fieldName, String message) {
        super(String.format("Validation failed for '%s': %s", fieldName, message), "REST-400");
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
        this.fieldName = fieldName;
    }

    /**
     * Constructs a new ValidationException with multiple errors.
     *
     * @param errors list of validation error messages
     */
    public ValidationException(List<String> errors) {
        super(String.format("Validation failed with %d error(s): %s", 
                errors.size(), String.join(", ", errors)), "REST-400");
        this.validationErrors = new ArrayList<>(errors);
        this.fieldName = null;
    }

    /**
     * Gets the list of validation errors.
     *
     * @return unmodifiable list of validation errors
     */
    public List<String> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    /**
     * Gets the field name that failed validation.
     *
     * @return the field name, or null if not specific to a field
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Checks if there are multiple validation errors.
     *
     * @return true if there are multiple errors
     */
    public boolean hasMultipleErrors() {
        return validationErrors.size() > 1;
    }

    /**
     * Gets the number of validation errors.
     *
     * @return the error count
     */
    public int getErrorCount() {
        return validationErrors.size();
    }
}
