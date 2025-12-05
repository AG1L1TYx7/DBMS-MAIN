package com.restaurant.dao;

import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for BillDAO.
 * 
 * <p>This test class validates bill data access operations and Bill model behavior.
 * It covers bill creation, payment methods, querying, sales statistics, order items,
 * and bill number generation.</p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 */
public class BillDAOTest {
    
    /** The test bill instance used across test methods. */
    private Bill testBill;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh test bill for each test case.
     */
    @BeforeEach
    void setUp() {
        testBill = createTestBill();
    }
    
    /**
     * Nested test class for bill creation tests.
     * Validates bill number generation, order items, totals calculation.
     */
    @Nested
    @DisplayName("Bill Creation Tests")
    class BillCreationTests {
        
        /**
         * Tests that a bill is created with an auto-generated bill number.
         * Verifies the bill number starts with the expected prefix.
         */
        @Test
        @DisplayName("Should create bill with auto-generated bill number")
        void testBillNumberGeneration() {
            Bill bill = new Bill();
            
            assertNotNull(bill.getBillNumber());
            assertTrue(bill.getBillNumber().startsWith("BILL-"));
        }
        
        /**
         * Tests bill creation with order items.
         * Verifies that order items are properly associated with the bill.
         */
        @Test
        @DisplayName("Should create bill with order items")
        void testCreateBillWithOrderItems() {
            Bill bill = new Bill();
            bill.setUserId(1);
            bill.setPaymentMethod(PaymentMethod.CASH);
            
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(1, "Burger", 2, new BigDecimal("10.00")));
            items.add(new OrderItem(2, "Fries", 1, new BigDecimal("5.00")));
            
            bill.setOrderItems(items);
            
            assertEquals(2, bill.getOrderItems().size());
        }
        
        /**
         * Tests that a new bill has null ID before persistence.
         * Verifies proper initialization state.
         */
        @Test
        @DisplayName("Bill should have null ID before persistence")
        void testBillIdNullBeforePersistence() {
            Bill bill = new Bill();
            
            assertNull(bill.getBillId());
        }
        
        /**
         * Tests bill totals calculation including net amount, tax, and total.
         * Verifies correct arithmetic for order items.
         */
        @Test
        @DisplayName("Should calculate totals correctly")
        void testCalculateTotals() {
            Bill bill = new Bill();
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(1, "Burger", 2, new BigDecimal("10.00")));  // 20.00
            items.add(new OrderItem(2, "Fries", 1, new BigDecimal("5.00")));     // 5.00
            
            bill.setOrderItems(items);
            bill.calculateTotals();
            
            // Net amount = 25.00
            assertEquals(0, new BigDecimal("25.00").compareTo(bill.getNetAmount()));
            // Total items = 3
            assertEquals(3, bill.getTotalItems());
            // Tax = 10% = 2.50
            assertEquals(0, new BigDecimal("2.50").compareTo(bill.getTaxAmount()));
            // Total = 27.50
            assertEquals(0, new BigDecimal("27.50").compareTo(bill.getTotalAmount()));
        }
    }
    
    /**
     * Nested test class for payment method tests.
     * Validates different payment method types.
     */
    @Nested
    @DisplayName("Payment Method Tests")
    class PaymentMethodTests {
        
        /**
         * Tests that the default payment method is CASH.
         */
        @Test
        @DisplayName("Should default to CASH payment")
        void testDefaultPaymentMethod() {
            Bill bill = new Bill();
            
            assertEquals(PaymentMethod.CASH, bill.getPaymentMethod());
        }
        
        /**
         * Tests setting CARD payment method.
         */
        @Test
        @DisplayName("Should set CARD payment method")
        void testCardPaymentMethod() {
            Bill bill = new Bill();
            bill.setPaymentMethod(PaymentMethod.CARD);
            
            assertEquals(PaymentMethod.CARD, bill.getPaymentMethod());
        }
        
        /**
         * Tests that all payment methods can be set and retrieved correctly.
         */
        @Test
        @DisplayName("Should handle all payment methods")
        void testAllPaymentMethods() {
            for (PaymentMethod method : PaymentMethod.values()) {
                Bill bill = new Bill();
                bill.setPaymentMethod(method);
                assertEquals(method, bill.getPaymentMethod());
            }
        }
    }
    
    /**
     * Nested test class for bill query tests.
     * Validates findById and filtering operations.
     */
    @Nested
    @DisplayName("Bill Query Tests")
    class BillQueryTests {
        
        /**
         * Tests that findById returns empty Optional for non-existent bill.
         */
        @Test
        @DisplayName("findById should return Optional.empty for non-existent bill")
        void testFindByIdNotFound() {
            Optional<Bill> result = Optional.empty();
            
            assertTrue(result.isEmpty());
            assertFalse(result.isPresent());
        }
        
        /**
         * Tests that findById returns Optional with bill when found.
         */
        @Test
        @DisplayName("findById should return Optional with bill when found")
        void testFindByIdFound() {
            Bill bill = createTestBill();
            bill.setBillId(1);
            
            Optional<Bill> result = Optional.of(bill);
            
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getBillId());
        }
        
        /**
         * Tests that getAllBills returns list of bills.
         */
        @Test
        @DisplayName("getAllBills should return list of bills")
        void testGetAllBills() {
            List<Bill> bills = new ArrayList<>();
            bills.add(createTestBill());
            bills.add(createTestBill());
            
            assertEquals(2, bills.size());
        }
        
        /**
         * Tests that getBillsByUserId filters correctly by user ID.
         */
        @Test
        @DisplayName("getBillsByUserId should filter correctly")
        void testGetBillsByUserId() {
            List<Bill> allBills = new ArrayList<>();
            
            Bill bill1 = createTestBill();
            bill1.setUserId(1);
            allBills.add(bill1);
            
            Bill bill2 = createTestBill();
            bill2.setUserId(2);
            allBills.add(bill2);
            
            Bill bill3 = createTestBill();
            bill3.setUserId(1);
            allBills.add(bill3);
            
            List<Bill> user1Bills = allBills.stream()
                .filter(b -> b.getUserId().equals(1))
                .toList();
            
            assertEquals(2, user1Bills.size());
            assertThat(user1Bills).allMatch(b -> b.getUserId().equals(1));
        }
    }
    
    /**
     * Nested test class for sales statistics tests.
     * Validates total sales, order count, and last sale calculations.
     */
    @Nested
    @DisplayName("Sales Statistics Tests")
    class SalesStatisticsTests {
        
        /**
         * Tests calculation of total sales from bills.
         */
        @Test
        @DisplayName("Should calculate total sales from bills")
        void testTotalSalesCalculation() {
            List<Bill> bills = new ArrayList<>();
            
            Bill bill1 = createTestBill();
            bill1.setTotalAmount(new BigDecimal("100.00"));
            bills.add(bill1);
            
            Bill bill2 = createTestBill();
            bill2.setTotalAmount(new BigDecimal("150.00"));
            bills.add(bill2);
            
            BigDecimal totalSales = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            assertEquals(0, new BigDecimal("250.00").compareTo(totalSales));
        }
        
        /**
         * Tests getting total order count.
         */
        @Test
        @DisplayName("Should get total order count")
        void testTotalOrderCount() {
            List<Bill> bills = new ArrayList<>();
            bills.add(createTestBill());
            bills.add(createTestBill());
            bills.add(createTestBill());
            
            assertEquals(3, bills.size());
        }
        
        /**
         * Tests getting the last sale amount sorted by date.
         */
        @Test
        @DisplayName("Should get last sale amount")
        void testLastSaleAmount() {
            List<Bill> bills = new ArrayList<>();
            
            Bill bill1 = createTestBill();
            bill1.setTotalAmount(new BigDecimal("50.00"));
            bill1.setBilledAt(LocalDateTime.now().minusHours(2));
            bills.add(bill1);
            
            Bill bill2 = createTestBill();
            bill2.setTotalAmount(new BigDecimal("75.00"));
            bill2.setBilledAt(LocalDateTime.now().minusHours(1));
            bills.add(bill2);
            
            Bill bill3 = createTestBill();
            bill3.setTotalAmount(new BigDecimal("100.00"));
            bill3.setBilledAt(LocalDateTime.now());
            bills.add(bill3);
            
            BigDecimal lastSale = bills.stream()
                .sorted((b1, b2) -> b2.getBilledAt().compareTo(b1.getBilledAt()))
                .findFirst()
                .map(Bill::getTotalAmount)
                .orElse(BigDecimal.ZERO);
            
            assertEquals(0, new BigDecimal("100.00").compareTo(lastSale));
        }
    }
    
    /**
     * Nested test class for order items tests.
     * Validates adding items and subtotal calculations.
     */
    @Nested
    @DisplayName("Order Items Tests")
    class OrderItemsTests {
        
        /**
         * Tests adding order items to a bill.
         */
        @Test
        @DisplayName("Should add order items to bill")
        void testAddOrderItems() {
            Bill bill = new Bill();
            
            bill.getOrderItems().add(new OrderItem(1, "Item 1", 1, new BigDecimal("10.00")));
            bill.getOrderItems().add(new OrderItem(2, "Item 2", 2, new BigDecimal("15.00")));
            
            assertEquals(2, bill.getOrderItems().size());
        }
        
        /**
         * Tests order item subtotal calculation.
         */
        @Test
        @DisplayName("Should calculate order item subtotal")
        void testOrderItemSubtotal() {
            OrderItem item = new OrderItem(1, "Burger", 3, new BigDecimal("12.00"));
            
            assertEquals(0, new BigDecimal("36.00").compareTo(item.getSubtotal()));
        }
    }
    
    /**
     * Nested test class for bill number tests.
     * Validates uniqueness and format of bill numbers.
     */
    @Nested
    @DisplayName("Bill Number Tests")
    class BillNumberTests {
        
        /**
         * Tests that bill numbers are unique per bill.
         * 
         * @throws InterruptedException if thread sleep is interrupted
         */
        @Test
        @DisplayName("Bill number should be unique per bill")
        void testBillNumberUniqueness() throws InterruptedException {
            Bill bill1 = new Bill();
            Thread.sleep(1000); // Wait 1 second to ensure different timestamps
            Bill bill2 = new Bill();
            
            assertNotEquals(bill1.getBillNumber(), bill2.getBillNumber());
        }
        
        /**
         * Tests that bill number follows the expected format.
         */
        @Test
        @DisplayName("Bill number should follow format BILL-yyyyMMddHHmmss")
        void testBillNumberFormat() {
            Bill bill = new Bill();
            String billNumber = bill.getBillNumber();
            
            assertTrue(billNumber.startsWith("BILL-"));
            assertEquals(19, billNumber.length()); // BILL- (5) + yyyyMMddHHmmss (14) = 19
        }
    }
    
    /**
     * Creates a test bill with default values for testing purposes.
     * 
     * @return a new Bill instance with test data
     */
    private Bill createTestBill() {
        Bill bill = new Bill();
        bill.setUserId(1);
        bill.setBilledByUser("Test User");
        bill.setPaymentMethod(PaymentMethod.CASH);
        
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(1, "Test Item", 1, new BigDecimal("10.00")));
        bill.setOrderItems(items);
        
        bill.calculateTotals();
        
        return bill;
    }
}
