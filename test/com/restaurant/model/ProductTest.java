package com.restaurant.model;

import com.restaurant.model.Product.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link Product} model class.
 * 
 * <p>This test class provides comprehensive coverage of the Product model's functionality,
 * including basic properties, category management, availability status,
 * inventory tracking, timestamps, and constructors.</p>
 * 
 * <p>Tests are organized into nested classes by functionality:
 * <ul>
 *   <li>{@link BasicPropertiesTests} - Tests for basic product properties</li>
 *   <li>{@link CategoryTests} - Tests for product category enum handling</li>
 *   <li>{@link AvailabilityTests} - Tests for product availability status</li>
 *   <li>{@link InventoryTests} - Tests for stock and inventory management</li>
 *   <li>{@link TimestampTests} - Tests for timestamp handling</li>
 *   <li>{@link ConstructorTests} - Tests for default and parameterized constructors</li>
 * </ul>
 * </p>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 * @see Product
 * @see ProductCategory
 */
public class ProductTest {
    
    /** The test product instance used across test methods. */
    private Product product;
    
    /**
     * Sets up the test environment before each test.
     * Creates a fresh Product instance for each test case.
     */
    @BeforeEach
    void setUp() {
        product = new Product();
    }
    
    /**
     * Nested test class for basic product property tests.
     * 
     * <p>Validates getter and setter functionality for core product properties
     * including product ID, name, price, description, image path, and unit.</p>
     */
    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {
        
        /**
         * Tests that product ID can be set and retrieved correctly.
         * Verifies the setProductId and getProductId methods work as expected.
         */
        @Test
        @DisplayName("Should set and get product ID")
        void testProductId() {
            product.setProductId(1);
            assertEquals(1, product.getProductId());
        }
        
        /**
         * Tests that product name can be set and retrieved correctly.
         * Product name is the display name shown on the menu.
         */
        @Test
        @DisplayName("Should set and get product name")
        void testProductName() {
            product.setProductName("Classic Burger");
            assertEquals("Classic Burger", product.getProductName());
        }
        
        /**
         * Tests that price can be set and retrieved correctly.
         * Price represents the selling price of the product.
         */
        @Test
        @DisplayName("Should set and get price")
        void testPrice() {
            BigDecimal price = new BigDecimal("9.99");
            product.setPrice(price);
            assertEquals(price, product.getPrice());
        }
        
        /**
         * Tests that description can be set and retrieved correctly.
         * Description provides detailed information about the product.
         */
        @Test
        @DisplayName("Should set and get description")
        void testDescription() {
            product.setDescription("Delicious beef burger");
            assertEquals("Delicious beef burger", product.getDescription());
        }
        
        /**
         * Tests that image path can be set and retrieved correctly.
         * Image path stores the location of the product image file.
         */
        @Test
        @DisplayName("Should set and get image path")
        void testImagePath() {
            product.setImagePath("/images/burger.jpg");
            assertEquals("/images/burger.jpg", product.getImagePath());
        }
        
        /**
         * Tests that unit can be set and retrieved correctly.
         * Unit specifies the measurement unit (e.g., "pcs", "kg").
         */
        @Test
        @DisplayName("Should set and get unit")
        void testUnit() {
            product.setUnit("pcs");
            assertEquals("pcs", product.getUnit());
        }
    }
    
    /**
     * Nested test class for product category tests.
     * 
     * <p>Validates ProductCategory enum functionality including setting categories,
     * enum values, display names, and string conversion.</p>
     */
    @Nested
    @DisplayName("Category Tests")
    class CategoryTests {
        
        /**
         * Tests setting and getting BURGER category.
         * Verifies the category setter and getter for burger products.
         */
        @Test
        @DisplayName("Should set and get BURGER category")
        void testBurgerCategory() {
            product.setCategory(ProductCategory.BURGER);
            assertEquals(ProductCategory.BURGER, product.getCategory());
        }
        
        /**
         * Tests setting and getting BEVERAGE category.
         * Verifies the category setter and getter for beverage products.
         */
        @Test
        @DisplayName("Should set and get BEVERAGE category")
        void testBeverageCategory() {
            product.setCategory(ProductCategory.BEVERAGE);
            assertEquals(ProductCategory.BEVERAGE, product.getCategory());
        }
        
        /**
         * Tests that ProductCategory enum contains all expected values.
         * Verifies all product categories are available.
         */
        @Test
        @DisplayName("ProductCategory enum should have correct values")
        void testProductCategoryEnumValues() {
            ProductCategory[] categories = ProductCategory.values();
            assertThat(categories).contains(
                ProductCategory.BURGER,
                ProductCategory.CHICKEN_ROLL,
                ProductCategory.RICE_MEALS,
                ProductCategory.BEVERAGE,
                ProductCategory.FRIES,
                ProductCategory.DESSERTS
            );
        }
        
        /**
         * Tests that ProductCategory enum values have correct display names.
         * Verifies human-readable names for UI display purposes.
         */
        @Test
        @DisplayName("ProductCategory should have display names")
        void testProductCategoryDisplayNames() {
            assertEquals("Burger", ProductCategory.BURGER.getDisplayName());
            assertEquals("Beverage", ProductCategory.BEVERAGE.getDisplayName());
            assertEquals("Rice Meals", ProductCategory.RICE_MEALS.getDisplayName());
        }
        
        /**
         * Tests that ProductCategory.fromString works with display name.
         * Verifies parsing category from display name format.
         */
        @Test
        @DisplayName("ProductCategory fromString should work with display name")
        void testProductCategoryFromStringDisplayName() {
            ProductCategory category = ProductCategory.fromString("Burger");
            assertEquals(ProductCategory.BURGER, category);
        }
        
        /**
         * Tests that ProductCategory.fromString works with enum name.
         * Verifies parsing category from uppercase enum name.
         */
        @Test
        @DisplayName("ProductCategory fromString should work with enum name")
        void testProductCategoryFromStringEnumName() {
            ProductCategory category = ProductCategory.fromString("BURGER");
            assertEquals(ProductCategory.BURGER, category);
        }
        
        /**
         * Tests that ProductCategory.fromString returns null for invalid input.
         * Verifies graceful handling of unrecognized category names.
         */
        @Test
        @DisplayName("ProductCategory fromString should return null for invalid")
        void testProductCategoryFromStringInvalid() {
            ProductCategory category = ProductCategory.fromString("InvalidCategory");
            assertNull(category);
        }
    }
    
    /**
     * Nested test class for product availability tests.
     * 
     * <p>Validates availability status including default value
     * and setting available/unavailable status.</p>
     */
    @Nested
    @DisplayName("Availability Tests")
    class AvailabilityTests {
        
        /**
         * Tests that new products default to available status.
         * Verifies products are available for ordering by default.
         */
        @Test
        @DisplayName("Should default to available")
        void testDefaultAvailable() {
            Product newProduct = new Product();
            assertTrue(newProduct.isAvailable());
        }
        
        /**
         * Tests that available status can be set to true.
         * Verifies the setAvailable method for making products available.
         */
        @Test
        @DisplayName("Should set available to true")
        void testSetAvailableTrue() {
            product.setAvailable(true);
            assertTrue(product.isAvailable());
        }
        
        /**
         * Tests that available status can be set to false.
         * Verifies the setAvailable method for making products unavailable.
         */
        @Test
        @DisplayName("Should set available to false")
        void testSetAvailableFalse() {
            product.setAvailable(false);
            assertFalse(product.isAvailable());
        }
    }
    
    /**
     * Nested test class for inventory management tests.
     * 
     * <p>Validates inventory-related properties including stock quantity,
     * reorder level, and maximum stock level.</p>
     */
    @Nested
    @DisplayName("Inventory Tests")
    class InventoryTests {
        
        /**
         * Tests that stock quantity can be set and retrieved correctly.
         * Stock quantity tracks current inventory level.
         */
        @Test
        @DisplayName("Should set and get stock quantity")
        void testStockQuantity() {
            product.setStockQuantity(100);
            assertEquals(100, product.getStockQuantity());
        }
        
        /**
         * Tests that reorder level can be set and retrieved correctly.
         * Reorder level triggers alerts when stock falls below this threshold.
         */
        @Test
        @DisplayName("Should set and get reorder level")
        void testReorderLevel() {
            product.setReorderLevel(10);
            assertEquals(10, product.getReorderLevel());
        }
        
        /**
         * Tests that max stock level can be set and retrieved correctly.
         * Max stock level defines the maximum inventory to maintain.
         */
        @Test
        @DisplayName("Should set and get max stock level")
        void testMaxStockLevel() {
            product.setMaxStockLevel(500);
            assertEquals(500, product.getMaxStockLevel());
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
         * Verifies new products have a non-null creation timestamp.
         */
        @Test
        @DisplayName("Should set createdAt on construction")
        void testCreatedAtOnConstruction() {
            Product newProduct = new Product();
            assertNotNull(newProduct.getCreatedAt());
        }
        
        /**
         * Tests that updatedAt timestamp can be set and retrieved correctly.
         * Updated at records when the product was last modified.
         */
        @Test
        @DisplayName("Should set and get updated at timestamp")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            product.setUpdatedAt(now);
            assertEquals(now, product.getUpdatedAt());
        }
    }
    
    /**
     * Nested test class for constructor tests.
     * 
     * <p>Validates proper initialization of Product objects through
     * default and parameterized constructors.</p>
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Tests that the default constructor creates an available product.
         * Verifies default initialization with available status and timestamp.
         */
        @Test
        @DisplayName("Default constructor should create available product")
        void testDefaultConstructor() {
            Product newProduct = new Product();
            assertNotNull(newProduct);
            assertTrue(newProduct.isAvailable());
            assertNotNull(newProduct.getCreatedAt());
        }
        
        /**
         * Tests that the parameterized constructor sets all fields correctly.
         * Verifies name, category, price, description, imagePath, and availability.
         */
        @Test
        @DisplayName("Parameterized constructor should set all fields")
        void testParameterizedConstructor() {
            Product newProduct = new Product(
                "Test Burger",
                ProductCategory.BURGER,
                new BigDecimal("12.99"),
                "Test description",
                "/images/test.jpg"
            );
            
            assertEquals("Test Burger", newProduct.getProductName());
            assertEquals(ProductCategory.BURGER, newProduct.getCategory());
            assertEquals(new BigDecimal("12.99"), newProduct.getPrice());
            assertEquals("Test description", newProduct.getDescription());
            assertEquals("/images/test.jpg", newProduct.getImagePath());
            assertTrue(newProduct.isAvailable());
        }
    }
}
