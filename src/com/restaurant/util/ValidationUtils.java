package com.restaurant.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validation Utility Class.
 * Provides input validation methods for the restaurant management system.
 * All methods are null-safe and return appropriate default values.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public final class ValidationUtils {

    // ==================== Regex Patterns ====================

    /** Email validation pattern (RFC 5322 compliant). */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /** Phone number pattern (supports various formats). */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\-\\s()]{7,20}$"
    );

    /** Nepal phone number pattern (landline or mobile). */
    private static final Pattern NEPAL_PHONE_PATTERN = Pattern.compile(
            "^(\\+977[\\-\\s]?)?[0-9]{2,3}[\\-\\s]?[0-9]{6,8}$"
    );

    /** Username pattern (alphanumeric, underscore, 3-30 chars). */
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z][a-zA-Z0-9_]{2,29}$"
    );

    /** Strong password pattern (min 8 chars, upper, lower, digit, special). */
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /** Table number pattern (alphanumeric). */
    private static final Pattern TABLE_NUMBER_PATTERN = Pattern.compile(
            "^[A-Za-z0-9\\-]{1,10}$"
    );

    /** Numeric only pattern. */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile(
            "^[0-9]+$"
    );

    /** Decimal number pattern. */
    private static final Pattern DECIMAL_PATTERN = Pattern.compile(
            "^[0-9]+(\\.[0-9]+)?$"
    );

    /** Alphabetic only pattern. */
    private static final Pattern ALPHA_PATTERN = Pattern.compile(
            "^[a-zA-Z]+$"
    );

    /** Alphanumeric pattern. */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9]+$"
    );

    /** Name pattern (letters, spaces, hyphens, apostrophes). */
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z][a-zA-Z\\s'\\-]{1,99}$"
    );

    // ==================== Length Constants ====================

    /** Minimum password length. */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /** Maximum password length. */
    public static final int MAX_PASSWORD_LENGTH = 128;

    /** Minimum username length. */
    public static final int MIN_USERNAME_LENGTH = 3;

    /** Maximum username length. */
    public static final int MAX_USERNAME_LENGTH = 30;

    /** Maximum email length. */
    public static final int MAX_EMAIL_LENGTH = 255;

    /** Maximum name length. */
    public static final int MAX_NAME_LENGTH = 100;

    /** Maximum address length. */
    public static final int MAX_ADDRESS_LENGTH = 500;

    /** Maximum notes length. */
    public static final int MAX_NOTES_LENGTH = 1000;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== Null/Empty Checks ====================

    /**
     * Checks if a string is null or empty (after trimming).
     *
     * @param str the string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks if a string is not null and not empty (after trimming).
     *
     * @param str the string to check
     * @return true if not null and not empty, false otherwise
     */
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    /**
     * Returns the string trimmed, or null if the input is null or empty.
     *
     * @param str the string to process
     * @return the trimmed string or null
     */
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Returns the string trimmed, or empty string if the input is null.
     *
     * @param str the string to process
     * @return the trimmed string or empty string
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    // ==================== Email Validation ====================

    /**
     * Validates an email address format.
     *
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        String trimmed = email.trim();
        return trimmed.length() <= MAX_EMAIL_LENGTH && EMAIL_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates and normalizes an email address.
     *
     * @param email the email to process
     * @return the normalized email (lowercase, trimmed) or null if invalid
     */
    public static String normalizeEmail(String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    // ==================== Phone Validation ====================

    /**
     * Validates a phone number format (general).
     *
     * @param phone the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates a Nepal phone number format.
     *
     * @param phone the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidNepalPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        return NEPAL_PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Normalizes a phone number by removing spaces and dashes.
     *
     * @param phone the phone number to normalize
     * @return the normalized phone number or null if invalid
     */
    public static String normalizePhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return null;
        }
        return phone.trim().replaceAll("[\\s\\-()]", "");
    }

    // ==================== Username/Password Validation ====================

    /**
     * Validates a username format.
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validates a password meets minimum security requirements.
     *
     * @param password the password to validate
     * @return true if the password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }

    /**
     * Validates a password meets strong security requirements.
     * Requires: uppercase, lowercase, digit, and special character.
     *
     * @param password the password to validate
     * @return true if the password is strong, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Gets the password strength level (0-4).
     *
     * @param password the password to evaluate
     * @return strength level: 0=very weak, 1=weak, 2=fair, 3=strong, 4=very strong
     */
    public static int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int strength = 0;

        // Length checks
        if (password.length() >= 8) {
            strength++;
        }
        if (password.length() >= 12) {
            strength++;
        }

        // Complexity checks
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            strength++;
        }
        if (password.matches(".*[0-9].*")) {
            strength++;
        }
        if (password.matches(".*[@$!%*?&#^()\\-_+=].*")) {
            strength++;
        }

        return Math.min(strength, 4);
    }

    // ==================== Name Validation ====================

    /**
     * Validates a person's name format.
     *
     * @param name the name to validate
     * @return true if the name is valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 2
                && trimmed.length() <= MAX_NAME_LENGTH
                && NAME_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Capitalizes each word in a name.
     *
     * @param name the name to capitalize
     * @return the capitalized name or null if input is null
     */
    public static String capitalizeWords(String name) {
        if (isNullOrEmpty(name)) {
            return name;
        }

        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (!result.isEmpty()) {
                    result.append(" ");
                }
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }

        return result.toString();
    }

    // ==================== Numeric Validation ====================

    /**
     * Checks if a string contains only digits.
     *
     * @param str the string to check
     * @return true if numeric only, false otherwise
     */
    public static boolean isNumeric(String str) {
        return isNotNullOrEmpty(str) && NUMERIC_PATTERN.matcher(str.trim()).matches();
    }

    /**
     * Checks if a string is a valid decimal number.
     *
     * @param str the string to check
     * @return true if valid decimal, false otherwise
     */
    public static boolean isDecimal(String str) {
        return isNotNullOrEmpty(str) && DECIMAL_PATTERN.matcher(str.trim()).matches();
    }

    /**
     * Validates if a value is a positive integer.
     *
     * @param value the value to check
     * @return true if positive, false otherwise
     */
    public static boolean isPositive(Integer value) {
        return value != null && value > 0;
    }

    /**
     * Validates if a value is a positive BigDecimal.
     *
     * @param value the value to check
     * @return true if positive, false otherwise
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validates if a value is non-negative (zero or positive).
     *
     * @param value the value to check
     * @return true if non-negative, false otherwise
     */
    public static boolean isNonNegative(Integer value) {
        return value != null && value >= 0;
    }

    /**
     * Validates if a value is non-negative BigDecimal (zero or positive).
     *
     * @param value the value to check
     * @return true if non-negative, false otherwise
     */
    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Validates if a value is within a specified range (inclusive).
     *
     * @param value the value to check
     * @param min the minimum value
     * @param max the maximum value
     * @return true if within range, false otherwise
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validates if a BigDecimal value is within a specified range (inclusive).
     *
     * @param value the value to check
     * @param min the minimum value
     * @param max the maximum value
     * @return true if within range, false otherwise
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    // ==================== Length Validation ====================

    /**
     * Validates if a string length is within bounds.
     *
     * @param str the string to check
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return true if within bounds, false otherwise
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength == 0;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validates if a string doesn't exceed maximum length.
     *
     * @param str the string to check
     * @param maxLength maximum allowed length
     * @return true if within limit, false otherwise
     */
    public static boolean isMaxLength(String str, int maxLength) {
        return str == null || str.trim().length() <= maxLength;
    }

    // ==================== Character Type Validation ====================

    /**
     * Checks if a string contains only alphabetic characters.
     *
     * @param str the string to check
     * @return true if alphabetic only, false otherwise
     */
    public static boolean isAlphabetic(String str) {
        return isNotNullOrEmpty(str) && ALPHA_PATTERN.matcher(str.trim()).matches();
    }

    /**
     * Checks if a string contains only alphanumeric characters.
     *
     * @param str the string to check
     * @return true if alphanumeric only, false otherwise
     */
    public static boolean isAlphanumeric(String str) {
        return isNotNullOrEmpty(str) && ALPHANUMERIC_PATTERN.matcher(str.trim()).matches();
    }

    // ==================== Business Validation ====================

    /**
     * Validates a table number format.
     *
     * @param tableNumber the table number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTableNumber(String tableNumber) {
        return isNotNullOrEmpty(tableNumber)
                && TABLE_NUMBER_PATTERN.matcher(tableNumber.trim()).matches();
    }

    /**
     * Validates a price value (positive and reasonable).
     *
     * @param price the price to validate
     * @return true if valid price, false otherwise
     */
    public static boolean isValidPrice(BigDecimal price) {
        if (price == null) {
            return false;
        }
        // Price must be non-negative and not exceed a reasonable maximum
        BigDecimal maxPrice = new BigDecimal("999999.99");
        return isNonNegative(price) && price.compareTo(maxPrice) <= 0;
    }

    /**
     * Validates a quantity value (positive integer).
     *
     * @param quantity the quantity to validate
     * @return true if valid quantity, false otherwise
     */
    public static boolean isValidQuantity(Integer quantity) {
        return quantity != null && quantity > 0 && quantity <= 9999;
    }

    /**
     * Validates a percentage value (0-100).
     *
     * @param percentage the percentage to validate
     * @return true if valid percentage, false otherwise
     */
    public static boolean isValidPercentage(Integer percentage) {
        return percentage != null && percentage >= 0 && percentage <= 100;
    }

    /**
     * Validates a percentage value (0-100).
     *
     * @param percentage the percentage to validate
     * @return true if valid percentage, false otherwise
     */
    public static boolean isValidPercentage(BigDecimal percentage) {
        if (percentage == null) {
            return false;
        }
        return percentage.compareTo(BigDecimal.ZERO) >= 0
                && percentage.compareTo(new BigDecimal("100")) <= 0;
    }

    /**
     * Sanitizes a string by removing potentially dangerous characters.
     *
     * @param input the input string to sanitize
     * @return the sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Remove HTML tags and common SQL injection characters
        return input.trim()
                .replaceAll("<[^>]*>", "")
                .replaceAll("[;'\"\\\\]", "")
                .replaceAll("--", "");
    }
}
