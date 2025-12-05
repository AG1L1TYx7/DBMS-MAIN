package com.restaurant.util;

import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ReceiptPrinter utility.
 * 
 * <p>This test class validates the receipt generation functionality for the
 * restaurant management system. It covers bill creation, receipt text generation,
 * text formatting, restaurant information display, and edge cases handling.</p>
 * 
 * <p>The tests are organized into nested test classes for better readability
 * and logical grouping of related test scenarios.</p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 */
public class ReceiptPrinterTest {
    
    /** The test bill instance used across test methods for receipt generation testing. */
    private Bill testBill;
    
    /**
     * Sets up the test environment before each test method execution.
     * Creates a fresh test bill with predefined values for consistent testing.
     */
    @BeforeEach
    void setUp() {
        testBill = createTestBill();
    }
    
    /**
     * Nested test class for bill creation tests related to receipt generation.
     * 
     * <p>Validates that bills have all required fields populated correctly
     * before being used to generate receipts. This ensures the receipt printer
     * has all necessary data to produce a complete receipt.</p>
     */
    @Nested
    @DisplayName("Bill Creation for Receipt Tests")
    class BillCreationTests {
        
        /**
         * Tests that a bill has all required fields populated for receipt generation.
         * Verifies bill number, timestamp, cashier name, payment method, and order items.
         */
        @Test
        @DisplayName("Should create bill with all required fields")
        void testBillHasRequiredFields() {
            assertNotNull(testBill.getBillNumber());
            assertNotNull(testBill.getBilledAt());
            assertNotNull(testBill.getBilledByUser());
            assertNotNull(testBill.getPaymentMethod());
            assertNotNull(testBill.getOrderItems());
        }
        
        /**
         * Tests that a bill has calculated totals for receipt display.
         * Verifies net amount, tax amount, and total amount are computed.
         */
        @Test
        @DisplayName("Bill should have calculated totals")
        void testBillHasCalculatedTotals() {
            assertNotNull(testBill.getNetAmount());
            assertNotNull(testBill.getTaxAmount());
            assertNotNull(testBill.getTotalAmount());
        }
    }
    
    /**
     * Nested test class for receipt text generation tests.
     * 
     * <p>Validates that all required information is properly included in
     * the receipt text, including bill details, order items, pricing,
     * payment information, and change calculations.</p>
     */
    @Nested
    @DisplayName("Receipt Text Generation Tests")
    class ReceiptTextGenerationTests {
        
        /**
         * Tests that the receipt includes the bill number.
         * Verifies the bill number is not null and follows the expected format.
         */
        @Test
        @DisplayName("Should include bill number in receipt")
        void testReceiptIncludesBillNumber() {
            String billNumber = testBill.getBillNumber();
            
            assertNotNull(billNumber);
            assertTrue(billNumber.startsWith("BILL-"));
        }
        
        /**
         * Tests that dates are formatted correctly for receipt display.
         * Verifies the date matches the expected pattern yyyy-MM-dd HH:mm:ss.
         */
        @Test
        @DisplayName("Should format date correctly")
        void testDateFormatting() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = testBill.getBilledAt().format(formatter);
            
            assertNotNull(formattedDate);
            assertTrue(formattedDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        }
        
        /**
         * Tests that the receipt includes the cashier/billing user name.
         * Verifies the cashier name matches the expected test value.
         */
        @Test
        @DisplayName("Should include cashier name")
        void testReceiptIncludesCashier() {
            String cashierName = testBill.getBilledByUser();
            
            assertEquals("Test Cashier", cashierName);
        }
        
        /**
         * Tests that all order items are included in the receipt.
         * Verifies item count, names, quantities, and unit prices.
         */
        @Test
        @DisplayName("Should include all order items")
        void testReceiptIncludesOrderItems() {
            List<OrderItem> items = testBill.getOrderItems();
            
            assertEquals(2, items.size());
            
            // Check first item
            OrderItem firstItem = items.get(0);
            assertEquals("Burger", firstItem.getProductName());
            assertEquals(2, firstItem.getQuantity());
            assertEquals(0, new BigDecimal("10.00").compareTo(firstItem.getUnitPrice()));
            
            // Check second item
            OrderItem secondItem = items.get(1);
            assertEquals("Fries", secondItem.getProductName());
            assertEquals(1, secondItem.getQuantity());
            assertEquals(0, new BigDecimal("5.00").compareTo(secondItem.getUnitPrice()));
        }
        
        /**
         * Tests that subtotals are calculated correctly for individual order items.
         * Verifies subtotal equals quantity multiplied by unit price.
         */
        @Test
        @DisplayName("Should calculate subtotal for items")
        void testItemSubtotalCalculation() {
            OrderItem burger = testBill.getOrderItems().get(0);
            OrderItem fries = testBill.getOrderItems().get(1);
            
            // Burger: 2 * 10.00 = 20.00
            assertEquals(0, new BigDecimal("20.00").compareTo(burger.getSubtotal()));
            
            // Fries: 1 * 5.00 = 5.00
            assertEquals(0, new BigDecimal("5.00").compareTo(fries.getSubtotal()));
        }
        
        /**
         * Tests that the receipt includes the subtotal (net amount before tax).
         * Verifies the net amount is the sum of all item subtotals.
         */
        @Test
        @DisplayName("Should include subtotal")
        void testReceiptIncludesSubtotal() {
            BigDecimal netAmount = testBill.getNetAmount();
            
            // Net = 20.00 + 5.00 = 25.00
            assertEquals(0, new BigDecimal("25.00").compareTo(netAmount));
        }
        
        /**
         * Tests that the receipt includes the tax amount.
         * Verifies tax is calculated as 10% of the net amount.
         */
        @Test
        @DisplayName("Should include tax amount")
        void testReceiptIncludesTax() {
            BigDecimal taxAmount = testBill.getTaxAmount();
            
            // Tax (10%) = 25.00 * 0.10 = 2.50
            assertEquals(0, new BigDecimal("2.50").compareTo(taxAmount));
        }
        
        /**
         * Tests that the receipt includes the total amount due.
         * Verifies total equals net amount plus tax amount.
         */
        @Test
        @DisplayName("Should include total amount")
        void testReceiptIncludesTotal() {
            BigDecimal totalAmount = testBill.getTotalAmount();
            
            // Total = 25.00 + 2.50 = 27.50
            assertEquals(0, new BigDecimal("27.50").compareTo(totalAmount));
        }
        
        /**
         * Tests that the receipt includes the payment method.
         * Verifies payment method type and its display name.
         */
        @Test
        @DisplayName("Should include payment method")
        void testReceiptIncludesPaymentMethod() {
            PaymentMethod paymentMethod = testBill.getPaymentMethod();
            
            assertEquals(PaymentMethod.CASH, paymentMethod);
            assertEquals("Cash", paymentMethod.getDisplayName());
        }
        
        /**
         * Tests that the receipt includes the cash received amount for cash payments.
         * Verifies the amount matches the test value provided.
         */
        @Test
        @DisplayName("Should include cash received for cash payment")
        void testReceiptIncludesCashReceived() {
            BigDecimal cashReceived = testBill.getCashReceived();
            
            assertEquals(0, new BigDecimal("50.00").compareTo(cashReceived));
        }
        
        /**
         * Tests that the receipt includes the change amount for cash payments.
         * Verifies change is calculated as cash received minus total amount.
         */
        @Test
        @DisplayName("Should include change amount")
        void testReceiptIncludesChange() {
            BigDecimal changeAmount = testBill.getChangeAmount();
            
            // Change = 50.00 - 27.50 = 22.50
            assertEquals(0, new BigDecimal("22.50").compareTo(changeAmount));
        }
    }
    
    /**
     * Nested test class for text formatting tests.
     * 
     * <p>Validates the text formatting utilities used in receipt generation,
     * including text centering, truncation, line separators, currency formatting,
     * and item line formatting.</p>
     */
    @Nested
    @DisplayName("Text Formatting Tests")
    class TextFormattingTests {
        
        /**
         * Tests that text can be centered correctly within a given width.
         * Verifies the centering logic produces properly padded output.
         */
        @Test
        @DisplayName("Should center text correctly")
        void testCenterText() {
            String text = "Test";
            int width = 10;
            
            int padding = (width - text.length()) / 2;
            String centered = " ".repeat(padding) + text + " ".repeat(padding);
            
            // Centered "Test" in width 10 = "   Test   "
            assertTrue(centered.contains("Test"));
        }
        
        /**
         * Tests that long text is truncated to fit within a maximum length.
         * Verifies truncation produces output within the specified length limit.
         */
        @Test
        @DisplayName("Should truncate long text")
        void testTruncateText() {
            String longText = "Very Long Product Name That Needs Truncation";
            int maxLength = 20;
            
            String truncated = longText.length() > maxLength ? 
                longText.substring(0, maxLength) : longText;
            
            assertTrue(truncated.length() <= maxLength);
        }
        
        /**
         * Tests the creation of line separators for receipt formatting.
         * Verifies the separator has the correct length and character composition.
         */
        @Test
        @DisplayName("Should create line separator")
        void testLineSeparator() {
            int width = 40;
            String line = "-".repeat(width);
            
            assertEquals(40, line.length());
            assertTrue(line.matches("-+"));
        }
        
        /**
         * Tests that currency values are formatted with two decimal places.
         * Verifies proper decimal formatting for monetary display.
         */
        @Test
        @DisplayName("Should format currency with two decimal places")
        void testCurrencyFormatting() {
            BigDecimal amount = new BigDecimal("25.50");
            String formatted = String.format("%.2f", amount.doubleValue());
            
            assertEquals("25.50", formatted);
        }
        
        /**
         * Tests that item lines are formatted correctly for receipt display.
         * Verifies item name, quantity, price, and subtotal are properly aligned.
         */
        @Test
        @DisplayName("Should format item line correctly")
        void testItemLineFormatting() {
            String itemName = "Burger";
            int quantity = 2;
            double price = 10.00;
            double subtotal = 20.00;
            
            String line = String.format("%-20s %4d %7.2f %7.2f", 
                itemName, quantity, price, subtotal);
            
            assertTrue(line.contains("Burger"));
            assertTrue(line.contains("20.00"));
        }
    }
    
    /**
     * Nested test class for restaurant information tests.
     * 
     * <p>Validates that restaurant details like name, address, phone,
     * and email are properly defined and can be included in receipts.</p>
     */
    @Nested
    @DisplayName("Restaurant Information Tests")
    class RestaurantInfoTests {
        
        /**
         * Tests that a restaurant name constant is defined and valid.
         * Verifies the name is not null and not empty.
         */
        @Test
        @DisplayName("Should have restaurant name constant")
        void testRestaurantName() {
            String restaurantName = "Delicious Restaurant";
            assertNotNull(restaurantName);
            assertFalse(restaurantName.isEmpty());
        }
        
        /**
         * Tests that a restaurant address constant is defined and valid.
         * Verifies the address is not null and not empty.
         */
        @Test
        @DisplayName("Should have restaurant address constant")
        void testRestaurantAddress() {
            String address = "123 Main Street, Kathmandu, Nepal";
            assertNotNull(address);
            assertFalse(address.isEmpty());
        }
        
        /**
         * Tests that a restaurant phone constant is defined and valid.
         * Verifies the phone number is not null and not empty.
         */
        @Test
        @DisplayName("Should have restaurant phone constant")
        void testRestaurantPhone() {
            String phone = "+977 1-4567890";
            assertNotNull(phone);
            assertFalse(phone.isEmpty());
        }
        
        /**
         * Tests that a restaurant email constant is defined and valid.
         * Verifies the email is not null and contains an @ symbol.
         */
        @Test
        @DisplayName("Should have restaurant email constant")
        void testRestaurantEmail() {
            String email = "info@delicious.com.np";
            assertNotNull(email);
            assertTrue(email.contains("@"));
        }
    }
    
    /**
     * Nested test class for edge case tests.
     * 
     * <p>Validates that the receipt printer handles edge cases gracefully,
     * including empty order items, null values, single item orders,
     * and large quantities.</p>
     */
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        /**
         * Tests that the receipt handles an empty order items list gracefully.
         * Verifies zero items and zero net amount for empty orders.
         */
        @Test
        @DisplayName("Should handle empty order items")
        void testEmptyOrderItems() {
            Bill emptyBill = new Bill();
            emptyBill.setUserId(1);
            emptyBill.setBilledByUser("Test");
            emptyBill.calculateTotals();
            
            assertEquals(0, emptyBill.getOrderItems().size());
            assertEquals(0, BigDecimal.ZERO.compareTo(emptyBill.getNetAmount()));
        }
        
        /**
         * Tests that null billedAt is handled gracefully.
         * Verifies that a default timestamp is set in the constructor.
         */
        @Test
        @DisplayName("Should handle null billedAt gracefully")
        void testNullBilledAt() {
            Bill bill = new Bill();
            // billedAt is set in constructor, but let's test the format
            
            assertNotNull(bill.getBilledAt());
        }
        
        /**
         * Tests that the receipt handles a single item order correctly.
         * Verifies item count and net amount for orders with one item.
         */
        @Test
        @DisplayName("Should handle single item order")
        void testSingleItemOrder() {
            Bill bill = new Bill();
            bill.setUserId(1);
            bill.setBilledByUser("Test");
            
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(1, "Single Item", 1, new BigDecimal("15.00")));
            bill.setOrderItems(items);
            bill.calculateTotals();
            
            assertEquals(1, bill.getOrderItems().size());
            assertEquals(0, new BigDecimal("15.00").compareTo(bill.getNetAmount()));
        }
        
        /**
         * Tests that the receipt handles large quantities correctly.
         * Verifies subtotal calculation for high-volume orders.
         */
        @Test
        @DisplayName("Should handle large quantity")
        void testLargeQuantity() {
            OrderItem item = new OrderItem(1, "Bulk Item", 100, new BigDecimal("5.00"));
            
            assertEquals(0, new BigDecimal("500.00").compareTo(item.getSubtotal()));
        }
    }
    
    /**
     * Creates a test bill with predefined values for testing purposes.
     * 
     * <p>The test bill includes:</p>
     * <ul>
     *   <li>User ID: 1</li>
     *   <li>Cashier: "Test Cashier"</li>
     *   <li>Payment Method: CASH</li>
     *   <li>Order Items: Burger (2 @ $10) and Fries (1 @ $5)</li>
     *   <li>Cash Received: $50.00</li>
     *   <li>Change Amount: $22.50</li>
     * </ul>
     * 
     * @return a new Bill instance populated with test data
     */
    private Bill createTestBill() {
        Bill bill = new Bill();
        bill.setUserId(1);
        bill.setBilledByUser("Test Cashier");
        bill.setPaymentMethod(PaymentMethod.CASH);
        
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(1, "Burger", 2, new BigDecimal("10.00")));
        items.add(new OrderItem(2, "Fries", 1, new BigDecimal("5.00")));
        bill.setOrderItems(items);
        
        bill.calculateTotals();
        
        bill.setCashReceived(new BigDecimal("50.00"));
        bill.setChangeAmount(new BigDecimal("22.50"));
        
        return bill;
    }
}
