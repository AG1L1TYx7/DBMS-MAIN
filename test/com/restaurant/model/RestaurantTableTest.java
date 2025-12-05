package com.restaurant.model;

import com.restaurant.model.RestaurantTable.TableStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link RestaurantTable} model class.
 * 
 * <p>This test class provides comprehensive coverage of the RestaurantTable model's functionality,
 * including basic properties, table status management, timestamp handling,
 * constructors, and edge cases.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic table properties</li>
 *   <li>{@link TableStatusTests} - Tests for table status enum handling</li>
 *   <li>{@link TimestampTests} - Tests for timestamp handling</li>
 *   <li>{@link ConstructorTests} - Tests for default and parameterized constructors</li>
 *   <li>{@link EdgeCasesTests} - Tests for boundary conditions and edge cases</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see RestaurantTable
 * @see TableStatus
 */
public class RestaurantTableTest {
    
    /** The test table instance used across test methods. */
    private RestaurantTable table;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh RestaurantTable instance for each test case.
     */
    @BeforeEach
    void setUp() {
        table = new RestaurantTable();
    }
    
    /**
     * Nested test class for basic table property tests.
     * 
     * <p>Validates getter and setter functionality for core table properties
     * including table ID, number, name, capacity, location, and description.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that table ID can be set and retrieved correctly.
         * Verifies the setTableId and getTableId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get table ID")
        void testTableId() {
            table.setTableId(1);
            assertEquals(1, table.getTableId());
        }
        
        /**
         * Tests that table number can be set and retrieved correctly.
         * Table number is a short identifier (e.g., "T5") for the table.
         */
        @Test
        @DisplayName("Should set and get table number")
        void testTableNumber() {
            table.setTableNumber("T5");
            assertEquals("T5", table.getTableNumber());
        }
        
        /**
         * Tests that table name can be set and retrieved correctly.
         * Table name provides a descriptive name for the table.
         */
        @Test
        @DisplayName("Should set and get table name")
        void testTableName() {
            table.setTableName("Window Table");
            assertEquals("Window Table", table.getTableName());
        }
        
        /**
         * Tests that capacity can be set and retrieved correctly.
         * Capacity indicates maximum number of guests the table can accommodate.
         */
        @Test
        @DisplayName("Should set and get capacity")
        void testCapacity() {
            table.setCapacity(4);
            assertEquals(4, table.getCapacity());
        }
        
        /**
         * Tests that location can be set and retrieved correctly.
         * Location describes where the table is situated in the restaurant.
         */
        @Test
        @DisplayName("Should set and get location")
        void testLocation() {
            table.setLocation("Main Hall");
            assertEquals("Main Hall", table.getLocation());
        }
        
        /**
         * Tests that description can be set and retrieved correctly.
         * Description provides additional details about the table.
         */
        @Test
        @DisplayName("Should set and get description")
        void testDescription() {
            table.setDescription("Near the window with city view");
            assertEquals("Near the window with city view", table.getDescription());
        }
    }
    
    /**
     * Nested test class for table status tests.
     * 
     * <p>Validates TableStatus enum functionality including default status,
     * setting various statuses, display names, string conversion, and enum values.</p>
     */
    @Nested
    @DisplayName("Table Status Tests")
    class TableStatusTests {
        
        /**
         * Tests that new tables default to AVAILABLE status.
         * Verifies proper initial status for newly created tables.
         */
        @Test
        @DisplayName("Should default to AVAILABLE status")
        void testDefaultStatus() {
            assertEquals(TableStatus.AVAILABLE, table.getStatus());
        }
        
        /**
         * Tests setting and getting AVAILABLE status.
         * Table is ready for seating customers.
         */
        @Test
        @DisplayName("Should set and get AVAILABLE status")
        void testAvailableStatus() {
            table.setStatus(TableStatus.AVAILABLE);
            assertEquals(TableStatus.AVAILABLE, table.getStatus());
        }
        
        /**
         * Tests setting and getting OCCUPIED status.
         * Table is currently in use by customers.
         */
        @Test
        @DisplayName("Should set and get OCCUPIED status")
        void testOccupiedStatus() {
            table.setStatus(TableStatus.OCCUPIED);
            assertEquals(TableStatus.OCCUPIED, table.getStatus());
        }
        
        /**
         * Tests setting and getting RESERVED status.
         * Table has an upcoming reservation.
         */
        @Test
        @DisplayName("Should set and get RESERVED status")
        void testReservedStatus() {
            table.setStatus(TableStatus.RESERVED);
            assertEquals(TableStatus.RESERVED, table.getStatus());
        }
        
        /**
         * Tests setting and getting MAINTENANCE status.
         * Table is temporarily unavailable for maintenance.
         */
        @Test
        @DisplayName("Should set and get MAINTENANCE status")
        void testMaintenanceStatus() {
            table.setStatus(TableStatus.MAINTENANCE);
            assertEquals(TableStatus.MAINTENANCE, table.getStatus());
        }
        
        /**
         * Tests that all TableStatus enum values have correct display names.
         * Verifies human-readable names for UI display purposes.
         */
        @Test
        @DisplayName("TableStatus enum should have correct display names")
        void testStatusDisplayNames() {
            assertEquals("Available", TableStatus.AVAILABLE.getDisplayName());
            assertEquals("Occupied", TableStatus.OCCUPIED.getDisplayName());
            assertEquals("Reserved", TableStatus.RESERVED.getDisplayName());
            assertEquals("Maintenance", TableStatus.MAINTENANCE.getDisplayName());
        }
        
        /**
         * Tests that TableStatus.fromString works with enum name.
         * Verifies parsing status from uppercase enum name.
         */
        @Test
        @DisplayName("TableStatus fromString should work with enum name")
        void testStatusFromStringEnumName() {
            assertEquals(TableStatus.OCCUPIED, TableStatus.fromString("OCCUPIED"));
        }
        
        /**
         * Tests that TableStatus.fromString works with display name.
         * Verifies parsing status from display name format.
         */
        @Test
        @DisplayName("TableStatus fromString should work with display name")
        void testStatusFromStringDisplayName() {
            assertEquals(TableStatus.RESERVED, TableStatus.fromString("Reserved"));
        }
        
        /**
         * Tests that TableStatus.fromString defaults to AVAILABLE for invalid input.
         * Verifies graceful handling of unrecognized status names.
         */
        @Test
        @DisplayName("TableStatus fromString should default to AVAILABLE for invalid")
        void testStatusFromStringInvalid() {
            assertEquals(TableStatus.AVAILABLE, TableStatus.fromString("Invalid"));
        }
        
        /**
         * Tests that TableStatus enum contains exactly 4 values.
         * Verifies all expected statuses are present.
         */
        @Test
        @DisplayName("TableStatus enum should have all values")
        void testAllStatusValues() {
            TableStatus[] statuses = TableStatus.values();
            assertThat(statuses).hasSize(4);
            assertThat(statuses).containsExactlyInAnyOrder(
                TableStatus.AVAILABLE,
                TableStatus.OCCUPIED,
                TableStatus.RESERVED,
                TableStatus.MAINTENANCE
            );
        }
    }
    
    /**
     * Nested test class for timestamp tests.
     * 
     * <p>Validates timestamp handling including automatic createdAt
     * initialization and updatedAt setting.</p>
     */
    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {
        
        /**
         * Tests that createdAt timestamp is set automatically on construction.
         * Verifies new tables have a non-null creation timestamp.
         */
        @Test
        @DisplayName("Should set createdAt on construction")
        void testCreatedAtOnConstruction() {
            assertNotNull(table.getCreatedAt());
        }
        
        /**
         * Tests that createdAt timestamp can be manually set and retrieved.
         * Verifies the setCreatedAt and getCreatedAt methods.
         */
        @Test
        @DisplayName("Should set and get createdAt")
        void testSetCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            table.setCreatedAt(now);
            assertEquals(now, table.getCreatedAt());
        }
        
        /**
         * Tests that updatedAt timestamp can be set and retrieved correctly.
         * Updated at records when the table was last modified.
         */
        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            table.setUpdatedAt(now);
            assertEquals(now, table.getUpdatedAt());
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of RestaurantTable objects through
     * default and parameterized constructors.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor properly initializes all fields.
         * Verifies status defaults to AVAILABLE and createdAt is set.
         */
        @Test
        @DisplayName("Default constructor should initialize properly")
        void testDefaultConstructor() {
            RestaurantTable newTable = new RestaurantTable();
            
            assertNotNull(newTable);
            assertEquals(TableStatus.AVAILABLE, newTable.getStatus());
            assertNotNull(newTable.getCreatedAt());
        }
        
        /**
         * Tests that the parameterized constructor sets all fields correctly.
         * Verifies table number, name, capacity, location, status, and timestamp.
         */
        @Test
        @DisplayName("Parameterized constructor should set all fields")
        void testParameterizedConstructor() {
            RestaurantTable newTable = new RestaurantTable(
                "T10",
                "VIP Table",
                6,
                "VIP Room"
            );
            
            assertEquals("T10", newTable.getTableNumber());
            assertEquals("VIP Table", newTable.getTableName());
            assertEquals(6, newTable.getCapacity());
            assertEquals("VIP Room", newTable.getLocation());
            assertEquals(TableStatus.AVAILABLE, newTable.getStatus());
            assertNotNull(newTable.getCreatedAt());
        }
    }
    
    /**
     * Nested test class for edge case tests.
     * 
     * <p>Validates behavior with boundary conditions such as null values
     * and various capacity limits.</p>
     */
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        /**
         * Tests that null table name can be set and handled.
         * Verifies null handling for optional properties.
         */
        @Test
        @DisplayName("Should handle null table name")
        void testNullTableName() {
            table.setTableName(null);
            assertNull(table.getTableName());
        }
        
        /**
         * Tests that zero capacity can be set.
         * Handles edge case for tables being set up.
         */
        @Test
        @DisplayName("Should handle zero capacity")
        void testZeroCapacity() {
            table.setCapacity(0);
            assertEquals(0, table.getCapacity());
        }
        
        /**
         * Tests that large capacity values are handled correctly.
         * Verifies handling of large party tables.
         */
        @Test
        @DisplayName("Should handle large capacity")
        void testLargeCapacity() {
            table.setCapacity(20);
            assertEquals(20, table.getCapacity());
        }
    }
}
