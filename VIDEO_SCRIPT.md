# Restaurant Management System - Video Demonstration Script

## 📹 Video Overview
- **Duration:** 5-7 minutes
- **Team Members:** [Add all team member names here]
- **Project:** Restaurant Management System (DBMS Final Project)

---

## 🎬 SECTION 1: Introduction (30 seconds)
**[All team members on screen or taking turns speaking]**

### Script:
> "Hello, we are [Team Member Names]. Today we'll be demonstrating our Restaurant Management System, a full-stack Java application with a MySQL database backend. This system manages restaurant operations including orders, products, customers, reservations, and employee management."

**[Show the application login screen]**

---

## 🎬 SECTION 2: Application Description (45 seconds)
**[Speaker: Team Member 1]**

### Script:
> "Our Restaurant Management System provides the following functionality:"

**[Navigate through the application while explaining]**

1. **User Authentication** - Secure login with role-based access (Admin, Server, Chef, Customer)
2. **Order Management** - Create, view, modify, and complete customer orders
3. **Product/Menu Management** - Add, update, delete menu items with categories
4. **Inventory Control** - Track stock levels with low-stock alerts
5. **Customer Management** - Customer profiles with loyalty points system
6. **Table & Reservation System** - Manage restaurant tables and bookings
7. **Employee Management** - Schedules, time clock, leave requests
8. **Reporting** - Sales reports, inventory reports, employee hours

---

## 🎬 SECTION 3: Data Schema Description (1.5 minutes)
**[Speaker: Team Member 2]**

### Script:
> "Let me walk you through our database schema and logical design."

**[Show MySQL Workbench or the ER Diagram from FINAL_REPORT.md]**

### 3.1 Database Overview
> "Our database `restaurant_db` contains **17 tables** in **3rd Normal Form**:"

**Core Tables (show each briefly):**
| Table | Purpose |
|-------|---------|
| `users` | Employee accounts with role-based access |
| `products` | Menu items with pricing and stock |
| `product_categories` | Menu categorization |
| `customers` | Customer profiles and loyalty data |
| `orders` | Customer orders |
| `order_items` | Line items for each order |
| `bills` | Payment records |
| `restaurant_tables` | Physical table management |
| `reservations` | Table bookings |
| `employee_schedules` | Work schedules |
| `leave_requests` | Time-off requests |
| `time_clock` | Clock in/out records |
| `inventory_adjustments` | Stock audit trail |
| `loyalty_transactions` | Points history |
| `customer_preferences` | Dietary preferences |
| `kitchen_order_items` | Kitchen display items |
| `order_status_history` | Order state tracking |

### 3.2 Key Relationships
> "All tables have properly defined PRIMARY KEYS and FOREIGN KEYS with ON UPDATE CASCADE and ON DELETE constraints."

**[Show in MySQL Workbench]**
```sql
-- Example: Show foreign key definition
SHOW CREATE TABLE orders;
-- Point out: FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
```

### 3.3 Database Programming Objects
> "We've implemented extensive database programming objects:"

**📁 Show SQL File: `restaurant_db_complete.sql`**

| Object Type | Examples | SQL File Lines |
|-------------|----------|----------------|
| **Stored Procedures** | `sp_create_bill`, `sp_add_loyalty_points`, `sp_adjust_inventory`, `sp_create_reservation` | Lines 1438-1844 |
| **Functions** | `fn_calculate_tax`, `fn_calculate_bill_total`, `fn_get_loyalty_points`, `fn_get_membership_tier` | Lines 1845-2098 |
| **Triggers** | `trg_prevent_negative_points`, `trg_update_product_availability`, `trg_reduce_stock_on_order` | Lines 2799-2980 |
| **Views** | `v_kitchen_display`, `v_daily_sales_summary`, `v_low_stock_alert`, `v_customer_loyalty_summary` | Lines 968-1430 |

**[Run this command in MySQL]**
```sql
-- Show all programming objects
SHOW PROCEDURE STATUS WHERE Db = 'restaurant_db';
SHOW FUNCTION STATUS WHERE Db = 'restaurant_db';
SHOW TRIGGERS FROM restaurant_db;
SHOW EVENTS FROM restaurant_db;
```

---

## 🎬 SECTION 4: CRUD Operations Demonstration (3 minutes)
**[Speaker: Team Member 3 - or rotate between members]**

### ⚠️ IMPORTANT: Show BOTH the application AND MySQL Workbench side-by-side for verification!

---

### 4.1 CREATE Operation - Add New Product (45 seconds)

#### 📂 CODE TO SHOW:

**File: `src/com/restaurant/dao/ProductDAOImpl.java`**
```java
// Lines 29-66: createProduct() method
@Override
public Product createProduct(Product product) throws SQLException {
    // First, get category_id from category name
    Integer categoryId = getCategoryIdByName(product.getCategory().name());
    
    String sql = "INSERT INTO products (product_name, category_id, price, description, " +
                "stock_quantity, reorder_level, max_stock_level, unit, is_available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = dbConfig.createNewConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        // ... parameter binding
    }
}
```
> "This is our DAO layer that handles the INSERT operation using prepared statements."

**File: `src/com/restaurant/controller/ProductController.java`**
```java
// Lines 32-67: createProduct() method in controller
public boolean createProduct(String name, String category, BigDecimal price, String description) {
    // Validation
    if (name == null || name.trim().isEmpty()) {
        showError("Product name is required");
        return false;
    }
    // ... validation and product creation
    Product created = productDAO.createProduct(product);  // Line 59
    return created != null && created.getProductId() != null;
}
```
> "Our controller handles validation before calling the DAO."

**Step 1: Show current state in MySQL**
```sql
-- Before: Show products table
SELECT product_id, product_name, price, stock_quantity 
FROM products 
ORDER BY product_id DESC 
LIMIT 5;
```
> "Here are the current products in our database."

**Step 2: Perform CREATE in Application**
1. Login as **Admin** (username: `admin`, password: `admin123`)
2. Go to **Products Tab**
3. Click **"Add Product"**
4. Enter:
   - Product Name: "Chocolate Lava Cake"
   - Category: Desserts
   - Price: $8.99
   - Stock: 25
5. Click **Save**

> "We've added a new dessert to our menu."

**Step 3: VERIFY in MySQL Workbench**
```sql
-- After: Verify the new product exists
SELECT product_id, product_name, price, stock_quantity 
FROM products 
WHERE product_name = 'Chocolate Lava Cake';
```
> "As you can see, the new product has been successfully inserted into the database with product_id [X]."

---

### 4.2 READ Operation - View Orders (30 seconds)

#### 📂 CODE TO SHOW:

**File: `src/com/restaurant/dao/ProductDAOImpl.java`**
```java
// Lines 91-109: getAllProducts() method
@Override
public List<Product> getAllProducts() throws SQLException {
    String sql = "SELECT p.product_id, p.product_name, p.price, p.description, " +
                "p.stock_quantity, p.reorder_level, p.max_stock_level, p.unit, " +
                "p.is_available, p.created_at, p.updated_at, pc.category_name " +
                "FROM products p " +
                "JOIN product_categories pc ON p.category_id = pc.category_id " +
                "ORDER BY p.product_name";
    // ... execute query and return list
}
```
> "This SELECT query joins products with categories to display full product information."

**File: `src/com/restaurant/controller/ProductController.java`**
```java
// Lines 147-156: getAllProducts() method
public List<Product> getAllProducts() {
    try {
        return productDAO.getAllProducts();  // Line 149
    } catch (SQLException e) {
        showError("Failed to load products: " + e.getMessage());
        return List.of();
    }
}
```

**Step 1: Show data in MySQL**
```sql
-- Show orders data
SELECT o.order_id, o.order_number, o.order_status, u.full_name as server
FROM orders o
JOIN users u ON o.user_id = u.user_id
ORDER BY o.created_at DESC
LIMIT 5;
```

**Step 2: Perform READ in Application**
1. Go to **Orders Tab** or **Server View**
2. View the list of orders

> "The application reads and displays the same order data from our database. Notice the order numbers and statuses match exactly."

---

### 4.3 UPDATE Operation - Modify Product Price (45 seconds)

#### 📂 CODE TO SHOW:

**File: `src/com/restaurant/dao/ProductDAOImpl.java`**
```java
// Lines 163-198: updateProduct() method
@Override
public boolean updateProduct(Product product) throws SQLException {
    Integer categoryId = getCategoryIdByName(product.getCategory().name());
    
    String sql = "UPDATE products SET product_name = ?, category_id = ?, price = ?, " +
                "description = ?, stock_quantity = ?, reorder_level = ?, " +
                "max_stock_level = ?, unit = ?, is_available = ? WHERE product_id = ?";
    
    try (Connection conn = dbConfig.createNewConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, product.getProductName());
        pstmt.setBigDecimal(3, product.getPrice());
        // ... more parameters
        pstmt.setInt(10, product.getProductId());
        
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    }
}
```
> "The UPDATE query modifies the product and returns true if successful."

**File: `src/com/restaurant/controller/ProductController.java`**
```java
// Lines 72-113: updateProduct() method
public boolean updateProduct(Integer productId, String name, String category, 
                             BigDecimal price, String description, boolean available) {
    // Validation and duplicate check
    Optional<Product> existingOpt = productDAO.findProductById(productId);
    // ... update logic
    return productDAO.updateProduct(existing);  // Line 103
}
```

**Step 1: Show current state in MySQL**
```sql
-- Before: Show current price
SELECT product_id, product_name, price 
FROM products 
WHERE product_name = 'Chocolate Lava Cake';
```
> "The current price is $8.99."

**Step 2: Perform UPDATE in Application**
1. In **Products Tab**, find "Chocolate Lava Cake"
2. Click **Edit**
3. Change price from $8.99 to **$9.99**
4. Click **Save**

> "We've updated the price."

**Step 3: VERIFY in MySQL Workbench**
```sql
-- After: Verify the price change
SELECT product_id, product_name, price, updated_at
FROM products 
WHERE product_name = 'Chocolate Lava Cake';
```
> "The database now shows the updated price of $9.99, and notice the `updated_at` timestamp was automatically updated by our trigger."

---

### 4.4 DELETE Operation - Remove Product (45 seconds)

#### 📂 CODE TO SHOW:

**File: `src/com/restaurant/dao/ProductDAOImpl.java`**
```java
// Lines 200-213: deleteProduct() method
@Override
public boolean deleteProduct(Integer productId) throws SQLException {
    String sql = "DELETE FROM products WHERE product_id = ?";
    
    try (Connection conn = dbConfig.createNewConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, productId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    }
}
```
> "Simple DELETE query using prepared statement to prevent SQL injection."

**File: `src/com/restaurant/controller/ProductController.java`**
```java
// Lines 120-144: deleteProduct() method with confirmation
public boolean deleteProduct(Integer productId) {
    try {
        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to delete this product?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            return productDAO.deleteProduct(productId);  // Line 134
        }
        return false;
    } catch (SQLException e) {
        showError("Database error: " + e.getMessage());
        return false;
    }
}
```
> "We show a confirmation dialog before deleting to prevent accidental deletions."

**Step 1: Show current state in MySQL**
```sql
-- Before: Confirm product exists
SELECT product_id, product_name 
FROM products 
WHERE product_name = 'Chocolate Lava Cake';
```
> "The product currently exists in our database."

**Step 2: Perform DELETE in Application**
1. In **Products Tab**, find "Chocolate Lava Cake"
2. Click **Delete**
3. Confirm deletion in the dialog

> "We've deleted the product."

**Step 3: VERIFY in MySQL Workbench**
```sql
-- After: Verify deletion
SELECT product_id, product_name 
FROM products 
WHERE product_name = 'Chocolate Lava Cake';
-- Should return: Empty set (0 rows)
```
> "The query returns zero rows, confirming the product has been permanently deleted from the database."

---

## 🎬 SECTION 5: Additional Features Showcase (1 minute)
**[Quick demonstration - any team member]**

> "Let me quickly show some additional features:"

### 5.1 Customer Loyalty Points System (30 seconds)

#### 📂 CODE TO SHOW:

**File: `src/com/restaurant/view/CustomerView.java`**
```java
// Lines 1-25: Customer Portal with Loyalty Points
package com.restaurant.view;

import com.restaurant.controller.CustomerController;
import com.restaurant.model.Customer;
import com.restaurant.model.Customer.MembershipTier;

/**
 * Customer View - Customer Profile and Loyalty Points Management
 * Allows customers to view their profile, loyalty points, and membership status
 */
public class CustomerView extends JFrame {
    // Displays: Points balance, Membership tier (Bronze/Silver/Gold/Platinum)
    // Features: Point redemption, Transaction history
}
```

**File: `src/com/restaurant/controller/CustomerController.java`**
```java
// Lines 187-210: Loyalty Points Methods
public boolean addLoyaltyPoints(Integer customerId, Integer billId, BigDecimal billAmount) {
    String sql = "{CALL sp_add_loyalty_points(?, ?, ?)}";
    // Calls stored procedure to add points
}

public BigDecimal redeemLoyaltyPoints(Integer customerId, Integer points) {
    String sql = "{CALL sp_redeem_loyalty_points(?, ?, ?)}";
    // Returns discount value (100 points = $1.00)
}
```

**Demo Steps:**
1. Open **Customer Portal** from main menu
2. Enter phone number to lookup customer
3. Show **loyalty points balance** and **membership tier**
4. Demonstrate **points redemption** feature

> "Our loyalty program has 4 tiers: Bronze (5% discount), Silver (8%), Gold (12%), and Platinum (15%). Customers earn points on every purchase."

---

### 5.2 Database Programming Objects (30 seconds)

**1. Stored Procedure - File: `restaurant_db_complete.sql` (Lines 1438-1495)**
```sql
-- sp_create_bill procedure
DELIMITER //
CREATE PROCEDURE sp_create_bill(
    IN p_user_id INT,
    IN p_payment_method ENUM('CASH','CREDIT_CARD','DEBIT_CARD','MOBILE_PAYMENT'),
    IN p_cash_received DECIMAL(10,2),
    IN p_notes TEXT,
    OUT p_bill_id INT,
    OUT p_bill_number VARCHAR(50)
)
BEGIN
    -- Generate unique bill number
    -- Calculate totals using fn_calculate_tax function
    -- Insert into bills table
END //
```

**2. Trigger - File: `restaurant_db_complete.sql` (Lines 2915-2945)**
```sql
-- trg_reduce_stock_on_order - Automatically reduces stock when order is placed
CREATE TRIGGER trg_reduce_stock_on_order
    AFTER INSERT ON order_items
    FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE product_id = NEW.product_id;
END;
```

**3. View - File: `restaurant_db_complete.sql` (Lines 1086-1128)**
```sql
-- v_daily_sales_summary - Used by dashboard
CREATE VIEW v_daily_sales_summary AS
SELECT 
    DATE(b.created_at) as sale_date,
    COUNT(DISTINCT b.bill_id) as total_transactions,
    SUM(b.total_amount) as total_revenue,
    -- ... more aggregations
FROM bills b
GROUP BY DATE(b.created_at);
```

**Demo in MySQL:**
```sql
-- Show trigger effect
SELECT product_name, stock_quantity FROM products WHERE product_id = 1;
-- [Place an order with this product in the app]
-- Re-run query to show stock decreased
```

---

## 🎬 SECTION 6: Conclusion (30 seconds)
**[All team members]**

### Script:
> "In summary, our Restaurant Management System demonstrates:
> - A complex database schema with 17 tables in 3rd Normal Form
> - Proper use of primary keys, foreign keys with ON UPDATE/DELETE clauses
> - Extensive database programming objects (procedures, functions, triggers, events, views)
> - Full CRUD operations with verification
> - A user-friendly Java Swing interface
> - Robust error handling throughout the application
>
> Thank you for watching our demonstration!"

---

## 📋 Pre-Recording Checklist

### Database Setup
- [ ] MySQL Server running
- [ ] `restaurant_db` database loaded with sample data
- [ ] MySQL Workbench open and connected

### Application Setup
- [ ] Application built and running (`./gradlew run`)
- [ ] Login credentials ready:
  - Admin: `admin` / `admin123`
  - Server: `server1` / `server123`
  - Chef: `chef1` / `chef123`

### Recording Setup
- [ ] Screen recording software ready
- [ ] Microphone tested (clear audio)
- [ ] Resolution: 1080p recommended
- [ ] Both MySQL Workbench AND Application visible (split screen)

### Team Coordination
- [ ] Each member knows their speaking parts
- [ ] Smooth transitions planned between speakers
- [ ] Practice run completed

---

## 🎯 Rubric Points Covered

| Criterion | Points | How We Address It |
|-----------|--------|-------------------|
| Data schema described | 3 | Section 3: Full schema walkthrough |
| CRUD Demonstration | 3 | Section 4: All 4 operations with MySQL verification |
| Application description | 1 | Section 2: Complete feature overview |
| Quality of presentation | 1 | Clear audio, visible screens |
| Group members participate | 1 | All members have speaking parts |
| Flow of the video | 1 | Logical sequence: Intro → Schema → CRUD → Conclusion |

**Total Video Points: 10/10**

---

## 📝 Speaker Assignment Template

| Section | Duration | Speaker |
|---------|----------|---------|
| 1. Introduction | 30 sec | All members |
| 2. Application Description | 45 sec | Member 1: __________ |
| 3. Data Schema | 1.5 min | Member 2: __________ |
| 4. CRUD Operations | 3 min | Member 3: __________ |
| 5. Additional Features | 30 sec | Member 1: __________ |
| 6. Conclusion | 30 sec | All members |

**Total: ~6.5 minutes**

---

## 🚨 Common Mistakes to Avoid

1. ❌ Don't just show the app - ALWAYS verify in MySQL Workbench
2. ❌ Don't rush through the schema - take time to explain relationships
3. ❌ Don't forget to show programming objects (procedures, triggers, etc.)
4. ❌ Don't have only one person speak - all members must participate
5. ❌ Don't record with poor audio - test microphone first
6. ❌ Don't skip showing the "before" state when doing CRUD operations

---

## ✅ SQL Commands Reference for Demo

```sql
-- Connect to database
USE restaurant_db;

-- Show all tables
SHOW TABLES;

-- Show table structure
DESCRIBE products;
DESCRIBE orders;

-- Show foreign keys
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'restaurant_db'
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Show stored procedures
SHOW PROCEDURE STATUS WHERE Db = 'restaurant_db';

-- Show functions
SHOW FUNCTION STATUS WHERE Db = 'restaurant_db';

-- Show triggers
SHOW TRIGGERS FROM restaurant_db;

-- Show views
SHOW FULL TABLES WHERE Table_type = 'VIEW';

-- Show events
SHOW EVENTS FROM restaurant_db;

-- Count tables (should be 17)
SELECT COUNT(*) as table_count 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'restaurant_db' AND TABLE_TYPE = 'BASE TABLE';
```

---

**Good luck with your recording! 🎬**
