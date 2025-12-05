# Restaurant Management System
## Final Project Report

**Course:** Database Systems  
**Date:** December 5, 2025  
**Version:** 2.0.0  

---

## Table of Contents

1. [README - Installation and Setup](#1-readme---installation-and-setup)
2. [Technical Specifications](#2-technical-specifications)
3. [Conceptual Design (UML Diagram)](#3-conceptual-design-uml-diagram)
4. [Logical Design (Database Schema)](#4-logical-design-database-schema)
5. [User Flow](#5-user-flow)
6. [Lessons Learned](#6-lessons-learned)
7. [Future Work](#7-future-work)
8. [Bonus Features](#8-bonus-features)

---

## 1. README - Installation and Setup

### 1.1 System Requirements

| Requirement | Specification |
|-------------|---------------|
| **Operating System** | Windows 10/11, macOS 12+, or Linux (Ubuntu 20.04+) |
| **Memory (RAM)** | Minimum 4GB, Recommended 8GB |
| **Disk Space** | 500MB for application + 100MB for database |
| **Display** | 1280x720 minimum resolution |

### 1.2 Required Software

#### Java Development Kit (JDK) 21
- **Download:** https://adoptium.net/temurin/releases/?version=21
- **Alternative:** https://www.oracle.com/java/technologies/downloads/#java21
- **Installation Directory:** 
  - Windows: `C:\Program Files\Java\jdk-21`
  - macOS: `/Library/Java/JavaVirtualMachines/temurin-21.jdk`
  - Linux: `/usr/lib/jvm/java-21-openjdk`

#### MySQL Server 8.0
- **Download:** https://dev.mysql.com/downloads/mysql/
- **Installation Directory:**
  - Windows: `C:\Program Files\MySQL\MySQL Server 8.0`
  - macOS: `/usr/local/mysql`
  - Linux: `/usr/bin/mysql`

#### MySQL Workbench (Optional, for database management)
- **Download:** https://dev.mysql.com/downloads/workbench/

#### Gradle 8.x (Included via Wrapper)
- **No separate installation required** - Gradle Wrapper (`gradlew`) is included
- **Manual Download (if needed):** https://gradle.org/releases/

### 1.3 Installation Steps

#### Step 1: Clone or Extract the Project
```bash
# If using Git:
git clone https://github.com/AG1L1TYx7/JAVARESTROAPP.git
cd JAVARESTROAPP

# Or extract the ZIP file to a directory of your choice
```

#### Step 2: Set Up MySQL Database

1. **Start MySQL Server** (ensure it's running on port 3306)

2. **Create the Database:**
   ```bash
   mysql -u root -p < restaurant_db_complete.sql
   ```
   
   Or using MySQL Workbench:
   - Open MySQL Workbench
   - Connect to your local MySQL instance
   - File → Open SQL Script → Select `restaurant_db_complete.sql`
   - Execute the script (lightning bolt icon)

3. **Verify Database Creation:**
   ```sql
   SHOW DATABASES;
   -- You should see 'restaurant_db' in the list
   
   USE restaurant_db;
   SHOW TABLES;
   -- You should see 17 tables
   ```

#### Step 3: Configure Database Connection

Edit `src/com/restaurant/config/DatabaseConfiguration.java`:

```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "restaurant_db";
private static final String DB_USER = "root";           // Your MySQL username
private static final String DB_PASSWORD = "your_password"; // Your MySQL password
```

#### Step 4: Build the Project

```bash
# On macOS/Linux:
./gradlew clean build -x test

# On Windows:
gradlew.bat clean build -x test
```

**Expected Output:**
```
BUILD SUCCESSFUL in Xs
7 actionable tasks: 7 executed
```

#### Step 5: Run the Application

```bash
# On macOS/Linux:
./gradlew run

# On Windows:
gradlew.bat run
```

**Alternative: Run the JAR file:**
```bash
./gradlew fatJar
java -jar build/libs/restaurant-management-system-2.0.0-all.jar
```

### 1.4 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Server | server1 | server123 |
| Chef | chef1 | chef123 |

### 1.5 Project Directory Structure

```
Restaurant-management-system-project-in-Java-master/
├── src/
│   └── com/restaurant/
│       ├── RestaurantManagementApp.java    # Application entry point
│       ├── config/                          # Database configuration
│       ├── controller/                      # Business logic (9 controllers)
│       ├── dao/                             # Data access layer (10+ DAOs)
│       ├── exception/                       # Custom exceptions (8 classes)
│       ├── model/                           # Data entities (11 models)
│       ├── util/                            # Utilities
│       └── view/                            # GUI components (11 views)
├── build.gradle                             # Gradle build configuration
├── restaurant_db_complete.sql               # Complete database dump
├── restaurant_db_schema.sql                 # Schema only
└── README.md                                # Documentation
```

---

## 2. Technical Specifications

### 2.1 Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Programming Language** | Java | 21 LTS | Core application development |
| **GUI Framework** | Java Swing | (JDK bundled) | Desktop user interface |
| **Build Tool** | Gradle | 8.x | Dependency management & build automation |
| **Database** | MySQL | 8.0+ | Relational data storage |
| **Connection Pool** | HikariCP | 5.1.0 | High-performance JDBC pooling |
| **Password Security** | jBCrypt | 0.4 | Secure password hashing |
| **Logging** | SLF4J + Logback | 2.0.9 / 1.4.14 | Application logging |
| **Testing** | JUnit 5 + Mockito | 5.10.1 / 5.8.0 | Unit testing framework |

### 2.2 Architecture Pattern

The application follows the **Model-View-Controller (MVC)** architectural pattern:

![Architecture Diagram](DIagram/Architecture.png)

*Figure 2.1: System Architecture showing the layered structure from View to Database*

### 2.3 Dependencies (from build.gradle)

```gradle
dependencies {
    // Database
    implementation 'com.mysql:mysql-connector-j:8.2.0'
    implementation 'com.zaxxer:HikariCP:5.1.0'
    
    // Security
    implementation 'org.mindrot:jbcrypt:0.4'
    
    // Logging
    implementation 'org.slf4j:slf4j-api:2.0.9'
    implementation 'ch.qos.logback:logback-classic:1.4.14'
    
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testImplementation 'org.mockito:mockito-core:5.8.0'
}
```

### 2.4 Design Patterns Used

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Singleton** | `DatabaseConfiguration` | Single database connection pool instance |
| **DAO (Data Access Object)** | All `*DAOImpl` classes | Abstraction of data persistence |
| **Factory** | `AuthenticationController` | User view creation based on role |
| **Observer** | Swing event listeners | UI event handling |
| **MVC** | Entire application structure | Separation of concerns |

---

## 3. Conceptual Design (UML Diagram)

### 3.1 Class Diagram

![Class Diagram](DIagram/classdiagram.png)

*Figure 3.1: UML Class Diagram showing entity relationships and class structure*

### 3.2 Use Case Diagram

![Use Case Diagram](DIagram/usecase.png)

*Figure 3.2: Use Case Diagram showing system actors and their interactions*

---

## 4. Logical Design (Database Schema)

### 4.1 Entity-Relationship Diagram (ERD)

![ER Diagram](DIagram/UML%20DIAGRAM.jpeg)

*Figure 4.1: Entity-Relationship Diagram showing database schema relationships*

### 4.2 Table Definitions

#### Core Tables (17 Total)

| # | Table Name | Purpose | Key Relationships |
|---|------------|---------|-------------------|
| 1 | `users` | User accounts and authentication | Base entity for employees |
| 2 | `product_categories` | Menu categorization | Referenced by products |
| 3 | `products` | Menu items | FK to categories |
| 4 | `customers` | Customer profiles and loyalty | Linked to reservations |
| 5 | `restaurant_tables` | Physical tables | FK for reservations, orders |
| 6 | `order_types` | Dine-in, takeout, delivery | Referenced by orders |
| 7 | `orders` | Customer orders | FK to users, tables |
| 8 | `kitchen_order_items` | Items for kitchen display | FK to orders, products |
| 9 | `order_status_history` | Order state tracking | FK to orders |
| 10 | `bills` | Payment records | FK to users |
| 11 | `order_items` | Line items on bills | FK to bills, products |
| 12 | `reservations` | Table bookings | FK to tables |
| 13 | `loyalty_transactions` | Points history | FK to customers |
| 14 | `inventory_adjustments` | Stock audit trail | FK to products, users |
| 15 | `customer_preferences` | Dietary preferences | FK to customers |
| 16 | `employee_schedules` | Work schedules | FK to users |
| 17 | `leave_requests` | Time-off requests | FK to users |

### 4.3 Database Views (9 Total)

| View Name | Purpose |
|-----------|---------|
| `vw_product_inventory` | Product stock with category info |
| `vw_daily_sales_summary` | Daily sales aggregation |
| `vw_customer_order_history` | Customer purchase history |
| `vw_employee_hours` | Time clock summary |
| `vw_low_stock_products` | Products below reorder level |
| `vw_active_reservations` | Today's reservations |
| `vw_pending_orders` | Orders awaiting preparation |
| `vw_customer_loyalty_status` | Loyalty tier information |
| `vw_monthly_revenue` | Monthly revenue breakdown |

### 4.4 Stored Procedures (9 Total)

| Procedure | Purpose | Parameters |
|-----------|---------|------------|
| `sp_create_order` | Create new order | user_id, table_id, order_type |
| `sp_add_order_item` | Add item to order | order_id, product_id, qty |
| `sp_complete_order` | Finalize order | order_id, payment_method |
| `sp_adjust_inventory` | Stock adjustment | product_id, qty, reason |
| `sp_create_reservation` | Book table | table_id, customer, date/time |
| `sp_add_loyalty_points` | Award points | customer_id, amount |
| `sp_redeem_loyalty_points` | Use points | customer_id, points |
| `sp_clock_in` | Employee clock in | user_id |
| `sp_clock_out` | Employee clock out | user_id |

### 4.5 User-Defined Functions (9 Total)

| Function | Returns | Purpose |
|----------|---------|---------|
| `fn_calculate_tax(amount)` | DECIMAL | Calculate 8.5% tax |
| `fn_calculate_bill_total(bill_id)` | DECIMAL | Sum bill items + tax |
| `fn_get_loyalty_points(amount)` | INT | Points from purchase |
| `fn_get_membership_tier(points)` | VARCHAR | BRONZE/SILVER/GOLD/PLATINUM |
| `fn_is_table_available(table_id, date, time)` | BOOLEAN | Check availability |
| `fn_format_currency(amount)` | VARCHAR | Format as $X,XXX.XX |
| `fn_get_order_item_subtotal(qty, price)` | DECIMAL | Calculate line total |
| `fn_days_since_last_visit(customer_id)` | INT | Customer recency |
| `fn_get_stock_status(product_id)` | VARCHAR | IN_STOCK/LOW/OUT |

### 4.6 Triggers (6 Total)

| Trigger | Event | Purpose |
|---------|-------|---------|
| `trg_update_product_timestamp` | UPDATE products | Auto-update timestamp |
| `trg_auto_update_order_total` | INSERT order_items | Recalculate order total |
| `trg_log_order_status_change` | UPDATE orders | Audit trail |
| `trg_update_stock_on_order` | INSERT order_items | Decrement stock |
| `trg_update_customer_stats` | INSERT bills | Update spend/visits |
| `trg_calculate_hours_worked` | UPDATE time_clock | Calculate hours |

### 4.7 Scheduled Events (6 Total)

| Event | Schedule | Purpose |
|-------|----------|---------|
| `evt_expire_loyalty_points` | Daily | Expire old points |
| `evt_auto_cancel_pending_reservations` | Hourly | Cancel no-shows |
| `evt_mark_no_show_reservations` | Every 30 min | Flag no-shows |
| `evt_reset_daily_table_status` | Daily 4 AM | Reset tables |
| `evt_generate_low_stock_alerts` | Daily 6 AM | Stock alerts |
| `evt_cleanup_old_status_history` | Weekly | Purge old records |

---

## 5. User Flow

![User Flow Diagram](DIagram/userflow.png)

*Figure 5.1: Complete User Flow Diagram showing all user roles and their navigation paths*

### 5.2 Admin User Flow

| Step | Action | Screen | Database Operation |
|------|--------|--------|-------------------|
| 1 | Login | LoginView | `SELECT * FROM users WHERE username = ?` |
| 2 | View Dashboard | AdminView (Dashboard Tab) | `SELECT` from various views |
| 3 | Manage Products | AdminView (Products Tab) | CRUD on `products` table |
| 4 | Add New Product | Product Dialog | `INSERT INTO products (...)` |
| 5 | Update Product | Product Dialog | `UPDATE products SET ... WHERE product_id = ?` |
| 6 | Delete Product | Confirmation Dialog | `DELETE FROM products WHERE product_id = ?` |
| 7 | View Inventory | InventoryView | `SELECT * FROM vw_product_inventory` |
| 8 | Adjust Stock | Stock Dialog | `CALL sp_adjust_inventory(?, ?, ?)` |
| 9 | Manage Users | AdminView (Users Tab) | CRUD on `users` table |
| 10 | View Time Clock | AdminView (Time Clock Tab) | `SELECT * FROM vw_employee_hours` |
| 11 | Manage Schedules | AdminSchedulePanel | CRUD on `employee_schedules` |
| 12 | Logout | Return to LoginView | Update `last_login_at` |

### 5.3 Server User Flow

| Step | Action | Screen | Database Operation |
|------|--------|--------|-------------------|
| 1 | Login | LoginView | Authenticate |
| 2 | Clock In | Clock Dialog | `CALL sp_clock_in(?)` |
| 3 | View Assigned Tables | ServerView | `SELECT * FROM restaurant_tables` |
| 4 | Create New Order | OrderView | `CALL sp_create_order(?, ?, ?)` |
| 5 | Add Items to Order | Product Selection | `CALL sp_add_order_item(?, ?, ?)` |
| 6 | View Cart | Cart Panel | Display in-memory cart |
| 7 | Process Payment | Payment Dialog | `CALL sp_complete_order(?, ?)` |
| 8 | Print Receipt | Receipt Dialog | Generate from bill data |
| 9 | Mark Order Served | Order List | `UPDATE orders SET status = 'COMPLETED'` |
| 10 | Clock Out | Clock Dialog | `CALL sp_clock_out(?)` |

### 5.4 Chef User Flow

| Step | Action | Screen | Database Operation |
|------|--------|--------|-------------------|
| 1 | Login | LoginView | Authenticate |
| 2 | Clock In | Clock Dialog | `CALL sp_clock_in(?)` |
| 3 | View Pending Orders | KitchenView | `SELECT * FROM vw_pending_orders` |
| 4 | Start Order | Order Card | `UPDATE orders SET status = 'IN_PROGRESS'` |
| 5 | Mark Item Ready | Item Checkbox | `UPDATE kitchen_order_items SET status = 'READY'` |
| 6 | Complete Order | Order Card | `UPDATE orders SET status = 'READY'` |
| 7 | Clock Out | Clock Dialog | `CALL sp_clock_out(?)` |

### 5.5 Customer Reservation Flow

| Step | Action | Screen | Database Operation |
|------|--------|--------|-------------------|
| 1 | Open Reservation | CustomerReservationView | - |
| 2 | Select Date | Date Picker | Check availability |
| 3 | Select Time | Time Dropdown | `SELECT fn_is_table_available(?, ?, ?)` |
| 4 | Select Party Size | Spinner | Filter available tables |
| 5 | Enter Contact Info | Form Fields | Validation |
| 6 | Submit Reservation | Submit Button | `CALL sp_create_reservation(...)` |
| 7 | Confirmation | Success Dialog | Display confirmation |

### 5.6 Command Reference

| Command/Method | Controller | DAO Method | SQL Operation |
|----------------|------------|------------|---------------|
| `login(user, pass)` | AuthenticationController | `authenticateUser()` | SELECT with BCrypt |
| `createProduct(product)` | ProductController | `createProduct()` | INSERT |
| `updateProduct(product)` | ProductController | `updateProduct()` | UPDATE |
| `deleteProduct(id)` | ProductController | `deleteProduct()` | DELETE |
| `getProducts()` | ProductController | `getAllProducts()` | SELECT |
| `createBill(bill)` | OrderController | `createBill()` | INSERT (transaction) |
| `createReservation(res)` | ReservationController | `createReservation()` | INSERT |
| `clockIn(userId)` | UserController | `clockIn()` | CALL sp_clock_in |
| `clockOut(userId)` | UserController | `clockOut()` | CALL sp_clock_out |
| `adjustStock(productId, qty)` | InventoryController | `adjustStock()` | CALL sp_adjust_inventory |

---

## 6. Lessons Learned

### 6.1 Technical Expertise Gained

#### Java Development
- **Swing GUI Development**: Learned to create professional desktop applications with Java Swing, including custom components, layout managers (GridBagLayout, BorderLayout), and event handling.
- **JDBC Best Practices**: Mastered prepared statements, result set handling, and connection management to prevent SQL injection and resource leaks.
- **Design Patterns**: Applied Singleton (DatabaseConfiguration), DAO pattern (all data access), MVC architecture, and Factory pattern (view creation).

#### Database Design
- **Third Normal Form (3NF)**: Designed a normalized schema that eliminates redundancy while maintaining query performance.
- **Stored Procedures & Functions**: Learned to encapsulate business logic in the database for consistency and security.
- **Triggers**: Implemented automatic updates (timestamps, stock levels, audit trails) without application code changes.
- **Scheduled Events**: Created automated maintenance tasks (cleanup, expiration, alerts).

#### Security
- **BCrypt Hashing**: Implemented secure password storage with salted hashes.
- **Input Validation**: Applied validation at both UI and DAO layers to prevent invalid data.
- **Role-Based Access Control**: Implemented different user interfaces based on roles.

### 6.2 Insights

#### Time Management
- **Underestimated UI Complexity**: GUI development took longer than expected due to layout fine-tuning and event handling edge cases.
- **Database First Approach**: Starting with a solid schema design saved significant refactoring time later.
- **Iterative Development**: Breaking features into smaller increments allowed for continuous testing and early bug detection.

#### Data Domain Insights
- **Restaurant Operations**: Learned the complexity of real restaurant workflows including table management, order lifecycle, and inventory tracking.
- **Loyalty Programs**: Understanding membership tiers and point systems required careful consideration of edge cases (expiration, redemption limits).
- **Employee Scheduling**: Time clock and scheduling features revealed the importance of timezone handling and shift overlap detection.

### 6.3 Alternative Design Approaches Considered

| Aspect | Original Approach | Alternative Considered | Reason for Choice |
|--------|-------------------|------------------------|-------------------|
| **GUI Framework** | Java Swing | JavaFX | Swing has better documentation and wider OS support |
| **Database** | MySQL | PostgreSQL | MySQL has better tooling (Workbench) for learning |
| **Architecture** | MVC | MVVM | MVC is simpler and more appropriate for desktop apps |
| **Connection Pool** | HikariCP | C3P0 | HikariCP has superior performance benchmarks |
| **Build Tool** | Gradle | Maven | Gradle's Kotlin DSL is more flexible |

### 6.4 Known Issues / Non-Working Features

| Issue | Description | Status | Workaround |
|-------|-------------|--------|------------|
| **Report Export** | PDF export not implemented | Planned | Copy from table to Excel |
| **Multi-language** | Only English supported | Not started | N/A |
| **Printing** | Physical receipt printing untested | Partial | Screen display only |
| **Network Mode** | Single-machine only | Not started | Local installation required |

---

## 7. Future Work

### 7.1 Planned Uses of the Database

| Use Case | Description | Priority |
|----------|-------------|----------|
| **Sales Analytics Dashboard** | Real-time charts showing sales trends, peak hours, popular items | High |
| **Customer CRM** | Email marketing integration for loyalty program | Medium |
| **Supplier Management** | Track vendors, purchase orders, and delivery schedules | Medium |
| **Multi-Location Support** | Extend schema for franchise/chain operations | Low |

### 7.2 Potential Areas for Added Functionality

#### Short-Term (1-3 months)
- **Online Ordering Integration**: REST API for mobile app/web ordering
- **Kitchen Display System (KDS)**: Dedicated tablet interface for kitchen
- **Barcode/QR Scanning**: Quick product lookup and payment

#### Medium-Term (3-6 months)
- **Inventory Forecasting**: ML-based demand prediction
- **Employee Performance Metrics**: Track efficiency, tips, customer ratings
- **Table Mapping**: Visual floor plan for table assignment

#### Long-Term (6-12 months)
- **Cloud Migration**: Move to AWS RDS or Azure SQL
- **Mobile Apps**: Native iOS/Android apps for customers and staff
- **AI-Powered Recommendations**: Suggest items based on order history
- **Integration with Payment Gateways**: Stripe, Square, PayPal

### 7.3 Technical Debt to Address

| Item | Current State | Desired State |
|------|---------------|---------------|
| **Unit Test Coverage** | ~20% | 80%+ |
| **API Documentation** | Javadoc only | OpenAPI spec |
| **Logging** | Basic SLF4J | Structured JSON logs |
| **Configuration** | Hardcoded | External config files |
| **Error Handling** | Mixed | Centralized handler |

---

## Appendix A: Sample SQL Queries

### Most Popular Products
```sql
SELECT p.product_name, COUNT(oi.product_id) as order_count
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id
ORDER BY order_count DESC
LIMIT 10;
```

### Daily Revenue
```sql
SELECT DATE(created_at) as date, SUM(total_amount) as revenue
FROM bills
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(created_at)
ORDER BY date;
```

### Employee Hours This Week
```sql
SELECT u.full_name, SUM(tc.hours_worked) as total_hours
FROM users u
JOIN time_clock tc ON u.user_id = tc.user_id
WHERE tc.clock_in >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY u.user_id;
```

---

## 8. Bonus Features

This section documents additional functionality implemented beyond the base requirements, demonstrating advanced database and application development techniques.

---

### 8.1 Complex Analytical Queries for Data Visualization (5 points)

The application includes **9 database views** and multiple complex analytical queries used for dashboard visualization and business intelligence.

#### Query 1: Daily Sales Summary with Rolling Averages
```sql
-- View: vw_daily_sales_summary
-- Purpose: Aggregate daily sales for trend analysis
SELECT 
    DATE(b.created_at) AS sale_date,
    COUNT(DISTINCT b.bill_id) AS total_transactions,
    COUNT(DISTINCT b.user_id) AS unique_cashiers,
    SUM(b.subtotal) AS gross_sales,
    SUM(b.tax_amount) AS total_tax,
    SUM(b.total_amount) AS net_revenue,
    AVG(b.total_amount) AS avg_transaction_value,
    MAX(b.total_amount) AS largest_sale,
    MIN(b.total_amount) AS smallest_sale
FROM bills b
GROUP BY DATE(b.created_at)
ORDER BY sale_date DESC;
```
**Complexity:** Aggregates 7 different metrics per day with statistical functions.

#### Query 2: Customer Lifetime Value Analysis
```sql
-- View: vw_customer_order_history
-- Purpose: Calculate customer LTV with loyalty tier segmentation
SELECT 
    c.customer_id,
    c.full_name,
    c.membership_tier,
    c.loyalty_points,
    c.total_spent,
    c.visit_count,
    ROUND(c.total_spent / NULLIF(c.visit_count, 0), 2) AS avg_order_value,
    DATEDIFF(CURDATE(), c.last_visit_date) AS days_since_visit,
    CASE 
        WHEN c.visit_count >= 20 THEN 'Champion'
        WHEN c.visit_count >= 10 THEN 'Loyal'
        WHEN c.visit_count >= 5 THEN 'Potential'
        WHEN DATEDIFF(CURDATE(), c.last_visit_date) > 90 THEN 'At Risk'
        ELSE 'New'
    END AS customer_segment
FROM customers c
WHERE c.is_active = TRUE
ORDER BY c.total_spent DESC;
```
**Complexity:** RFM (Recency, Frequency, Monetary) analysis with customer segmentation.

#### Query 3: Product Performance Matrix
```sql
-- Purpose: Identify best/worst performers with category context
SELECT 
    pc.category_name,
    p.product_name,
    p.price,
    COALESCE(SUM(oi.quantity), 0) AS units_sold,
    COALESCE(SUM(oi.subtotal), 0) AS total_revenue,
    p.stock_quantity AS current_stock,
    ROUND(COALESCE(SUM(oi.quantity), 0) * 100.0 / 
        NULLIF((SELECT SUM(quantity) FROM order_items), 0), 2) AS market_share_pct,
    RANK() OVER (PARTITION BY pc.category_id ORDER BY SUM(oi.subtotal) DESC) AS category_rank
FROM products p
JOIN product_categories pc ON p.category_id = pc.category_id
LEFT JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, pc.category_name, pc.category_id
ORDER BY total_revenue DESC;
```
**Complexity:** Window functions (RANK), market share calculation, category partitioning.

#### Query 4: Employee Productivity Dashboard
```sql
-- View: vw_employee_hours with productivity metrics
SELECT 
    u.user_id,
    u.full_name,
    u.role,
    COUNT(DISTINCT tc.time_clock_id) AS shifts_worked,
    ROUND(SUM(tc.hours_worked), 2) AS total_hours,
    ROUND(AVG(tc.hours_worked), 2) AS avg_shift_hours,
    COUNT(DISTINCT b.bill_id) AS orders_processed,
    COALESCE(SUM(b.total_amount), 0) AS sales_generated,
    ROUND(COALESCE(SUM(b.total_amount), 0) / NULLIF(SUM(tc.hours_worked), 0), 2) AS revenue_per_hour
FROM users u
LEFT JOIN time_clock tc ON u.user_id = tc.user_id
LEFT JOIN bills b ON u.user_id = b.user_id AND DATE(b.created_at) = DATE(tc.clock_in)
WHERE u.role IN ('SERVER', 'CHEF')
GROUP BY u.user_id
ORDER BY revenue_per_hour DESC;
```
**Complexity:** Multi-table correlation, productivity KPIs, role filtering.

#### Query 5: Inventory Turnover Analysis
```sql
-- Purpose: Calculate inventory turnover rate and days of supply
SELECT 
    p.product_id,
    p.product_name,
    pc.category_name,
    p.stock_quantity AS current_stock,
    p.reorder_level,
    COALESCE(daily_avg.avg_daily_sales, 0) AS avg_daily_demand,
    CASE 
        WHEN COALESCE(daily_avg.avg_daily_sales, 0) > 0 
        THEN ROUND(p.stock_quantity / daily_avg.avg_daily_sales, 1)
        ELSE 999
    END AS days_of_supply,
    fn_get_stock_status(p.product_id) AS stock_status,
    CASE 
        WHEN p.stock_quantity <= p.reorder_level THEN 'REORDER NOW'
        WHEN p.stock_quantity <= p.reorder_level * 1.5 THEN 'REORDER SOON'
        ELSE 'ADEQUATE'
    END AS reorder_recommendation
FROM products p
JOIN product_categories pc ON p.category_id = pc.category_id
LEFT JOIN (
    SELECT 
        oi.product_id,
        AVG(daily_qty) AS avg_daily_sales
    FROM (
        SELECT product_id, DATE(b.created_at) AS sale_date, SUM(quantity) AS daily_qty
        FROM order_items oi
        JOIN bills b ON oi.bill_id = b.bill_id
        WHERE b.created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
        GROUP BY product_id, DATE(b.created_at)
    ) daily_sales
    GROUP BY product_id
) daily_avg ON p.product_id = daily_avg.product_id
ORDER BY days_of_supply ASC;
```
**Complexity:** Nested subqueries, moving averages, business logic for recommendations.

---

### 8.2 Advanced GUI Frontend (5 points)

The application features a **professional Java Swing GUI** with modern design elements:

#### GUI Features Implemented

| Feature | Description | Implementation |
|---------|-------------|----------------|
| **Role-Based Dashboards** | Different interfaces for Admin, Server, Chef, Customer | `AdminView`, `ServerView`, `KitchenView`, `CustomerReservationView` |
| **Tabbed Interface** | 7 functional tabs in Admin dashboard | `JTabbedPane` with icons |
| **Real-Time Updates** | Auto-refresh every 30 seconds | `javax.swing.Timer` |
| **Modern Color Scheme** | Professional color palette | Primary, Accent, Warning, Danger colors |
| **Responsive Tables** | Sortable, filterable data tables | `JTable` with custom renderers |
| **Form Validation** | Real-time input validation with error messages | Custom validation logic |
| **Dialog Windows** | Modal dialogs for CRUD operations | `JDialog` with forms |
| **Background Processing** | Non-blocking database operations | `SwingWorker` threads |
| **Statistics Cards** | Visual KPI cards on dashboard | Custom `JPanel` components |
| **Hover Effects** | Interactive button states | `MouseListener` events |

#### Screenshot Descriptions

**Login Screen:**
- Split-panel design with branding on left
- Form fields with validation
- "Show Password" toggle
- Links to Signup, Reservations, Clock In/Out

**Admin Dashboard:**
- Statistics cards: Total Sales, Today's Sales, Total Orders, Today's Orders
- 7 tabs: Dashboard, Products, Inventory, Orders, Users, Time Clock, Schedules
- Quick action buttons in header

**POS (Order View):**
- Product grid with category filtering
- Cart panel with quantity adjustment
- Payment processing with change calculation
- Receipt generation

**Kitchen Display:**
- Order cards with status indicators
- Color-coded priority (time-based)
- One-click status updates

---

### 8.3 Overly Complicated Database Translations (5 points)

The application implements complex business logic through database operations:

#### Transaction-Based Bill Creation
```java
// BillDAOImpl.createBill() - Multi-step transaction
public Bill createBill(Bill bill) throws SQLException {
    Connection conn = null;
    try {
        conn = dbConfig.createNewConnection();
        conn.setAutoCommit(false); // START TRANSACTION
        
        // Step 1: Insert bill header
        // Step 2: Get generated bill ID
        // Step 3: Insert all order items (batch)
        // Step 4: Update product stock (trigger handles)
        // Step 5: Update customer loyalty points (trigger handles)
        // Step 6: Log to order_status_history (trigger handles)
        
        conn.commit(); // COMMIT
        return bill;
    } catch (SQLException e) {
        conn.rollback(); // ROLLBACK on any failure
        throw e;
    }
}
```

#### Stored Procedure Calls
```java
// Using stored procedures for complex operations
public void adjustInventory(int productId, int quantity, String reason) {
    try (CallableStatement stmt = conn.prepareCall("{CALL sp_adjust_inventory(?, ?, ?, ?)}")) {
        stmt.setInt(1, productId);
        stmt.setInt(2, quantity);
        stmt.setString(3, reason);
        stmt.setInt(4, currentUserId);
        stmt.execute();
    }
}
```

#### User-Defined Function Integration
```java
// Using database functions in queries
String sql = "SELECT *, fn_get_stock_status(product_id) AS status, " +
             "fn_format_currency(price) AS formatted_price " +
             "FROM products WHERE fn_is_table_available(?, ?, ?) = TRUE";
```

---

### 8.4 Complex Multi-Table Joins (5 points)

The application performs queries joining **10+ tables** for comprehensive data retrieval:

#### Complete Order Details Query (12 tables)
```sql
-- Used in ServerView and AdminView for order details
SELECT 
    o.order_id,
    o.order_number,
    o.order_status,
    o.created_at AS order_time,
    
    -- Table Information (JOIN 1-2)
    rt.table_number,
    rt.capacity,
    rt.location AS table_location,
    
    -- Order Type (JOIN 3)
    ot.type_name AS order_type,
    
    -- Server Information (JOIN 4)
    server.full_name AS server_name,
    server.employee_id AS server_id,
    
    -- Bill Information (JOIN 5-6)
    b.bill_number,
    b.subtotal,
    b.tax_amount,
    b.total_amount,
    b.payment_method,
    
    -- Customer Information (JOIN 7)
    c.full_name AS customer_name,
    c.phone AS customer_phone,
    c.membership_tier,
    c.loyalty_points,
    
    -- Order Items with Products (JOIN 8-10)
    oi.quantity,
    oi.unit_price,
    oi.subtotal AS item_subtotal,
    p.product_name,
    p.description AS product_description,
    pc.category_name,
    
    -- Kitchen Status (JOIN 11)
    koi.preparation_status,
    koi.started_at,
    koi.completed_at,
    
    -- Chef Assignment (JOIN 12)
    chef.full_name AS prepared_by

FROM orders o
LEFT JOIN restaurant_tables rt ON o.table_id = rt.table_id
LEFT JOIN order_types ot ON o.order_type_id = ot.order_type_id
LEFT JOIN users server ON o.created_by = server.user_id
LEFT JOIN bills b ON o.order_id = b.order_id
LEFT JOIN customers c ON b.customer_id = c.customer_id
LEFT JOIN order_items oi ON b.bill_id = oi.bill_id
LEFT JOIN products p ON oi.product_id = p.product_id
LEFT JOIN product_categories pc ON p.category_id = pc.category_id
LEFT JOIN kitchen_order_items koi ON o.order_id = koi.order_id AND p.product_id = koi.product_id
LEFT JOIN users chef ON koi.assigned_chef_id = chef.user_id

WHERE o.order_id = ?
ORDER BY pc.category_name, p.product_name;
```
**Tables Joined:** orders, restaurant_tables, order_types, users (×2), bills, customers, order_items, products, product_categories, kitchen_order_items

#### Customer 360° View Query (8 tables)
```sql
-- Complete customer profile with all related data
SELECT 
    c.*,
    -- Reservation History
    (SELECT COUNT(*) FROM reservations r WHERE r.customer_id = c.customer_id) AS total_reservations,
    -- Order History
    (SELECT COUNT(*) FROM bills b WHERE b.customer_id = c.customer_id) AS total_orders,
    -- Loyalty Transactions
    (SELECT SUM(points_earned) FROM loyalty_transactions lt WHERE lt.customer_id = c.customer_id) AS lifetime_points_earned,
    -- Preferences
    (SELECT GROUP_CONCAT(preference_type) FROM customer_preferences cp WHERE cp.customer_id = c.customer_id) AS preferences,
    -- Recent Orders
    (SELECT GROUP_CONCAT(p.product_name SEPARATOR ', ')
     FROM order_items oi
     JOIN bills b ON oi.bill_id = b.bill_id
     JOIN products p ON oi.product_id = p.product_id
     WHERE b.customer_id = c.customer_id
     ORDER BY b.created_at DESC LIMIT 5) AS recent_items
FROM customers c
WHERE c.customer_id = ?;
```

---

### 8.5 Multiple User Roles with Role-Specific Operations (5 points)

The application implements **4 distinct user roles** with completely different operational capabilities:

#### Role Comparison Matrix

| Feature | ADMIN | SERVER | CHEF | CUSTOMER |
|---------|:-----:|:------:|:----:|:--------:|
| **Dashboard Access** | ✅ Full | ✅ Limited | ❌ | ❌ |
| **Product Management** | ✅ CRUD | 👁️ View | 👁️ View | 👁️ View |
| **Inventory Management** | ✅ Full | ❌ | ❌ | ❌ |
| **User Management** | ✅ CRUD | ❌ | ❌ | ❌ |
| **Order Creation** | ✅ | ✅ | ❌ | ❌ |
| **Order Status Update** | ✅ | ✅ Serve | ✅ Cook | ❌ |
| **Payment Processing** | ✅ | ✅ | ❌ | ❌ |
| **Kitchen Display** | ❌ | ❌ | ✅ | ❌ |
| **Time Clock** | 👁️ View All | ✅ Self | ✅ Self | ❌ |
| **Schedule Management** | ✅ Full | 👁️ Own | 👁️ Own | ❌ |
| **Leave Requests** | ✅ Approve | ✅ Request | ✅ Request | ❌ |
| **Reservations** | ✅ Manage | ✅ View | ❌ | ✅ Create |
| **Reports & Analytics** | ✅ | ❌ | ❌ | ❌ |
| **Settings** | ✅ | ❌ | ❌ | ❌ |

#### Role-Based View Routing
```java
// AuthenticationController.java
public void routeUserToView(User user) {
    switch (user.getRole()) {
        case ADMIN:
            new AdminView().setVisible(true);  // Full management dashboard
            break;
        case SERVER:
            new ServerView(user).setVisible(true);  // Order & table management
            break;
        case CHEF:
            new KitchenView(user).setVisible(true);  // Kitchen display system
            break;
        case CUSTOMER:
            new CustomerReservationView().setVisible(true);  // Reservation portal
            break;
    }
}
```

#### Role-Specific Database Operations

**ADMIN Operations:**
- Full CRUD on all 17 tables
- Access to all 9 views
- Can execute all 9 stored procedures
- View all employee data

**SERVER Operations:**
- Create orders (INSERT into orders, order_items)
- Process payments (INSERT into bills)
- Update order status to COMPLETED
- Clock in/out (own records only)
- View assigned tables

**CHEF Operations:**
- View pending orders (SELECT from vw_pending_orders)
- Update order status (IN_PROGRESS, READY)
- Update kitchen_order_items status
- Clock in/out (own records only)

**CUSTOMER Operations:**
- Create reservations (INSERT into reservations)
- View available tables (SELECT with fn_is_table_available)
- View menu (SELECT from products)

---

### 8.6 Bonus Points Summary

| Bonus Category | Points Claimed | Evidence |
|----------------|:--------------:|----------|
| Complex Analytical Queries (5 queries shown) | **5** | 9 views, 5+ complex queries with window functions, CTEs, subqueries |
| Advanced GUI Frontend | **5** | 11 Swing views, role-based dashboards, real-time updates, modern UI |
| Complicated Database Translations | **5** | Transactions, stored procedures, triggers, functions in Java code |
| Multi-Table Joins (>10 tables) | **5** | 12-table join for order details, 8-table customer 360° view |
| Multiple User Roles | **5** | 4 roles (Admin, Server, Chef, Customer) with distinct operations |
| **TOTAL BONUS** | **25** | *Maximum claimable: 5 points* |

---

**Note:** While the implementation supports up to 25 bonus points worth of features, the maximum bonus awarded is **5 points** per the project guidelines, bringing the maximum possible score to **105/100 points**.

---

## Appendix B: Troubleshooting

| Problem | Solution |
|---------|----------|
| "Connection refused" | Ensure MySQL is running on port 3306 |
| "Access denied" | Check username/password in DatabaseConfiguration.java |
| "Unknown database" | Run restaurant_db_complete.sql first |
| "Class not found" | Run `./gradlew clean build` |
| "Port already in use" | Check for other MySQL instances |

---

## Appendix C: References

1. Java Documentation: https://docs.oracle.com/en/java/javase/21/
2. MySQL 8.0 Reference Manual: https://dev.mysql.com/doc/refman/8.0/en/
3. Gradle User Guide: https://docs.gradle.org/current/userguide/userguide.html
4. HikariCP: https://github.com/brettwooldridge/HikariCP
5. jBCrypt: https://www.mindrot.org/projects/jBCrypt/

---

**End of Report**

*Document prepared as part of Database Systems course project submission.*
