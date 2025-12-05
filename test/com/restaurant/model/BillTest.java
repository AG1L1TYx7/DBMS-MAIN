package com.restaurant.model;

import com.restaurant.model.Bill.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Bill} model class.
 * 
 * <p>This test class provides comprehensive coverage of the Bill model's functionality,
 * including property getters/setters, payment methods, order item management,
 * total calculations, and timestamp handling.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic bill properties</li>
 *   <li>{@link AmountTests} - Tests for monetary amounts</li>
 *   <li>{@link PaymentMethodTests} - Tests for payment method handling</li>
 *   <li>{@link OrderItemsTests} - Tests for order item management</li>
 *   <li>{@link CalculateTotalsTests} - Tests for total calculations</li>
 *   <li>{@link TimestampTests} - Tests for timestamp handling</li>
 *   <li>{@link ConstructorTests} - Tests for constructors</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see Bill
 * @see PaymentMethod
 */
public class BillTest {
    
    /** The test bill instance used across test methods. */
    private Bill bill;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh Bill instance for each test case.
     */
    @BeforeEach
    void setUp() {
        bill = new Bill();
    }
    
    /**
     * Nested test class for basic bill property tests.
     * 
     * <p>Validates getter and setter functionality for core bill properties
     * including bill ID, bill number, user ID, billed by user, table number,
     * payment status, and notes.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that bill ID can be set and retrieved correctly.
         * Verifies the setBillId and getBillId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get bill ID")
        void testBillId() {
            bill.setBillId(1);
            assertEquals(1, bill.getBillId());
        }
        
        /**
         * Tests that a bill number is automatically generated on construction.
         * Verifies the bill number is not null and starts with "BILL-" prefix.
         */
        @Test
        @DisplayName("Should generate bill number on construction")
        void testBillNumberGenerated() {
            assertNotNull(bill.getBillNumber());
            assertTrue(bill.getBillNumber().startsWith("BILL-"));
        }
        
        /**
         * Tests that bill number can be manually set and retrieved.
         * Verifies the setBillNumber and getBillNumber methods.
         */
        @Test
        @DisplayName("Should set and get bill number")
        void testSetBillNumber() {
            bill.setBillNumber("BILL-123456");
            assertEquals("BILL-123456", bill.getBillNumber());
        }
        
        /**
         * Tests that user ID can be set and retrieved correctly.
         * User ID identifies which user created the bill.
         */
        @Test
        @DisplayName("Should set and get user ID")
        void testUserId() {
            bill.setUserId(5);
            assertEquals(5, bill.getUserId());
        }
        
        /**
         * Tests that the billed by user name can be set and retrieved.
         * This stores the display name of the user who created the bill.
         */
        @Test
        @DisplayName("Should set and get billed by user")
        void testBilledByUser() {
            bill.setBilledByUser("John Doe");
            assertEquals("John Doe", bill.getBilledByUser());
        }
        
        /**
         * Tests that table number can be set and retrieved correctly.
         * Table number identifies which table the bill is associated with.
         */
        @Test
        @DisplayName("Should set and get table number")
        void testTableNumber() {
            bill.setTableNumber("T5");
            assertEquals("T5", bill.getTableNumber());
        }
        
        /**
         * Tests that payment status can be set and retrieved.
         * Payment status indicates whether the bill has been paid.
         */
        @Test
        @DisplayName("Should set and get payment status")
        void testPaymentStatus() {
            bill.setPaymentStatus("PAID");
            assertEquals("PAID", bill.getPaymentStatus());
        }
        
        /**
         * Tests that notes can be set and retrieved correctly.
         * Notes provide additional information about the bill.
         */
        @Test
        @DisplayName("Should set and get notes")
        void testNotes() {
            bill.setNotes("Customer requested receipt");
            assertEquals("Customer requested receipt", bill.getNotes());
        }
    }
    
    /**
     * Nested test class for monetary amount tests.
     * 
     * <p>Validates getter and setter functionality for all amount-related
     * properties including net amount, tax amount, total amount, cash received,
     * change amount, and total items count.</p>
     */
    @Nested
    @DisplayName("Amount Tests")
    class AmountTests {
        
        /**
         * Tests that net amount can be set and retrieved correctly.
         * Net amount is the subtotal before tax.
         */
        @Test
        @DisplayName("Should set and get net amount")
        void testNetAmount() {
            BigDecimal amount = new BigDecimal("50.00");
            bill.setNetAmount(amount);
            assertEquals(amount, bill.getNetAmount());
        }
        
        /**
         * Tests that tax amount can be set and retrieved correctly.
         * Tax amount represents the calculated tax on the order.
         */
        @Test
        @DisplayName("Should set and get tax amount")
        void testTaxAmount() {
            BigDecimal tax = new BigDecimal("5.00");
            bill.setTaxAmount(tax);
            assertEquals(tax, bill.getTaxAmount());
        }
        
        /**
         * Tests that tax amount is initialized to zero.
         * Ensures proper default value for tax amount on new bills.
         */
        @Test
        @DisplayName("Should initialize tax amount to zero")
        void testTaxAmountInitializedToZero() {
            assertEquals(BigDecimal.ZERO, bill.getTaxAmount());
        }
        
        /**
         * Tests that total amount can be set and retrieved correctly.
         * Total amount is the final amount including tax.
         */
        @Test
        @DisplayName("Should set and get total amount")
        void testTotalAmount() {
            BigDecimal total = new BigDecimal("55.00");
            bill.setTotalAmount(total);
            assertEquals(total, bill.getTotalAmount());
        }
        
        /**
         * Tests that cash received can be set and retrieved correctly.
         * Cash received tracks the amount of cash given by the customer.
         */
        @Test
        @DisplayName("Should set and get cash received")
        void testCashReceived() {
            BigDecimal cash = new BigDecimal("60.00");
            bill.setCashReceived(cash);
            assertEquals(cash, bill.getCashReceived());
        }
        
        /**
         * Tests that change amount can be set and retrieved correctly.
         * Change amount is the amount to return to the customer.
         */
        @Test
        @DisplayName("Should set and get change amount")
        void testChangeAmount() {
            BigDecimal change = new BigDecimal("5.00");
            bill.setChangeAmount(change);
            assertEquals(change, bill.getChangeAmount());
        }
        
        /**
         * Tests that total items count can be set and retrieved correctly.
         * Total items represents the sum of quantities of all order items.
         */
        @Test
        @DisplayName("Should set and get total items")
        void testTotalItems() {
            bill.setTotalItems(3);
            assertEquals(3, bill.getTotalItems());
        }
    }
    
    /**
     * Nested test class for payment method tests.
     * 
     * <p>Validates the PaymentMethod enum functionality including default
     * values, setting different payment methods, and display name retrieval.</p>
     */
    @Nested
    @DisplayName("Payment Method Tests")
    class PaymentMethodTests {
        
        /**
         * Tests that the default payment method is CASH.
         * Verifies new bills default to cash payment.
         */
        @Test
        @DisplayName("Should default to CASH payment method")
        void testDefaultPaymentMethod() {
            assertEquals(PaymentMethod.CASH, bill.getPaymentMethod());
        }
        
        /**
         * Tests setting and getting CARD payment method.
         * Verifies credit/debit card payment can be set.
         */
        @Test
        @DisplayName("Should set and get CARD payment method")
        void testCardPaymentMethod() {
            bill.setPaymentMethod(PaymentMethod.CARD);
            assertEquals(PaymentMethod.CARD, bill.getPaymentMethod());
        }
        
        /**
         * Tests setting and getting DIGITAL_WALLET payment method.
         * Verifies digital wallet payments (e.g., Apple Pay) can be set.
         */
        @Test
        @DisplayName("Should set and get DIGITAL_WALLET payment method")
        void testDigitalWalletPaymentMethod() {
            bill.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
            assertEquals(PaymentMethod.DIGITAL_WALLET, bill.getPaymentMethod());
        }
        
        /**
         * Tests setting and getting UPI payment method.
         * Verifies UPI (Unified Payments Interface) payment can be set.
         */
        @Test
        @DisplayName("Should set and get UPI payment method")
        void testUpiPaymentMethod() {
            bill.setPaymentMethod(PaymentMethod.UPI);
            assertEquals(PaymentMethod.UPI, bill.getPaymentMethod());
        }
        
        /**
         * Tests that all PaymentMethod enum values have correct display names.
         * Verifies human-readable names for UI display purposes.
         */
        @Test
        @DisplayName("PaymentMethod enum should have correct display names")
        void testPaymentMethodDisplayNames() {
            assertEquals("Cash", PaymentMethod.CASH.getDisplayName());
            assertEquals("Card", PaymentMethod.CARD.getDisplayName());
            assertEquals("Digital Wallet", PaymentMethod.DIGITAL_WALLET.getDisplayName());
            assertEquals("UPI", PaymentMethod.UPI.getDisplayName());
        }
    }
    
    /**
     * Nested test class for order items tests.
     * 
     * <p>Validates order item management including initialization,
     * setting order items list, and adding individual items.</p>
     */
    @Nested
    @DisplayName("Order Items Tests")
    class OrderItemsTests {
        
        /**
         * Tests that order items list is initialized as empty.
         * Ensures new bills have an empty but non-null order items list.
         */
        @Test
        @DisplayName("Should initialize order items as empty list")
        void testOrderItemsInitialized() {
            assertNotNull(bill.getOrderItems());
            assertTrue(bill.getOrderItems().isEmpty());
        }
        
        /**
         * Tests that a list of order items can be set and retrieved.
         * Verifies multiple order items can be associated with a bill.
         */
        @Test
        @DisplayName("Should set and get order items")
        void testSetOrderItems() {
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(1, "Burger", 2, new BigDecimal("10.00")));
            items.add(new OrderItem(2, "Fries", 1, new BigDecimal("5.00")));
            
            bill.setOrderItems(items);
            assertEquals(2, bill.getOrderItems().size());
        }
        
        /**
         * Tests adding a single order item to the bill's order items list.
         * Verifies items can be added directly to the order items collection.
         */
        @Test
        @DisplayName("Should add order item")
        void testAddOrderItem() {
            OrderItem item = new OrderItem(1, "Burger", 2, new BigDecimal("10.00"));
            bill.getOrderItems().add(item);
            assertEquals(1, bill.getOrderItems().size());
        }
    }
    
    /**
     * Nested test class for total calculation tests.
     * 
     * <p>Validates the calculateTotals method which computes net amount,
     * tax amount, total amount, and total items from the order items list.</p>
     */
    @Nested
    @DisplayName("Calculate Totals Tests")
    class CalculateTotalsTests {
        
        /**
         * Tests that calculateTotals correctly computes all amounts.
         * Verifies net amount (25.00), total items (3), tax (2.50), and total (27.50)
         * for order items: 2x Burger @ 10.00 + 1x Fries @ 5.00.
         */
        @Test
        @DisplayName("Should calculate totals from order items")
        void testCalculateTotals() {
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(1, "Burger", 2, new BigDecimal("10.00")));  // 20.00
            items.add(new OrderItem(2, "Fries", 1, new BigDecimal("5.00")));     // 5.00
            bill.setOrderItems(items);
            
            bill.calculateTotals();
            
            // Net amount should be 25.00
            assertEquals(0, new BigDecimal("25.00").compareTo(bill.getNetAmount()));
            // Total items should be 3
            assertEquals(3, bill.getTotalItems());
            // Tax should be 10% = 2.50
            assertEquals(0, new BigDecimal("2.50").compareTo(bill.getTaxAmount()));
            // Total should be 27.50
            assertEquals(0, new BigDecimal("27.50").compareTo(bill.getTotalAmount()));
        }
        
        /**
         * Tests that calculateTotals handles empty order items list.
         * Verifies all amounts are zero when there are no order items.
         */
        @Test
        @DisplayName("Should calculate totals with empty order items")
        void testCalculateTotalsEmpty() {
            bill.calculateTotals();
            
            assertEquals(0, BigDecimal.ZERO.compareTo(bill.getNetAmount()));
            assertEquals(0, bill.getTotalItems());
            assertEquals(0, BigDecimal.ZERO.compareTo(bill.getTaxAmount()));
            assertEquals(0, BigDecimal.ZERO.compareTo(bill.getTotalAmount()));
        }
    }
    
    /**
     * Nested test class for timestamp tests.
     * 
     * <p>Validates timestamp handling including automatic billedAt
     * initialization and manual timestamp setting.</p>
     */
    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {
        
        /**
         * Tests that billedAt timestamp is set automatically on construction.
         * Verifies new bills have a non-null billing timestamp.
         */
        @Test
        @DisplayName("Should set billedAt on construction")
        void testBilledAtOnConstruction() {
            assertNotNull(bill.getBilledAt());
        }
        
        /**
         * Tests that billedAt timestamp can be manually set and retrieved.
         * Verifies the setBilledAt and getBilledAt methods.
         */
        @Test
        @DisplayName("Should set and get billedAt")
        void testSetBilledAt() {
            LocalDateTime now = LocalDateTime.now();
            bill.setBilledAt(now);
            assertEquals(now, bill.getBilledAt());
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of Bill objects through
     * the default constructor including all default values.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor properly initializes all fields.
         * Verifies billedAt, billNumber, payment method, order items, and tax amount.
         */
        @Test
        @DisplayName("Default constructor should initialize properly")
        void testDefaultConstructor() {
            Bill newBill = new Bill();
            
            assertNotNull(newBill);
            assertNotNull(newBill.getBilledAt());
            assertNotNull(newBill.getBillNumber());
            assertTrue(newBill.getBillNumber().startsWith("BILL-"));
            assertEquals(PaymentMethod.CASH, newBill.getPaymentMethod());
            assertNotNull(newBill.getOrderItems());
            assertEquals(BigDecimal.ZERO, newBill.getTaxAmount());
        }
    }
}
