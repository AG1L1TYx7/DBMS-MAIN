package com.restaurant.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link OrderItem} model class.
 * 
 * <p>This test class provides comprehensive coverage of the OrderItem model's functionality,
 * including basic properties, quantity and price handling, subtotal calculations,
 * constructors, and edge cases.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic order item properties</li>
 *   <li>{@link QuantityAndPriceTests} - Tests for quantity, price, and subtotal calculations</li>
 *   <li>{@link ConstructorTests} - Tests for default and parameterized constructors</li>
 *   <li>{@link EdgeCasesTests} - Tests for boundary conditions and edge cases</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see OrderItem
 */
public class OrderItemTest {
    
    /** The test order item instance used across test methods. */
    private OrderItem orderItem;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh OrderItem instance for each test case.
     */
    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
    }
    
    /**
     * Nested test class for basic order item property tests.
     * 
     * <p>Validates getter and setter functionality for core order item properties
     * including order item ID, order ID, product ID, product name, and notes.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that order item ID can be set and retrieved correctly.
         * Verifies the setOrderItemId and getOrderItemId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get order item ID")
        void testOrderItemId() {
            orderItem.setOrderItemId(1);
            assertEquals(1, orderItem.getOrderItemId());
        }
        
        /**
         * Tests that order ID can be set and retrieved correctly.
         * Order ID links the item to its parent order.
         */
        @Test
        @DisplayName("Should set and get order ID")
        void testOrderId() {
            orderItem.setOrderId(10);
            assertEquals(10, orderItem.getOrderId());
        }
        
        /**
         * Tests that product ID can be set and retrieved correctly.
         * Product ID references the menu item being ordered.
         */
        @Test
        @DisplayName("Should set and get product ID")
        void testProductId() {
            orderItem.setProductId(5);
            assertEquals(5, orderItem.getProductId());
        }
        
        /**
         * Tests that product name can be set and retrieved correctly.
         * Product name stores the display name of the ordered item.
         */
        @Test
        @DisplayName("Should set and get product name")
        void testProductName() {
            orderItem.setProductName("Classic Burger");
            assertEquals("Classic Burger", orderItem.getProductName());
        }
        
        /**
         * Tests that notes can be set and retrieved correctly.
         * Notes store special instructions or modifications for the order item.
         */
        @Test
        @DisplayName("Should set and get notes")
        void testNotes() {
            orderItem.setNotes("No onions please");
            assertEquals("No onions please", orderItem.getNotes());
        }
    }
    
    /**
     * Nested test class for quantity, price, and subtotal tests.
     * 
     * <p>Validates quantity and price handling including default values,
     * setting values, and automatic subtotal calculations.</p>
     */
    @Nested
    @DisplayName("Quantity and Price Tests")
    class QuantityAndPriceTests {
        
        /**
         * Tests that quantity defaults to 1 for new order items.
         * Ensures a sensible default for item quantity.
         */
        @Test
        @DisplayName("Should default quantity to 1")
        void testDefaultQuantity() {
            OrderItem newItem = new OrderItem();
            assertEquals(1, newItem.getQuantity());
        }
        
        /**
         * Tests that quantity can be set and retrieved correctly.
         * Verifies the setQuantity and getQuantity methods.
         */
        @Test
        @DisplayName("Should set and get quantity")
        void testSetQuantity() {
            orderItem.setQuantity(5);
            assertEquals(5, orderItem.getQuantity());
        }
        
        /**
         * Tests that unit price can be set and retrieved correctly.
         * Unit price is the price per single item.
         */
        @Test
        @DisplayName("Should set and get unit price")
        void testUnitPrice() {
            BigDecimal price = new BigDecimal("12.99");
            orderItem.setUnitPrice(price);
            assertEquals(price, orderItem.getUnitPrice());
        }
        
        /**
         * Tests that subtotal is automatically calculated when quantity changes.
         * Verifies subtotal = quantity × unit price after quantity update.
         */
        @Test
        @DisplayName("Should calculate subtotal when setting quantity")
        void testCalculateSubtotalOnQuantityChange() {
            orderItem.setUnitPrice(new BigDecimal("10.00"));
            orderItem.setQuantity(3);
            
            BigDecimal expected = new BigDecimal("30.00");
            assertEquals(0, expected.compareTo(orderItem.getSubtotal()));
        }
        
        /**
         * Tests that subtotal is automatically calculated when unit price changes.
         * Verifies subtotal = quantity × unit price after price update.
         */
        @Test
        @DisplayName("Should calculate subtotal when setting unit price")
        void testCalculateSubtotalOnPriceChange() {
            orderItem.setQuantity(2);
            orderItem.setUnitPrice(new BigDecimal("15.00"));
            
            BigDecimal expected = new BigDecimal("30.00");
            assertEquals(0, expected.compareTo(orderItem.getSubtotal()));
        }
        
        /**
         * Tests that subtotal can be set and retrieved directly.
         * Allows manual override of calculated subtotal if needed.
         */
        @Test
        @DisplayName("Should set and get subtotal directly")
        void testSetSubtotal() {
            BigDecimal subtotal = new BigDecimal("50.00");
            orderItem.setSubtotal(subtotal);
            assertEquals(subtotal, orderItem.getSubtotal());
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of OrderItem objects through
     * default and parameterized constructors.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor sets quantity to 1.
         * Verifies sensible default initialization.
         */
        @Test
        @DisplayName("Default constructor should set quantity to 1")
        void testDefaultConstructor() {
            OrderItem newItem = new OrderItem();
            assertNotNull(newItem);
            assertEquals(1, newItem.getQuantity());
        }
        
        /**
         * Tests that the parameterized constructor sets all fields correctly.
         * Verifies productId, productName, quantity, unitPrice, and calculated subtotal.
         */
        @Test
        @DisplayName("Parameterized constructor should set all fields")
        void testParameterizedConstructor() {
            OrderItem newItem = new OrderItem(
                5,                          // productId
                "Cheese Burger",            // productName
                2,                          // quantity
                new BigDecimal("15.00")     // unitPrice
            );
            
            assertEquals(5, newItem.getProductId());
            assertEquals("Cheese Burger", newItem.getProductName());
            assertEquals(2, newItem.getQuantity());
            assertEquals(new BigDecimal("15.00"), newItem.getUnitPrice());
            
            // Subtotal should be 2 * 15.00 = 30.00
            assertEquals(0, new BigDecimal("30.00").compareTo(newItem.getSubtotal()));
        }
        
        /**
         * Tests that the parameterized constructor calculates correct subtotals.
         * Verifies subtotal calculation for various quantity and price combinations.
         */
        @Test
        @DisplayName("Parameterized constructor should calculate correct subtotal")
        void testParameterizedConstructorSubtotal() {
            OrderItem item1 = new OrderItem(1, "Item", 1, new BigDecimal("10.00"));
            assertEquals(0, new BigDecimal("10.00").compareTo(item1.getSubtotal()));
            
            OrderItem item2 = new OrderItem(1, "Item", 5, new BigDecimal("3.50"));
            assertEquals(0, new BigDecimal("17.50").compareTo(item2.getSubtotal()));
            
            OrderItem item3 = new OrderItem(1, "Item", 10, new BigDecimal("1.99"));
            assertEquals(0, new BigDecimal("19.90").compareTo(item3.getSubtotal()));
        }
    }
    
    /**
     * Nested test class for edge case tests.
     * 
     * <p>Validates behavior with boundary conditions such as zero quantity,
     * zero price, large quantities, and decimal prices.</p>
     */
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        /**
         * Tests that zero quantity results in zero subtotal.
         * Handles edge case where no items are ordered.
         */
        @Test
        @DisplayName("Should handle zero quantity")
        void testZeroQuantity() {
            orderItem.setUnitPrice(new BigDecimal("10.00"));
            orderItem.setQuantity(0);
            
            assertEquals(0, BigDecimal.ZERO.compareTo(orderItem.getSubtotal()));
        }
        
        /**
         * Tests that zero price results in zero subtotal.
         * Handles edge case of complimentary items.
         */
        @Test
        @DisplayName("Should handle zero price")
        void testZeroPrice() {
            orderItem.setQuantity(5);
            orderItem.setUnitPrice(BigDecimal.ZERO);
            
            assertEquals(0, BigDecimal.ZERO.compareTo(orderItem.getSubtotal()));
        }
        
        /**
         * Tests that large quantities are handled correctly.
         * Verifies subtotal calculation with high item counts.
         */
        @Test
        @DisplayName("Should handle large quantity")
        void testLargeQuantity() {
            orderItem.setUnitPrice(new BigDecimal("5.00"));
            orderItem.setQuantity(1000);
            
            BigDecimal expected = new BigDecimal("5000.00");
            assertEquals(0, expected.compareTo(orderItem.getSubtotal()));
        }
        
        /**
         * Tests that decimal prices are calculated correctly.
         * Verifies precise subtotal calculation with fractional prices.
         */
        @Test
        @DisplayName("Should handle decimal prices")
        void testDecimalPrices() {
            orderItem.setUnitPrice(new BigDecimal("9.99"));
            orderItem.setQuantity(3);
            
            BigDecimal expected = new BigDecimal("29.97");
            assertEquals(0, expected.compareTo(orderItem.getSubtotal()));
        }
    }
}
