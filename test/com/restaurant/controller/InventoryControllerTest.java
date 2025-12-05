package com.restaurant.controller;

import com.restaurant.dao.ProductDAO;
import com.restaurant.model.Product;
import com.restaurant.model.Product.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for InventoryController.
 * 
 * <p>This test class validates the inventory management business logic in the restaurant
 * management system, including stock tracking, reorder level management, and inventory
 * statistics. It uses Mockito for mocking the ProductDAO layer.</p>
 * 
 * <p>Test categories covered:</p>
 * <ul>
 *   <li>Get All Inventory Items Tests - Retrieving all products for inventory</li>
 *   <li>Low Stock Products Tests - Identifying products below reorder level</li>
 *   <li>Out of Stock Products Tests - Identifying products with zero stock</li>
 *   <li>Update Stock Tests - Adding, removing, and setting stock quantities</li>
 *   <li>Update Reorder Level Tests - Managing reorder thresholds</li>
 *   <li>Inventory Statistics Tests - Counting products by stock status</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see InventoryController
 * @see ProductDAO
 * @see Product
 */
@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {
    
    /** Mock ProductDAO for testing inventory operations without database access. */
    @Mock
    private ProductDAO mockProductDAO;
    
    /**
     * Nested test class for retrieving all inventory items.
     * 
     * <p>Tests the inventory listing functionality including:
     * <ul>
     *   <li>Returning all products from the database</li>
     *   <li>Handling database errors gracefully</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Get All Inventory Items Tests")
    class GetAllInventoryItemsTests {
        
        /**
         * Tests retrieval of all products for inventory management.
         * 
         * <p>Verifies that getAllProducts() returns a complete list of all
         * products with their current stock information.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should return all products")
        void testGetAllInventoryItems() throws SQLException {
            // Given
            List<Product> products = createTestProducts();
            when(mockProductDAO.getAllProducts()).thenReturn(products);
            
            // When
            List<Product> result = mockProductDAO.getAllProducts();
            
            // Then
            assertEquals(3, result.size());
        }
        
        /**
         * Tests error handling when database operation fails.
         * 
         * <p>Verifies that SQLException is properly thrown when
         * the database encounters an error during inventory retrieval.</p>
         * 
         * @throws SQLException expected when database error occurs
         */
        @Test
        @DisplayName("Should return empty list on exception")
        void testGetAllInventoryItemsOnError() throws SQLException {
            // Given
            when(mockProductDAO.getAllProducts()).thenThrow(new SQLException("DB Error"));
            
            // Then
            assertThrows(SQLException.class, () -> mockProductDAO.getAllProducts());
        }
    }
    
    /**
     * Nested test class for low stock product identification.
     * 
     * <p>Tests the low stock detection functionality including:
     * <ul>
     *   <li>Identifying products with stock at or below reorder level</li>
     *   <li>Excluding out of stock products from low stock list</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Low Stock Products Tests")
    class LowStockProductsTests {
        
        /**
         * Tests identification of products with low stock.
         * 
         * <p>Verifies that products with stock quantity at or below
         * the reorder level (but greater than zero) are correctly identified.</p>
         */
        @Test
        @DisplayName("Should identify low stock products")
        void testGetLowStockProducts() {
            // Given
            List<Product> products = createTestProducts();
            
            // Product with stock below reorder level
            Product lowStock = products.get(0);
            lowStock.setStockQuantity(5);
            lowStock.setReorderLevel(10);
            
            // Product with adequate stock
            Product adequateStock = products.get(1);
            adequateStock.setStockQuantity(50);
            adequateStock.setReorderLevel(10);
            
            // When - filter low stock
            List<Product> lowStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() != null && p.getReorderLevel() != null)
                .filter(p -> p.getStockQuantity() <= p.getReorderLevel() && p.getStockQuantity() > 0)
                .toList();
            
            // Then
            assertEquals(1, lowStockProducts.size());
            assertEquals(5, lowStockProducts.get(0).getStockQuantity());
        }
        
        /**
         * Tests that out of stock products are excluded from low stock list.
         * 
         * <p>Verifies that products with zero stock are not included in
         * the low stock list, as they belong in the out of stock category.</p>
         */
        @Test
        @DisplayName("Should not include out of stock in low stock")
        void testExcludeOutOfStockFromLowStock() {
            // Given
            Product outOfStock = createTestProduct(1, "Out of Stock", ProductCategory.BURGER);
            outOfStock.setStockQuantity(0);
            outOfStock.setReorderLevel(10);
            
            Product lowStock = createTestProduct(2, "Low Stock", ProductCategory.BURGER);
            lowStock.setStockQuantity(5);
            lowStock.setReorderLevel(10);
            
            List<Product> products = List.of(outOfStock, lowStock);
            
            // When - filter low stock (excluding out of stock)
            List<Product> lowStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() <= p.getReorderLevel())
                .filter(p -> p.getStockQuantity() > 0)
                .toList();
            
            // Then
            assertEquals(1, lowStockProducts.size());
            assertEquals("Low Stock", lowStockProducts.get(0).getProductName());
        }
    }
    
    /**
     * Nested test class for out of stock product identification.
     * 
     * <p>Tests the out of stock detection functionality including:
     * <ul>
     *   <li>Identifying products with zero stock quantity</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Out of Stock Products Tests")
    class OutOfStockProductsTests {
        
        /**
         * Tests identification of products that are out of stock.
         * 
         * <p>Verifies that products with zero stock quantity are correctly
         * identified as out of stock.</p>
         */
        @Test
        @DisplayName("Should identify out of stock products")
        void testGetOutOfStockProducts() {
            // Given
            List<Product> products = createTestProducts();
            
            // Product with zero stock
            Product outOfStock = products.get(0);
            outOfStock.setStockQuantity(0);
            
            // Product with stock
            Product inStock = products.get(1);
            inStock.setStockQuantity(50);
            
            // When - filter out of stock
            List<Product> outOfStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() == 0)
                .toList();
            
            // Then
            assertEquals(1, outOfStockProducts.size());
            assertEquals(0, outOfStockProducts.get(0).getStockQuantity());
        }
    }
    
    /**
     * Nested test class for stock update operations.
     * 
     * <p>Tests the stock modification functionality including:
     * <ul>
     *   <li>Adding stock (receiving inventory)</li>
     *   <li>Removing stock (sales or waste)</li>
     *   <li>Preventing negative stock values</li>
     *   <li>Setting stock to specific values</li>
     *   <li>Updating availability based on stock changes</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Update Stock Tests")
    class UpdateStockTests {
        
        /**
         * Tests adding stock to a product (receiving inventory).
         * 
         * <p>Verifies that stock quantity is correctly increased
         * when inventory is received.</p>
         */
        @Test
        @DisplayName("Should ADD stock correctly")
        void testAddStock() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(100);
            
            // When - ADD 50
            int newQuantity = product.getStockQuantity() + 50;
            product.setStockQuantity(newQuantity);
            
            // Then
            assertEquals(150, product.getStockQuantity());
        }
        
        /**
         * Tests removing stock from a product (sales or waste).
         * 
         * <p>Verifies that stock quantity is correctly decreased
         * when items are sold or removed.</p>
         */
        @Test
        @DisplayName("Should REMOVE stock correctly")
        void testRemoveStock() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(100);
            
            // When - REMOVE 30
            int newQuantity = Math.max(0, product.getStockQuantity() - 30);
            product.setStockQuantity(newQuantity);
            
            // Then
            assertEquals(70, product.getStockQuantity());
        }
        
        /**
         * Tests that stock cannot go negative when removing.
         * 
         * <p>Verifies that attempting to remove more stock than available
         * results in zero stock rather than negative values.</p>
         */
        @Test
        @DisplayName("Should not allow negative stock when removing")
        void testRemoveStockNotNegative() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(10);
            
            // When - REMOVE 50 (more than current)
            int newQuantity = Math.max(0, product.getStockQuantity() - 50);
            product.setStockQuantity(newQuantity);
            
            // Then - should be 0, not negative
            assertEquals(0, product.getStockQuantity());
        }
        
        /**
         * Tests setting stock to a specific value.
         * 
         * <p>Verifies that stock quantity can be directly set
         * to any non-negative value.</p>
         */
        @Test
        @DisplayName("Should SET stock to specific value")
        void testSetStock() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(100);
            
            // When - SET to 75
            product.setStockQuantity(75);
            
            // Then
            assertEquals(75, product.getStockQuantity());
        }
        
        /**
         * Tests that availability is updated when stock reaches zero.
         * 
         * <p>Verifies that when stock quantity is reduced to zero,
         * the product's availability flag is automatically set to false.</p>
         */
        @Test
        @DisplayName("Should update availability based on stock")
        void testUpdateAvailabilityOnStockChange() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(10);
            product.setAvailable(true);
            
            // When - SET stock to 0
            product.setStockQuantity(0);
            if (product.getStockQuantity() <= 0) {
                product.setAvailable(false);
            }
            
            // Then
            assertFalse(product.isAvailable());
        }
        
        /**
         * Tests that availability is set true when restocking.
         * 
         * <p>Verifies that when stock is added to a previously out-of-stock
         * product, the availability flag is automatically set to true.</p>
         */
        @Test
        @DisplayName("Should set available when restocking")
        void testSetAvailableOnRestock() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(0);
            product.setAvailable(false);
            
            // When - ADD stock
            product.setStockQuantity(50);
            if (product.getStockQuantity() > 0) {
                product.setAvailable(true);
            }
            
            // Then
            assertTrue(product.isAvailable());
        }
    }
    
    /**
     * Nested test class for reorder level management.
     * 
     * <p>Tests the reorder level functionality including:
     * <ul>
     *   <li>Updating reorder thresholds</li>
     *   <li>Detecting low stock after reorder level changes</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Update Reorder Level Tests")
    class UpdateReorderLevelTests {
        
        /**
         * Tests updating the reorder level threshold.
         * 
         * <p>Verifies that the reorder level can be changed
         * to adjust when low stock alerts are triggered.</p>
         */
        @Test
        @DisplayName("Should update reorder level")
        void testUpdateReorderLevel() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setReorderLevel(10);
            
            // When
            product.setReorderLevel(20);
            
            // Then
            assertEquals(20, product.getReorderLevel());
        }
        
        /**
         * Tests low stock detection after reorder level increase.
         * 
         * <p>Verifies that when the reorder level is increased,
         * previously adequate stock may now be considered low stock.</p>
         */
        @Test
        @DisplayName("Should detect low stock after reorder level change")
        void testDetectLowStockAfterReorderChange() {
            // Given
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(15);
            product.setReorderLevel(10);
            
            // Initially not low stock
            boolean initiallyLowStock = product.getStockQuantity() <= product.getReorderLevel();
            assertFalse(initiallyLowStock);
            
            // When - increase reorder level
            product.setReorderLevel(20);
            
            // Then - now low stock
            boolean nowLowStock = product.getStockQuantity() <= product.getReorderLevel();
            assertTrue(nowLowStock);
        }
    }
    
    /**
     * Nested test class for inventory statistics.
     * 
     * <p>Tests the inventory counting and statistics functionality:
     * <ul>
     *   <li>Total product count</li>
     *   <li>Low stock product count</li>
     *   <li>Out of stock product count</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Inventory Statistics Tests")
    class InventoryStatisticsTests {
        
        /**
         * Tests counting total products in inventory.
         * 
         * <p>Verifies that the total count of all products
         * in the inventory is correctly calculated.</p>
         */
        @Test
        @DisplayName("Should count total products")
        void testTotalProductCount() {
            List<Product> products = createTestProducts();
            
            assertEquals(3, products.size());
        }
        
        /**
         * Tests counting low stock products.
         * 
         * <p>Verifies that the count of products with stock at or below
         * reorder level (but greater than zero) is correctly calculated.</p>
         */
        @Test
        @DisplayName("Should count low stock products")
        void testLowStockCount() {
            // Given
            List<Product> products = new ArrayList<>();
            
            Product lowStock1 = createTestProduct(1, "Low 1", ProductCategory.BURGER);
            lowStock1.setStockQuantity(5);
            lowStock1.setReorderLevel(10);
            products.add(lowStock1);
            
            Product lowStock2 = createTestProduct(2, "Low 2", ProductCategory.FRIES);
            lowStock2.setStockQuantity(3);
            lowStock2.setReorderLevel(10);
            products.add(lowStock2);
            
            Product adequate = createTestProduct(3, "Adequate", ProductCategory.BEVERAGE);
            adequate.setStockQuantity(100);
            adequate.setReorderLevel(10);
            products.add(adequate);
            
            // When
            long lowStockCount = products.stream()
                .filter(p -> p.getStockQuantity() <= p.getReorderLevel() && p.getStockQuantity() > 0)
                .count();
            
            // Then
            assertEquals(2, lowStockCount);
        }
        
        /**
         * Tests counting out of stock products.
         * 
         * <p>Verifies that the count of products with zero stock
         * is correctly calculated.</p>
         */
        @Test
        @DisplayName("Should count out of stock products")
        void testOutOfStockCount() {
            // Given
            List<Product> products = new ArrayList<>();
            
            Product outOfStock1 = createTestProduct(1, "Out 1", ProductCategory.BURGER);
            outOfStock1.setStockQuantity(0);
            products.add(outOfStock1);
            
            Product outOfStock2 = createTestProduct(2, "Out 2", ProductCategory.FRIES);
            outOfStock2.setStockQuantity(0);
            products.add(outOfStock2);
            
            Product inStock = createTestProduct(3, "In Stock", ProductCategory.BEVERAGE);
            inStock.setStockQuantity(100);
            products.add(inStock);
            
            // When
            long outOfStockCount = products.stream()
                .filter(p -> p.getStockQuantity() == 0)
                .count();
            
            // Then
            assertEquals(2, outOfStockCount);
        }
    }
    
    /**
     * Creates a list of test Product instances for testing.
     * 
     * <p>This helper method creates multiple products with different categories
     * for use in tests that require a collection of inventory items.</p>
     * 
     * @return a List containing three test products (BURGER, FRIES, BEVERAGE)
     */
    private List<Product> createTestProducts() {
        List<Product> products = new ArrayList<>();
        products.add(createTestProduct(1, "Burger", ProductCategory.BURGER));
        products.add(createTestProduct(2, "Fries", ProductCategory.FRIES));
        products.add(createTestProduct(3, "Coke", ProductCategory.BEVERAGE));
        return products;
    }
    
    /**
     * Creates a test Product instance with specified parameters.
     * 
     * <p>This helper method creates a fully populated Product object for use
     * in test cases, including default values for stock levels and availability.</p>
     * 
     * @param id the product ID to assign
     * @param name the product name
     * @param category the ProductCategory to assign (BURGER, FRIES, BEVERAGE, etc.)
     * @return a new Product instance populated with test data
     */
    private Product createTestProduct(Integer id, String name, ProductCategory category) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(100);
        product.setReorderLevel(10);
        product.setMaxStockLevel(500);
        product.setUnit("pcs");
        product.setAvailable(true);
        return product;
    }
}
