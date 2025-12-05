package com.restaurant.model;

import com.restaurant.model.Customer.MembershipTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Customer} model class.
 * 
 * <p>This test class provides comprehensive coverage of the Customer model's functionality,
 * including basic properties, membership tiers, loyalty points, spending tracking,
 * visit counts, active status, and timestamp handling.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic customer properties</li>
 *   <li>{@link MembershipTierTests} - Tests for membership tier handling and calculations</li>
 *   <li>{@link LoyaltyPointsTests} - Tests for loyalty points management</li>
 *   <li>{@link TotalSpentTests} - Tests for total spending tracking</li>
 *   <li>{@link VisitCountTests} - Tests for visit count tracking</li>
 *   <li>{@link ActiveStatusTests} - Tests for customer active status</li>
 *   <li>{@link DateTests} - Tests for various date fields</li>
 *   <li>{@link ConstructorTests} - Tests for constructors</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see Customer
 * @see MembershipTier
 */
public class CustomerTest {
    
    /** The test customer instance used across test methods. */
    private Customer customer;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh Customer instance for each test case.
     */
    @BeforeEach
    void setUp() {
        customer = new Customer();
    }
    
    /**
     * Nested test class for basic customer property tests.
     * 
     * <p>Validates getter and setter functionality for core customer properties
     * including customer ID, full name, email, phone, date of birth, address, and notes.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that customer ID can be set and retrieved correctly.
         * Verifies the setCustomerId and getCustomerId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get customer ID")
        void testCustomerId() {
            customer.setCustomerId(1);
            assertEquals(1, customer.getCustomerId());
        }
        
        /**
         * Tests that full name can be set and retrieved correctly.
         * Full name is the customer's complete name for display purposes.
         */
        @Test
        @DisplayName("Should set and get full name")
        void testFullName() {
            customer.setFullName("John Doe");
            assertEquals("John Doe", customer.getFullName());
        }
        
        /**
         * Tests that email can be set and retrieved correctly.
         * Email is used for customer communications and notifications.
         */
        @Test
        @DisplayName("Should set and get email")
        void testEmail() {
            customer.setEmail("john@example.com");
            assertEquals("john@example.com", customer.getEmail());
        }
        
        /**
         * Tests that phone number can be set and retrieved correctly.
         * Phone number is used for reservations and contact purposes.
         */
        @Test
        @DisplayName("Should set and get phone")
        void testPhone() {
            customer.setPhone("1234567890");
            assertEquals("1234567890", customer.getPhone());
        }
        
        /**
         * Tests that date of birth can be set and retrieved correctly.
         * Date of birth is used for birthday promotions and age verification.
         */
        @Test
        @DisplayName("Should set and get date of birth")
        void testDateOfBirth() {
            LocalDate dob = LocalDate.of(1990, 1, 15);
            customer.setDateOfBirth(dob);
            assertEquals(dob, customer.getDateOfBirth());
        }
        
        /**
         * Tests that address can be set and retrieved correctly.
         * Address stores the customer's mailing or delivery address.
         */
        @Test
        @DisplayName("Should set and get address")
        void testAddress() {
            customer.setAddress("123 Main St");
            assertEquals("123 Main St", customer.getAddress());
        }
        
        /**
         * Tests that notes can be set and retrieved correctly.
         * Notes store additional information about the customer.
         */
        @Test
        @DisplayName("Should set and get notes")
        void testNotes() {
            customer.setNotes("VIP customer");
            assertEquals("VIP customer", customer.getNotes());
        }
    }
    
    /**
     * Nested test class for membership tier tests.
     * 
     * <p>Validates MembershipTier enum functionality including default values,
     * display names, discount percentages, and tier calculation from spending.</p>
     */
    @Nested
    @DisplayName("Membership Tier Tests")
    class MembershipTierTests {
        
        /**
         * Tests that new customers default to BRONZE membership tier.
         * Verifies the initial membership status for new customers.
         */
        @Test
        @DisplayName("Should default to BRONZE membership")
        void testDefaultMembership() {
            assertEquals(MembershipTier.BRONZE, customer.getMembershipTier());
        }
        
        /**
         * Tests that membership tier can be set and retrieved correctly.
         * Verifies the setMembershipTier and getMembershipTier methods.
         */
        @Test
        @DisplayName("Should set and get membership tier")
        void testSetMembershipTier() {
            customer.setMembershipTier(MembershipTier.GOLD);
            assertEquals(MembershipTier.GOLD, customer.getMembershipTier());
        }
        
        /**
         * Tests that all MembershipTier enum values have correct display names.
         * Verifies human-readable names for UI display purposes.
         */
        @Test
        @DisplayName("MembershipTier enum should have correct display names")
        void testMembershipTierDisplayNames() {
            assertEquals("Bronze", MembershipTier.BRONZE.getDisplayName());
            assertEquals("Silver", MembershipTier.SILVER.getDisplayName());
            assertEquals("Gold", MembershipTier.GOLD.getDisplayName());
            assertEquals("Platinum", MembershipTier.PLATINUM.getDisplayName());
        }
        
        /**
         * Tests that all MembershipTier enum values have correct discount percentages.
         * Verifies the discount rates: Bronze=5%, Silver=8%, Gold=12%, Platinum=15%.
         */
        @Test
        @DisplayName("MembershipTier enum should have correct discount percentages")
        void testMembershipTierDiscounts() {
            assertEquals(5, MembershipTier.BRONZE.getDiscountPercentage());
            assertEquals(8, MembershipTier.SILVER.getDiscountPercentage());
            assertEquals(12, MembershipTier.GOLD.getDiscountPercentage());
            assertEquals(15, MembershipTier.PLATINUM.getDiscountPercentage());
        }
        
        /**
         * Tests that MembershipTier.fromString works with enum name.
         * Verifies parsing membership tier from uppercase enum name.
         */
        @Test
        @DisplayName("MembershipTier fromString should work with enum name")
        void testMembershipTierFromStringEnumName() {
            assertEquals(MembershipTier.GOLD, MembershipTier.fromString("GOLD"));
        }
        
        /**
         * Tests that MembershipTier.fromString works with display name.
         * Verifies parsing membership tier from display name format.
         */
        @Test
        @DisplayName("MembershipTier fromString should work with display name")
        void testMembershipTierFromStringDisplayName() {
            assertEquals(MembershipTier.GOLD, MembershipTier.fromString("Gold"));
        }
        
        /**
         * Tests that MembershipTier.fromString defaults to BRONZE for invalid input.
         * Verifies graceful handling of unrecognized tier names.
         */
        @Test
        @DisplayName("MembershipTier fromString should default to BRONZE for invalid")
        void testMembershipTierFromStringInvalid() {
            assertEquals(MembershipTier.BRONZE, MembershipTier.fromString("Invalid"));
        }
        
        /**
         * Tests that MembershipTier.fromSpending returns correct tier based on total spent.
         * Verifies tier thresholds: Bronze(&lt;10000), Silver(&lt;25000), Gold(&lt;50000), Platinum(≥50000).
         */
        @Test
        @DisplayName("MembershipTier fromSpending should return correct tier")
        void testMembershipTierFromSpending() {
            assertEquals(MembershipTier.BRONZE, MembershipTier.fromSpending(new BigDecimal("5000")));
            assertEquals(MembershipTier.SILVER, MembershipTier.fromSpending(new BigDecimal("15000")));
            assertEquals(MembershipTier.GOLD, MembershipTier.fromSpending(new BigDecimal("40000")));
            assertEquals(MembershipTier.PLATINUM, MembershipTier.fromSpending(new BigDecimal("80000")));
        }
    }
    
    /**
     * Nested test class for loyalty points tests.
     * 
     * <p>Validates loyalty points tracking including default values
     * and setting/getting point values.</p>
     */
    @Nested
    @DisplayName("Loyalty Points Tests")
    class LoyaltyPointsTests {
        
        /**
         * Tests that loyalty points default to 0 for new customers.
         * Verifies proper initialization of loyalty points.
         */
        @Test
        @DisplayName("Should default loyalty points to 0")
        void testDefaultLoyaltyPoints() {
            assertEquals(0, customer.getLoyaltyPoints());
        }
        
        /**
         * Tests that loyalty points can be set and retrieved correctly.
         * Verifies the setLoyaltyPoints and getLoyaltyPoints methods.
         */
        @Test
        @DisplayName("Should set and get loyalty points")
        void testSetLoyaltyPoints() {
            customer.setLoyaltyPoints(500);
            assertEquals(500, customer.getLoyaltyPoints());
        }
    }
    
    /**
     * Nested test class for total spent tests.
     * 
     * <p>Validates total spending tracking including default values
     * and setting/getting spending amounts.</p>
     */
    @Nested
    @DisplayName("Total Spent Tests")
    class TotalSpentTests {
        
        /**
         * Tests that total spent defaults to zero for new customers.
         * Verifies proper initialization of spending tracking.
         */
        @Test
        @DisplayName("Should default total spent to zero")
        void testDefaultTotalSpent() {
            assertEquals(BigDecimal.ZERO, customer.getTotalSpent());
        }
        
        /**
         * Tests that total spent can be set and retrieved correctly.
         * Verifies the setTotalSpent and getTotalSpent methods.
         */
        @Test
        @DisplayName("Should set and get total spent")
        void testSetTotalSpent() {
            BigDecimal spent = new BigDecimal("1500.00");
            customer.setTotalSpent(spent);
            assertEquals(spent, customer.getTotalSpent());
        }
    }
    
    /**
     * Nested test class for visit count tests.
     * 
     * <p>Validates visit count tracking including default values
     * and setting/getting visit counts.</p>
     */
    @Nested
    @DisplayName("Visit Count Tests")
    class VisitCountTests {
        
        /**
         * Tests that visit count defaults to 0 for new customers.
         * Verifies proper initialization of visit tracking.
         */
        @Test
        @DisplayName("Should default visit count to 0")
        void testDefaultVisitCount() {
            assertEquals(0, customer.getVisitCount());
        }
        
        /**
         * Tests that visit count can be set and retrieved correctly.
         * Verifies the setVisitCount and getVisitCount methods.
         */
        @Test
        @DisplayName("Should set and get visit count")
        void testSetVisitCount() {
            customer.setVisitCount(10);
            assertEquals(10, customer.getVisitCount());
        }
    }
    
    /**
     * Nested test class for active status tests.
     * 
     * <p>Validates customer active status including default values
     * and setting active/inactive status.</p>
     */
    @Nested
    @DisplayName("Active Status Tests")
    class ActiveStatusTests {
        
        /**
         * Tests that new customers default to active status.
         * Verifies the default active state for new customers.
         */
        @Test
        @DisplayName("Should default to active")
        void testDefaultActive() {
            assertTrue(customer.isActive());
        }
        
        /**
         * Tests that active status can be set to false.
         * Verifies the setActive method for deactivating customers.
         */
        @Test
        @DisplayName("Should set active to false")
        void testSetActiveFalse() {
            customer.setActive(false);
            assertFalse(customer.isActive());
        }
    }
    
    /**
     * Nested test class for date-related property tests.
     * 
     * <p>Validates various date fields including last visit date,
     * registration date, created at, and updated at timestamps.</p>
     */
    @Nested
    @DisplayName("Date Tests")
    class DateTests {
        
        /**
         * Tests that last visit date can be set and retrieved correctly.
         * Last visit date tracks when the customer last visited the restaurant.
         */
        @Test
        @DisplayName("Should set and get last visit date")
        void testLastVisitDate() {
            LocalDate date = LocalDate.now();
            customer.setLastVisitDate(date);
            assertEquals(date, customer.getLastVisitDate());
        }
        
        /**
         * Tests that registration date can be set and retrieved correctly.
         * Registration date records when the customer first registered.
         */
        @Test
        @DisplayName("Should set and get registration date")
        void testRegistrationDate() {
            LocalDateTime now = LocalDateTime.now();
            customer.setRegistrationDate(now);
            assertEquals(now, customer.getRegistrationDate());
        }
        
        /**
         * Tests that created at timestamp can be set and retrieved correctly.
         * Created at records when the customer record was created.
         */
        @Test
        @DisplayName("Should set and get created at")
        void testCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            customer.setCreatedAt(now);
            assertEquals(now, customer.getCreatedAt());
        }
        
        /**
         * Tests that updated at timestamp can be set and retrieved correctly.
         * Updated at records when the customer record was last modified.
         */
        @Test
        @DisplayName("Should set and get updated at")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            customer.setUpdatedAt(now);
            assertEquals(now, customer.getUpdatedAt());
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of Customer objects through
     * the default constructor including all default values.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor properly initializes all fields.
         * Verifies membership tier, loyalty points, total spent, visit count, and active status.
         */
        @Test
        @DisplayName("Default constructor should initialize properly")
        void testDefaultConstructor() {
            Customer newCustomer = new Customer();
            
            assertNotNull(newCustomer);
            assertEquals(MembershipTier.BRONZE, newCustomer.getMembershipTier());
            assertEquals(0, newCustomer.getLoyaltyPoints());
            assertEquals(BigDecimal.ZERO, newCustomer.getTotalSpent());
            assertEquals(0, newCustomer.getVisitCount());
            assertTrue(newCustomer.isActive());
        }
    }
}
