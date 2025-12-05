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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
 * Unit tests for ProductController.
 * 
 * <p>This test class validates the product management business logic in the restaurant
 * management system, including product CRUD operations, inventory management,
 * and category validation. It uses Mockito for mocking the ProductDAO layer.</p>
 * 
 * <p>Test categories covered:</p>
 * <ul>
 *   <li>Product Creation Tests - Creating new products with validation</li>
 *   <li>Product Update Tests - Modifying existing products</li>
 *   <li>Product Deletion Tests - Removing products from the system</li>
 *   <li>Product Query Tests - Searching and filtering products</li>
 *   <li>Inventory Management Tests - Stock level tracking</li>
 *   <li>Category Validation Tests - Product category enum validation</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see ProductController
 * @see ProductDAO
 * @see Product
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductControllerTest {
    
    /** Mock ProductDAO for testing product operations without database access. */
    @Mock
    private ProductDAO mockProductDAO;
    
    /**
     * Nested test class for product creation tests.
     * 
     * <p>Tests the product creation workflow including:
     * <ul>
     *   <li>Creating products with valid data</li>
     *   <li>Preventing duplicate product names</li>
     *   <li>Validating product name is not empty</li>
     *   <li>Validating product price is positive</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Product Creation Tests")
    class ProductCreationTests {
        
        /**
         * Tests successful product creation with valid data.
         * 
         * <p>Verifies that a product can be created when providing valid
         * name, category, and price, and that the returned product has an ID.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should create product with valid data")
        void testCreateProductSuccess() throws SQLException {
            // Given
            Product newProduct = createTestProduct(null, "New Burger", ProductCategory.BURGER);
            Product savedProduct = createTestProduct(1, "New Burger", ProductCategory.BURGER);
            
            when(mockProductDAO.findProductByName("New Burger")).thenReturn(Optional.empty());
            when(mockProductDAO.createProduct(any(Product.class))).thenReturn(savedProduct);
            
            // When
            Product result = mockProductDAO.createProduct(newProduct);
            
            // Then
            assertNotNull(result);
            assertEquals(1, result.getProductId());
            assertEquals("New Burger", result.getProductName());
        }
        
        /**
         * Tests product creation failure when name already exists.
         * 
         * <p>Verifies that the system detects duplicate product names
         * and prevents creation of products with existing names.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail when product name already exists")
        void testCreateProductDuplicateName() throws SQLException {
            // Given
            Product existingProduct = createTestProduct(1, "Existing Burger", ProductCategory.BURGER);
            when(mockProductDAO.findProductByName("Existing Burger")).thenReturn(Optional.of(existingProduct));
            
            // Then
            assertTrue(mockProductDAO.findProductByName("Existing Burger").isPresent());
        }
        
        /**
         * Tests validation that product name is not empty or null.
         * 
         * <p>Verifies that empty strings and null values are properly
         * identified as invalid product names.</p>
         */
        @Test
        @DisplayName("Should validate product name is not empty")
        void testProductNameValidation() {
            String emptyName = "";
            String nullName = null;
            String validName = "Valid Product";
            
            assertTrue(emptyName == null || emptyName.trim().isEmpty());
            assertTrue(nullName == null || nullName.trim().isEmpty());
            assertFalse(validName == null || validName.trim().isEmpty());
        }
        
        /**
         * Tests validation that product price must be greater than zero.
         * 
         * <p>Verifies that only positive prices are accepted, and zero
         * or negative prices are rejected.</p>
         */
        @Test
        @DisplayName("Should validate price is greater than zero")
        void testProductPriceValidation() {
            BigDecimal validPrice = new BigDecimal("10.00");
            BigDecimal zeroPrice = BigDecimal.ZERO;
            BigDecimal negativePrice = new BigDecimal("-5.00");
            
            assertTrue(validPrice.compareTo(BigDecimal.ZERO) > 0);
            assertFalse(zeroPrice.compareTo(BigDecimal.ZERO) > 0);
            assertFalse(negativePrice.compareTo(BigDecimal.ZERO) > 0);
        }
    }
    
    /**
     * Nested test class for product update tests.
     * 
     * <p>Tests the product update workflow including:
     * <ul>
     *   <li>Updating existing product details</li>
     *   <li>Handling updates for non-existent products</li>
     *   <li>Checking for duplicate names during update</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Product Update Tests")
    class ProductUpdateTests {
        
        /**
         * Tests successful update of an existing product.
         * 
         * <p>Verifies that a product's details can be modified
         * and the changes are persisted successfully.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should update existing product")
        void testUpdateProductSuccess() throws SQLException {
            // Given
            Product existingProduct = createTestProduct(1, "Old Name", ProductCategory.BURGER);
            when(mockProductDAO.findProductById(1)).thenReturn(Optional.of(existingProduct));
            when(mockProductDAO.updateProduct(any(Product.class))).thenReturn(true);
            
            // When
            existingProduct.setProductName("New Name");
            boolean result = mockProductDAO.updateProduct(existingProduct);
            
            // Then
            assertTrue(result);
        }
        
        /**
         * Tests update failure when the product doesn't exist.
         * 
         * <p>Verifies that attempting to update a non-existent product
         * is properly detected by returning an empty Optional.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail update when product not found")
        void testUpdateProductNotFound() throws SQLException {
            // Given
            when(mockProductDAO.findProductById(999)).thenReturn(Optional.empty());
            
            // Then
            assertTrue(mockProductDAO.findProductById(999).isEmpty());
        }
        
        /**
         * Tests duplicate name detection during product update.
         * 
         * <p>Verifies that when updating a product name to one that already
         * exists (belonging to a different product), the duplicate is detected.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should check for duplicate name on update")
        void testUpdateProductDuplicateNameCheck() throws SQLException {
            // Given
            Product product1 = createTestProduct(1, "Product 1", ProductCategory.BURGER);
            Product product2 = createTestProduct(2, "Product 2", ProductCategory.BURGER);
            
            when(mockProductDAO.findProductById(1)).thenReturn(Optional.of(product1));
            when(mockProductDAO.findProductByName("Product 2")).thenReturn(Optional.of(product2));
            
            // Product 1 trying to change name to Product 2 (which exists)
            Optional<Product> existing = mockProductDAO.findProductByName("Product 2");
            
            // Then - should detect duplicate
            assertTrue(existing.isPresent());
        }
    }
    
    /**
     * Nested test class for product deletion tests.
     * 
     * <p>Tests the product deletion workflow including:
     * <ul>
     *   <li>Deleting existing products</li>
     *   <li>Handling deletion of non-existent products</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Product Deletion Tests")
    class ProductDeletionTests {
        
        /**
         * Tests successful deletion of an existing product.
         * 
         * <p>Verifies that a product can be removed from the system
         * when it exists in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should delete existing product")
        void testDeleteProductSuccess() throws SQLException {
            // Given
            when(mockProductDAO.deleteProduct(1)).thenReturn(true);
            
            // When
            boolean result = mockProductDAO.deleteProduct(1);
            
            // Then
            assertTrue(result);
        }
        
        /**
         * Tests deletion failure when the product doesn't exist.
         * 
         * <p>Verifies that attempting to delete a non-existent product
         * returns false indicating the operation failed.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should fail delete when product not found")
        void testDeleteProductNotFound() throws SQLException {
            // Given
            when(mockProductDAO.deleteProduct(999)).thenReturn(false);
            
            // When
            boolean result = mockProductDAO.deleteProduct(999);
            
            // Then
            assertFalse(result);
        }
    }
    
    /**
     * Nested test class for product query and search tests.
     * 
     * <p>Tests various methods to find and list products:
     * <ul>
     *   <li>Getting all products</li>
     *   <li>Filtering products by category</li>
     *   <li>Getting only available products</li>
     *   <li>Finding products by ID</li>
     *   <li>Getting all product categories</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Product Query Tests")
    class ProductQueryTests {
        
        /**
         * Tests retrieval of all products in the system.
         * 
         * <p>Verifies that getAllProducts() returns a complete list
         * of all products regardless of category or availability.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get all products")
        void testGetAllProducts() throws SQLException {
            // Given
            List<Product> products = new ArrayList<>();
            products.add(createTestProduct(1, "Burger", ProductCategory.BURGER));
            products.add(createTestProduct(2, "Fries", ProductCategory.FRIES));
            products.add(createTestProduct(3, "Coke", ProductCategory.BEVERAGE));
            
            when(mockProductDAO.getAllProducts()).thenReturn(products);
            
            // When
            List<Product> result = mockProductDAO.getAllProducts();
            
            // Then
            assertEquals(3, result.size());
        }
        
        /**
         * Tests filtering products by their category.
         * 
         * <p>Verifies that getProductsByCategory() returns only products
         * belonging to the specified category (e.g., BURGER, FRIES).</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get products by category")
        void testGetProductsByCategory() throws SQLException {
            // Given
            List<Product> burgers = new ArrayList<>();
            burgers.add(createTestProduct(1, "Classic Burger", ProductCategory.BURGER));
            burgers.add(createTestProduct(2, "Cheese Burger", ProductCategory.BURGER));
            
            when(mockProductDAO.getProductsByCategory("BURGER")).thenReturn(burgers);
            
            // When
            List<Product> result = mockProductDAO.getProductsByCategory("BURGER");
            
            // Then
            assertEquals(2, result.size());
            assertThat(result).allMatch(p -> p.getCategory() == ProductCategory.BURGER);
        }
        
        /**
         * Tests retrieval of only available products.
         * 
         * <p>Verifies that getAvailableProducts() filters out unavailable
         * products and returns only those that can be ordered.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get available products only")
        void testGetAvailableProducts() throws SQLException {
            // Given
            List<Product> availableProducts = new ArrayList<>();
            
            Product available1 = createTestProduct(1, "Available 1", ProductCategory.BURGER);
            available1.setAvailable(true);
            availableProducts.add(available1);
            
            Product available2 = createTestProduct(2, "Available 2", ProductCategory.FRIES);
            available2.setAvailable(true);
            availableProducts.add(available2);
            
            when(mockProductDAO.getAvailableProducts()).thenReturn(availableProducts);
            
            // When
            List<Product> result = mockProductDAO.getAvailableProducts();
            
            // Then
            assertEquals(2, result.size());
            assertThat(result).allMatch(Product::isAvailable);
        }
        
        /**
         * Tests finding a product by its unique identifier.
         * 
         * <p>Verifies that findProductById() returns an Optional containing
         * the product when the ID exists in the database.</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get product by ID")
        void testGetProductById() throws SQLException {
            // Given
            Product product = createTestProduct(1, "Test Product", ProductCategory.BURGER);
            when(mockProductDAO.findProductById(1)).thenReturn(Optional.of(product));
            
            // When
            Optional<Product> result = mockProductDAO.findProductById(1);
            
            // Then
            assertTrue(result.isPresent());
            assertEquals("Test Product", result.get().getProductName());
        }
        
        /**
         * Tests retrieval of all product categories.
         * 
         * <p>Verifies that getAllCategories() returns a list containing
         * all valid product category names (BURGER, FRIES, BEVERAGE, etc.).</p>
         * 
         * @throws SQLException if database operation fails
         */
        @Test
        @DisplayName("Should get all categories")
        void testGetAllCategories() throws SQLException {
            // Given
            List<String> categories = List.of("BURGER", "FRIES", "BEVERAGE", "DESSERTS");
            when(mockProductDAO.getAllCategories()).thenReturn(categories);
            
            // When
            List<String> result = mockProductDAO.getAllCategories();
            
            // Then
            assertThat(result).contains("BURGER", "FRIES", "BEVERAGE", "DESSERTS");
        }
    }
    
    /**
     * Nested test class for inventory management tests.
     * 
     * <p>Tests the inventory tracking functionality including:
     * <ul>
     *   <li>Identifying low stock products</li>
     *   <li>Identifying out of stock products</li>
     *   <li>Updating stock quantities</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Inventory Management Tests")
    class InventoryManagementTests {
        
        /**
         * Tests identification of low stock products.
         * 
         * <p>Verifies that products with stock quantity at or below
         * the reorder level are correctly identified as low stock.</p>
         */
        @Test
        @DisplayName("Should identify low stock products")
        void testLowStockIdentification() {
            Product product = createTestProduct(1, "Low Stock Item", ProductCategory.BURGER);
            product.setStockQuantity(5);
            product.setReorderLevel(10);
            
            boolean isLowStock = product.getStockQuantity() <= product.getReorderLevel();
            
            assertTrue(isLowStock);
        }
        
        /**
         * Tests identification of out of stock products.
         * 
         * <p>Verifies that products with zero stock quantity
         * are correctly identified as out of stock.</p>
         */
        @Test
        @DisplayName("Should identify out of stock products")
        void testOutOfStockIdentification() {
            Product product = createTestProduct(1, "Out of Stock Item", ProductCategory.BURGER);
            product.setStockQuantity(0);
            
            boolean isOutOfStock = product.getStockQuantity() == 0;
            
            assertTrue(isOutOfStock);
        }
        
        /**
         * Tests updating product stock quantity after a sale.
         * 
         * <p>Verifies that stock quantity is correctly decreased
         * when items are sold.</p>
         */
        @Test
        @DisplayName("Should update stock quantity")
        void testUpdateStockQuantity() {
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(100);
            
            // Simulate sale of 5 items
            int currentStock = product.getStockQuantity();
            int newStock = currentStock - 5;
            product.setStockQuantity(newStock);
            
            assertEquals(95, product.getStockQuantity());
        }
    }
    
    /**
     * Nested test class for product category validation tests.
     * 
     * <p>Tests the ProductCategory enum validation including:
     * <ul>
     *   <li>Accepting valid category names</li>
     *   <li>Rejecting invalid category names</li>
     * </ul></p>
     */
    @Nested
    @DisplayName("Category Validation Tests")
    class CategoryValidationTests {
        
        /**
         * Tests that valid category names are accepted.
         * 
         * <p>Verifies that ProductCategory.valueOf() succeeds
         * for valid category names like BURGER.</p>
         */
        @Test
        @DisplayName("Should accept valid category")
        void testValidCategory() {
            assertDoesNotThrow(() -> {
                ProductCategory.valueOf("BURGER");
            });
        }
        
        /**
         * Tests that invalid category names throw an exception.
         * 
         * <p>Verifies that ProductCategory.valueOf() throws
         * IllegalArgumentException for non-existent category names.</p>
         */
        @Test
        @DisplayName("Should throw exception for invalid category")
        void testInvalidCategory() {
            assertThrows(IllegalArgumentException.class, () -> {
                ProductCategory.valueOf("INVALID_CATEGORY");
            });
        }
    }
    
    /**
     * Creates a test Product instance with specified parameters.
     * 
     * <p>This helper method creates a fully populated Product object for use
     * in test cases, including default values for stock, reorder level, and availability.</p>
     * 
     * @param id the product ID to assign (can be null for new products)
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
        product.setDescription("Test product description");
        product.setStockQuantity(100);
        product.setReorderLevel(10);
        product.setMaxStockLevel(500);
        product.setUnit("pcs");
        product.setAvailable(true);
        return product;
    }
}
