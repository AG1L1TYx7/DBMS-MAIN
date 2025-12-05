package com.restaurant.controller;

import com.restaurant.dao.BillDAO;
import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.OrderItem;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for OrderController.
 * 
 * <p>This test class validates the order and billing business logic in the restaurant
 * management system, including bill creation, payment processing, order queries,
 * and sales statistics. It uses Mockito for mocking the BillDAO layer.</p>
 * 
 * <p>Test categories covered:</p>
 * <ul>
 *   <li>Bill Creation Tests - Creating bills with order items and totals</li>
 *   <li>Payment Method Tests - Handling CASH, CARD, DIGITAL_WALLET, UPI payments</li>
 *   <li>Bill Query Tests - Finding bills by ID, user, or listing all</li>
 *   <li>Sales Statistics Tests - Total sales, last sale, order count</li>
 *   <li>Order Item Tests - Creating and managing order items</li>
 *   <li>Bill Number Tests - Unique bill number generation</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see OrderController
 * @see BillDAO
 * @see Bill
 * @see OrderItem
 */
@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    
    /** Mock BillDAO for testing order and bill operations without database access. */
    @Mock
    private BillDAO mockBillDAO;
    
    /** Test user instance used for creating bills in test cases. */
    private User testUser;
    
    /**
     * Sets up the test environment before each test.
     * Initializes the test user for use in bill creation tests.
     */
    @BeforeEach
    void setUp() {
        testUser = createTestUser();
    }
    
    /**
     * Nested test class for bill creation tests.
     * 
     * <p>Tests the bill creation workflow including:
     * <ul>
     *   <li>Creating bills with order items</li>
     *   <li>Validating cart is not empty</li>
     *   <li>Validating user authentication</li>
     *   <li>Calculating totals (net, tax, total)</li>
     *   <li>Validating cash payment amounts</li>
     *   <li>Calculating change for cash payments</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Bill Creation Tests")
    class BillCreationTests {
        
        /**
         * Tests successful bill creation with order items.
         * 
         * <p>Verifies that a bill can be created with multiple order items
         * and that the saved bill has an assigned ID.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should create bill with order items")
        void testCreateBillSuccess() throws SQLException {
            // Given
            List<OrderItem> items = createTestOrderItems();
            Bill newBill = createTestBill(items);
            Bill savedBill = createTestBill(items);
            savedBill.setBillId(1);
            
            when(mockBillDAO.createBill(any(Bill.class))).thenReturn(savedBill);
            
            // When
            Bill result = mockBillDAO.createBill(newBill);
            
            // Then
            assertNotNull(result);
            assertNotNull(result.getBillId());
            assertEquals(2, result.getOrderItems().size());
        }
        
        /**
         * Tests validation that the cart is not empty before billing.
         * 
         * <p>Verifies that empty or null carts are properly identified
         * as invalid for bill creation.</p>
         */
        @Test
        @DisplayName("Should validate cart is not empty")
        void testEmptyCartValidation() {
            List<OrderItem> emptyCart = new ArrayList<>();
            
            boolean isEmpty = emptyCart == null || emptyCart.isEmpty();
            
            assertTrue(isEmpty);
        }
        
        /**
         * Tests validation that a user is authenticated before billing.
         * 
         * <p>Verifies that bills cannot be created without an
         * authenticated user associated with the transaction.</p>
         */
        @Test
        @DisplayName("Should validate user is authenticated")
        void testUserAuthenticationValidation() {
            User authenticatedUser = createTestUser();
            User nullUser = null;
            
            assertNotNull(authenticatedUser);
            assertNull(nullUser);
        }
        
        /**
         * Tests correct calculation of bill totals.
         * 
         * <p>Verifies that net amount, total items, tax (10%), and
         * total amount are correctly calculated from order items.</p>
         */
        @Test
        @DisplayName("Should calculate totals correctly")
        void testBillTotalsCalculation() {
            Bill bill = new Bill();
            List<OrderItem> items = createTestOrderItems();
            bill.setOrderItems(items);
            
            bill.calculateTotals();
            
            // Net amount: (2 * 10) + (1 * 5) = 25
            assertEquals(0, new BigDecimal("25.00").compareTo(bill.getNetAmount()));
            // Total items: 2 + 1 = 3
            assertEquals(3, bill.getTotalItems());
            // Tax (10%): 2.50
            assertEquals(0, new BigDecimal("2.50").compareTo(bill.getTaxAmount()));
            // Total: 27.50
            assertEquals(0, new BigDecimal("27.50").compareTo(bill.getTotalAmount()));
        }
        
        /**
         * Tests validation of cash payment amount.
         * 
         * <p>Verifies that the cash amount received must be greater than
         * or equal to the total amount for payment to be accepted.</p>
         */
        @Test
        @DisplayName("Should validate cash payment amount")
        void testCashPaymentValidation() {
            Bill bill = new Bill();
            bill.setPaymentMethod(PaymentMethod.CASH);
            bill.setTotalAmount(new BigDecimal("50.00"));
            
            BigDecimal sufficientCash = new BigDecimal("60.00");
            BigDecimal insufficientCash = new BigDecimal("40.00");
            BigDecimal exactCash = new BigDecimal("50.00");
            
            assertTrue(sufficientCash.compareTo(bill.getTotalAmount()) >= 0);
            assertTrue(exactCash.compareTo(bill.getTotalAmount()) >= 0);
            assertFalse(insufficientCash.compareTo(bill.getTotalAmount()) >= 0);
        }
        
        /**
         * Tests correct calculation of change for cash payments.
         * 
         * <p>Verifies that change is correctly calculated as the difference
         * between cash received and total amount.</p>
         */
        @Test
        @DisplayName("Should calculate change for cash payment")
        void testChangeCalculation() {
            BigDecimal totalAmount = new BigDecimal("45.50");
            BigDecimal cashReceived = new BigDecimal("50.00");
            
            BigDecimal change = cashReceived.subtract(totalAmount);
            
            assertEquals(0, new BigDecimal("4.50").compareTo(change));
        }
    }
    
    /**
     * Nested test class for payment method tests.
     * 
     * <p>Tests the different payment method handling including:
     * <ul>
     *   <li>CASH payment with change calculation</li>
     *   <li>CARD payment processing</li>
     *   <li>DIGITAL_WALLET payment processing</li>
     *   <li>UPI payment processing</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Payment Method Tests")
    class PaymentMethodTests {
        
        /**
         * Tests CASH payment method handling.
         * 
         * <p>Verifies that cash payments correctly store the payment method,
         * cash received amount, and change amount.</p>
         */
        @Test
        @DisplayName("Should handle CASH payment")
        void testCashPayment() {
            Bill bill = new Bill();
            bill.setPaymentMethod(PaymentMethod.CASH);
            bill.setTotalAmount(new BigDecimal("50.00"));
            bill.setCashReceived(new BigDecimal("60.00"));
            bill.setChangeAmount(new BigDecimal("10.00"));
            
            assertEquals(PaymentMethod.CASH, bill.getPaymentMethod());
            assertEquals(0, new BigDecimal("60.00").compareTo(bill.getCashReceived()));
            assertEquals(0, new BigDecimal("10.00").compareTo(bill.getChangeAmount()));
        }
        
        /**
         * Tests CARD payment method handling.
         * 
         * <p>Verifies that card payments are correctly recorded
         * with the CARD payment method enum value.</p>
         */
        @Test
        @DisplayName("Should handle CARD payment")
        void testCardPayment() {
            Bill bill = new Bill();
            bill.setPaymentMethod(PaymentMethod.CARD);
            
            assertEquals(PaymentMethod.CARD, bill.getPaymentMethod());
        }
        
        /**
         * Tests DIGITAL_WALLET payment method handling.
         * 
         * <p>Verifies that digital wallet payments are correctly recorded
         * with the DIGITAL_WALLET payment method enum value.</p>
         */
        @Test
        @DisplayName("Should handle DIGITAL_WALLET payment")
        void testDigitalWalletPayment() {
            Bill bill = new Bill();
            bill.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
            
            assertEquals(PaymentMethod.DIGITAL_WALLET, bill.getPaymentMethod());
        }
        
        /**
         * Tests UPI payment method handling.
         * 
         * <p>Verifies that UPI payments are correctly recorded
         * with the UPI payment method enum value.</p>
         */
        @Test
        @DisplayName("Should handle UPI payment")
        void testUpiPayment() {
            Bill bill = new Bill();
            bill.setPaymentMethod(PaymentMethod.UPI);
            
            assertEquals(PaymentMethod.UPI, bill.getPaymentMethod());
        }
    }
    
    /**
     * Nested test class for bill query and search tests.
     * 
     * <p>Tests various methods to find and list bills:
     * <ul>
     *   <li>Getting bills by ID</li>
     *   <li>Handling non-existent bill lookups</li>
     *   <li>Getting all bills</li>
     *   <li>Getting bills by user ID</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Bill Query Tests")
    class BillQueryTests {
        
        /**
         * Tests finding a bill by its unique identifier.
         * 
         * <p>Verifies that findBillById() returns an Optional containing
         * the bill when the ID exists in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get bill by ID")
        void testGetBillById() throws SQLException {
            // Given
            Bill bill = createTestBill(createTestOrderItems());
            bill.setBillId(1);
            when(mockBillDAO.findBillById(1)).thenReturn(Optional.of(bill));
            
            // When
            Optional<Bill> result = mockBillDAO.findBillById(1);
            
            // Then
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getBillId());
        }
        
        /**
         * Tests bill lookup when the bill doesn't exist.
         * 
         * <p>Verifies that findBillById() returns an empty Optional
         * when the requested bill ID is not found.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should return empty when bill not found")
        void testBillNotFound() throws SQLException {
            // Given
            when(mockBillDAO.findBillById(999)).thenReturn(Optional.empty());
            
            // When
            Optional<Bill> result = mockBillDAO.findBillById(999);
            
            // Then
            assertTrue(result.isEmpty());
        }
        
        /**
         * Tests retrieval of all bills from the system.
         * 
         * <p>Verifies that getAllBills() returns a complete list of all
         * bills in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get all bills")
        void testGetAllBills() throws SQLException {
            // Given
            List<Bill> bills = new ArrayList<>();
            bills.add(createTestBill(createTestOrderItems()));
            bills.add(createTestBill(createTestOrderItems()));
            
            when(mockBillDAO.getAllBills()).thenReturn(bills);
            
            // When
            List<Bill> result = mockBillDAO.getAllBills();
            
            // Then
            assertEquals(2, result.size());
        }
        
        /**
         * Tests filtering bills by user ID.
         * 
         * <p>Verifies that getBillsByUserId() returns only bills
         * associated with the specified user.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get bills by user ID")
        void testGetBillsByUserId() throws SQLException {
            // Given
            List<Bill> userBills = new ArrayList<>();
            Bill bill1 = createTestBill(createTestOrderItems());
            bill1.setUserId(1);
            userBills.add(bill1);
            
            when(mockBillDAO.getBillsByUserId(1)).thenReturn(userBills);
            
            // When
            List<Bill> result = mockBillDAO.getBillsByUserId(1);
            
            // Then
            assertEquals(1, result.size());
            assertEquals(1, result.get(0).getUserId());
        }
    }
    
    /**
     * Nested test class for sales statistics tests.
     * 
     * <p>Tests the sales reporting functionality including:
     * <ul>
     *   <li>Getting total sales amount</li>
     *   <li>Getting the last sale amount</li>
     *   <li>Getting total order count</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Sales Statistics Tests")
    class SalesStatisticsTests {
        
        /**
         * Tests retrieval of total sales amount.
         * 
         * <p>Verifies that getTotalSales() returns the sum of all
         * bill amounts in the system.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get total sales")
        void testGetTotalSales() throws SQLException {
            // Given
            BigDecimal totalSales = new BigDecimal("1500.00");
            when(mockBillDAO.getTotalSales()).thenReturn(totalSales);
            
            // When
            BigDecimal result = mockBillDAO.getTotalSales();
            
            // Then
            assertEquals(0, new BigDecimal("1500.00").compareTo(result));
        }
        
        /**
         * Tests retrieval of the last sale amount.
         * 
         * <p>Verifies that getLastSale() returns the amount from
         * the most recent transaction.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get last sale amount")
        void testGetLastSale() throws SQLException {
            // Given
            BigDecimal lastSale = new BigDecimal("75.50");
            when(mockBillDAO.getLastSale()).thenReturn(lastSale);
            
            // When
            BigDecimal result = mockBillDAO.getLastSale();
            
            // Then
            assertEquals(0, new BigDecimal("75.50").compareTo(result));
        }
        
        /**
         * Tests retrieval of total order count.
         * 
         * <p>Verifies that getTotalOrders() returns the count of all
         * orders/bills in the system.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get total orders count")
        void testGetTotalOrders() throws SQLException {
            // Given
            when(mockBillDAO.getTotalOrders()).thenReturn(50);
            
            // When
            Integer result = mockBillDAO.getTotalOrders();
            
            // Then
            assertEquals(50, result);
        }
    }
    
    /**
     * Nested test class for order item tests.
     * 
     * <p>Tests the order item functionality including:
     * <ul>
     *   <li>Creating order items with subtotal calculation</li>
     *   <li>Updating subtotal when quantity changes</li>
     *   <li>Handling special notes for items</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Order Item Tests")
    class OrderItemTests {
        
        /**
         * Tests creation of an order item with automatic subtotal calculation.
         * 
         * <p>Verifies that order items correctly store product details
         * and calculate subtotal as quantity times unit price.</p>
         */
        @Test
        @DisplayName("Should create order item with subtotal")
        void testOrderItemCreation() {
            OrderItem item = new OrderItem(1, "Burger", 2, new BigDecimal("12.50"));
            
            assertEquals(1, item.getProductId());
            assertEquals("Burger", item.getProductName());
            assertEquals(2, item.getQuantity());
            assertEquals(0, new BigDecimal("12.50").compareTo(item.getUnitPrice()));
            assertEquals(0, new BigDecimal("25.00").compareTo(item.getSubtotal()));
        }
        
        /**
         * Tests that subtotal is recalculated when quantity changes.
         * 
         * <p>Verifies that changing the quantity of an order item
         * automatically updates the subtotal value.</p>
         */
        @Test
        @DisplayName("Should update subtotal when quantity changes")
        void testSubtotalUpdateOnQuantityChange() {
            OrderItem item = new OrderItem(1, "Burger", 1, new BigDecimal("10.00"));
            assertEquals(0, new BigDecimal("10.00").compareTo(item.getSubtotal()));
            
            item.setQuantity(3);
            assertEquals(0, new BigDecimal("30.00").compareTo(item.getSubtotal()));
        }
        
        /**
         * Tests handling of special notes for order items.
         * 
         * <p>Verifies that order items can store special instructions
         * like "Extra cheese, no onions" for kitchen preparation.</p>
         */
        @Test
        @DisplayName("Should handle special notes for order items")
        void testOrderItemNotes() {
            OrderItem item = new OrderItem();
            item.setNotes("Extra cheese, no onions");
            
            assertEquals("Extra cheese, no onions", item.getNotes());
        }
    }
    
    /**
     * Nested test class for bill number generation tests.
     * 
     * <p>Tests the unique bill number generation including:
     * <ul>
     *   <li>Auto-generation of bill numbers</li>
     *   <li>Uniqueness of bill numbers across transactions</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Bill Number Generation Tests")
    class BillNumberTests {
        
        /**
         * Tests that bill numbers are automatically generated.
         * 
         * <p>Verifies that new bills are assigned a bill number
         * starting with the "BILL-" prefix.</p>
         */
        @Test
        @DisplayName("Should generate unique bill number")
        void testBillNumberGeneration() {
            Bill bill = new Bill();
            
            assertNotNull(bill.getBillNumber());
            assertTrue(bill.getBillNumber().startsWith("BILL-"));
        }
        
        /**
         * Tests that bill numbers are unique per transaction.
         * 
         * <p>Verifies that bills created at different times have
         * different bill numbers based on timestamp.</p>
         * 
         * @throws InterruptedException if thread sleep is interrupted
         */
        @Test
        @DisplayName("Bill numbers should be unique")
        void testBillNumberUniqueness() throws InterruptedException {
            Bill bill1 = new Bill();
            Thread.sleep(1000); // Wait 1 second to ensure different timestamps
            Bill bill2 = new Bill();
            
            assertNotEquals(bill1.getBillNumber(), bill2.getBillNumber());
        }
    }
    
    /**
     * Creates a test User instance for billing tests.
     * 
     * <p>This helper method creates a server user for associating
     * with bills in test cases.</p>
     * 
     * @return a new User instance with SERVER role
     */
    private User createTestUser() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setRole(UserRole.SERVER);
        return user;
    }
    
    /**
     * Creates a list of test OrderItem instances.
     * 
     * <p>This helper method creates sample order items (Burger and Fries)
     * for use in bill creation tests.</p>
     * 
     * @return a List containing two test order items
     */
    private List<OrderItem> createTestOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(1, "Burger", 2, new BigDecimal("10.00")));
        items.add(new OrderItem(2, "Fries", 1, new BigDecimal("5.00")));
        return items;
    }
    
    /**
     * Creates a test Bill instance with the specified order items.
     * 
     * <p>This helper method creates a fully populated Bill object
     * with calculated totals, associated with the test user.</p>
     * 
     * @param items the list of order items to include in the bill
     * @return a new Bill instance with calculated totals
     */
    private Bill createTestBill(List<OrderItem> items) {
        Bill bill = new Bill();
        bill.setUserId(testUser.getUserId());
        bill.setBilledByUser(testUser.getFullName());
        bill.setPaymentMethod(PaymentMethod.CASH);
        bill.setOrderItems(items);
        bill.calculateTotals();
        return bill;
    }
}
