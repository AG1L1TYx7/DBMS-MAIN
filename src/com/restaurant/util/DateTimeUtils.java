package com.restaurant.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Date and Time Utility Class.
 * Provides common date/time operations and formatting utilities
 * for the restaurant management system.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public final class DateTimeUtils {

    /** Default date format pattern (YYYY-MM-DD). */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** Default time format pattern (HH:mm:ss). */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /** Short time format pattern (HH:mm). */
    public static final String TIME_FORMAT_SHORT = "HH:mm";

    /** Default datetime format pattern. */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** Display datetime format for UI (e.g., Dec 04, 2024 02:30 PM). */
    public static final String DATETIME_DISPLAY_FORMAT = "MMM dd, yyyy hh:mm a";

    /** Display date format for UI (e.g., Dec 04, 2024). */
    public static final String DATE_DISPLAY_FORMAT = "MMM dd, yyyy";

    /** ISO 8601 datetime format. */
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /** Restaurant timezone (Nepal Time - NPT). */
    public static final ZoneId RESTAURANT_TIMEZONE = ZoneId.of("Asia/Kathmandu");

    /** Restaurant opening hour (24-hour format). */
    private static final int OPENING_HOUR = 7;

    /** Restaurant closing hour (24-hour format). */
    private static final int CLOSING_HOUR = 22;

    // Pre-compiled formatters for better performance
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final DateTimeFormatter TIME_SHORT_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_SHORT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_DISPLAY_FORMAT, Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_DISPLAY_FORMAT, Locale.ENGLISH);

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== Formatting Methods ====================

    /**
     * Formats a LocalDate to the default date format (YYYY-MM-DD).
     *
     * @param date the date to format
     * @return the formatted date string, or empty string if date is null
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Formats a LocalTime to the default time format (HH:mm:ss).
     *
     * @param time the time to format
     * @return the formatted time string, or empty string if time is null
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    /**
     * Formats a LocalTime to short time format (HH:mm).
     *
     * @param time the time to format
     * @return the formatted time string, or empty string if time is null
     */
    public static String formatTimeShort(LocalTime time) {
        return time != null ? time.format(TIME_SHORT_FORMATTER) : "";
    }

    /**
     * Formats a LocalDateTime to the default datetime format.
     *
     * @param dateTime the datetime to format
     * @return the formatted datetime string, or empty string if datetime is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    /**
     * Formats a LocalDateTime for display purposes (e.g., Dec 04, 2024 02:30 PM).
     *
     * @param dateTime the datetime to format
     * @return the formatted datetime string, or empty string if datetime is null
     */
    public static String formatDateTimeForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME_FORMATTER) : "";
    }

    /**
     * Formats a LocalDate for display purposes (e.g., Dec 04, 2024).
     *
     * @param date the date to format
     * @return the formatted date string, or empty string if date is null
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE_FORMATTER) : "";
    }

    // ==================== Parsing Methods ====================

    /**
     * Parses a date string in the default format (YYYY-MM-DD).
     *
     * @param dateString the date string to parse
     * @return the parsed LocalDate, or null if parsing fails
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a time string in the default format (HH:mm:ss) or short format (HH:mm).
     *
     * @param timeString the time string to parse
     * @return the parsed LocalTime, or null if parsing fails
     */
    public static LocalTime parseTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        try {
            String trimmed = timeString.trim();
            if (trimmed.length() == 5) {
                return LocalTime.parse(trimmed, TIME_SHORT_FORMATTER);
            }
            return LocalTime.parse(trimmed, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a datetime string in the default format.
     *
     * @param dateTimeString the datetime string to parse
     * @return the parsed LocalDateTime, or null if parsing fails
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString.trim(), DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ==================== Current Date/Time Methods ====================

    /**
     * Gets the current date in restaurant timezone.
     *
     * @return the current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now(RESTAURANT_TIMEZONE);
    }

    /**
     * Gets the current time in restaurant timezone.
     *
     * @return the current time
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now(RESTAURANT_TIMEZONE);
    }

    /**
     * Gets the current datetime in restaurant timezone.
     *
     * @return the current datetime
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(RESTAURANT_TIMEZONE);
    }

    /**
     * Gets the current timestamp as formatted string.
     *
     * @return the current timestamp string
     */
    public static String getCurrentTimestamp() {
        return formatDateTime(getCurrentDateTime());
    }

    // ==================== Date Calculation Methods ====================

    /**
     * Calculates the difference in days between two dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of days between the dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calculates the difference in hours between two datetimes.
     *
     * @param startDateTime the start datetime
     * @param endDateTime the end datetime
     * @return the number of hours between the datetimes
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * Calculates the difference in minutes between two datetimes.
     *
     * @param startDateTime the start datetime
     * @param endDateTime the end datetime
     * @return the number of minutes between the datetimes
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }

    /**
     * Adds days to a date.
     *
     * @param date the base date
     * @param days the number of days to add (can be negative)
     * @return the resulting date
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * Adds hours to a datetime.
     *
     * @param dateTime the base datetime
     * @param hours the number of hours to add (can be negative)
     * @return the resulting datetime
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        return dateTime != null ? dateTime.plusHours(hours) : null;
    }

    /**
     * Adds minutes to a datetime.
     *
     * @param dateTime the base datetime
     * @param minutes the number of minutes to add (can be negative)
     * @return the resulting datetime
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, int minutes) {
        return dateTime != null ? dateTime.plusMinutes(minutes) : null;
    }

    // ==================== Date Range Methods ====================

    /**
     * Gets the start of the current day (00:00:00).
     *
     * @return the start of today
     */
    public static LocalDateTime getStartOfToday() {
        return getCurrentDate().atStartOfDay();
    }

    /**
     * Gets the end of the current day (23:59:59.999999999).
     *
     * @return the end of today
     */
    public static LocalDateTime getEndOfToday() {
        return getCurrentDate().atTime(LocalTime.MAX);
    }

    /**
     * Gets the start of the current week (Monday 00:00:00).
     *
     * @return the start of the current week
     */
    public static LocalDateTime getStartOfWeek() {
        LocalDate today = getCurrentDate();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        return monday.atStartOfDay();
    }

    /**
     * Gets the start of the current month (1st day 00:00:00).
     *
     * @return the start of the current month
     */
    public static LocalDateTime getStartOfMonth() {
        return getCurrentDate().withDayOfMonth(1).atStartOfDay();
    }

    /**
     * Gets the end of the current month.
     *
     * @return the end of the current month
     */
    public static LocalDateTime getEndOfMonth() {
        LocalDate today = getCurrentDate();
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());
        return lastDay.atTime(LocalTime.MAX);
    }

    /**
     * Gets the start of the current year (Jan 1st 00:00:00).
     *
     * @return the start of the current year
     */
    public static LocalDateTime getStartOfYear() {
        return getCurrentDate().withDayOfYear(1).atStartOfDay();
    }

    // ==================== Business Logic Methods ====================

    /**
     * Checks if the current time is within restaurant operating hours.
     *
     * @return true if the restaurant is open, false otherwise
     */
    public static boolean isRestaurantOpen() {
        LocalTime now = getCurrentTime();
        return now.getHour() >= OPENING_HOUR && now.getHour() < CLOSING_HOUR;
    }

    /**
     * Checks if a given datetime is within restaurant operating hours.
     *
     * @param dateTime the datetime to check
     * @return true if within operating hours, false otherwise
     */
    public static boolean isWithinOperatingHours(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        int hour = dateTime.getHour();
        return hour >= OPENING_HOUR && hour < CLOSING_HOUR;
    }

    /**
     * Gets the remaining operating minutes for today.
     *
     * @return the remaining minutes until closing, or 0 if closed
     */
    public static long getRemainingOperatingMinutes() {
        if (!isRestaurantOpen()) {
            return 0;
        }
        LocalTime now = getCurrentTime();
        LocalTime closingTime = LocalTime.of(CLOSING_HOUR, 0);
        return ChronoUnit.MINUTES.between(now, closingTime);
    }

    /**
     * Checks if a date is today.
     *
     * @param date the date to check
     * @return true if the date is today, false otherwise
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(getCurrentDate());
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date the date to check
     * @return true if the date is before today, false otherwise
     */
    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(getCurrentDate());
    }

    /**
     * Checks if a date is in the future.
     *
     * @param date the date to check
     * @return true if the date is after today, false otherwise
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(getCurrentDate());
    }

    /**
     * Checks if a datetime is in the past.
     *
     * @param dateTime the datetime to check
     * @return true if the datetime is before now, false otherwise
     */
    public static boolean isPastDateTime(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(getCurrentDateTime());
    }

    /**
     * Checks if a datetime is in the future.
     *
     * @param dateTime the datetime to check
     * @return true if the datetime is after now, false otherwise
     */
    public static boolean isFutureDateTime(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(getCurrentDateTime());
    }

    // ==================== Duration Formatting Methods ====================

    /**
     * Formats a duration in minutes to a human-readable string.
     *
     * @param totalMinutes the total minutes
     * @return formatted string like "2h 30m" or "45m"
     */
    public static String formatDuration(long totalMinutes) {
        if (totalMinutes < 0) {
            return "0m";
        }

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh", hours);
        } else {
            return String.format("%dm", minutes);
        }
    }

    /**
     * Formats a duration between two datetimes to a human-readable string.
     *
     * @param start the start datetime
     * @param end the end datetime
     * @return formatted duration string
     */
    public static String formatDurationBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "0m";
        }
        return formatDuration(minutesBetween(start, end));
    }

    /**
     * Gets a relative time description (e.g., "2 hours ago", "in 30 minutes").
     *
     * @param dateTime the datetime to describe
     * @return a relative time description
     */
    public static String getRelativeTimeDescription(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        LocalDateTime now = getCurrentDateTime();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        boolean isPast = minutes > 0;
        minutes = Math.abs(minutes);

        String timeUnit;
        long value;

        if (minutes < 1) {
            return "just now";
        } else if (minutes < 60) {
            value = minutes;
            timeUnit = minutes == 1 ? "minute" : "minutes";
        } else if (minutes < 1440) { // Less than a day
            value = minutes / 60;
            timeUnit = value == 1 ? "hour" : "hours";
        } else if (minutes < 10080) { // Less than a week
            value = minutes / 1440;
            timeUnit = value == 1 ? "day" : "days";
        } else {
            value = minutes / 10080;
            timeUnit = value == 1 ? "week" : "weeks";
        }

        if (isPast) {
            return String.format("%d %s ago", value, timeUnit);
        } else {
            return String.format("in %d %s", value, timeUnit);
        }
    }

    // ==================== Validation Methods ====================

    /**
     * Validates if a string is a valid date in the default format.
     *
     * @param dateString the date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDate(String dateString) {
        return parseDate(dateString) != null;
    }

    /**
     * Validates if a string is a valid time in the default or short format.
     *
     * @param timeString the time string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTime(String timeString) {
        return parseTime(timeString) != null;
    }

    /**
     * Validates if a string is a valid datetime in the default format.
     *
     * @param dateTimeString the datetime string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateTime(String dateTimeString) {
        return parseDateTime(dateTimeString) != null;
    }

    /**
     * Gets the age in years from a birth date.
     *
     * @param birthDate the birth date
     * @return the age in years, or 0 if birthDate is null
     */
    public static int getAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, getCurrentDate()).getYears();
    }

    /**
     * Gets the day of week name for a date.
     *
     * @param date the date
     * @return the day name (e.g., "Monday"), or empty string if date is null
     */
    public static String getDayOfWeekName(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * Gets the month name for a date.
     *
     * @param date the date
     * @return the month name (e.g., "December"), or empty string if date is null
     */
    public static String getMonthName(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }
}
