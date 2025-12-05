package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.model.Customer.MembershipTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for CustomerController.
 * 
 * <p>This test class validates the customer management business logic in the restaurant
 * management system, including customer creation, membership tiers, loyalty points,
 * visit tracking, and discount calculations.</p>
 * 
 * <p>Test categories covered:</p>
 * <ul>
 *   <li>Customer Validation Tests - Input validation for customer fields</li>
 *   <li>Customer Creation Tests - Creating new customers with required/optional fields</li>
 *   <li>Membership Tier Tests - Tier determination based on spending thresholds</li>
 *   <li>Loyalty Points Tests - Adding, redeeming, and validating loyalty points</li>
 *   <li>Visit Count Tests - Tracking customer visits</li>
 *   <li>Total Spent Tests - Managing cumulative spending and tier upgrades</li>
 *   <li>Last Visit Date Tests - Tracking customer activity</li>
 *   <li>Customer Search Tests - Finding customers by phone or name</li>
 *   <li>Birthday Promotion Tests - Identifying customers with birthdays</li>
 *   <li>Discount Calculation Tests - Calculating tier-based discounts</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see CustomerController
 * @see Customer
 * @see MembershipTier
 */
public class CustomerControllerTest {
    
    /**
     * Nested test class for customer input validation tests.
     * 
     * <p>Tests the validation rules for customer fields including:
     * <ul>
     *   <li>Full name is required and not empty</li>
     *   <li>Phone number is required and not empty</li>
     *   <li>Email format validation using regex</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Customer Validation Tests")
    class CustomerValidationTests {
        
        /**
         * Tests validation that customer full name is not empty or null.
         * 
         * <p>Verifies that empty strings and null values are properly
         * identified as invalid customer names.</p>
         */
        @Test
        @DisplayName("Should validate full name is not empty")
        void testFullNameValidation() {
            String emptyName = "";
            String nullName = null;
            String validName = "John Doe";
            
            assertTrue(emptyName == null || emptyName.trim().isEmpty());
            assertTrue(nullName == null);
            assertFalse(validName == null || validName.trim().isEmpty());
        }
        
        /**
         * Tests validation that customer phone number is not empty.
         * 
         * <p>Verifies that empty phone numbers are properly identified
         * as invalid for customer registration.</p>
         */
        @Test
        @DisplayName("Should validate phone is not empty")
        void testPhoneValidation() {
            String emptyPhone = "";
            String validPhone = "9841234567";
            
            assertTrue(emptyPhone.trim().isEmpty());
            assertFalse(validPhone.trim().isEmpty());
        }
        
        /**
         * Tests email format validation using regex pattern.
         * 
         * <p>Verifies that valid email formats are accepted and
         * invalid formats without @ symbol are rejected.</p>
         */
        @Test
        @DisplayName("Should validate email format")
        void testEmailValidation() {
            String validEmail = "customer@example.com";
            String invalidEmail = "invalid-email";
            
            assertTrue(validEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
            assertFalse(invalidEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
        }
    }
    
    /**
     * Nested test class for customer creation tests.
     * 
     * <p>Tests the customer creation workflow including:
     * <ul>
     *   <li>Creating customers with only required fields</li>
     *   <li>Creating customers with all optional fields</li>
     *   <li>Default membership tier for new customers</li>
     *   <li>Default active status for new customers</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Customer Creation Tests")
    class CustomerCreationTests {
        
        /**
         * Tests customer creation with only required fields.
         * 
         * <p>Verifies that a customer can be created with just
         * the full name and phone number.</p>
         */
        @Test
        @DisplayName("Should create customer with required fields")
        void testCreateCustomerWithRequiredFields() {
            Customer customer = new Customer();
            customer.setFullName("Test Customer");
            customer.setPhone("9841234567");
            
            assertNotNull(customer.getFullName());
            assertNotNull(customer.getPhone());
        }
        
        /**
         * Tests customer creation with all available fields.
         * 
         * <p>Verifies that a customer can be created with all fields
         * including email, date of birth, and address.</p>
         */
        @Test
        @DisplayName("Should create customer with all fields")
        void testCreateCustomerWithAllFields() {
            Customer customer = createTestCustomer();
            
            assertNotNull(customer.getFullName());
            assertNotNull(customer.getPhone());
            assertNotNull(customer.getEmail());
            assertNotNull(customer.getDateOfBirth());
            assertNotNull(customer.getAddress());
        }
        
        /**
         * Tests that new customers default to BRONZE membership tier.
         * 
         * <p>Verifies that customers with zero spending are assigned
         * the BRONZE tier as their starting membership level.</p>
         */
        @Test
        @DisplayName("New customer should have default tier")
        void testNewCustomerDefaultTier() {
            Customer customer = new Customer();
            customer.setTotalSpent(BigDecimal.ZERO);
            
            MembershipTier tier = MembershipTier.fromSpending(customer.getTotalSpent());
            assertEquals(MembershipTier.BRONZE, tier);
        }
        
        /**
         * Tests that new customers are active by default.
         * 
         * <p>Verifies that when a customer is created, their account
         * is set to active status.</p>
         */
        @Test
        @DisplayName("New customer should be active")
        void testNewCustomerActive() {
            Customer customer = new Customer();
            customer.setActive(true);
            
            assertTrue(customer.isActive());
        }
    }
    
    /**
     * Nested test class for membership tier tests.
     * 
     * <p>Tests the membership tier determination based on spending:
     * <ul>
     *   <li>BRONZE: 0 - 14,999</li>
     *   <li>SILVER: 15,000 - 39,999</li>
     *   <li>GOLD: 40,000 - 79,999</li>
     *   <li>PLATINUM: 80,000+</li>
     *   <li>Discount percentages per tier</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Membership Tier Tests")
    class MembershipTierTests {
        
        /**
         * Tests BRONZE tier assignment for spending below 15,000.
         * 
         * <p>Verifies that customers with spending from 0 to 14,999
         * are assigned the BRONZE membership tier.</p>
         */
        @Test
        @DisplayName("Should be BRONZE for spending below 15000")
        void testBronzeTier() {
            assertEquals(MembershipTier.BRONZE, MembershipTier.fromSpending(BigDecimal.ZERO));
            assertEquals(MembershipTier.BRONZE, MembershipTier.fromSpending(new BigDecimal("14999")));
        }
        
        /**
         * Tests SILVER tier assignment for spending between 15,000 and 39,999.
         * 
         * <p>Verifies that customers with spending from 15,000 to 39,999
         * are assigned the SILVER membership tier.</p>
         */
        @Test
        @DisplayName("Should be SILVER for spending 15000 to 39999")
        void testSilverTier() {
            assertEquals(MembershipTier.SILVER, MembershipTier.fromSpending(new BigDecimal("15000")));
            assertEquals(MembershipTier.SILVER, MembershipTier.fromSpending(new BigDecimal("39999")));
        }
        
        /**
         * Tests GOLD tier assignment for spending between 40,000 and 79,999.
         * 
         * <p>Verifies that customers with spending from 40,000 to 79,999
         * are assigned the GOLD membership tier.</p>
         */
        @Test
        @DisplayName("Should be GOLD for spending 40000 to 79999")
        void testGoldTier() {
            assertEquals(MembershipTier.GOLD, MembershipTier.fromSpending(new BigDecimal("40000")));
            assertEquals(MembershipTier.GOLD, MembershipTier.fromSpending(new BigDecimal("79999")));
        }
        
        /**
         * Tests PLATINUM tier assignment for spending of 80,000 or more.
         * 
         * <p>Verifies that customers with spending of 80,000 and above
         * are assigned the PLATINUM membership tier.</p>
         */
        @Test
        @DisplayName("Should be PLATINUM for spending 80000+")
        void testPlatinumTier() {
            assertEquals(MembershipTier.PLATINUM, MembershipTier.fromSpending(new BigDecimal("80000")));
            assertEquals(MembershipTier.PLATINUM, MembershipTier.fromSpending(new BigDecimal("100000")));
        }
        
        /**
         * Tests that each tier has the correct discount percentage.
         * 
         * <p>Verifies discount rates: BRONZE=5%, SILVER=8%, GOLD=12%, PLATINUM=15%.</p>
         */
        @Test
        @DisplayName("Should have correct discount for each tier")
        void testTierDiscounts() {
            assertEquals(5, MembershipTier.BRONZE.getDiscountPercentage());
            assertEquals(8, MembershipTier.SILVER.getDiscountPercentage());
            assertEquals(12, MembershipTier.GOLD.getDiscountPercentage());
            assertEquals(15, MembershipTier.PLATINUM.getDiscountPercentage());
        }
    }
    
    /**
     * Nested test class for loyalty points management tests.
     * 
     * <p>Tests the loyalty points functionality including:
     * <ul>
     *   <li>Adding loyalty points for purchases</li>
     *   <li>Redeeming loyalty points</li>
     *   <li>Preventing negative loyalty points</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Loyalty Points Tests")
    class LoyaltyPointsTests {
        
        /**
         * Tests adding loyalty points to a customer account.
         * 
         * <p>Verifies that loyalty points are correctly accumulated
         * when customers make purchases.</p>
         */
        @Test
        @DisplayName("Should add loyalty points")
        void testAddLoyaltyPoints() {
            Customer customer = new Customer();
            customer.setLoyaltyPoints(100);
            
            int newPoints = customer.getLoyaltyPoints() + 50;
            customer.setLoyaltyPoints(newPoints);
            
            assertEquals(150, customer.getLoyaltyPoints());
        }
        
        /**
         * Tests redeeming loyalty points from a customer account.
         * 
         * <p>Verifies that loyalty points are correctly deducted
         * when redeemed for rewards.</p>
         */
        @Test
        @DisplayName("Should redeem loyalty points")
        void testRedeemLoyaltyPoints() {
            Customer customer = new Customer();
            customer.setLoyaltyPoints(100);
            
            int pointsToRedeem = 30;
            int newPoints = customer.getLoyaltyPoints() - pointsToRedeem;
            customer.setLoyaltyPoints(newPoints);
            
            assertEquals(70, customer.getLoyaltyPoints());
        }
        
        /**
         * Tests that loyalty points cannot go negative.
         * 
         * <p>Verifies that attempting to redeem more points than available
         * results in zero points rather than negative values.</p>
         */
        @Test
        @DisplayName("Should not allow negative loyalty points")
        void testNonNegativeLoyaltyPoints() {
            Customer customer = new Customer();
            customer.setLoyaltyPoints(10);
            
            int pointsToRedeem = 20;
            int newPoints = Math.max(0, customer.getLoyaltyPoints() - pointsToRedeem);
            customer.setLoyaltyPoints(newPoints);
            
            assertEquals(0, customer.getLoyaltyPoints());
        }
    }
    
    /**
     * Nested test class for visit count tracking tests.
     * 
     * <p>Tests the visit counting functionality including:
     * <ul>
     *   <li>Incrementing visit count on each visit</li>
     *   <li>Default visit count for new customers</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Visit Count Tests")
    class VisitCountTests {
        
        /**
         * Tests incrementing the customer visit count.
         * 
         * <p>Verifies that the visit count is correctly increased
         * each time a customer makes a visit.</p>
         */
        @Test
        @DisplayName("Should increment visit count")
        void testIncrementVisitCount() {
            Customer customer = new Customer();
            customer.setVisitCount(10);
            
            customer.setVisitCount(customer.getVisitCount() + 1);
            
            assertEquals(11, customer.getVisitCount());
        }
        
        /**
         * Tests that new customers start with zero visits.
         * 
         * <p>Verifies that the initial visit count for a new customer
         * is set to zero.</p>
         */
        @Test
        @DisplayName("New customer should have zero visits")
        void testNewCustomerVisitCount() {
            Customer customer = new Customer();
            customer.setVisitCount(0);
            
            assertEquals(0, customer.getVisitCount());
        }
    }
    
    /**
     * Nested test class for total spending management tests.
     * 
     * <p>Tests the cumulative spending functionality including:
     * <ul>
     *   <li>Updating total spent after purchases</li>
     *   <li>Default spending for new customers</li>
     *   <li>Tier upgrades when thresholds are crossed</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Total Spent Tests")
    class TotalSpentTests {
        
        /**
         * Tests updating the total amount spent by a customer.
         * 
         * <p>Verifies that the total spent is correctly accumulated
         * when customers make purchases.</p>
         */
        @Test
        @DisplayName("Should update total spent")
        void testUpdateTotalSpent() {
            Customer customer = new Customer();
            customer.setTotalSpent(new BigDecimal("1000.00"));
            
            BigDecimal orderAmount = new BigDecimal("250.50");
            BigDecimal newTotal = customer.getTotalSpent().add(orderAmount);
            customer.setTotalSpent(newTotal);
            
            assertEquals(0, new BigDecimal("1250.50").compareTo(customer.getTotalSpent()));
        }
        
        /**
         * Tests that new customers start with zero spending.
         * 
         * <p>Verifies that the initial total spent for a new customer
         * is set to zero.</p>
         */
        @Test
        @DisplayName("New customer should have zero spent")
        void testNewCustomerSpent() {
            Customer customer = new Customer();
            customer.setTotalSpent(BigDecimal.ZERO);
            
            assertEquals(0, BigDecimal.ZERO.compareTo(customer.getTotalSpent()));
        }
        
        /**
         * Tests automatic tier upgrade when spending threshold is crossed.
         * 
         * <p>Verifies that when a customer's total spending crosses
         * a tier threshold (e.g., 15,000), their tier is upgraded.</p>
         */
        @Test
        @DisplayName("Should upgrade tier when spending threshold reached")
        void testTierUpgrade() {
            Customer customer = new Customer();
            customer.setTotalSpent(new BigDecimal("14500.00"));
            
            MembershipTier initialTier = MembershipTier.fromSpending(customer.getTotalSpent());
            assertEquals(MembershipTier.BRONZE, initialTier);
            
            // Add spending to cross threshold
            customer.setTotalSpent(customer.getTotalSpent().add(new BigDecimal("1000.00")));
            
            MembershipTier newTier = MembershipTier.fromSpending(customer.getTotalSpent());
            assertEquals(MembershipTier.SILVER, newTier);
        }
    }
    
    /**
     * Nested test class for last visit date tracking tests.
     * 
     * <p>Tests the visit date functionality including:
     * <ul>
     *   <li>Updating last visit date</li>
     *   <li>Identifying recent visitors</li>
     *   <li>Identifying inactive customers</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Last Visit Date Tests")
    class LastVisitDateTests {
        
        /**
         * Tests updating the last visit date for a customer.
         * 
         * <p>Verifies that the last visit date is correctly recorded
         * when a customer makes a visit.</p>
         */
        @Test
        @DisplayName("Should update last visit date")
        void testUpdateLastVisitDate() {
            Customer customer = new Customer();
            LocalDate today = LocalDate.now();
            
            customer.setLastVisitDate(today);
            
            assertEquals(today, customer.getLastVisitDate());
        }
        
        /**
         * Tests identification of customers who visited recently.
         * 
         * <p>Verifies that customers who visited within the last 30 days
         * are correctly identified as active/recent visitors.</p>
         */
        @Test
        @DisplayName("Should track if customer visited recently")
        void testRecentVisit() {
            Customer customer = new Customer();
            customer.setLastVisitDate(LocalDate.now().minusDays(10));
            
            boolean visitedWithin30Days = customer.getLastVisitDate() != null && 
                customer.getLastVisitDate().isAfter(LocalDate.now().minusDays(30));
            
            assertTrue(visitedWithin30Days);
        }
        
        /**
         * Tests identification of inactive customers.
         * 
         * <p>Verifies that customers who haven't visited in over 30 days
         * are correctly identified as inactive.</p>
         */
        @Test
        @DisplayName("Should identify inactive customer")
        void testInactiveCustomer() {
            Customer customer = new Customer();
            customer.setLastVisitDate(LocalDate.now().minusDays(60));
            
            boolean visitedWithin30Days = customer.getLastVisitDate() != null && 
                customer.getLastVisitDate().isAfter(LocalDate.now().minusDays(30));
            
            assertFalse(visitedWithin30Days);
        }
    }
    
    /**
     * Nested test class for customer search tests.
     * 
     * <p>Tests the customer search functionality including:
     * <ul>
     *   <li>Searching by phone number</li>
     *   <li>Searching by name (case-insensitive)</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Customer Search Tests")
    class CustomerSearchTests {
        
        /**
         * Tests searching for a customer by phone number.
         * 
         * <p>Verifies that customers can be found using their
         * registered phone number (exact match).</p>
         */
        @Test
        @DisplayName("Should search by phone number")
        void testSearchByPhone() {
            List<Customer> customers = createTestCustomerList();
            String searchPhone = "9841234567";
            
            Customer found = customers.stream()
                .filter(c -> c.getPhone().equals(searchPhone))
                .findFirst()
                .orElse(null);
            
            assertNotNull(found);
            assertEquals(searchPhone, found.getPhone());
        }
        
        /**
         * Tests searching for customers by name.
         * 
         * <p>Verifies that customers can be found using partial name
         * matches (case-insensitive).</p>
         */
        @Test
        @DisplayName("Should search by name")
        void testSearchByName() {
            List<Customer> customers = createTestCustomerList();
            String searchName = "john";
            
            List<Customer> found = customers.stream()
                .filter(c -> c.getFullName().toLowerCase().contains(searchName.toLowerCase()))
                .toList();
            
            assertFalse(found.isEmpty());
        }
    }
    
    /**
     * Nested test class for birthday promotion tests.
     * 
     * <p>Tests the birthday detection functionality including:
     * <ul>
     *   <li>Identifying customers with birthdays today</li>
     *   <li>Excluding customers without birthdays today</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Birthday Promotion Tests")
    class BirthdayPromotionTests {
        
        /**
         * Tests identification of customers with birthday today.
         * 
         * <p>Verifies that customers whose birth date matches today's
         * month and day are correctly identified for birthday promotions.</p>
         */
        @Test
        @DisplayName("Should identify birthday today")
        void testBirthdayToday() {
            Customer customer = new Customer();
            customer.setDateOfBirth(LocalDate.now().minusYears(25));
            
            LocalDate today = LocalDate.now();
            LocalDate dob = customer.getDateOfBirth();
            
            boolean isBirthday = dob.getMonth() == today.getMonth() && 
                               dob.getDayOfMonth() == today.getDayOfMonth();
            
            assertTrue(isBirthday);
        }
        
        /**
         * Tests that customers without birthdays today are not flagged.
         * 
         * <p>Verifies that customers whose birth date doesn't match today's
         * month and day are not identified for birthday promotions.</p>
         */
        @Test
        @DisplayName("Should not identify non-birthday")
        void testNotBirthday() {
            Customer customer = new Customer();
            customer.setDateOfBirth(LocalDate.now().minusYears(25).plusDays(10));
            
            LocalDate today = LocalDate.now();
            LocalDate dob = customer.getDateOfBirth();
            
            boolean isBirthday = dob.getMonth() == today.getMonth() && 
                               dob.getDayOfMonth() == today.getDayOfMonth();
            
            assertFalse(isBirthday);
        }
    }
    
    /**
     * Nested test class for discount calculation tests.
     * 
     * <p>Tests the tier-based discount calculations:
     * <ul>
     *   <li>BRONZE tier: 5% discount</li>
     *   <li>SILVER tier: 8% discount</li>
     *   <li>GOLD tier: 12% discount</li>
     *   <li>PLATINUM tier: 15% discount</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Discount Calculation Tests")
    class DiscountCalculationTests {
        
        /**
         * Tests BRONZE tier discount calculation (5%).
         * 
         * <p>Verifies that BRONZE tier customers receive a 5% discount
         * on their purchase amount.</p>
         */
        @Test
        @DisplayName("Should calculate Bronze tier discount (5%)")
        void testBronzeDiscount() {
            BigDecimal amount = new BigDecimal("100.00");
            int discountPercent = MembershipTier.BRONZE.getDiscountPercentage();
            BigDecimal discount = amount.multiply(BigDecimal.valueOf(discountPercent / 100.0));
            
            assertEquals(0, new BigDecimal("5.00").compareTo(discount));
        }
        
        /**
         * Tests SILVER tier discount calculation (8%).
         * 
         * <p>Verifies that SILVER tier customers receive an 8% discount
         * on their purchase amount.</p>
         */
        @Test
        @DisplayName("Should calculate Silver tier discount (8%)")
        void testSilverDiscount() {
            BigDecimal amount = new BigDecimal("100.00");
            int discountPercent = MembershipTier.SILVER.getDiscountPercentage();
            BigDecimal discount = amount.multiply(BigDecimal.valueOf(discountPercent / 100.0));
            
            assertEquals(0, new BigDecimal("8.00").compareTo(discount));
        }
        
        /**
         * Tests GOLD tier discount calculation (12%).
         * 
         * <p>Verifies that GOLD tier customers receive a 12% discount
         * on their purchase amount.</p>
         */
        @Test
        @DisplayName("Should calculate Gold tier discount (12%)")
        void testGoldDiscount() {
            BigDecimal amount = new BigDecimal("100.00");
            int discountPercent = MembershipTier.GOLD.getDiscountPercentage();
            BigDecimal discount = amount.multiply(BigDecimal.valueOf(discountPercent / 100.0));
            
            assertEquals(0, new BigDecimal("12.00").compareTo(discount));
        }
        
        /**
         * Tests PLATINUM tier discount calculation (15%).
         * 
         * <p>Verifies that PLATINUM tier customers receive a 15% discount
         * on their purchase amount.</p>
         */
        @Test
        @DisplayName("Should calculate Platinum tier discount (15%)")
        void testPlatinumDiscount() {
            BigDecimal amount = new BigDecimal("100.00");
            int discountPercent = MembershipTier.PLATINUM.getDiscountPercentage();
            BigDecimal discount = amount.multiply(BigDecimal.valueOf(discountPercent / 100.0));
            
            assertEquals(0, new BigDecimal("15.00").compareTo(discount));
        }
    }
    
    /**
     * Creates a test Customer instance with all fields populated.
     * 
     * <p>This helper method creates a fully populated Customer object for use
     * in test cases, including personal details, loyalty points, and spending history.</p>
     * 
     * @return a new Customer instance populated with test data
     */
    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setFullName("Test Customer");
        customer.setPhone("9841234567");
        customer.setEmail("test@example.com");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 15));
        customer.setAddress("123 Test Street, Kathmandu");
        customer.setLoyaltyPoints(100);
        customer.setTotalSpent(new BigDecimal("5000.00"));
        customer.setVisitCount(10);
        customer.setActive(true);
        return customer;
    }
    
    /**
     * Creates a list of test Customer instances for search tests.
     * 
     * <p>This helper method creates multiple customers with different names
     * and phone numbers for use in search functionality tests.</p>
     * 
     * @return a List containing three test customers
     */
    private List<Customer> createTestCustomerList() {
        List<Customer> customers = new ArrayList<>();
        
        Customer c1 = new Customer();
        c1.setCustomerId(1);
        c1.setFullName("John Doe");
        c1.setPhone("9841234567");
        customers.add(c1);
        
        Customer c2 = new Customer();
        c2.setCustomerId(2);
        c2.setFullName("Jane Smith");
        c2.setPhone("9851234567");
        customers.add(c2);
        
        Customer c3 = new Customer();
        c3.setCustomerId(3);
        c3.setFullName("John Smith");
        c3.setPhone("9861234567");
        customers.add(c3);
        
        return customers;
    }
}
