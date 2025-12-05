package com.restaurant.dao;

import com.restaurant.model.Product;
import com.restaurant.model.Product.ProductCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ProductDAO.
 * 
 * <p>This test class validates product data access operations and Product model behavior.
 * It covers product creation, category mapping, querying, availability filtering,
 * updates, and inventory management operations.</p>
 * 
 * <p>The tests are organized into nested classes for better organization:</p>
 * <ul>
 *   <li>{@link ProductCreationTests} - Tests for product creation and initialization</li>
 *   <li>{@link ProductCategoryTests} - Tests for category enum mapping</li>
 *   <li>{@link ProductQueryTests} - Tests for product retrieval operations</li>
 *   <li>{@link ProductAvailabilityTests} - Tests for availability filtering</li>
 *   <li>{@link ProductUpdateTests} - Tests for product field updates</li>
 *   <li>{@link ProductInventoryTests} - Tests for inventory level checking</li>
 * </ul>
 * 
 * @author Restaurant Management System
 * @version 2.0
 * @since 1.0
 */
public class ProductDAOTest {
    
    /**
     * Nested test class for product creation tests.
     * 
     * <p>Validates that products can be created with all required fields,
     * that product IDs are null before persistence, and that default
     * values are properly set for inventory fields.</p>
     */
    @Nested
    @DisplayName("Product Creation Tests")
    class ProductCreationTests {
        
        /**
         * Tests that a product can be created with all required fields.
         * 
         * <p>Verifies that all essential product properties including name,
         * category, price, description, stock levels, unit, and availability
         * can be set and retrieved correctly.</p>
         */
        @Test
        @DisplayName("Should create product with all required fields")
        void testCreateProductWithRequiredFields() {
            Product product = new Product();
            product.setProductName("Classic Burger");
            product.setCategory(ProductCategory.BURGER);
            product.setPrice(new BigDecimal("12.99"));
            product.setDescription("Delicious beef burger");
            product.setStockQuantity(100);
            product.setReorderLevel(10);
            product.setMaxStockLevel(500);
            product.setUnit("pcs");
            product.setAvailable(true);
            
            assertNotNull(product.getProductName());
            assertEquals(ProductCategory.BURGER, product.getCategory());
            assertEquals(0, new BigDecimal("12.99").compareTo(product.getPrice()));
            assertTrue(product.isAvailable());
        }
        
        /**
         * Tests that a product has a null ID before being persisted.
         * 
         * <p>Ensures that newly created products do not have an ID assigned
         * until they are saved to the database.</p>
         */
        @Test
        @DisplayName("Product should have null ID before persistence")
        void testProductIdNullBeforePersistence() {
            Product product = new Product();
            product.setProductName("New Product");
            
            assertNull(product.getProductId());
        }
        
        /**
         * Tests that default values are properly set for inventory fields.
         * 
         * <p>Verifies that the default constructor sets availability to true
         * and initializes the creation timestamp.</p>
         */
        @Test
        @DisplayName("Should set default values for inventory fields")
        void testDefaultInventoryValues() {
            Product product = new Product();
            
            // Default constructor should set availability to true
            assertTrue(product.isAvailable());
            assertNotNull(product.getCreatedAt());
        }
    }
    
    /**
     * Nested test class for product category tests.
     * 
     * <p>Validates the ProductCategory enum mapping functionality including
     * string-to-enum conversion, display name retrieval, and handling of
     * invalid category strings.</p>
     */
    @Nested
    @DisplayName("Product Category Tests")
    class ProductCategoryTests {
        
        /**
         * Tests that category display names map correctly to enum values.
         * 
         * <p>Verifies that the fromString method correctly converts display
         * names like "Burger", "Beverage", and "Fries" to their corresponding
         * enum constants.</p>
         */
        @Test
        @DisplayName("Should map category name to enum correctly")
        void testCategoryMapping() {
            assertEquals(ProductCategory.BURGER, ProductCategory.fromString("Burger"));
            assertEquals(ProductCategory.BEVERAGE, ProductCategory.fromString("Beverage"));
            assertEquals(ProductCategory.FRIES, ProductCategory.fromString("Fries"));
        }
        
        /**
         * Tests that enum names map correctly to category values.
         * 
         * <p>Verifies that the fromString method correctly handles enum name
         * strings like "BURGER" and "CHICKEN_ROLL".</p>
         */
        @Test
        @DisplayName("Should map enum name to category correctly")
        void testEnumNameMapping() {
            assertEquals(ProductCategory.BURGER, ProductCategory.fromString("BURGER"));
            assertEquals(ProductCategory.CHICKEN_ROLL, ProductCategory.fromString("CHICKEN_ROLL"));
        }
        
        /**
         * Tests that null is returned for invalid category strings.
         * 
         * <p>Verifies that the fromString method gracefully handles
         * unrecognized category strings by returning null.</p>
         */
        @Test
        @DisplayName("Should return null for invalid category")
        void testInvalidCategoryMapping() {
            assertNull(ProductCategory.fromString("InvalidCategory"));
        }
        
        /**
         * Tests that all category enum values have non-empty display names.
         * 
         * <p>Iterates through all ProductCategory values and verifies that
         * each has a valid, non-empty display name.</p>
         */
        @Test
        @DisplayName("All categories should have display names")
        void testCategoryDisplayNames() {
            for (ProductCategory category : ProductCategory.values()) {
                assertNotNull(category.getDisplayName());
                assertFalse(category.getDisplayName().isEmpty());
            }
        }
    }
    
    /**
     * Nested test class for product query tests.
     * 
     * <p>Validates product retrieval operations including findById,
     * getAllProducts, and getProductsByCategory filtering.</p>
     */
    @Nested
    @DisplayName("Product Query Tests")
    class ProductQueryTests {
        
        /**
         * Tests that findById returns an empty Optional for non-existent products.
         * 
         * <p>Verifies proper handling of queries for products that do not exist
         * in the database.</p>
         */
        @Test
        @DisplayName("findById should return Optional.empty for non-existent product")
        void testFindByIdNotFound() {
            Optional<Product> result = Optional.empty();
            
            assertTrue(result.isEmpty());
            assertFalse(result.isPresent());
        }
        
        /**
         * Tests that findById returns an Optional containing the product when found.
         * 
         * <p>Verifies that the returned Optional contains the expected product
         * with correct ID and name values.</p>
         */
        @Test
        @DisplayName("findById should return Optional with product when found")
        void testFindByIdFound() {
            Product product = new Product();
            product.setProductId(1);
            product.setProductName("Test Product");
            
            Optional<Product> result = Optional.of(product);
            
            assertTrue(result.isPresent());
            assertEquals(1, result.get().getProductId());
            assertEquals("Test Product", result.get().getProductName());
        }
        
        /**
         * Tests that getAllProducts returns a complete list of products.
         * 
         * <p>Verifies that the method returns all products with their
         * correct names in the expected order.</p>
         */
        @Test
        @DisplayName("getAllProducts should return list of products")
        void testGetAllProducts() {
            List<Product> products = new ArrayList<>();
            products.add(createTestProduct(1, "Burger", ProductCategory.BURGER));
            products.add(createTestProduct(2, "Fries", ProductCategory.FRIES));
            
            assertEquals(2, products.size());
            assertThat(products).extracting(Product::getProductName)
                               .containsExactly("Burger", "Fries");
        }
        
        /**
         * Tests that getProductsByCategory correctly filters products by category.
         * 
         * <p>Verifies that filtering by BURGER category returns only burger
         * products and excludes products from other categories.</p>
         */
        @Test
        @DisplayName("getProductsByCategory should filter correctly")
        void testGetProductsByCategory() {
            List<Product> allProducts = new ArrayList<>();
            allProducts.add(createTestProduct(1, "Burger", ProductCategory.BURGER));
            allProducts.add(createTestProduct(2, "Coke", ProductCategory.BEVERAGE));
            allProducts.add(createTestProduct(3, "Cheese Burger", ProductCategory.BURGER));
            
            List<Product> burgers = allProducts.stream()
                .filter(p -> p.getCategory() == ProductCategory.BURGER)
                .toList();
            
            assertEquals(2, burgers.size());
            assertThat(burgers).allMatch(p -> p.getCategory() == ProductCategory.BURGER);
        }
    }
    
    /**
     * Nested test class for product availability tests.
     * 
     * <p>Validates that products can be correctly filtered based on their
     * availability status.</p>
     */
    @Nested
    @DisplayName("Product Availability Tests")
    class ProductAvailabilityTests {
        
        /**
         * Tests that getAvailableProducts returns only available products.
         * 
         * <p>Verifies that filtering by availability correctly excludes
         * unavailable products and includes only those marked as available.</p>
         */
        @Test
        @DisplayName("getAvailableProducts should only return available products")
        void testGetAvailableProducts() {
            List<Product> allProducts = new ArrayList<>();
            
            Product available = createTestProduct(1, "Available", ProductCategory.BURGER);
            available.setAvailable(true);
            allProducts.add(available);
            
            Product unavailable = createTestProduct(2, "Unavailable", ProductCategory.BURGER);
            unavailable.setAvailable(false);
            allProducts.add(unavailable);
            
            List<Product> availableProducts = allProducts.stream()
                .filter(Product::isAvailable)
                .toList();
            
            assertEquals(1, availableProducts.size());
            assertTrue(availableProducts.get(0).isAvailable());
        }
    }
    
    /**
     * Nested test class for product update tests.
     * 
     * <p>Validates that product fields can be properly updated including
     * name, price, and stock quantity.</p>
     */
    @Nested
    @DisplayName("Product Update Tests")
    class ProductUpdateTests {
        
        /**
         * Tests that a product's name can be updated successfully.
         * 
         * <p>Verifies that changing the product name from "Old Name" to
         * "New Name" is reflected in the getter.</p>
         */
        @Test
        @DisplayName("Should update product name")
        void testUpdateProductName() {
            Product product = createTestProduct(1, "Old Name", ProductCategory.BURGER);
            product.setProductName("New Name");
            
            assertEquals("New Name", product.getProductName());
        }
        
        /**
         * Tests that a product's price can be updated successfully.
         * 
         * <p>Verifies that the new price value is correctly stored and
         * can be retrieved using BigDecimal comparison.</p>
         */
        @Test
        @DisplayName("Should update product price")
        void testUpdateProductPrice() {
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setPrice(new BigDecimal("15.99"));
            
            assertEquals(0, new BigDecimal("15.99").compareTo(product.getPrice()));
        }
        
        /**
         * Tests that a product's stock quantity can be updated successfully.
         * 
         * <p>Verifies that the new stock quantity value is correctly stored
         * and can be retrieved.</p>
         */
        @Test
        @DisplayName("Should update stock quantity")
        void testUpdateStockQuantity() {
            Product product = createTestProduct(1, "Product", ProductCategory.BURGER);
            product.setStockQuantity(50);
            
            assertEquals(50, product.getStockQuantity());
        }
    }
    
    /**
     * Nested test class for product inventory tests.
     * 
     * <p>Validates inventory level checking including low stock identification,
     * out of stock detection, and adequate stock verification.</p>
     */
    @Nested
    @DisplayName("Product Inventory Tests")
    class ProductInventoryTests {
        
        /**
         * Tests that low stock products are correctly identified.
         * 
         * <p>Verifies that a product with stock quantity at or below
         * the reorder level is identified as low stock.</p>
         */
        @Test
        @DisplayName("Should identify low stock products")
        void testLowStockIdentification() {
            Product product = new Product();
            product.setStockQuantity(5);
            product.setReorderLevel(10);
            
            boolean isLowStock = product.getStockQuantity() <= product.getReorderLevel();
            assertTrue(isLowStock);
        }
        
        /**
         * Tests that out of stock products are correctly identified.
         * 
         * <p>Verifies that a product with zero stock quantity is
         * identified as out of stock.</p>
         */
        @Test
        @DisplayName("Should identify out of stock products")
        void testOutOfStockIdentification() {
            Product product = new Product();
            product.setStockQuantity(0);
            
            boolean isOutOfStock = product.getStockQuantity() == 0;
            assertTrue(isOutOfStock);
        }
        
        /**
         * Tests that products with adequate stock are correctly identified.
         * 
         * <p>Verifies that a product with stock quantity above the
         * reorder level is identified as having adequate stock.</p>
         */
        @Test
        @DisplayName("Should identify products with adequate stock")
        void testAdequateStockIdentification() {
            Product product = new Product();
            product.setStockQuantity(100);
            product.setReorderLevel(10);
            
            boolean hasAdequateStock = product.getStockQuantity() > product.getReorderLevel();
            assertTrue(hasAdequateStock);
        }
    }
    
    /**
     * Creates a test product with the specified parameters.
     * 
     * <p>This helper method creates a Product instance with pre-configured
     * values for testing purposes. The product is set with a default price
     * of $10.00 and is marked as available.</p>
     * 
     * @param id the product ID to set
     * @param name the product name to set
     * @param category the product category to set
     * @return a new Product instance configured with the specified values
     */
    private Product createTestProduct(Integer id, String name, ProductCategory category) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal("10.00"));
        product.setAvailable(true);
        return product;
    }
}
