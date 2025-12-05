package com.restaurant.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * String Utility Class.
 * Provides common string manipulation and generation utilities
 * for the restaurant management system.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public final class StringUtils {

    /** Characters for generating random alphanumeric strings. */
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** Characters for generating numeric strings. */
    private static final String NUMERIC = "0123456789";

    /** Characters for generating uppercase alphanumeric strings. */
    private static final String UPPER_ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /** Secure random generator for generating random strings. */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Default ellipsis string. */
    private static final String ELLIPSIS = "...";

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private StringUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== Null/Empty Handling ====================

    /**
     * Returns the input string or empty string if null.
     *
     * @param str the input string
     * @return the input string or empty string
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * Returns null if the string is empty, otherwise returns the string.
     *
     * @param str the input string
     * @return null if empty, otherwise the input string
     */
    public static String emptyToNull(String str) {
        return (str == null || str.isEmpty()) ? null : str;
    }

    /**
     * Returns a default value if the string is null or empty.
     *
     * @param str the input string
     * @param defaultValue the default value to return
     * @return the input string or default value
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return (str == null || str.isEmpty()) ? defaultValue : str;
    }

    /**
     * Returns a default value if the string is null, empty, or blank.
     *
     * @param str the input string
     * @param defaultValue the default value to return
     * @return the input string or default value
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return (str == null || str.isBlank()) ? defaultValue : str;
    }

    // ==================== String Manipulation ====================

    /**
     * Truncates a string to a maximum length with ellipsis.
     *
     * @param str the string to truncate
     * @param maxLength the maximum length (must be at least 4 for ellipsis)
     * @return the truncated string
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        if (maxLength < 4) {
            return str.substring(0, maxLength);
        }
        return str.substring(0, maxLength - 3) + ELLIPSIS;
    }

    /**
     * Truncates a string from the middle with ellipsis.
     *
     * @param str the string to truncate
     * @param maxLength the maximum length
     * @return the truncated string with ellipsis in the middle
     */
    public static String truncateMiddle(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        if (maxLength < 5) {
            return truncate(str, maxLength);
        }

        int frontChars = (maxLength - 3) / 2;
        int backChars = maxLength - 3 - frontChars;

        return str.substring(0, frontChars) + ELLIPSIS + str.substring(str.length() - backChars);
    }

    /**
     * Pads a string on the left to reach the specified length.
     *
     * @param str the string to pad
     * @param length the desired length
     * @param padChar the character to pad with
     * @return the padded string
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = str.length(); i < length; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * Pads a string on the right to reach the specified length.
     *
     * @param str the string to pad
     * @param length the desired length
     * @param padChar the character to pad with
     * @return the padded string
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * Centers a string within a specified width.
     *
     * @param str the string to center
     * @param width the total width
     * @return the centered string
     */
    public static String center(String str, int width) {
        return center(str, width, ' ');
    }

    /**
     * Centers a string within a specified width using a pad character.
     *
     * @param str the string to center
     * @param width the total width
     * @param padChar the character to pad with
     * @return the centered string
     */
    public static String center(String str, int width, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= width) {
            return str;
        }
        int leftPad = (width - str.length()) / 2;
        int rightPad = width - str.length() - leftPad;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPad; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        for (int i = 0; i < rightPad; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * Reverses a string.
     *
     * @param str the string to reverse
     * @return the reversed string
     */
    public static String reverse(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Removes all whitespace from a string.
     *
     * @param str the input string
     * @return the string without whitespace
     */
    public static String removeWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\s+", "");
    }

    /**
     * Normalizes whitespace in a string (multiple spaces to single space).
     *
     * @param str the input string
     * @return the string with normalized whitespace
     */
    public static String normalizeWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().replaceAll("\\s+", " ");
    }

    // ==================== Case Conversion ====================

    /**
     * Converts a string to title case (first letter of each word capitalized).
     *
     * @param str the input string
     * @return the title case string
     */
    public static String toTitleCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    /**
     * Converts a string to sentence case (first letter capitalized).
     *
     * @param str the input string
     * @return the sentence case string
     */
    public static String toSentenceCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    /**
     * Converts a string to camelCase.
     *
     * @param str the input string (space or underscore separated)
     * @return the camelCase string
     */
    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split("[\\s_-]+");
        StringBuilder result = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    /**
     * Converts a string to snake_case.
     *
     * @param str the input string
     * @return the snake_case string
     */
    public static String toSnakeCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String result = str.replaceAll("([A-Z])", "_$1")
                .replaceAll("[\\s-]+", "_")
                .toLowerCase();

        if (result.startsWith("_")) {
            result = result.substring(1);
        }

        return result.replaceAll("_+", "_");
    }

    /**
     * Converts a string to CONSTANT_CASE.
     *
     * @param str the input string
     * @return the CONSTANT_CASE string
     */
    public static String toConstantCase(String str) {
        return toSnakeCase(str).toUpperCase();
    }

    // ==================== Random String Generation ====================

    /**
     * Generates a random alphanumeric string of specified length.
     *
     * @param length the desired length
     * @return the random string
     */
    public static String randomAlphanumeric(int length) {
        return generateRandom(length, ALPHANUMERIC);
    }

    /**
     * Generates a random numeric string of specified length.
     *
     * @param length the desired length
     * @return the random numeric string
     */
    public static String randomNumeric(int length) {
        return generateRandom(length, NUMERIC);
    }

    /**
     * Generates a random uppercase alphanumeric string of specified length.
     *
     * @param length the desired length
     * @return the random uppercase string
     */
    public static String randomUpperAlphanumeric(int length) {
        return generateRandom(length, UPPER_ALPHANUMERIC);
    }

    /**
     * Generates a random string from the given character set.
     *
     * @param length the desired length
     * @param charSet the characters to choose from
     * @return the random string
     */
    private static String generateRandom(int length, String charSet) {
        if (length <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(charSet.length());
            sb.append(charSet.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * Generates a unique order number.
     *
     * @return a unique order number (format: ORD-XXXXXX)
     */
    public static String generateOrderNumber() {
        return "ORD-" + randomUpperAlphanumeric(6);
    }

    /**
     * Generates a unique bill number.
     *
     * @return a unique bill number (format: BIL-XXXXXX)
     */
    public static String generateBillNumber() {
        return "BIL-" + randomUpperAlphanumeric(6);
    }

    /**
     * Generates a unique reservation code.
     *
     * @return a unique reservation code (format: RES-XXXXXX)
     */
    public static String generateReservationCode() {
        return "RES-" + randomUpperAlphanumeric(6);
    }

    /**
     * Generates a UUID string.
     *
     * @return a UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a short UUID (first 8 characters).
     *
     * @return a short UUID string
     */
    public static String generateShortUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // ==================== Counting and Analysis ====================

    /**
     * Counts the occurrences of a character in a string.
     *
     * @param str the string to search
     * @param ch the character to count
     * @return the number of occurrences
     */
    public static int countOccurrences(String str, char ch) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the occurrences of a substring in a string.
     *
     * @param str the string to search
     * @param sub the substring to count
     * @return the number of occurrences
     */
    public static int countOccurrences(String str, String sub) {
        if (str == null || sub == null || str.isEmpty() || sub.isEmpty()) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * Counts the number of words in a string.
     *
     * @param str the string to analyze
     * @return the word count
     */
    public static int countWords(String str) {
        if (str == null || str.isBlank()) {
            return 0;
        }
        return str.trim().split("\\s+").length;
    }

    // ==================== Comparison and Matching ====================

    /**
     * Checks if a string contains another string (case-insensitive).
     *
     * @param str the string to search in
     * @param searchStr the string to search for
     * @return true if found, false otherwise
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    /**
     * Checks if a string equals another string (case-insensitive, null-safe).
     *
     * @param str1 the first string
     * @param str2 the second string
     * @return true if equal, false otherwise
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * Checks if a string starts with another string (case-insensitive).
     *
     * @param str the string to check
     * @param prefix the prefix to look for
     * @return true if starts with prefix, false otherwise
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Checks if a string ends with another string (case-insensitive).
     *
     * @param str the string to check
     * @param suffix the suffix to look for
     * @return true if ends with suffix, false otherwise
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.toLowerCase().endsWith(suffix.toLowerCase());
    }

    // ==================== Masking ====================

    /**
     * Masks an email address (e.g., j***@example.com).
     *
     * @param email the email to mask
     * @return the masked email
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }

        return email.charAt(0) + "*".repeat(atIndex - 1) + email.substring(atIndex);
    }

    /**
     * Masks a phone number (shows last 4 digits).
     *
     * @param phone the phone number to mask
     * @return the masked phone number
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }

        String digitsOnly = phone.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 4) {
            return phone;
        }

        int visibleDigits = 4;
        int maskedLength = digitsOnly.length() - visibleDigits;

        return "*".repeat(maskedLength) + digitsOnly.substring(maskedLength);
    }

    /**
     * Masks a credit card number (shows last 4 digits).
     *
     * @param cardNumber the card number to mask
     * @return the masked card number
     */
    public static String maskCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }

        String digitsOnly = cardNumber.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 4) {
            return cardNumber;
        }

        return "**** **** **** " + digitsOnly.substring(digitsOnly.length() - 4);
    }

    // ==================== Formatting ====================

    /**
     * Formats a number as currency (Nepali Rupee format).
     *
     * @param amount the amount to format
     * @return the formatted currency string
     */
    public static String formatCurrency(double amount) {
        return String.format("Rs. %,.2f", amount);
    }

    /**
     * Formats a number with thousand separators.
     *
     * @param number the number to format
     * @return the formatted number string
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }

    /**
     * Repeats a string a specified number of times.
     *
     * @param str the string to repeat
     * @param times the number of times to repeat
     * @return the repeated string
     */
    public static String repeat(String str, int times) {
        if (str == null || times <= 0) {
            return "";
        }
        return str.repeat(times);
    }

    /**
     * Joins strings with a delimiter.
     *
     * @param delimiter the delimiter
     * @param strings the strings to join
     * @return the joined string
     */
    public static String join(String delimiter, String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        return String.join(delimiter, strings);
    }
}
