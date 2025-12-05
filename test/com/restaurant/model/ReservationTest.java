package com.restaurant.model;

import com.restaurant.model.Reservation.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Reservation} model class.
 * 
 * <p>This test class provides comprehensive coverage of the Reservation model's functionality,
 * including basic properties, reservation status management, date/time handling,
 * and constructors.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic reservation properties</li>
 *   <li>{@link ReservationStatusTests} - Tests for reservation status enum handling</li>
 *   <li>{@link DateTimeTests} - Tests for date and time fields</li>
 *   <li>{@link ConstructorTests} - Tests for default and parameterized constructors</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see Reservation
 * @see ReservationStatus
 */
public class ReservationTest {
    
    /** The test reservation instance used across test methods. */
    private Reservation reservation;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh Reservation instance for each test case.
     */
    @BeforeEach
    void setUp() {
        reservation = new Reservation();
    }
    
    /**
     * Nested test class for basic reservation property tests.
     * 
     * <p>Validates getter and setter functionality for core reservation properties
     * including reservation ID, customer details, table information, guest count,
     * special requests, and creator information.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that reservation ID can be set and retrieved correctly.
         * Verifies the setReservationId and getReservationId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get reservation ID")
        void testReservationId() {
            reservation.setReservationId(1);
            assertEquals(1, reservation.getReservationId());
        }
        
        /**
         * Tests that customer name can be set and retrieved correctly.
         * Customer name identifies who made the reservation.
         */
        @Test
        @DisplayName("Should set and get customer name")
        void testCustomerName() {
            reservation.setCustomerName("John Doe");
            assertEquals("John Doe", reservation.getCustomerName());
        }
        
        /**
         * Tests that customer phone can be set and retrieved correctly.
         * Phone number is used for reservation confirmations and reminders.
         */
        @Test
        @DisplayName("Should set and get customer phone")
        void testCustomerPhone() {
            reservation.setCustomerPhone("1234567890");
            assertEquals("1234567890", reservation.getCustomerPhone());
        }
        
        /**
         * Tests that customer email can be set and retrieved correctly.
         * Email is used for sending reservation confirmations.
         */
        @Test
        @DisplayName("Should set and get customer email")
        void testCustomerEmail() {
            reservation.setCustomerEmail("john@example.com");
            assertEquals("john@example.com", reservation.getCustomerEmail());
        }
        
        /**
         * Tests that table ID can be set and retrieved correctly.
         * Table ID references the reserved table in the database.
         */
        @Test
        @DisplayName("Should set and get table ID")
        void testTableId() {
            reservation.setTableId(5);
            assertEquals(5, reservation.getTableId());
        }
        
        /**
         * Tests that table number can be set and retrieved correctly.
         * Table number is the display identifier for the table.
         */
        @Test
        @DisplayName("Should set and get table number")
        void testTableNumber() {
            reservation.setTableNumber("T5");
            assertEquals("T5", reservation.getTableNumber());
        }
        
        /**
         * Tests that table name can be set and retrieved correctly.
         * Table name provides a descriptive name for the table.
         */
        @Test
        @DisplayName("Should set and get table name")
        void testTableName() {
            reservation.setTableName("Window Table");
            assertEquals("Window Table", reservation.getTableName());
        }
        
        /**
         * Tests that number of guests can be set and retrieved correctly.
         * Number of guests indicates party size for the reservation.
         */
        @Test
        @DisplayName("Should set and get number of guests")
        void testNumberOfGuests() {
            reservation.setNumberOfGuests(4);
            assertEquals(4, reservation.getNumberOfGuests());
        }
        
        /**
         * Tests that special requests can be set and retrieved correctly.
         * Special requests store customer preferences or requirements.
         */
        @Test
        @DisplayName("Should set and get special requests")
        void testSpecialRequests() {
            reservation.setSpecialRequests("Birthday celebration");
            assertEquals("Birthday celebration", reservation.getSpecialRequests());
        }
        
        /**
         * Tests that created by user ID can be set and retrieved correctly.
         * Created by tracks which staff member created the reservation.
         */
        @Test
        @DisplayName("Should set and get created by")
        void testCreatedBy() {
            reservation.setCreatedBy(10);
            assertEquals(10, reservation.getCreatedBy());
        }
    }
    
    /**
     * Nested test class for reservation status tests.
     * 
     * <p>Validates ReservationStatus enum functionality including default status,
     * setting various statuses, display names, and string conversion.</p>
     */
    @Nested
    @DisplayName("Reservation Status Tests")
    class ReservationStatusTests {
        
        /**
         * Tests that new reservations default to PENDING status.
         * Verifies proper initial status for new reservations.
         */
        @Test
        @DisplayName("Should default to PENDING status")
        void testDefaultStatus() {
            assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        }
        
        /**
         * Tests setting and getting CONFIRMED status.
         * Verified when staff confirms a reservation.
         */
        @Test
        @DisplayName("Should set and get CONFIRMED status")
        void testConfirmedStatus() {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        }
        
        /**
         * Tests setting and getting CANCELLED status.
         * Used when a reservation is cancelled by customer or staff.
         */
        @Test
        @DisplayName("Should set and get CANCELLED status")
        void testCancelledStatus() {
            reservation.setStatus(ReservationStatus.CANCELLED);
            assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        }
        
        /**
         * Tests setting and getting COMPLETED status.
         * Used when the customer has arrived and dining is complete.
         */
        @Test
        @DisplayName("Should set and get COMPLETED status")
        void testCompletedStatus() {
            reservation.setStatus(ReservationStatus.COMPLETED);
            assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
        }
        
        /**
         * Tests setting and getting NO_SHOW status.
         * Used when customer did not arrive for their reservation.
         */
        @Test
        @DisplayName("Should set and get NO_SHOW status")
        void testNoShowStatus() {
            reservation.setStatus(ReservationStatus.NO_SHOW);
            assertEquals(ReservationStatus.NO_SHOW, reservation.getStatus());
        }
        
        /**
         * Tests that all ReservationStatus enum values have correct display names.
         * Verifies human-readable names for UI display purposes.
         */
        @Test
        @DisplayName("ReservationStatus enum should have correct display names")
        void testStatusDisplayNames() {
            assertEquals("Pending", ReservationStatus.PENDING.getDisplayName());
            assertEquals("Confirmed", ReservationStatus.CONFIRMED.getDisplayName());
            assertEquals("Cancelled", ReservationStatus.CANCELLED.getDisplayName());
            assertEquals("Completed", ReservationStatus.COMPLETED.getDisplayName());
            assertEquals("No Show", ReservationStatus.NO_SHOW.getDisplayName());
        }
        
        /**
         * Tests that ReservationStatus.fromString works with enum name.
         * Verifies parsing status from uppercase enum name.
         */
        @Test
        @DisplayName("ReservationStatus fromString should work with enum name")
        void testStatusFromStringEnumName() {
            assertEquals(ReservationStatus.CONFIRMED, ReservationStatus.fromString("CONFIRMED"));
        }
        
        /**
         * Tests that ReservationStatus.fromString works with display name.
         * Verifies parsing status from display name format.
         */
        @Test
        @DisplayName("ReservationStatus fromString should work with display name")
        void testStatusFromStringDisplayName() {
            assertEquals(ReservationStatus.NO_SHOW, ReservationStatus.fromString("No Show"));
        }
        
        /**
         * Tests that ReservationStatus.fromString defaults to PENDING for invalid input.
         * Verifies graceful handling of unrecognized status names.
         */
        @Test
        @DisplayName("ReservationStatus fromString should default to PENDING for invalid")
        void testStatusFromStringInvalid() {
            assertEquals(ReservationStatus.PENDING, ReservationStatus.fromString("Invalid"));
        }
    }
    
    /**
     * Nested test class for date and time property tests.
     * 
     * <p>Validates date and time handling including reservation date/time,
     * createdAt, and updatedAt timestamps.</p>
     */
    @Nested
    @DisplayName("Date and Time Tests")
    class DateTimeTests {
        
        /**
         * Tests that reservation date can be set and retrieved correctly.
         * Reservation date specifies when the reservation is for.
         */
        @Test
        @DisplayName("Should set and get reservation date")
        void testReservationDate() {
            LocalDate date = LocalDate.of(2024, 12, 25);
            reservation.setReservationDate(date);
            assertEquals(date, reservation.getReservationDate());
        }
        
        /**
         * Tests that reservation time can be set and retrieved correctly.
         * Reservation time specifies the arrival time for the party.
         */
        @Test
        @DisplayName("Should set and get reservation time")
        void testReservationTime() {
            LocalTime time = LocalTime.of(19, 30);
            reservation.setReservationTime(time);
            assertEquals(time, reservation.getReservationTime());
        }
        
        /**
         * Tests that createdAt timestamp is set automatically on construction.
         * Verifies new reservations have a non-null creation timestamp.
         */
        @Test
        @DisplayName("Should set createdAt on construction")
        void testCreatedAtOnConstruction() {
            assertNotNull(reservation.getCreatedAt());
        }
        
        /**
         * Tests that createdAt timestamp can be manually set and retrieved.
         * Verifies the setCreatedAt and getCreatedAt methods.
         */
        @Test
        @DisplayName("Should set and get createdAt")
        void testSetCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            reservation.setCreatedAt(now);
            assertEquals(now, reservation.getCreatedAt());
        }
        
        /**
         * Tests that updatedAt timestamp can be set and retrieved correctly.
         * Updated at records when the reservation was last modified.
         */
        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            reservation.setUpdatedAt(now);
            assertEquals(now, reservation.getUpdatedAt());
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of Reservation objects through
     * default and parameterized constructors.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor properly initializes all fields.
         * Verifies status defaults to PENDING and createdAt is set.
         */
        @Test
        @DisplayName("Default constructor should initialize properly")
        void testDefaultConstructor() {
            Reservation newReservation = new Reservation();
            
            assertNotNull(newReservation);
            assertEquals(ReservationStatus.PENDING, newReservation.getStatus());
            assertNotNull(newReservation.getCreatedAt());
        }
        
        /**
         * Tests that the parameterized constructor sets all fields correctly.
         * Verifies customer details, table ID, date/time, guests, status, and timestamp.
         */
        @Test
        @DisplayName("Parameterized constructor should set all fields")
        void testParameterizedConstructor() {
            LocalDate date = LocalDate.of(2024, 12, 25);
            LocalTime time = LocalTime.of(19, 30);
            
            Reservation newReservation = new Reservation(
                "John Doe",
                "1234567890",
                5,
                date,
                time,
                4
            );
            
            assertEquals("John Doe", newReservation.getCustomerName());
            assertEquals("1234567890", newReservation.getCustomerPhone());
            assertEquals(5, newReservation.getTableId());
            assertEquals(date, newReservation.getReservationDate());
            assertEquals(time, newReservation.getReservationTime());
            assertEquals(4, newReservation.getNumberOfGuests());
            assertEquals(ReservationStatus.PENDING, newReservation.getStatus());
            assertNotNull(newReservation.getCreatedAt());
        }
    }
}
