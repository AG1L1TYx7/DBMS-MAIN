-- ============================================
-- Restaurant Management System Database
-- Complete Schema in 3rd Normal Form (3NF)
-- Database: restaurant_db
-- ============================================

-- Drop database if exists and create fresh
DROP DATABASE IF EXISTS restaurant_db;
CREATE DATABASE restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE restaurant_db;

-- ============================================
-- CORE TABLES
-- ============================================

/*
 * ============================================
 * TABLE: users
 * ============================================
 * Purpose: Stores all system users including staff and customers
 * 
 * Roles:
 *   - ADMIN: System administrators with full access
 *   - SERVER: Waiters/waitresses who take orders and serve customers
 *   - CHEF: Kitchen staff who prepare food items
 *   - CUSTOMER: Registered customers for loyalty programs
 * 
 * 3NF Compliance:
 *   - All attributes depend only on user_id (no transitive dependencies)
 *   - Email, phone, username are unique identifiers
 * 
 * Indexes:
 *   - idx_username: Fast lookup during login authentication
 *   - idx_role: Filter users by their role for scheduling/reports
 *   - idx_active: Quick filtering of active/inactive users
 * 
 * Security Notes:
 *   - password_hash stores BCrypt encrypted passwords
 *   - Never store plain-text passwords
 */
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    address VARCHAR(255),
    role ENUM('ADMIN', 'SERVER', 'CHEF', 'CUSTOMER') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: product_categories
 * ============================================
 * Purpose: Normalizes product categories to eliminate redundancy
 * 
 * Benefits of Normalization:
 *   - Category name stored once, not repeated for each product
 *   - Easy to rename categories without updating all products
 *   - Maintains referential integrity
 * 
 * Sample Categories:
 *   - BURGER, CHICKEN_ROLL, RICE_MEALS, BEVERAGE, etc.
 * 
 * Usage:
 *   - Products reference this table via category_id
 *   - Used for menu organization and reporting
 */
CREATE TABLE product_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: products
 * ============================================
 * Purpose: Stores menu items available for ordering
 * 
 * 3NF Compliance:
 *   - Category moved to separate table (product_categories)
 *   - All attributes depend only on product_id
 *   - No partial or transitive dependencies
 * 
 * Inventory Management:
 *   - stock_quantity: Current available stock
 *   - reorder_level: Threshold to trigger reorder alert
 *   - max_stock_level: Maximum storage capacity
 * 
 * Indexes:
 *   - idx_category: Filter products by category for menu display
 *   - idx_available: Show only available items on menu
 *   - idx_stock: Quick access for low stock alerts
 * 
 * Constraints:
 *   - Price must be >= 0
 *   - Stock quantities must be non-negative
 */
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) UNIQUE NOT NULL,
    category_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    description TEXT,
    image_path VARCHAR(500),
    stock_quantity INT DEFAULT 0 CHECK (stock_quantity >= 0),
    reorder_level INT DEFAULT 10 CHECK (reorder_level >= 0),
    max_stock_level INT DEFAULT 100 CHECK (max_stock_level >= 0),
    unit VARCHAR(20) DEFAULT 'pcs',
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES product_categories(category_id) ON DELETE RESTRICT,
    INDEX idx_category (category_id),
    INDEX idx_available (is_available),
    INDEX idx_stock (stock_quantity)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: customers
 * ============================================
 * Purpose: Stores customer information for CRM and loyalty programs
 * 
 * 3NF Compliance:
 *   - All attributes depend solely on customer_id
 *   - No transitive dependencies exist
 * 
 * Loyalty Program:
 *   - membership_tier: BRONZE -> SILVER -> GOLD -> PLATINUM
 *   - loyalty_points: Accumulated points (1 point per Rs. 100 spent)
 *   - Tier upgrades based on total_spent thresholds
 * 
 * Membership Tier Thresholds:
 *   - BRONZE: Rs. 0 - 14,999
 *   - SILVER: Rs. 15,000 - 39,999
 *   - GOLD: Rs. 40,000 - 79,999
 *   - PLATINUM: Rs. 80,000+
 * 
 * Customer Analytics:
 *   - total_spent: Lifetime spending for tier calculation
 *   - visit_count: Number of visits for engagement tracking
 *   - last_visit_date: For identifying inactive customers
 */
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20) UNIQUE NOT NULL,
    date_of_birth DATE,
    address TEXT,
    membership_tier ENUM('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') DEFAULT 'BRONZE',
    loyalty_points INT DEFAULT 0 CHECK (loyalty_points >= 0),
    total_spent DECIMAL(12,2) DEFAULT 0.00 CHECK (total_spent >= 0),
    visit_count INT DEFAULT 0 CHECK (visit_count >= 0),
    last_visit_date DATE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_membership (membership_tier),
    INDEX idx_active (is_active)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: restaurant_tables
 * ============================================
 * Purpose: Represents physical dining tables in the restaurant
 * 
 * Table Management:
 *   - table_number: Human-readable identifier (e.g., T-01, T-02)
 *   - capacity: Maximum number of guests the table can seat
 *   - location: Area of restaurant (Window Side, Main Hall, Private Area, Outdoor)
 * 
 * Status Values:
 *   - AVAILABLE: Table is free for seating
 *   - OCCUPIED: Currently serving customers
 *   - RESERVED: Reserved for future reservation
 *   - MAINTENANCE: Under cleaning or repair
 * 
 * Integration:
 *   - Used by orders for dine-in tracking
 *   - Used by reservations for booking management
 *   - Used by bills for table-wise reporting
 */
CREATE TABLE restaurant_tables (
    table_id INT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(10) UNIQUE NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    location VARCHAR(50),
    status ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_table_number (table_number)
) ENGINE=InnoDB;

-- ============================================
-- ORDERS SYSTEM (3NF - For Kitchen Display)
-- ============================================

/*
 * ============================================
 * TABLE: order_types
 * ============================================
 * Purpose: Normalizes order types for flexibility and reporting
 * 
 * Default Order Types:
 *   - DINE_IN: Customer eating at restaurant table
 *   - TAKEOUT: Customer picking up order to go
 *   - DELIVERY: Order to be delivered to customer address
 * 
 * Benefits:
 *   - Easy to add new order types (e.g., CATERING, DRIVE_THRU)
 *   - Consistent naming across all orders
 *   - Enables order type-specific reporting
 */
CREATE TABLE order_types (
    order_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Insert default order types
INSERT INTO order_types (type_name, description) VALUES
('DINE_IN', 'Customer eating at restaurant table'),
('TAKEOUT', 'Customer picking up order to go'),
('DELIVERY', 'Order to be delivered to customer');

/*
 * ============================================
 * TABLE: orders
 * ============================================
 * Purpose: Main order tracking for Kitchen Display System (KDS)
 * 
 * IMPORTANT: Orders vs Bills
 *   - Orders: What kitchen needs to prepare (operational)
 *   - Bills: Financial record of payment (accounting)
 *   - One order can have one bill (linked via order_id in bills)
 * 
 * Order Workflow:
 *   PENDING -> CONFIRMED -> PREPARING -> READY -> SERVED -> COMPLETED
 *                                                      -> CANCELLED
 * 
 * Priority Levels:
 *   - NORMAL: Standard processing order
 *   - RUSH: Expedited preparation required
 *   - VIP: Special attention, priority service
 * 
 * Kitchen Integration:
 *   - kitchen_notes: Instructions for chefs
 *   - special_instructions: Customer requests
 *   - estimated_ready_time: For customer communication
 * 
 * Indexes:
 *   - idx_order_number: Quick lookup by order number
 *   - idx_status: Filter active orders for kitchen display
 *   - idx_table: Find orders by table number
 *   - idx_created_at: Sort orders by time for queue management
 */
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    order_type_id INT NOT NULL,
    table_id INT,
    customer_id INT,
    server_id INT NOT NULL,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(20),
    guest_count INT DEFAULT 1 CHECK (guest_count > 0),
    order_status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    kitchen_notes TEXT,
    special_instructions TEXT,
    priority ENUM('NORMAL', 'RUSH', 'VIP') DEFAULT 'NORMAL',
    estimated_ready_time TIMESTAMP NULL,
    actual_ready_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_type_id) REFERENCES order_types(order_type_id) ON DELETE RESTRICT,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (server_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_order_number (order_number),
    INDEX idx_status (order_status),
    INDEX idx_table (table_id),
    INDEX idx_server (server_id),
    INDEX idx_created_at (created_at),
    INDEX idx_order_type (order_type_id)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: kitchen_order_items
 * ============================================
 * Purpose: Individual items within an order for kitchen preparation
 * 
 * Kitchen Display System (KDS) Integration:
 *   - Each row = one item to prepare
 *   - Status tracked per item (not just per order)
 *   - Chefs can mark items ready individually
 * 
 * Item Status Workflow:
 *   PENDING -> PREPARING -> READY -> SERVED
 *                               -> CANCELLED
 * 
 * Tracking:
 *   - prepared_by: Which chef prepared the item
 *   - prepared_at: Timestamp for performance metrics
 *   - special_requests: Item-specific modifications
 * 
 * Relationships:
 *   - Links to orders table (parent order)
 *   - Links to products table (what to prepare)
 *   - Links to users table (chef who prepared)
 */
CREATE TABLE kitchen_order_items (
    kitchen_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    item_status ENUM('PENDING', 'PREPARING', 'READY', 'SERVED', 'CANCELLED') DEFAULT 'PENDING',
    special_requests TEXT,
    prepared_by INT,
    prepared_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    FOREIGN KEY (prepared_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_order (order_id),
    INDEX idx_product (product_id),
    INDEX idx_status (item_status)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: order_status_history
 * ============================================
 * Purpose: Complete audit trail of all order status changes
 * 
 * Audit Trail Benefits:
 *   - Track who changed order status and when
 *   - Investigate delays or issues
 *   - Performance analytics (time between statuses)
 *   - Compliance and dispute resolution
 * 
 * Captured Data:
 *   - previous_status: Status before change (NULL for new orders)
 *   - new_status: Status after change
 *   - changed_by: User who made the change
 *   - notes: Optional reason for status change
 * 
 * Example Timeline:
 *   1. PENDING (Server places order)
 *   2. CONFIRMED (Kitchen acknowledges)
 *   3. PREPARING (Cooking started)
 *   4. READY (Food ready for serving)
 *   5. SERVED (Delivered to table)
 *   6. COMPLETED (Bill paid)
 */
CREATE TABLE order_status_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    previous_status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'COMPLETED', 'CANCELLED'),
    new_status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'COMPLETED', 'CANCELLED') NOT NULL,
    changed_by INT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_order (order_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

-- ============================================
-- BILLING SYSTEM (Linked to Orders)
-- ============================================

/*
 * ============================================
 * TABLE: bills
 * ============================================
 * Purpose: Financial records of all transactions
 * 
 * 3NF Compliance:
 *   - Customer, user, table info stored via foreign keys
 *   - No redundant data stored
 * 
 * Financial Calculations:
 *   - subtotal: Sum of all order items before tax/discount
 *   - tax_amount: Tax calculated on subtotal
 *   - discount_amount: Any discounts applied (loyalty, promo)
 *   - total_amount: Final amount = subtotal + tax - discount
 * 
 * Payment Tracking:
 *   - payment_method: CASH, CARD, DIGITAL_WALLET, OTHER
 *   - payment_status: PENDING, PAID, CANCELLED, REFUNDED
 *   - cash_received: For cash payments (calculate change)
 *   - change_amount: Amount returned to customer
 * 
 * Relationships:
 *   - order_id: Links to operational order (optional)
 *   - customer_id: For loyalty points and CRM
 *   - user_id: Cashier who processed payment
 *   - table_id: For table-wise reporting
 */
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    order_id INT,
    customer_id INT,
    user_id INT NOT NULL,
    table_id INT,
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    tax_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (tax_amount >= 0),
    discount_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (discount_amount >= 0),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    payment_method ENUM('CASH', 'CARD', 'DIGITAL_WALLET', 'OTHER') DEFAULT 'CASH',
    payment_status ENUM('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    cash_received DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id) ON DELETE SET NULL,
    INDEX idx_order (order_id),
    INDEX idx_customer (customer_id),
    INDEX idx_user (user_id),
    INDEX idx_table (table_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: order_items
 * ============================================
 * Purpose: Line items on a bill (what was purchased)
 * 
 * 3NF Compliance:
 *   - Weak entity: Depends on bills table
 *   - Each row represents one product on the bill
 * 
 * Financial Data:
 *   - quantity: How many units ordered
 *   - unit_price: Price at time of purchase (may differ from current price)
 *   - subtotal: quantity × unit_price (auto-calculated by trigger)
 * 
 * Important Notes:
 *   - unit_price stored separately from products table
 *   - This preserves historical pricing for accurate records
 *   - Even if product price changes, bill remains accurate
 * 
 * Triggers:
 *   - trg_calculate_order_subtotal: Auto-calculates subtotal on INSERT
 *   - trg_update_order_subtotal: Auto-updates subtotal on UPDATE
 *   - trg_reduce_stock_on_order: Reduces product stock automatically
 */
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    INDEX idx_bill (bill_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: reservations
 * ============================================
 * Purpose: Manages table reservations and bookings
 * 
 * Reservation Workflow:
 *   PENDING -> CONFIRMED -> COMPLETED
 *                       -> CANCELLED
 *                       -> NO_SHOW
 * 
 * Customer Info:
 *   - customer_id: Link to registered customer (optional)
 *   - customer_name/phone/email: For walk-in reservations
 *   - party_size: Number of guests expected
 * 
 * Scheduling:
 *   - reservation_date: Date of reservation
 *   - reservation_time: Time slot reserved
 *   - Conflict check prevents double-booking via stored procedure
 * 
 * Trigger Integration:
 *   - trg_update_table_on_reservation: Updates table status
 *     when reservation is confirmed, completed, or cancelled
 * 
 * Special Features:
 *   - special_requests: Dietary needs, occasion, preferences
 *   - notes: Internal staff notes
 */
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT NOT NULL,
    customer_id INT,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(100),
    party_size INT NOT NULL CHECK (party_size > 0),
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW') DEFAULT 'PENDING',
    special_requests TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id) ON DELETE RESTRICT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    INDEX idx_table (table_id),
    INDEX idx_customer (customer_id),
    INDEX idx_date (reservation_date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: loyalty_transactions
 * ============================================
 * Purpose: Complete history of loyalty point movements
 * 
 * Transaction Types:
 *   - EARNED: Points added from purchases (1 point per Rs. 100)
 *   - REDEEMED: Points used for discounts (1 point = Rs. 1)
 *   - EXPIRED: Points expired due to inactivity
 *   - ADJUSTED: Manual adjustment by admin
 *   - BONUS: Promotional bonus points
 * 
 * Audit Trail:
 *   - points_before: Balance before transaction
 *   - points_after: Balance after transaction
 *   - description: Human-readable transaction detail
 *   - created_by: Admin who made adjustment (if applicable)
 * 
 * Reporting:
 *   - Track point earning patterns
 *   - Analyze redemption behavior
 *   - Calculate point liability
 * 
 * Related Stored Procedures:
 *   - sp_add_loyalty_points: Called after bill payment
 *   - sp_redeem_loyalty_points: Called during checkout
 */
CREATE TABLE loyalty_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    bill_id INT,
    transaction_type ENUM('EARNED', 'REDEEMED', 'EXPIRED', 'ADJUSTED', 'BONUS') NOT NULL,
    points INT NOT NULL,
    points_before INT NOT NULL,
    points_after INT NOT NULL,
    description TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_bill (bill_id),
    INDEX idx_type (transaction_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: inventory_adjustments
 * ============================================
 * Purpose: Tracks all inventory/stock movements for audit
 * 
 * Adjustment Types:
 *   - ADD: General addition to stock
 *   - REMOVE: General removal from stock
 *   - SET: Set stock to specific quantity
 *   - RESTOCK: Supplier delivery received
 *   - DAMAGE: Items damaged and removed
 *   - EXPIRED: Items expired and disposed
 * 
 * Audit Trail:
 *   - quantity_before: Stock level before adjustment
 *   - quantity_after: Stock level after adjustment
 *   - reason: Explanation for the adjustment
 *   - adjusted_by: User who made the change
 * 
 * Inventory Control Benefits:
 *   - Track shrinkage (theft, damage, waste)
 *   - Reconcile physical counts vs system
 *   - Audit supplier deliveries
 *   - Analyze usage patterns
 * 
 * Related Stored Procedure:
 *   - sp_adjust_inventory: Safe way to modify stock
 */
CREATE TABLE inventory_adjustments (
    adjustment_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    adjustment_type ENUM('ADD', 'REMOVE', 'SET', 'RESTOCK', 'DAMAGE', 'EXPIRED') NOT NULL,
    quantity_change INT NOT NULL,
    quantity_before INT NOT NULL,
    quantity_after INT NOT NULL,
    reason TEXT,
    adjusted_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    FOREIGN KEY (adjusted_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_product (product_id),
    INDEX idx_type (adjustment_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: customer_preferences
 * ============================================
 * Purpose: Stores detailed customer preferences for personalized service
 * 
 * 3NF Compliance:
 *   - Separated from customers table to avoid NULL columns
 *   - One-to-one relationship with customers
 *   - Only created when customer has preferences to store
 * 
 * Preference Categories:
 *   - preferred_table_location: Window Side, Quiet Area, etc.
 *   - dietary_restrictions: Vegetarian, Vegan, Halal, Kosher
 *   - favorite_items: Frequently ordered products
 *   - allergies: Critical safety information (nuts, gluten, dairy)
 *   - special_occasions: Birthday, anniversary dates
 * 
 * Communication:
 *   - communication_preference: How customer prefers to be contacted
 *     EMAIL, SMS, PHONE, or NONE
 * 
 * VIP Service:
 *   - Staff can view preferences when customer arrives
 *   - Personalized recommendations based on favorites
 *   - Safety alerts for allergies
 */
CREATE TABLE customer_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    preferred_table_location VARCHAR(50),
    dietary_restrictions TEXT,
    favorite_items TEXT,
    allergies TEXT,
    special_occasions TEXT,
    communication_preference ENUM('EMAIL', 'SMS', 'PHONE', 'NONE') DEFAULT 'EMAIL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    UNIQUE KEY unique_customer_pref (customer_id)
) ENGINE=InnoDB;

-- ============================================
-- EMPLOYEE SCHEDULING SYSTEM (3NF)
-- ============================================

/*
 * ============================================
 * TABLE: employee_schedules
 * ============================================
 * Purpose: Manages staff work schedules and shifts
 * 
 * 3NF Compliance:
 *   - All attributes depend only on schedule_id
 *   - User info referenced via foreign key
 * 
 * Shift Types:
 *   - MORNING: Early shift (typically 6:00 AM - 2:00 PM)
 *   - AFTERNOON: Mid-day shift (typically 2:00 PM - 10:00 PM)
 *   - EVENING: Late shift (typically 4:00 PM - 12:00 AM)
 *   - NIGHT: Night shift (typically 10:00 PM - 6:00 AM)
 *   - FULL_DAY: Extended shift (typically 8:00 AM - 8:00 PM)
 * 
 * Schedule Management:
 *   - start_time/end_time: Exact shift hours
 *   - break_minutes: Allocated break time
 *   - is_active: Soft delete for cancelled schedules
 * 
 * Constraints:
 *   - unique_user_date_shift: Prevents duplicate shifts
 *   - One employee can't work same shift type twice on same day
 * 
 * Indexes:
 *   - idx_user_date: Quick lookup of employee's schedule
 */
CREATE TABLE employee_schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    schedule_date DATE NOT NULL,
    shift_type ENUM('MORNING', 'AFTERNOON', 'EVENING', 'NIGHT', 'FULL_DAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    break_minutes INT DEFAULT 0 CHECK (break_minutes >= 0),
    notes TEXT,
    created_by INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_user (user_id),
    INDEX idx_date (schedule_date),
    INDEX idx_shift (shift_type),
    INDEX idx_active (is_active),
    INDEX idx_user_date (user_id, schedule_date),
    UNIQUE KEY unique_user_date_shift (user_id, schedule_date, shift_type)
) ENGINE=InnoDB;

/*
 * ============================================
 * TABLE: leave_requests
 * ============================================
 * Purpose: Manages employee time-off and leave requests
 * 
 * 3NF Compliance:
 *   - All attributes depend only on request_id
 *   - No transitive dependencies
 * 
 * Leave Types:
 *   - ANNUAL: Paid vacation leave
 *   - SICK: Medical leave
 *   - PERSONAL: Personal time off
 *   - EMERGENCY: Urgent unforeseen circumstances
 *   - UNPAID: Leave without pay
 * 
 * Approval Workflow:
 *   PENDING -> APPROVED (by manager/admin)
 *          -> REJECTED (with reason)
 *          -> CANCELLED (by employee)
 * 
 * Tracking:
 *   - start_date/end_date: Duration of leave
 *   - reason: Employee's explanation
 *   - approved_by: Manager who approved/rejected
 *   - approval_notes: Manager's comments
 * 
 * Constraints:
 *   - end_date must be >= start_date
 * 
 * Integration:
 *   - Check against employee_schedules for conflicts
 */
CREATE TABLE leave_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    leave_type ENUM('ANNUAL', 'SICK', 'PERSONAL', 'EMERGENCY', 'UNPAID') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    approved_by INT,
    approval_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_leave_type (leave_type),
    INDEX idx_user_status (user_id, status),
    CHECK (end_date >= start_date)
) ENGINE=InnoDB;

-- ============================================
-- INITIAL DATA
-- ============================================

-- Insert Product Categories
INSERT INTO product_categories (category_name, description) VALUES
('BURGER', 'Beef, chicken, and vegetarian burgers'),
('CHICKEN_ROLL', 'Various chicken roll varieties'),
('RICE_MEALS', 'Rice-based main dishes'),
('BEVERAGE', 'Hot and cold beverages'),
('FRIES', 'French fries and potato dishes'),
('DESSERTS', 'Sweet desserts and treats'),
('SOFT_COCKTAIL', 'Non-alcoholic cocktails'),
('MILKSHAKE', 'Milkshakes and smoothies'),
('APPETIZER', 'Starters and appetizers'),
('SPECIAL', 'Special menu items');

-- Insert Default Users
-- Default password for all users: 'password123' (BCrypt hash)
-- You can change passwords after first login
INSERT INTO users (username, password_hash, full_name, email_address, phone_number, address, role, is_active) VALUES
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4b4a4k4k4k4k4k4k', 'System Administrator', 'admin@restaurant.com', '1234567890', 'Restaurant HQ', 'ADMIN', TRUE),
('server1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4b4a4k4k4k4k4k4k', 'Sarah Server', 'server1@restaurant.com', '1234567893', 'Restaurant Floor', 'SERVER', TRUE),
('server2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4b4a4k4k4k4k4k4k', 'Mike Waiter', 'server2@restaurant.com', '1234567894', 'Restaurant Floor', 'SERVER', TRUE),
('chef1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4b4a4k4k4k4k4k4k', 'Chef Marco', 'chef1@restaurant.com', '1234567895', 'Kitchen', 'CHEF', TRUE),
('chef2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4b4a4k4k4k4k4k4k', 'Chef Rita', 'chef2@restaurant.com', '1234567896', 'Kitchen', 'CHEF', TRUE);

-- Insert Sample Employee Schedules
INSERT INTO employee_schedules (user_id, schedule_date, shift_type, start_time, end_time, break_minutes, notes, created_by, is_active) VALUES
-- Server 1 schedules (user_id 2)
(2, CURDATE(), 'MORNING', '06:00:00', '14:00:00', 30, 'Regular morning shift', 1, TRUE),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', '14:00:00', '22:00:00', 30, NULL, 1, TRUE),
(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'MORNING', '06:00:00', '14:00:00', 30, NULL, 1, TRUE),
(2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'FULL_DAY', '09:00:00', '21:00:00', 60, 'Weekend full shift', 1, TRUE),
-- Server 2 schedules (user_id 3)
(3, CURDATE(), 'AFTERNOON', '14:00:00', '22:00:00', 30, NULL, 1, TRUE),
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', '06:00:00', '14:00:00', 30, NULL, 1, TRUE),
(3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'EVENING', '16:00:00', '00:00:00', 30, 'Evening shift', 1, TRUE),
(3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AFTERNOON', '14:00:00', '22:00:00', 30, NULL, 1, TRUE),
-- Chef 1 schedules (user_id 4)
(4, CURDATE(), 'FULL_DAY', '08:00:00', '20:00:00', 60, 'Head chef duty', 1, TRUE),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'FULL_DAY', '08:00:00', '20:00:00', 60, NULL, 1, TRUE),
(4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'MORNING', '06:00:00', '14:00:00', 30, 'Breakfast prep', 1, TRUE),
(4, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'FULL_DAY', '08:00:00', '20:00:00', 60, NULL, 1, TRUE),
-- Chef 2 schedules (user_id 5)
(5, CURDATE(), 'AFTERNOON', '12:00:00', '20:00:00', 30, 'Lunch and dinner prep', 1, TRUE),
(5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'EVENING', '16:00:00', '00:00:00', 30, 'Dinner service', 1, TRUE),
(5, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'FULL_DAY', '08:00:00', '20:00:00', 60, 'Weekend full shift', 1, TRUE),
(5, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'MORNING', '06:00:00', '14:00:00', 30, NULL, 1, TRUE);

-- Insert Sample Leave Requests
INSERT INTO leave_requests (user_id, leave_type, start_date, end_date, reason, status, approved_by, approval_notes) VALUES
(2, 'ANNUAL', DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 17 DAY), 'Family vacation planned', 'APPROVED', 1, 'Approved. Enjoy your vacation!'),
(3, 'SICK', DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 8 DAY), 'Doctor appointment and recovery', 'PENDING', NULL, NULL),
(4, 'PERSONAL', DATE_ADD(CURDATE(), INTERVAL 21 DAY), DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'Personal matters to attend', 'PENDING', NULL, NULL),
(5, 'EMERGENCY', DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Family emergency', 'APPROVED', 1, 'Approved immediately');

-- Insert Sample Products
INSERT INTO products (product_name, category_id, price, description, stock_quantity, reorder_level, max_stock_level, unit) VALUES
('Classic Beef Burger', 1, 250.00, 'Juicy beef patty with fresh vegetables', 50, 10, 100, 'pcs'),
('Chicken Burger', 1, 220.00, 'Grilled chicken with special sauce', 45, 10, 100, 'pcs'),
('Chicken Roll', 2, 180.00, 'Spicy chicken wrapped in flatbread', 60, 15, 120, 'pcs'),
('Veg Roll', 2, 150.00, 'Fresh vegetables with special sauce', 40, 10, 80, 'pcs'),
('Dal Bhat Set', 3, 280.00, 'Traditional rice meal with lentils', 30, 10, 60, 'pcs'),
('Chicken Fried Rice', 3, 250.00, 'Fried rice with chicken pieces', 35, 10, 70, 'pcs'),
('Tea', 4, 30.00, 'Hot milk tea', 100, 20, 200, 'cup'),
('Coffee', 4, 50.00, 'Hot coffee', 80, 20, 150, 'cup'),
('Cold Drink', 4, 60.00, 'Chilled soft drink', 120, 30, 200, 'bottle'),
('French Fries', 5, 120.00, 'Crispy potato fries', 70, 20, 150, 'plate'),
('Loaded Fries', 5, 180.00, 'Fries with cheese and toppings', 50, 15, 100, 'plate'),
('Ice Cream', 6, 100.00, 'Vanilla ice cream', 60, 20, 120, 'scoop'),
('Chocolate Cake', 6, 150.00, 'Rich chocolate cake slice', 40, 10, 80, 'slice'),
('Virgin Mojito', 7, 120.00, 'Refreshing mint drink', 50, 15, 100, 'glass'),
('Chocolate Shake', 8, 180.00, 'Thick chocolate milkshake', 45, 10, 90, 'glass'),
('Momo', 9, 150.00, 'Steamed dumplings', 80, 20, 150, 'plate'),
('Paneer Tikka', 9, 200.00, 'Grilled cottage cheese', 40, 10, 80, 'plate'),
('Chef Special Platter', 10, 450.00, 'Mixed special items', 20, 5, 40, 'plate');

-- Insert Restaurant Tables
INSERT INTO restaurant_tables (table_number, capacity, location, status) VALUES
('T-01', 2, 'Window Side', 'AVAILABLE'),
('T-02', 2, 'Window Side', 'AVAILABLE'),
('T-03', 4, 'Main Hall', 'AVAILABLE'),
('T-04', 4, 'Main Hall', 'AVAILABLE'),
('T-05', 4, 'Main Hall', 'AVAILABLE'),
('T-06', 6, 'Main Hall', 'AVAILABLE'),
('T-07', 6, 'Private Area', 'AVAILABLE'),
('T-08', 8, 'Private Area', 'AVAILABLE'),
('T-09', 2, 'Outdoor', 'AVAILABLE'),
('T-10', 4, 'Outdoor', 'AVAILABLE');

-- Insert Sample Customers (Nepali Names with Various Membership Tiers)
INSERT INTO customers (full_name, email, phone, date_of_birth, address, membership_tier, loyalty_points, total_spent, visit_count, last_visit_date) VALUES
-- PLATINUM Members (High Spenders - $80,000+)
('Gita Poudel', 'gita.poudel@email.com', '9841444444', '1992-11-25', 'Baluwatar, Kathmandu', 'PLATINUM', 8500, 125000.00, 156, CURDATE()),
('Bishwo Raj Adhikari', 'bishwo.adhikari@email.com', '9841000001', '1990-06-15', 'Thamel, Kathmandu', 'PLATINUM', 12000, 180000.00, 210, CURDATE()),
('Anjali Shrestha', 'anjali.shrestha@email.com', '9841000002', '1988-09-22', 'Maharajgunj, Kathmandu', 'PLATINUM', 9200, 145000.00, 175, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
('Suraj Gurung', 'suraj.gurung@email.com', '9841000003', '1985-12-10', 'Lakeside, Pokhara', 'PLATINUM', 7800, 98000.00, 120, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),

-- GOLD Members (Good Spenders - $40,000 - $79,999)
('Rajesh Kumar', 'rajesh.kumar@email.com', '9841111111', '1985-05-15', 'Lazimpat, Kathmandu', 'GOLD', 4500, 72000.00, 85, CURDATE()),
('Priya Maharjan', 'priya.maharjan@email.com', '9841000004', '1993-03-18', 'Patan, Lalitpur', 'GOLD', 3200, 55000.00, 68, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('Santosh Karki', 'santosh.karki@email.com', '9841000005', '1987-07-25', 'Baneshwor, Kathmandu', 'GOLD', 4100, 62000.00, 78, CURDATE()),
('Sabina Tamang', 'sabina.tamang@email.com', '9841000006', '1991-01-30', 'Budhanilkantha, Kathmandu', 'GOLD', 3800, 58000.00, 72, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('Bikram Rai', 'bikram.rai@email.com', '9841000007', '1989-04-12', 'Dharan, Sunsari', 'GOLD', 2900, 48000.00, 55, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
('Mina Basnet', 'mina.basnet@email.com', '9841000008', '1994-08-05', 'Hetauda, Makwanpur', 'GOLD', 3500, 52000.00, 65, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),

-- SILVER Members (Regular Spenders - $15,000 - $39,999)
('Sita Sharma', 'sita.sharma@email.com', '9841222222', '1990-08-20', 'Kupondole, Lalitpur', 'SILVER', 1800, 32000.00, 38, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('Krishna Adhikari', 'krishna.adhikari@email.com', '9841555555', '1987-07-30', 'Bharatpur, Chitwan', 'SILVER', 2100, 38000.00, 45, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('Anil Bhandari', 'anil.bhandari@email.com', '9841000009', '1986-02-14', 'Biratnagar, Morang', 'SILVER', 1500, 28000.00, 32, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
('Sunita Pandey', 'sunita.pandey@email.com', '9841000010', '1995-06-28', 'Butwal, Rupandehi', 'SILVER', 1200, 22000.00, 25, DATE_SUB(CURDATE(), INTERVAL 8 DAY)),
('Dipak Khadka', 'dipak.khadka@email.com', '9841000011', '1992-11-11', 'Kirtipur, Kathmandu', 'SILVER', 1650, 30000.00, 35, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
('Parbati Lama', 'parbati.lama@email.com', '9841000012', '1988-09-03', 'Boudha, Kathmandu', 'SILVER', 1400, 25000.00, 28, DATE_SUB(CURDATE(), INTERVAL 12 DAY)),
('Nabin Thapa Magar', 'nabin.thapamagar@email.com', '9841000013', '1990-12-20', 'Dhulikhel, Kavre', 'SILVER', 1900, 35000.00, 42, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('Kamala Devi Koirala', 'kamala.koirala@email.com', '9841000014', '1983-04-17', 'Naxal, Kathmandu', 'SILVER', 2000, 36000.00, 40, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),

-- BRONZE Members (New/Occasional - $0 - $14,999)
('Ramesh Thapa', 'ramesh.thapa@email.com', '9841333333', '1988-03-10', 'Bhaktapur, Nepal', 'BRONZE', 650, 12000.00, 15, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('Hari Prasad Neupane', 'hari.neupane@email.com', '9841000015', '1996-05-22', 'Panauti, Kavre', 'BRONZE', 350, 6500.00, 8, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
('Maya Devi Limbu', 'maya.limbu@email.com', '9841000016', '1998-01-08', 'Damak, Jhapa', 'BRONZE', 200, 3500.00, 4, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
('Binod Chaudhary', 'binod.chaudhary@email.com', '9841000017', '1993-07-14', 'Nepalgunj, Banke', 'BRONZE', 480, 9000.00, 11, DATE_SUB(CURDATE(), INTERVAL 18 DAY)),
('Sarita Rijal', 'sarita.rijal@email.com', '9841000018', '1997-10-30', 'Tansen, Palpa', 'BRONZE', 150, 2800.00, 3, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
('Prakash Bhattarai', 'prakash.bhattarai@email.com', '9841000019', '1991-08-19', 'Janakpur, Dhanusha', 'BRONZE', 550, 10500.00, 13, DATE_SUB(CURDATE(), INTERVAL 14 DAY)),
('Laxmi Kumari Dahal', 'laxmi.dahal@email.com', '9841000020', '1989-03-25', 'Baglung, Baglung', 'BRONZE', 420, 7800.00, 9, DATE_SUB(CURDATE(), INTERVAL 22 DAY)),
('Roshan Manandhar', 'roshan.manandhar@email.com', '9841000021', '1994-12-05', 'Thimi, Bhaktapur', 'BRONZE', 280, 5200.00, 6, DATE_SUB(CURDATE(), INTERVAL 28 DAY)),
('Durga Bahadur Malla', 'durga.malla@email.com', '9841000022', '1982-06-18', 'Birtamod, Jhapa', 'BRONZE', 100, 1800.00, 2, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('Nirmala Subedi', 'nirmala.subedi@email.com', '9841000023', '1999-02-28', 'Itahari, Sunsari', 'BRONZE', 75, 1200.00, 1, DATE_SUB(CURDATE(), INTERVAL 60 DAY));

-- Insert Sample Orders (for Kitchen Display)
-- Customer IDs: 1=Gita(PLAT), 2=Bishwo(PLAT), 5=Rajesh(GOLD), 11=Sita(SILVER), 12=Krishna(SILVER), 19=Ramesh(BRONZE)
INSERT INTO orders (order_number, order_type_id, table_id, customer_id, server_id, customer_name, guest_count, order_status, kitchen_notes, priority) VALUES
('ORD-20251205-001', 1, 3, 5, 2, 'Rajesh Kumar', 2, 'PREPARING', 'No onions in burger', 'NORMAL'),
('ORD-20251205-002', 1, 5, 11, 2, 'Sita Sharma', 4, 'PENDING', NULL, 'NORMAL'),
('ORD-20251205-003', 2, NULL, NULL, 3, 'Walk-in Customer', 1, 'READY', 'Extra napkins', 'RUSH'),
('ORD-20251205-004', 1, 7, 1, 3, 'Gita Poudel', 6, 'CONFIRMED', 'VIP PLATINUM Guest - Priority service', 'VIP'),
('ORD-20251205-005', 3, NULL, 12, 2, 'Krishna Adhikari', 1, 'PENDING', 'Delivery to Chitwan office', 'NORMAL'),
('ORD-20251205-006', 1, 8, 2, 2, 'Bishwo Raj Adhikari', 8, 'CONFIRMED', 'PLATINUM Member - VIP Treatment', 'VIP'),
('ORD-20251205-007', 1, 4, 6, 3, 'Priya Maharjan', 3, 'PREPARING', 'GOLD Member', 'NORMAL'),
('ORD-20251205-008', 2, NULL, 3, 2, 'Anjali Shrestha', 2, 'READY', 'PLATINUM Takeout', 'VIP');

-- Insert Sample Kitchen Order Items
INSERT INTO kitchen_order_items (order_id, product_id, quantity, unit_price, item_status, special_requests) VALUES
-- Order 1 items (Table T-03)
(1, 1, 2, 250.00, 'PREPARING', 'No onions'),
(1, 10, 1, 120.00, 'READY', NULL),
(1, 8, 2, 50.00, 'SERVED', NULL),
-- Order 2 items (Table T-05)
(2, 5, 2, 280.00, 'PENDING', NULL),
(2, 16, 2, 150.00, 'PENDING', 'Extra spicy'),
(2, 7, 4, 30.00, 'PENDING', NULL),
-- Order 3 items (Takeout)
(3, 2, 1, 220.00, 'READY', NULL),
(3, 10, 1, 120.00, 'READY', 'Extra salt'),
(3, 9, 1, 60.00, 'READY', NULL),
-- Order 4 items (VIP Table T-07)
(4, 18, 2, 450.00, 'PENDING', 'Chef special plating'),
(4, 14, 6, 120.00, 'PENDING', NULL),
(4, 15, 4, 180.00, 'PENDING', NULL),
-- Order 5 items (Delivery)
(5, 6, 2, 250.00, 'PENDING', 'Pack separately'),
(5, 3, 2, 180.00, 'PENDING', NULL);

-- Insert Order Status History
INSERT INTO order_status_history (order_id, previous_status, new_status, changed_by, notes) VALUES
(1, NULL, 'PENDING', 2, 'Order placed by server'),
(1, 'PENDING', 'CONFIRMED', 4, 'Order confirmed by kitchen'),
(1, 'CONFIRMED', 'PREPARING', 4, 'Started preparation'),
(2, NULL, 'PENDING', 2, 'Order placed by server'),
(3, NULL, 'PENDING', 3, 'Takeout order placed'),
(3, 'PENDING', 'CONFIRMED', 4, 'Confirmed'),
(3, 'CONFIRMED', 'PREPARING', 4, 'Cooking'),
(3, 'PREPARING', 'READY', 4, 'Ready for pickup'),
(4, NULL, 'PENDING', 3, 'VIP order placed'),
(4, 'PENDING', 'CONFIRMED', 5, 'Priority confirmed'),
(5, NULL, 'PENDING', 2, 'Delivery order placed');

-- ============================================
-- VIEWS FOR REPORTING
-- ============================================

/*
 * ============================================
 * VIEW: v_kitchen_display
 * ============================================
 * Purpose: Real-time kitchen display system (KDS) view
 * 
 * Use Case:
 *   - Displayed on kitchen monitors for chefs
 *   - Shows all active orders needing preparation
 *   - Prioritized by VIP > RUSH > NORMAL, then by order time
 * 
 * Columns Returned:
 *   - order_id, order_number: Order identification
 *   - order_type: DINE_IN, TAKEOUT, or DELIVERY
 *   - table_number: For dine-in orders
 *   - customer_name, guest_count: Customer info
 *   - order_status, priority: Current state and priority
 *   - kitchen_notes: Special instructions
 *   - server_name: Who took the order
 *   - total_items: Count of items in order
 *   - pending_items, preparing_items, ready_items: Item status counts
 * 
 * Filtering:
 *   - Only shows orders with status: PENDING, CONFIRMED, PREPARING, READY
 *   - Excludes SERVED, COMPLETED, CANCELLED orders
 * 
 * Example Query:
 *   SELECT * FROM v_kitchen_display WHERE priority = 'VIP';
 */
CREATE VIEW v_kitchen_display AS
SELECT 
    o.order_id,
    o.order_number,
    ot.type_name as order_type,
    rt.table_number,
    o.customer_name,
    o.guest_count,
    o.order_status,
    o.priority,
    o.kitchen_notes,
    o.created_at as order_time,
    u.full_name as server_name,
    COUNT(koi.kitchen_item_id) as total_items,
    SUM(CASE WHEN koi.item_status = 'PENDING' THEN 1 ELSE 0 END) as pending_items,
    SUM(CASE WHEN koi.item_status = 'PREPARING' THEN 1 ELSE 0 END) as preparing_items,
    SUM(CASE WHEN koi.item_status = 'READY' THEN 1 ELSE 0 END) as ready_items
FROM orders o
JOIN order_types ot ON o.order_type_id = ot.order_type_id
LEFT JOIN restaurant_tables rt ON o.table_id = rt.table_id
JOIN users u ON o.server_id = u.user_id
LEFT JOIN kitchen_order_items koi ON o.order_id = koi.order_id
WHERE o.order_status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY')
GROUP BY o.order_id, o.order_number, ot.type_name, rt.table_number, 
         o.customer_name, o.guest_count, o.order_status, o.priority, 
         o.kitchen_notes, o.created_at, u.full_name
ORDER BY 
    CASE o.priority WHEN 'VIP' THEN 1 WHEN 'RUSH' THEN 2 ELSE 3 END,
    o.created_at;

/*
 * ============================================
 * VIEW: v_kitchen_order_items
 * ============================================
 * Purpose: Detailed item-level view for kitchen preparation
 * 
 * Use Case:
 *   - Shows individual items that need to be prepared
 *   - Chefs see exactly what to cook with special requests
 *   - Helps track which items are pending vs preparing
 * 
 * Columns Returned:
 *   - kitchen_item_id: Unique item identifier
 *   - order_number, order_type: Parent order info
 *   - table_number: For plating organization
 *   - product_name, category_name: What to prepare
 *   - quantity: How many to make
 *   - item_status: PENDING or PREPARING
 *   - special_requests: Item modifications
 *   - priority, kitchen_notes: From parent order
 *   - prepared_by_name: Chef who's working on it
 * 
 * Filtering:
 *   - Only shows items with status: PENDING, PREPARING
 *   - Sorted by priority, then order time
 * 
 * Example Query:
 *   SELECT * FROM v_kitchen_order_items WHERE category_name = 'BURGER';
 */
CREATE VIEW v_kitchen_order_items AS
SELECT 
    koi.kitchen_item_id,
    o.order_number,
    ot.type_name as order_type,
    rt.table_number,
    p.product_name,
    pc.category_name,
    koi.quantity,
    koi.item_status,
    koi.special_requests,
    o.priority,
    o.kitchen_notes,
    koi.created_at as item_ordered_at,
    chef.full_name as prepared_by_name
FROM kitchen_order_items koi
JOIN orders o ON koi.order_id = o.order_id
JOIN order_types ot ON o.order_type_id = ot.order_type_id
LEFT JOIN restaurant_tables rt ON o.table_id = rt.table_id
JOIN products p ON koi.product_id = p.product_id
JOIN product_categories pc ON p.category_id = pc.category_id
LEFT JOIN users chef ON koi.prepared_by = chef.user_id
WHERE koi.item_status IN ('PENDING', 'PREPARING')
ORDER BY 
    CASE o.priority WHEN 'VIP' THEN 1 WHEN 'RUSH' THEN 2 ELSE 3 END,
    koi.created_at;

/*
 * ============================================
 * VIEW: v_daily_sales_summary
 * ============================================
 * Purpose: Daily revenue and sales analytics
 * 
 * Use Case:
 *   - Daily closing reports
 *   - Revenue tracking dashboards
 *   - Trend analysis over time
 * 
 * Columns Returned:
 *   - sale_date: Date of transactions
 *   - total_bills: Number of bills generated
 *   - total_revenue: Sum of all bill amounts
 *   - avg_bill_amount: Average transaction value
 *   - paid_amount: Revenue from paid bills
 *   - pending_amount: Unpaid bill amounts
 * 
 * Insights:
 *   - Compare daily performance
 *   - Identify peak revenue days
 *   - Track pending payments
 * 
 * Example Queries:
 *   -- Today's sales:
 *   SELECT * FROM v_daily_sales_summary WHERE sale_date = CURDATE();
 *   
 *   -- Last 7 days:
 *   SELECT * FROM v_daily_sales_summary 
 *   WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
 */
CREATE VIEW v_daily_sales_summary AS
SELECT 
    DATE(created_at) as sale_date,
    COUNT(bill_id) as total_bills,
    SUM(total_amount) as total_revenue,
    AVG(total_amount) as avg_bill_amount,
    SUM(CASE WHEN payment_status = 'PAID' THEN total_amount ELSE 0 END) as paid_amount,
    SUM(CASE WHEN payment_status = 'PENDING' THEN total_amount ELSE 0 END) as pending_amount
FROM bills
GROUP BY DATE(created_at)
ORDER BY sale_date DESC;

/*
 * ============================================
 * VIEW: v_product_sales_summary
 * ============================================
 * Purpose: Product performance analytics
 * 
 * Use Case:
 *   - Identify best-selling products
 *   - Menu optimization decisions
 *   - Inventory planning based on sales velocity
 * 
 * Columns Returned:
 *   - product_id, product_name: Product identification
 *   - category_name: Product category
 *   - times_ordered: How many times product appears on bills
 *   - total_quantity_sold: Total units sold
 *   - total_revenue: Revenue generated by product
 *   - avg_selling_price: Average price sold at
 *   - current_stock: Current inventory level
 * 
 * Business Insights:
 *   - Top sellers for menu highlighting
 *   - Slow movers for potential removal
 *   - Price optimization opportunities
 * 
 * Example Queries:
 *   -- Top 10 products by revenue:
 *   SELECT * FROM v_product_sales_summary LIMIT 10;
 *   
 *   -- Products with low stock but high sales:
 *   SELECT * FROM v_product_sales_summary 
 *   WHERE current_stock < 20 AND total_quantity_sold > 50;
 */
CREATE VIEW v_product_sales_summary AS
SELECT 
    p.product_id,
    p.product_name,
    pc.category_name,
    COUNT(oi.order_item_id) as times_ordered,
    SUM(oi.quantity) as total_quantity_sold,
    SUM(oi.subtotal) as total_revenue,
    AVG(oi.unit_price) as avg_selling_price,
    p.stock_quantity as current_stock
FROM products p
JOIN product_categories pc ON p.category_id = pc.category_id
LEFT JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.product_name, pc.category_name, p.stock_quantity
ORDER BY total_revenue DESC;

/*
 * ============================================
 * VIEW: v_user_performance
 * ============================================
 * Purpose: Staff performance metrics and analytics
 * 
 * Use Case:
 *   - Employee performance reviews
 *   - Commission calculations
 *   - Identify top performers
 * 
 * Columns Returned:
 *   - user_id, username, full_name: User identification
 *   - role: User's job role
 *   - total_bills: Number of transactions processed
 *   - total_sales: Total revenue generated
 *   - avg_bill_amount: Average transaction value
 *   - last_sale_date: Most recent transaction
 * 
 * Performance Insights:
 *   - Compare servers by sales volume
 *   - Identify training opportunities
 *   - Reward top performers
 * 
 * Note:
 *   - Only counts PAID bills for accurate metrics
 * 
 * Example Query:
 *   SELECT * FROM v_user_performance WHERE role = 'SERVER';
 */
CREATE VIEW v_user_performance AS
SELECT 
    u.user_id,
    u.username,
    u.full_name,
    u.role,
    COUNT(b.bill_id) as total_bills,
    SUM(b.total_amount) as total_sales,
    AVG(b.total_amount) as avg_bill_amount,
    MAX(b.created_at) as last_sale_date
FROM users u
LEFT JOIN bills b ON u.user_id = b.user_id AND b.payment_status = 'PAID'
GROUP BY u.user_id, u.username, u.full_name, u.role
ORDER BY total_sales DESC;

/*
 * ============================================
 * VIEW: v_low_stock_alert
 * ============================================
 * Purpose: Identifies products that need restocking
 * 
 * Use Case:
 *   - Daily inventory management
 *   - Generate purchase orders
 *   - Prevent stockouts
 * 
 * Columns Returned:
 *   - product_id, product_name: Product identification
 *   - category_name: Product category
 *   - stock_quantity: Current stock level
 *   - reorder_level: Threshold that triggers alert
 *   - max_stock_level: Maximum storage capacity
 *   - quantity_to_order: Suggested order quantity
 *   - stock_status: OUT_OF_STOCK, LOW_STOCK, or SUFFICIENT
 * 
 * Stock Status Logic:
 *   - OUT_OF_STOCK: stock_quantity = 0
 *   - LOW_STOCK: stock_quantity <= reorder_level
 *   - SUFFICIENT: stock_quantity > reorder_level
 * 
 * Filtering:
 *   - Only shows products where stock <= reorder_level
 *   - Sorted by stock_quantity ascending (most urgent first)
 * 
 * Example Query:
 *   SELECT * FROM v_low_stock_alert WHERE stock_status = 'OUT_OF_STOCK';
 */
CREATE VIEW v_low_stock_alert AS
SELECT 
    p.product_id,
    p.product_name,
    pc.category_name,
    p.stock_quantity,
    p.reorder_level,
    p.max_stock_level,
    (p.max_stock_level - p.stock_quantity) as quantity_to_order,
    CASE 
        WHEN p.stock_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN p.stock_quantity <= p.reorder_level THEN 'LOW_STOCK'
        ELSE 'SUFFICIENT'
    END as stock_status
FROM products p
JOIN product_categories pc ON p.category_id = pc.category_id
WHERE p.stock_quantity <= p.reorder_level
ORDER BY p.stock_quantity ASC;

/*
 * ============================================
 * VIEW: v_customer_loyalty_summary
 * ============================================
 * Purpose: Customer loyalty program analytics and CRM
 * 
 * Use Case:
 *   - Customer segmentation
 *   - Targeted marketing campaigns
 *   - VIP customer identification
 * 
 * Columns Returned:
 *   - customer_id, full_name, phone, email: Customer info
 *   - membership_tier: BRONZE, SILVER, GOLD, PLATINUM
 *   - loyalty_points: Available points balance
 *   - total_spent: Lifetime spending
 *   - visit_count: Number of visits
 *   - last_visit_date: Most recent visit
 *   - avg_order_value: Average spending per visit
 *   - customer_status: Engagement classification
 * 
 * Customer Status Logic:
 *   - ACTIVE: Visited in last 30 days AND 10+ visits
 *   - REGULAR: Visited in last 30 days
 *   - INACTIVE: Last visited 31-60 days ago
 *   - DORMANT: Last visited 60+ days ago
 * 
 * Filtering:
 *   - Only active customers (is_active = TRUE)
 *   - Sorted by total_spent descending
 * 
 * Example Queries:
 *   -- Platinum members:
 *   SELECT * FROM v_customer_loyalty_summary 
 *   WHERE membership_tier = 'PLATINUM';
 *   
 *   -- Dormant high-value customers (win-back campaign):
 *   SELECT * FROM v_customer_loyalty_summary 
 *   WHERE customer_status = 'DORMANT' AND total_spent > 20000;
 */
CREATE VIEW v_customer_loyalty_summary AS
SELECT 
    c.customer_id,
    c.full_name,
    c.phone,
    c.email,
    c.membership_tier,
    c.loyalty_points,
    c.total_spent,
    c.visit_count,
    c.last_visit_date,
    ROUND(c.total_spent / NULLIF(c.visit_count, 0), 2) as avg_order_value,
    CASE 
        WHEN c.last_visit_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND c.visit_count >= 10 THEN 'ACTIVE'
        WHEN c.last_visit_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) THEN 'REGULAR'
        WHEN c.last_visit_date >= DATE_SUB(CURDATE(), INTERVAL 60 DAY) THEN 'INACTIVE'
        ELSE 'DORMANT'
    END as customer_status
FROM customers c
WHERE c.is_active = TRUE
ORDER BY c.total_spent DESC;

/*
 * ============================================
 * VIEW: v_table_utilization
 * ============================================
 * Purpose: Analyze table usage and revenue contribution
 * 
 * Use Case:
 *   - Optimize table layout
 *   - Identify underutilized tables
 *   - Revenue analysis by seating area
 * 
 * Columns Returned:
 *   - table_id, table_number: Table identification
 *   - capacity: Seating capacity
 *   - location: Area in restaurant
 *   - status: Current table status
 *   - times_used: Number of bills generated at table
 *   - total_revenue: Total revenue from table
 * 
 * Business Insights:
 *   - Revenue per seat analysis
 *   - Popular vs unpopular locations
 *   - Table turnover optimization
 * 
 * Example Queries:
 *   -- Revenue by location:
 *   SELECT location, SUM(total_revenue) as area_revenue 
 *   FROM v_table_utilization GROUP BY location;
 *   
 *   -- Underutilized large tables:
 *   SELECT * FROM v_table_utilization 
 *   WHERE capacity >= 6 AND times_used < 10;
 */
CREATE VIEW v_table_utilization AS
SELECT 
    rt.table_id,
    rt.table_number,
    rt.capacity,
    rt.location,
    rt.status,
    COUNT(b.bill_id) as times_used,
    SUM(b.total_amount) as total_revenue
FROM restaurant_tables rt
LEFT JOIN bills b ON rt.table_id = b.table_id AND b.payment_status = 'PAID'
GROUP BY rt.table_id, rt.table_number, rt.capacity, rt.location, rt.status
ORDER BY total_revenue DESC;

/*
 * ============================================
 * VIEW: v_reservation_status
 * ============================================
 * Purpose: Upcoming reservations dashboard
 * 
 * Use Case:
 *   - Host stand display
 *   - Daily reservation planning
 *   - Table assignment preparation
 * 
 * Columns Returned:
 *   - reservation_id: Unique identifier
 *   - customer_name, customer_phone: Guest contact info
 *   - party_size: Expected number of guests
 *   - reservation_date, reservation_time: When
 *   - status: PENDING, CONFIRMED, etc.
 *   - table_number, capacity, location: Assigned table
 *   - days_until_reservation: Days from today
 * 
 * Filtering:
 *   - Only shows reservations for today and future
 *   - Excludes past reservations
 *   - Sorted by date and time
 * 
 * Example Queries:
 *   -- Today's reservations:
 *   SELECT * FROM v_reservation_status 
 *   WHERE days_until_reservation = 0;
 *   
 *   -- Confirmed reservations this week:
 *   SELECT * FROM v_reservation_status 
 *   WHERE status = 'CONFIRMED' AND days_until_reservation <= 7;
 */
CREATE VIEW v_reservation_status AS
SELECT 
    r.reservation_id,
    r.customer_name,
    r.customer_phone,
    r.party_size,
    r.reservation_date,
    r.reservation_time,
    r.status,
    rt.table_number,
    rt.capacity,
    rt.location,
    DATEDIFF(r.reservation_date, CURDATE()) as days_until_reservation
FROM reservations r
JOIN restaurant_tables rt ON r.table_id = rt.table_id
WHERE r.reservation_date >= CURDATE()
ORDER BY r.reservation_date, r.reservation_time;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

/*
 * ============================================
 * PROCEDURE: sp_create_bill
 * ============================================
 * Purpose: Creates a new bill with automatic calculations
 * 
 * Parameters:
 *   IN p_customer_id INT      - Customer ID (NULL for walk-ins)
 *   IN p_user_id INT          - Cashier user ID (required)
 *   IN p_table_id INT         - Table ID (NULL for takeout)
 *   IN p_subtotal DECIMAL     - Sum of item prices
 *   IN p_tax_rate DECIMAL     - Tax rate (e.g., 0.13 for 13%)
 *   IN p_discount_amount DECIMAL - Discount to apply
 *   IN p_payment_method VARCHAR - CASH, CARD, etc.
 *   OUT p_bill_id INT         - Generated bill ID
 *   OUT p_bill_number VARCHAR - Generated bill number
 * 
 * Calculations:
 *   - tax_amount = subtotal × tax_rate
 *   - total_amount = subtotal + tax_amount - discount_amount
 * 
 * Bill Number Format:
 *   BILL-YYYYMMDD-XXXX (e.g., BILL-20251205-4829)
 * 
 * Example Usage:
 *   CALL sp_create_bill(1, 2, 3, 500.00, 0.13, 50.00, 'CASH', @bid, @bnum);
 *   SELECT @bid AS bill_id, @bnum AS bill_number;
 */
CREATE PROCEDURE sp_create_bill(
    IN p_customer_id INT,
    IN p_user_id INT,
    IN p_table_id INT,
    IN p_subtotal DECIMAL(10,2),
    IN p_tax_rate DECIMAL(5,4),
    IN p_discount_amount DECIMAL(10,2),
    IN p_payment_method VARCHAR(20),
    OUT p_bill_id INT,
    OUT p_bill_number VARCHAR(50)
)
BEGIN
    DECLARE v_tax_amount DECIMAL(10,2);
    DECLARE v_total_amount DECIMAL(10,2);
    DECLARE v_bill_num VARCHAR(50);
    
    -- Calculate tax and total
    SET v_tax_amount = p_subtotal * p_tax_rate;
    SET v_total_amount = p_subtotal + v_tax_amount - p_discount_amount;
    
    -- Generate bill number
    SET v_bill_num = CONCAT('BILL-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(FLOOR(RAND() * 10000), 4, '0'));
    
    -- Insert bill
    INSERT INTO bills (bill_number, customer_id, user_id, table_id, subtotal, tax_amount, discount_amount, total_amount, payment_method, payment_status)
    VALUES (v_bill_num, p_customer_id, p_user_id, p_table_id, p_subtotal, v_tax_amount, p_discount_amount, v_total_amount, p_payment_method, 'PENDING');
    
    -- Return values
    SET p_bill_id = LAST_INSERT_ID();
    SET p_bill_number = v_bill_num;
END //

/*
 * ============================================
 * PROCEDURE: sp_add_loyalty_points
 * ============================================
 * Purpose: Awards loyalty points after bill payment
 * 
 * Parameters:
 *   IN p_customer_id INT      - Customer to award points to
 *   IN p_bill_id INT          - Associated bill ID
 *   IN p_bill_amount DECIMAL  - Bill total for point calculation
 * 
 * Point Calculation:
 *   - 1 point earned per Rs. 100 spent
 *   - Example: Rs. 550 bill = 5 points earned
 * 
 * Actions Performed:
 *   1. Calculate points to add (bill_amount / 100)
 *   2. Update customer loyalty_points balance
 *   3. Update customer total_spent amount
 *   4. Increment visit_count
 *   5. Update last_visit_date to today
 *   6. Log transaction in loyalty_transactions table
 *   7. Check and update membership tier if needed
 * 
 * Example Usage:
 *   CALL sp_add_loyalty_points(1, 42, 1500.00);
 *   -- Awards 15 points to customer 1 from bill 42
 */
CREATE PROCEDURE sp_add_loyalty_points(
    IN p_customer_id INT,
    IN p_bill_id INT,
    IN p_bill_amount DECIMAL(10,2)
)
BEGIN
    DECLARE v_points_to_add INT;
    DECLARE v_current_points INT;
    DECLARE v_new_points INT;
    DECLARE v_current_spent DECIMAL(12,2);
    DECLARE v_new_spent DECIMAL(12,2);
    DECLARE v_visit_count INT;
    
    -- Calculate points (1 point per Rs. 100)
    SET v_points_to_add = FLOOR(p_bill_amount / 100);
    
    -- Get current customer data
    SELECT loyalty_points, total_spent, visit_count 
    INTO v_current_points, v_current_spent, v_visit_count
    FROM customers 
    WHERE customer_id = p_customer_id;
    
    -- Calculate new values
    SET v_new_points = v_current_points + v_points_to_add;
    SET v_new_spent = v_current_spent + p_bill_amount;
    
    -- Update customer
    UPDATE customers 
    SET loyalty_points = v_new_points,
        total_spent = v_new_spent,
        visit_count = visit_count + 1,
        last_visit_date = CURDATE()
    WHERE customer_id = p_customer_id;
    
    -- Log transaction
    INSERT INTO loyalty_transactions (customer_id, bill_id, transaction_type, points, points_before, points_after, description)
    VALUES (p_customer_id, p_bill_id, 'EARNED', v_points_to_add, v_current_points, v_new_points, CONCAT('Points earned from bill amount: Rs. ', p_bill_amount));
    
    -- Check and update membership tier
    CALL sp_check_membership_tier(p_customer_id);
END //

/*
 * ============================================
 * PROCEDURE: sp_redeem_loyalty_points
 * ============================================
 * Purpose: Redeems loyalty points for bill discount
 * 
 * Parameters:
 *   IN p_customer_id INT      - Customer redeeming points
 *   IN p_points INT           - Number of points to redeem
 *   OUT p_discount_amount DECIMAL - Discount value granted
 * 
 * Redemption Rate:
 *   - 1 point = Rs. 1 discount
 *   - Example: 500 points = Rs. 500 discount
 * 
 * Validation:
 *   - Checks if customer has sufficient points
 *   - Returns error if insufficient: SQLSTATE '45000'
 * 
 * Actions Performed:
 *   1. Verify customer has enough points
 *   2. Deduct points from customer balance
 *   3. Log redemption in loyalty_transactions
 *   4. Return discount amount for bill application
 * 
 * Example Usage:
 *   CALL sp_redeem_loyalty_points(1, 200, @discount);
 *   SELECT @discount; -- Returns 200.00
 * 
 * Error Handling:
 *   - If points < requested: Error 'Insufficient loyalty points'
 */
CREATE PROCEDURE sp_redeem_loyalty_points(
    IN p_customer_id INT,
    IN p_points INT,
    OUT p_discount_amount DECIMAL(10,2)
)
BEGIN
    DECLARE v_current_points INT;
    DECLARE v_new_points INT;
    
    -- Get current points
    SELECT loyalty_points INTO v_current_points
    FROM customers WHERE customer_id = p_customer_id;
    
    -- Check if sufficient points
    IF v_current_points >= p_points THEN
        SET v_new_points = v_current_points - p_points;
        SET p_discount_amount = p_points; -- 1 point = Rs. 1
        
        -- Update customer points
        UPDATE customers 
        SET loyalty_points = v_new_points
        WHERE customer_id = p_customer_id;
        
        -- Log transaction
        INSERT INTO loyalty_transactions (customer_id, transaction_type, points, points_before, points_after, description)
        VALUES (p_customer_id, 'REDEEMED', -p_points, v_current_points, v_new_points, CONCAT('Points redeemed for Rs. ', p_discount_amount, ' discount'));
    ELSE
        SET p_discount_amount = 0;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient loyalty points';
    END IF;
END //

/*
 * ============================================
 * PROCEDURE: sp_check_membership_tier
 * ============================================
 * Purpose: Automatically upgrades customer membership tier
 * 
 * Parameters:
 *   IN p_customer_id INT - Customer to check for tier upgrade
 * 
 * Tier Thresholds (based on total_spent):
 *   - BRONZE:   Rs. 0 - 14,999
 *   - SILVER:   Rs. 15,000 - 39,999
 *   - GOLD:     Rs. 40,000 - 79,999
 *   - PLATINUM: Rs. 80,000+
 * 
 * Tier Benefits (implement in application):
 *   - BRONZE: Base loyalty points (1 per Rs. 100)
 *   - SILVER: 1.25x points multiplier
 *   - GOLD: 1.5x points multiplier + priority seating
 *   - PLATINUM: 2x points multiplier + VIP treatment
 * 
 * Called By:
 *   - sp_add_loyalty_points (after every bill payment)
 * 
 * Note:
 *   - Only updates if tier actually changed
 *   - Tier downgrades are possible (if total_spent decreases)
 * 
 * Example Usage:
 *   CALL sp_check_membership_tier(1);
 */
CREATE PROCEDURE sp_check_membership_tier(IN p_customer_id INT)
BEGIN
    DECLARE v_total_spent DECIMAL(12,2);
    DECLARE v_new_tier VARCHAR(20);
    
    -- Get total spent
    SELECT total_spent INTO v_total_spent
    FROM customers WHERE customer_id = p_customer_id;
    
    -- Determine tier
    IF v_total_spent >= 80000 THEN
        SET v_new_tier = 'PLATINUM';
    ELSEIF v_total_spent >= 40000 THEN
        SET v_new_tier = 'GOLD';
    ELSEIF v_total_spent >= 15000 THEN
        SET v_new_tier = 'SILVER';
    ELSE
        SET v_new_tier = 'BRONZE';
    END IF;
    
    -- Update tier if changed
    UPDATE customers 
    SET membership_tier = v_new_tier
    WHERE customer_id = p_customer_id AND membership_tier != v_new_tier;
END //

/*
 * ============================================
 * PROCEDURE: sp_adjust_inventory
 * ============================================
 * Purpose: Safely adjusts product stock with full audit trail
 * 
 * Parameters:
 *   IN p_product_id INT       - Product to adjust
 *   IN p_adjustment_type VARCHAR - Type of adjustment
 *   IN p_quantity_change INT  - Quantity to add/remove/set
 *   IN p_reason TEXT          - Explanation for adjustment
 *   IN p_user_id INT          - User making adjustment
 * 
 * Adjustment Types:
 *   - ADD: General stock increase
 *   - REMOVE: General stock decrease
 *   - SET: Set stock to exact quantity
 *   - RESTOCK: Supplier delivery received
 *   - DAMAGE: Items damaged, removed from stock
 *   - EXPIRED: Items expired, disposed
 * 
 * Calculation Logic:
 *   - SET: new_quantity = quantity_change
 *   - ADD/RESTOCK: new_quantity = current + quantity_change
 *   - REMOVE/DAMAGE/EXPIRED: new_quantity = current - quantity_change
 * 
 * Safety Features:
 *   - Cannot go below 0 (auto-corrects to 0)
 *   - Full audit trail in inventory_adjustments table
 *   - Updates product timestamp
 * 
 * Example Usage:
 *   -- Receive 50 units from supplier:
 *   CALL sp_adjust_inventory(5, 'RESTOCK', 50, 'Weekly delivery', 1);
 *   
 *   -- Mark 3 items as damaged:
 *   CALL sp_adjust_inventory(5, 'DAMAGE', 3, 'Dropped in kitchen', 4);
 */
CREATE PROCEDURE sp_adjust_inventory(
    IN p_product_id INT,
    IN p_adjustment_type VARCHAR(20),
    IN p_quantity_change INT,
    IN p_reason TEXT,
    IN p_user_id INT
)
BEGIN
    DECLARE v_current_quantity INT;
    DECLARE v_new_quantity INT;
    
    -- Get current quantity
    SELECT stock_quantity INTO v_current_quantity
    FROM products WHERE product_id = p_product_id;
    
    -- Calculate new quantity
    IF p_adjustment_type = 'SET' THEN
        SET v_new_quantity = p_quantity_change;
    ELSEIF p_adjustment_type = 'ADD' OR p_adjustment_type = 'RESTOCK' THEN
        SET v_new_quantity = v_current_quantity + p_quantity_change;
    ELSE -- REMOVE, DAMAGE, EXPIRED
        SET v_new_quantity = v_current_quantity - p_quantity_change;
    END IF;
    
    -- Ensure non-negative
    IF v_new_quantity < 0 THEN
        SET v_new_quantity = 0;
    END IF;
    
    -- Update product quantity
    UPDATE products 
    SET stock_quantity = v_new_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
    
    -- Log adjustment
    INSERT INTO inventory_adjustments (product_id, adjustment_type, quantity_change, quantity_before, quantity_after, reason, adjusted_by)
    VALUES (p_product_id, p_adjustment_type, p_quantity_change, v_current_quantity, v_new_quantity, p_reason, p_user_id);
END //

/*
 * ============================================
 * PROCEDURE: sp_create_reservation
 * ============================================
 * Purpose: Creates table reservation with conflict checking
 * 
 * Parameters:
 *   IN p_table_id INT         - Table to reserve
 *   IN p_customer_id INT      - Customer ID (NULL for guests)
 *   IN p_customer_name VARCHAR - Guest name (required)
 *   IN p_customer_phone VARCHAR - Contact phone (required)
 *   IN p_customer_email VARCHAR - Email (optional)
 *   IN p_party_size INT       - Number of guests
 *   IN p_reservation_date DATE - Date of reservation
 *   IN p_reservation_time TIME - Time slot
 *   IN p_special_requests TEXT - Special requirements
 *   OUT p_reservation_id INT  - Generated reservation ID
 * 
 * Conflict Detection:
 *   - Checks if table already reserved for same date/time
 *   - Only considers PENDING and CONFIRMED reservations
 *   - Returns error if conflict found: SQLSTATE '45000'
 * 
 * Auto Table Status Update:
 *   - If reservation is for today, marks table as 'RESERVED'
 * 
 * Example Usage:
 *   CALL sp_create_reservation(
 *     3,                              -- table_id
 *     1,                              -- customer_id
 *     'Rajesh Kumar',                 -- name
 *     '9841111111',                   -- phone
 *     'rajesh@email.com',             -- email
 *     4,                              -- party_size
 *     '2025-12-10',                   -- date
 *     '19:00:00',                     -- time
 *     'Birthday celebration, need cake', -- requests
 *     @resv_id                        -- output
 *   );
 * 
 * Error Handling:
 *   - Error: 'Table is already reserved for this date and time'
 */
CREATE PROCEDURE sp_create_reservation(
    IN p_table_id INT,
    IN p_customer_id INT,
    IN p_customer_name VARCHAR(100),
    IN p_customer_phone VARCHAR(20),
    IN p_customer_email VARCHAR(100),
    IN p_party_size INT,
    IN p_reservation_date DATE,
    IN p_reservation_time TIME,
    IN p_special_requests TEXT,
    OUT p_reservation_id INT
)
BEGIN
    -- Check if table is available at that time
    DECLARE v_conflicts INT;
    
    SELECT COUNT(*) INTO v_conflicts
    FROM reservations
    WHERE table_id = p_table_id
    AND reservation_date = p_reservation_date
    AND reservation_time = p_reservation_time
    AND status IN ('PENDING', 'CONFIRMED');
    
    IF v_conflicts > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Table is already reserved for this date and time';
    END IF;
    
    -- Create reservation
    INSERT INTO reservations (table_id, customer_id, customer_name, customer_phone, customer_email, party_size, reservation_date, reservation_time, special_requests, status)
    VALUES (p_table_id, p_customer_id, p_customer_name, p_customer_phone, p_customer_email, p_party_size, p_reservation_date, p_reservation_time, p_special_requests, 'PENDING');
    
    SET p_reservation_id = LAST_INSERT_ID();
    
    -- Update table status if reservation is today
    IF p_reservation_date = CURDATE() THEN
        UPDATE restaurant_tables SET status = 'RESERVED' WHERE table_id = p_table_id;
    END IF;
END //

-- ============================================
-- USER-DEFINED FUNCTIONS
-- ============================================

/*
 * ============================================
 * FUNCTION: fn_calculate_tax
 * ============================================
 * Purpose: Calculates tax amount for a given subtotal
 * 
 * Parameters:
 *   p_subtotal DECIMAL - The pre-tax amount
 *   p_tax_rate DECIMAL - Tax rate as decimal (e.g., 0.13 for 13%)
 * 
 * Returns: DECIMAL(10,2) - The calculated tax amount
 * 
 * Example Usage:
 *   SELECT fn_calculate_tax(500.00, 0.13); -- Returns 65.00
 *   SELECT fn_calculate_tax(subtotal, 0.13) FROM bills;
 * 
 * Error Handling:
 *   - Returns 0 if subtotal is NULL or negative
 *   - Returns 0 if tax_rate is NULL or negative
 */
CREATE FUNCTION fn_calculate_tax(
    p_subtotal DECIMAL(10,2),
    p_tax_rate DECIMAL(5,4)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    -- Validate inputs
    IF p_subtotal IS NULL OR p_subtotal < 0 THEN
        RETURN 0.00;
    END IF;
    
    IF p_tax_rate IS NULL OR p_tax_rate < 0 THEN
        RETURN 0.00;
    END IF;
    
    RETURN ROUND(p_subtotal * p_tax_rate, 2);
END //

/*
 * ============================================
 * FUNCTION: fn_calculate_bill_total
 * ============================================
 * Purpose: Calculates final bill total with tax and discount
 * 
 * Parameters:
 *   p_subtotal DECIMAL - Sum of item prices
 *   p_tax_rate DECIMAL - Tax rate as decimal
 *   p_discount DECIMAL - Discount amount to subtract
 * 
 * Returns: DECIMAL(10,2) - Final total amount
 * 
 * Formula: total = subtotal + (subtotal × tax_rate) - discount
 * 
 * Example Usage:
 *   SELECT fn_calculate_bill_total(500.00, 0.13, 50.00); -- Returns 515.00
 * 
 * Error Handling:
 *   - Ensures result is never negative (returns 0 if would be negative)
 */
CREATE FUNCTION fn_calculate_bill_total(
    p_subtotal DECIMAL(10,2),
    p_tax_rate DECIMAL(5,4),
    p_discount DECIMAL(10,2)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_tax DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    
    -- Handle NULL values
    SET p_subtotal = COALESCE(p_subtotal, 0);
    SET p_tax_rate = COALESCE(p_tax_rate, 0);
    SET p_discount = COALESCE(p_discount, 0);
    
    -- Calculate
    SET v_tax = ROUND(p_subtotal * p_tax_rate, 2);
    SET v_total = p_subtotal + v_tax - p_discount;
    
    -- Ensure non-negative
    IF v_total < 0 THEN
        RETURN 0.00;
    END IF;
    
    RETURN v_total;
END //

/*
 * ============================================
 * FUNCTION: fn_get_loyalty_points
 * ============================================
 * Purpose: Calculates loyalty points to award for a bill amount
 * 
 * Parameters:
 *   p_bill_amount DECIMAL - The bill total amount
 *   p_membership_tier VARCHAR - Customer's membership tier
 * 
 * Returns: INT - Points to award
 * 
 * Point Multipliers by Tier:
 *   - BRONZE: 1x (1 point per Rs. 100)
 *   - SILVER: 1.25x
 *   - GOLD: 1.5x
 *   - PLATINUM: 2x
 * 
 * Example Usage:
 *   SELECT fn_get_loyalty_points(1000.00, 'GOLD'); -- Returns 15
 */
CREATE FUNCTION fn_get_loyalty_points(
    p_bill_amount DECIMAL(10,2),
    p_membership_tier VARCHAR(20)
) RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_base_points INT;
    DECLARE v_multiplier DECIMAL(3,2);
    
    -- Handle NULL
    IF p_bill_amount IS NULL OR p_bill_amount <= 0 THEN
        RETURN 0;
    END IF;
    
    -- Base: 1 point per Rs. 100
    SET v_base_points = FLOOR(p_bill_amount / 100);
    
    -- Set multiplier based on tier
    CASE COALESCE(p_membership_tier, 'BRONZE')
        WHEN 'PLATINUM' THEN SET v_multiplier = 2.00;
        WHEN 'GOLD' THEN SET v_multiplier = 1.50;
        WHEN 'SILVER' THEN SET v_multiplier = 1.25;
        ELSE SET v_multiplier = 1.00;
    END CASE;
    
    RETURN FLOOR(v_base_points * v_multiplier);
END //

/*
 * ============================================
 * FUNCTION: fn_get_membership_tier
 * ============================================
 * Purpose: Determines membership tier based on total spent
 * 
 * Parameters:
 *   p_total_spent DECIMAL - Customer's lifetime spending
 * 
 * Returns: VARCHAR(20) - Membership tier name
 * 
 * Tier Thresholds:
 *   - BRONZE: Rs. 0 - 14,999
 *   - SILVER: Rs. 15,000 - 39,999
 *   - GOLD: Rs. 40,000 - 79,999
 *   - PLATINUM: Rs. 80,000+
 * 
 * Example Usage:
 *   SELECT fn_get_membership_tier(50000.00); -- Returns 'GOLD'
 */
CREATE FUNCTION fn_get_membership_tier(
    p_total_spent DECIMAL(12,2)
) RETURNS VARCHAR(20)
DETERMINISTIC
READS SQL DATA
BEGIN
    SET p_total_spent = COALESCE(p_total_spent, 0);
    
    IF p_total_spent >= 80000 THEN
        RETURN 'PLATINUM';
    ELSEIF p_total_spent >= 40000 THEN
        RETURN 'GOLD';
    ELSEIF p_total_spent >= 15000 THEN
        RETURN 'SILVER';
    ELSE
        RETURN 'BRONZE';
    END IF;
END //

/*
 * ============================================
 * FUNCTION: fn_is_table_available
 * ============================================
 * Purpose: Checks if a table is available for reservation
 * 
 * Parameters:
 *   p_table_id INT - Table to check
 *   p_date DATE - Reservation date
 *   p_time TIME - Reservation time
 * 
 * Returns: BOOLEAN - TRUE if available, FALSE if not
 * 
 * Checks:
 *   - Table exists and is not in MAINTENANCE
 *   - No conflicting reservations (PENDING or CONFIRMED)
 * 
 * Example Usage:
 *   SELECT fn_is_table_available(3, '2025-12-10', '19:00:00');
 */
CREATE FUNCTION fn_is_table_available(
    p_table_id INT,
    p_date DATE,
    p_time TIME
) RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_table_status VARCHAR(20);
    DECLARE v_conflicts INT;
    
    -- Check table exists and status
    SELECT status INTO v_table_status
    FROM restaurant_tables
    WHERE table_id = p_table_id;
    
    IF v_table_status IS NULL THEN
        RETURN FALSE; -- Table doesn't exist
    END IF;
    
    IF v_table_status = 'MAINTENANCE' THEN
        RETURN FALSE;
    END IF;
    
    -- Check for reservation conflicts
    SELECT COUNT(*) INTO v_conflicts
    FROM reservations
    WHERE table_id = p_table_id
    AND reservation_date = p_date
    AND reservation_time = p_time
    AND status IN ('PENDING', 'CONFIRMED');
    
    RETURN v_conflicts = 0;
END //

/*
 * ============================================
 * FUNCTION: fn_format_currency
 * ============================================
 * Purpose: Formats a decimal amount as currency string
 * 
 * Parameters:
 *   p_amount DECIMAL - Amount to format
 * 
 * Returns: VARCHAR(50) - Formatted string (e.g., "Rs. 1,234.56")
 * 
 * Example Usage:
 *   SELECT fn_format_currency(1234.56); -- Returns 'Rs. 1,234.56'
 */
CREATE FUNCTION fn_format_currency(
    p_amount DECIMAL(12,2)
) RETURNS VARCHAR(50)
DETERMINISTIC
READS SQL DATA
BEGIN
    IF p_amount IS NULL THEN
        RETURN 'Rs. 0.00';
    END IF;
    
    RETURN CONCAT('Rs. ', FORMAT(p_amount, 2));
END //

/*
 * ============================================
 * FUNCTION: fn_get_order_item_subtotal
 * ============================================
 * Purpose: Calculates subtotal for an order item
 * 
 * Parameters:
 *   p_quantity INT - Number of items
 *   p_unit_price DECIMAL - Price per unit
 * 
 * Returns: DECIMAL(10,2) - Subtotal amount
 * 
 * Example Usage:
 *   SELECT fn_get_order_item_subtotal(3, 250.00); -- Returns 750.00
 */
CREATE FUNCTION fn_get_order_item_subtotal(
    p_quantity INT,
    p_unit_price DECIMAL(10,2)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    IF p_quantity IS NULL OR p_quantity <= 0 THEN
        RETURN 0.00;
    END IF;
    
    IF p_unit_price IS NULL OR p_unit_price < 0 THEN
        RETURN 0.00;
    END IF;
    
    RETURN ROUND(p_quantity * p_unit_price, 2);
END //

/*
 * ============================================
 * FUNCTION: fn_days_since_last_visit
 * ============================================
 * Purpose: Calculates days since customer's last visit
 * 
 * Parameters:
 *   p_customer_id INT - Customer to check
 * 
 * Returns: INT - Number of days (NULL if never visited)
 * 
 * Use Cases:
 *   - Identify inactive customers for win-back campaigns
 *   - Calculate customer engagement metrics
 * 
 * Example Usage:
 *   SELECT fn_days_since_last_visit(1); -- Returns days since last visit
 */
CREATE FUNCTION fn_days_since_last_visit(
    p_customer_id INT
) RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_last_visit DATE;
    
    SELECT last_visit_date INTO v_last_visit
    FROM customers
    WHERE customer_id = p_customer_id;
    
    IF v_last_visit IS NULL THEN
        RETURN NULL;
    END IF;
    
    RETURN DATEDIFF(CURDATE(), v_last_visit);
END //

/*
 * ============================================
 * FUNCTION: fn_get_stock_status
 * ============================================
 * Purpose: Returns stock status label for a product
 * 
 * Parameters:
 *   p_product_id INT - Product to check
 * 
 * Returns: VARCHAR(20) - Status: 'OUT_OF_STOCK', 'LOW_STOCK', 'SUFFICIENT', 'OVERSTOCKED'
 * 
 * Example Usage:
 *   SELECT product_name, fn_get_stock_status(product_id) FROM products;
 */
CREATE FUNCTION fn_get_stock_status(
    p_product_id INT
) RETURNS VARCHAR(20)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_stock INT;
    DECLARE v_reorder INT;
    DECLARE v_max INT;
    
    SELECT stock_quantity, reorder_level, max_stock_level
    INTO v_stock, v_reorder, v_max
    FROM products
    WHERE product_id = p_product_id;
    
    IF v_stock IS NULL THEN
        RETURN 'UNKNOWN';
    ELSEIF v_stock = 0 THEN
        RETURN 'OUT_OF_STOCK';
    ELSEIF v_stock <= v_reorder THEN
        RETURN 'LOW_STOCK';
    ELSEIF v_stock > v_max THEN
        RETURN 'OVERSTOCKED';
    ELSE
        RETURN 'SUFFICIENT';
    END IF;
END //

DELIMITER ;

-- ============================================
-- SCHEDULED EVENTS
-- ============================================

-- Enable event scheduler (run this command if not already enabled)
-- SET GLOBAL event_scheduler = ON;

DELIMITER //

/*
 * ============================================
 * EVENT: evt_expire_loyalty_points
 * ============================================
 * Purpose: Expires loyalty points that are older than 1 year
 * 
 * Schedule: Runs daily at 1:00 AM
 * 
 * Business Rule:
 *   - Points expire after 365 days of inactivity
 *   - Inactivity = no new points earned
 *   - Expired points logged in loyalty_transactions
 * 
 * Actions:
 *   1. Find customers with points who haven't visited in 365+ days
 *   2. Log expiration in loyalty_transactions
 *   3. Reset their points to 0
 * 
 * Note: Enable event scheduler with: SET GLOBAL event_scheduler = ON;
 */
CREATE EVENT IF NOT EXISTS evt_expire_loyalty_points
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 HOUR
DO
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_customer_id INT;
    DECLARE v_points INT;
    DECLARE cur CURSOR FOR 
        SELECT customer_id, loyalty_points 
        FROM customers 
        WHERE loyalty_points > 0 
        AND last_visit_date < DATE_SUB(CURDATE(), INTERVAL 365 DAY);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    expire_loop: LOOP
        FETCH cur INTO v_customer_id, v_points;
        IF done THEN
            LEAVE expire_loop;
        END IF;
        
        -- Log expiration
        INSERT INTO loyalty_transactions (
            customer_id, transaction_type, points, 
            points_before, points_after, description
        ) VALUES (
            v_customer_id, 'EXPIRED', -v_points,
            v_points, 0, 'Points expired due to 365 days of inactivity'
        );
        
        -- Reset points
        UPDATE customers 
        SET loyalty_points = 0 
        WHERE customer_id = v_customer_id;
    END LOOP;
    
    CLOSE cur;
END //

/*
 * ============================================
 * EVENT: evt_auto_cancel_pending_reservations
 * ============================================
 * Purpose: Auto-cancels unconfirmed reservations past their time
 * 
 * Schedule: Runs every hour
 * 
 * Business Rule:
 *   - PENDING reservations not confirmed by reservation time are cancelled
 *   - Frees up tables for walk-in customers
 * 
 * Actions:
 *   1. Find PENDING reservations where date/time has passed
 *   2. Update status to CANCELLED
 *   3. Set table back to AVAILABLE
 */
CREATE EVENT IF NOT EXISTS evt_auto_cancel_pending_reservations
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- Cancel past pending reservations
    UPDATE reservations 
    SET status = 'CANCELLED',
        notes = CONCAT(COALESCE(notes, ''), ' [Auto-cancelled: Not confirmed in time]'),
        updated_at = CURRENT_TIMESTAMP
    WHERE status = 'PENDING'
    AND (
        reservation_date < CURDATE()
        OR (reservation_date = CURDATE() AND reservation_time < CURTIME())
    );
    
    -- Free up tables
    UPDATE restaurant_tables rt
    SET rt.status = 'AVAILABLE'
    WHERE rt.status = 'RESERVED'
    AND NOT EXISTS (
        SELECT 1 FROM reservations r
        WHERE r.table_id = rt.table_id
        AND r.status IN ('PENDING', 'CONFIRMED')
        AND r.reservation_date = CURDATE()
    );
END //

/*
 * ============================================
 * EVENT: evt_mark_no_show_reservations
 * ============================================
 * Purpose: Marks reservations as NO_SHOW if not completed
 * 
 * Schedule: Runs daily at 11:59 PM
 * 
 * Business Rule:
 *   - CONFIRMED reservations for today that aren't COMPLETED
 *   - Marked as NO_SHOW at end of day
 *   - Helps track customer reliability
 */
CREATE EVENT IF NOT EXISTS evt_mark_no_show_reservations
ON SCHEDULE EVERY 1 DAY
STARTS (CURRENT_DATE + INTERVAL 1 DAY - INTERVAL 1 MINUTE)
DO
BEGIN
    UPDATE reservations
    SET status = 'NO_SHOW',
        notes = CONCAT(COALESCE(notes, ''), ' [Auto-marked NO_SHOW]'),
        updated_at = CURRENT_TIMESTAMP
    WHERE status = 'CONFIRMED'
    AND reservation_date = CURDATE();
    
    -- Free tables
    UPDATE restaurant_tables rt
    INNER JOIN reservations r ON rt.table_id = r.table_id
    SET rt.status = 'AVAILABLE'
    WHERE r.status = 'NO_SHOW'
    AND r.reservation_date = CURDATE();
END //

/*
 * ============================================
 * EVENT: evt_reset_daily_table_status
 * ============================================
 * Purpose: Resets all table statuses to AVAILABLE at start of day
 * 
 * Schedule: Runs daily at 5:00 AM (before opening)
 * 
 * Business Rule:
 *   - Fresh start each day
 *   - Tables in MAINTENANCE remain unchanged
 *   - Tables with today's reservations set to RESERVED
 */
CREATE EVENT IF NOT EXISTS evt_reset_daily_table_status
ON SCHEDULE EVERY 1 DAY
STARTS (CURRENT_DATE + INTERVAL 5 HOUR)
DO
BEGIN
    -- Reset all non-maintenance tables
    UPDATE restaurant_tables 
    SET status = 'AVAILABLE',
        updated_at = CURRENT_TIMESTAMP
    WHERE status != 'MAINTENANCE';
    
    -- Mark tables with today's confirmed reservations
    UPDATE restaurant_tables rt
    INNER JOIN reservations r ON rt.table_id = r.table_id
    SET rt.status = 'RESERVED'
    WHERE r.reservation_date = CURDATE()
    AND r.status = 'CONFIRMED';
END //

/*
 * ============================================
 * EVENT: evt_generate_low_stock_alerts
 * ============================================
 * Purpose: Logs products that need restocking
 * 
 * Schedule: Runs daily at 6:00 AM
 * 
 * Actions:
 *   - Checks products where stock <= reorder_level
 *   - Creates inventory adjustment record as alert
 *   - Helps staff know what to order
 * 
 * Note: In production, this could send email notifications
 */
CREATE EVENT IF NOT EXISTS evt_generate_low_stock_alerts
ON SCHEDULE EVERY 1 DAY
STARTS (CURRENT_DATE + INTERVAL 6 HOUR)
DO
BEGIN
    -- Log low stock items (system user_id = 1)
    INSERT INTO inventory_adjustments (
        product_id, adjustment_type, quantity_change, 
        quantity_before, quantity_after, reason, adjusted_by
    )
    SELECT 
        product_id,
        'SET',
        0,
        stock_quantity,
        stock_quantity,
        CONCAT('LOW STOCK ALERT: Current stock (', stock_quantity, 
               ') is at or below reorder level (', reorder_level, ')'),
        1  -- System admin
    FROM products
    WHERE stock_quantity <= reorder_level
    AND is_available = TRUE;
END //

/*
 * ============================================
 * EVENT: evt_cleanup_old_status_history
 * ============================================
 * Purpose: Archives/deletes old order status history records
 * 
 * Schedule: Runs weekly on Sunday at 3:00 AM
 * 
 * Business Rule:
 *   - Keep status history for 90 days
 *   - Older records deleted to save space
 *   - Adjust retention period as needed
 */
CREATE EVENT IF NOT EXISTS evt_cleanup_old_status_history
ON SCHEDULE EVERY 1 WEEK
STARTS (CURRENT_DATE + INTERVAL (7 - DAYOFWEEK(CURRENT_DATE)) DAY + INTERVAL 3 HOUR)
DO
BEGIN
    DELETE FROM order_status_history
    WHERE created_at < DATE_SUB(CURDATE(), INTERVAL 90 DAY);
END //

DELIMITER ;

-- ============================================
-- ENHANCED ERROR HANDLING PROCEDURES
-- ============================================

DELIMITER //

/*
 * ============================================
 * PROCEDURE: sp_safe_create_bill (Enhanced with Error Handling)
 * ============================================
 * Purpose: Creates a bill with comprehensive validation and error handling
 * 
 * Error Codes:
 *   - 45001: Invalid user ID
 *   - 45002: Invalid subtotal
 *   - 45003: Invalid tax rate
 *   - 45004: Discount exceeds subtotal
 *   - 45005: Invalid payment method
 *   - 45099: Unexpected error during bill creation
 * 
 * Features:
 *   - Input validation for all parameters
 *   - Transaction rollback on failure
 *   - Detailed error messages
 */
CREATE PROCEDURE sp_safe_create_bill(
    IN p_customer_id INT,
    IN p_user_id INT,
    IN p_table_id INT,
    IN p_subtotal DECIMAL(10,2),
    IN p_tax_rate DECIMAL(5,4),
    IN p_discount_amount DECIMAL(10,2),
    IN p_payment_method VARCHAR(20),
    OUT p_bill_id INT,
    OUT p_bill_number VARCHAR(50),
    OUT p_error_message VARCHAR(255)
)
BEGIN
    DECLARE v_tax_amount DECIMAL(10,2);
    DECLARE v_total_amount DECIMAL(10,2);
    DECLARE v_bill_num VARCHAR(50);
    DECLARE v_user_exists INT DEFAULT 0;
    
    -- Error handler
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_bill_id = NULL;
        SET p_bill_number = NULL;
        SET p_error_message = 'Unexpected error during bill creation';
        SIGNAL SQLSTATE '45099' SET MESSAGE_TEXT = 'Unexpected error during bill creation';
    END;
    
    -- Initialize output
    SET p_error_message = NULL;
    
    -- Validate user exists
    SELECT COUNT(*) INTO v_user_exists FROM users WHERE user_id = p_user_id AND is_active = TRUE;
    IF v_user_exists = 0 THEN
        SET p_error_message = 'Invalid or inactive user ID';
        SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Invalid or inactive user ID';
    END IF;
    
    -- Validate subtotal
    IF p_subtotal IS NULL OR p_subtotal < 0 THEN
        SET p_error_message = 'Subtotal must be a non-negative value';
        SIGNAL SQLSTATE '45002' SET MESSAGE_TEXT = 'Subtotal must be a non-negative value';
    END IF;
    
    -- Validate tax rate
    IF p_tax_rate IS NULL OR p_tax_rate < 0 OR p_tax_rate > 1 THEN
        SET p_error_message = 'Tax rate must be between 0 and 1';
        SIGNAL SQLSTATE '45003' SET MESSAGE_TEXT = 'Tax rate must be between 0 and 1';
    END IF;
    
    -- Validate discount
    SET p_discount_amount = COALESCE(p_discount_amount, 0);
    IF p_discount_amount > p_subtotal THEN
        SET p_error_message = 'Discount cannot exceed subtotal';
        SIGNAL SQLSTATE '45004' SET MESSAGE_TEXT = 'Discount cannot exceed subtotal';
    END IF;
    
    -- Validate payment method
    IF p_payment_method NOT IN ('CASH', 'CARD', 'DIGITAL_WALLET', 'OTHER') THEN
        SET p_error_message = 'Invalid payment method. Use: CASH, CARD, DIGITAL_WALLET, or OTHER';
        SIGNAL SQLSTATE '45005' SET MESSAGE_TEXT = 'Invalid payment method';
    END IF;
    
    START TRANSACTION;
    
    -- Calculate amounts
    SET v_tax_amount = fn_calculate_tax(p_subtotal, p_tax_rate);
    SET v_total_amount = fn_calculate_bill_total(p_subtotal, p_tax_rate, p_discount_amount);
    
    -- Generate unique bill number
    SET v_bill_num = CONCAT('BILL-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), '-', LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    -- Insert bill
    INSERT INTO bills (
        bill_number, customer_id, user_id, table_id, 
        subtotal, tax_amount, discount_amount, total_amount, 
        payment_method, payment_status
    ) VALUES (
        v_bill_num, p_customer_id, p_user_id, p_table_id,
        p_subtotal, v_tax_amount, p_discount_amount, v_total_amount,
        p_payment_method, 'PENDING'
    );
    
    SET p_bill_id = LAST_INSERT_ID();
    SET p_bill_number = v_bill_num;
    
    COMMIT;
END //

/*
 * ============================================
 * PROCEDURE: sp_safe_adjust_inventory (Enhanced with Error Handling)
 * ============================================
 * Purpose: Safely adjusts inventory with comprehensive validation
 * 
 * Error Codes:
 *   - 45010: Product not found
 *   - 45011: Invalid adjustment type
 *   - 45012: Invalid quantity
 *   - 45013: Insufficient stock for removal
 *   - 45014: User not authorized
 *   - 45019: Unexpected error
 * 
 * Features:
 *   - Validates product exists
 *   - Validates adjustment type
 *   - Prevents negative stock (with warning)
 *   - Logs all adjustments
 */
CREATE PROCEDURE sp_safe_adjust_inventory(
    IN p_product_id INT,
    IN p_adjustment_type VARCHAR(20),
    IN p_quantity_change INT,
    IN p_reason TEXT,
    IN p_user_id INT,
    OUT p_new_quantity INT,
    OUT p_error_message VARCHAR(255)
)
BEGIN
    DECLARE v_current_quantity INT;
    DECLARE v_product_exists INT DEFAULT 0;
    DECLARE v_user_authorized INT DEFAULT 0;
    
    -- Error handler
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_new_quantity = NULL;
        SET p_error_message = 'Unexpected error during inventory adjustment';
        SIGNAL SQLSTATE '45019' SET MESSAGE_TEXT = 'Unexpected error during inventory adjustment';
    END;
    
    -- Initialize
    SET p_error_message = NULL;
    
    -- Check product exists
    SELECT COUNT(*), stock_quantity INTO v_product_exists, v_current_quantity
    FROM products WHERE product_id = p_product_id
    GROUP BY product_id;
    
    IF v_product_exists = 0 THEN
        SET p_error_message = 'Product not found';
        SIGNAL SQLSTATE '45010' SET MESSAGE_TEXT = 'Product not found';
    END IF;
    
    -- Validate adjustment type
    IF p_adjustment_type NOT IN ('ADD', 'REMOVE', 'SET', 'RESTOCK', 'DAMAGE', 'EXPIRED') THEN
        SET p_error_message = 'Invalid adjustment type. Use: ADD, REMOVE, SET, RESTOCK, DAMAGE, or EXPIRED';
        SIGNAL SQLSTATE '45011' SET MESSAGE_TEXT = 'Invalid adjustment type';
    END IF;
    
    -- Validate quantity
    IF p_quantity_change IS NULL OR p_quantity_change < 0 THEN
        SET p_error_message = 'Quantity must be a non-negative integer';
        SIGNAL SQLSTATE '45012' SET MESSAGE_TEXT = 'Quantity must be a non-negative integer';
    END IF;
    
    -- Check user authorization (must be ADMIN or CHEF)
    SELECT COUNT(*) INTO v_user_authorized
    FROM users 
    WHERE user_id = p_user_id 
    AND is_active = TRUE 
    AND role IN ('ADMIN', 'CHEF');
    
    IF v_user_authorized = 0 THEN
        SET p_error_message = 'User not authorized for inventory adjustments';
        SIGNAL SQLSTATE '45014' SET MESSAGE_TEXT = 'User not authorized for inventory adjustments';
    END IF;
    
    START TRANSACTION;
    
    -- Calculate new quantity
    IF p_adjustment_type = 'SET' THEN
        SET p_new_quantity = p_quantity_change;
    ELSEIF p_adjustment_type IN ('ADD', 'RESTOCK') THEN
        SET p_new_quantity = v_current_quantity + p_quantity_change;
    ELSE -- REMOVE, DAMAGE, EXPIRED
        SET p_new_quantity = v_current_quantity - p_quantity_change;
        
        -- Check for insufficient stock
        IF p_new_quantity < 0 THEN
            SET p_error_message = CONCAT('Insufficient stock. Current: ', v_current_quantity, ', Requested removal: ', p_quantity_change);
            SIGNAL SQLSTATE '45013' SET MESSAGE_TEXT = 'Insufficient stock for removal';
        END IF;
    END IF;
    
    -- Ensure non-negative
    IF p_new_quantity < 0 THEN
        SET p_new_quantity = 0;
    END IF;
    
    -- Update product
    UPDATE products 
    SET stock_quantity = p_new_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
    
    -- Log adjustment
    INSERT INTO inventory_adjustments (
        product_id, adjustment_type, quantity_change, 
        quantity_before, quantity_after, reason, adjusted_by
    ) VALUES (
        p_product_id, p_adjustment_type, p_quantity_change,
        v_current_quantity, p_new_quantity, p_reason, p_user_id
    );
    
    COMMIT;
END //

/*
 * ============================================
 * PROCEDURE: sp_safe_redeem_points (Enhanced with Error Handling)
 * ============================================
 * Purpose: Redeems loyalty points with comprehensive validation
 * 
 * Error Codes:
 *   - 45020: Customer not found
 *   - 45021: Customer account inactive
 *   - 45022: Invalid points amount
 *   - 45023: Insufficient points
 *   - 45024: Minimum redemption not met
 *   - 45029: Unexpected error
 */
CREATE PROCEDURE sp_safe_redeem_points(
    IN p_customer_id INT,
    IN p_points_to_redeem INT,
    OUT p_discount_amount DECIMAL(10,2),
    OUT p_remaining_points INT,
    OUT p_error_message VARCHAR(255)
)
BEGIN
    DECLARE v_current_points INT;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_min_redemption INT DEFAULT 100; -- Minimum points to redeem
    
    -- Error handler
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_discount_amount = 0;
        SET p_remaining_points = NULL;
        SET p_error_message = 'Unexpected error during point redemption';
        SIGNAL SQLSTATE '45029' SET MESSAGE_TEXT = 'Unexpected error during point redemption';
    END;
    
    -- Initialize
    SET p_error_message = NULL;
    SET p_discount_amount = 0;
    
    -- Get customer data
    SELECT loyalty_points, is_active INTO v_current_points, v_is_active
    FROM customers
    WHERE customer_id = p_customer_id;
    
    -- Check customer exists
    IF v_current_points IS NULL THEN
        SET p_error_message = 'Customer not found';
        SIGNAL SQLSTATE '45020' SET MESSAGE_TEXT = 'Customer not found';
    END IF;
    
    -- Check customer active
    IF v_is_active = FALSE THEN
        SET p_error_message = 'Customer account is inactive';
        SIGNAL SQLSTATE '45021' SET MESSAGE_TEXT = 'Customer account is inactive';
    END IF;
    
    -- Validate points amount
    IF p_points_to_redeem IS NULL OR p_points_to_redeem <= 0 THEN
        SET p_error_message = 'Points to redeem must be a positive integer';
        SIGNAL SQLSTATE '45022' SET MESSAGE_TEXT = 'Points to redeem must be a positive integer';
    END IF;
    
    -- Check minimum redemption
    IF p_points_to_redeem < v_min_redemption THEN
        SET p_error_message = CONCAT('Minimum redemption is ', v_min_redemption, ' points');
        SIGNAL SQLSTATE '45024' SET MESSAGE_TEXT = 'Minimum redemption not met';
    END IF;
    
    -- Check sufficient points
    IF v_current_points < p_points_to_redeem THEN
        SET p_error_message = CONCAT('Insufficient points. Available: ', v_current_points, ', Requested: ', p_points_to_redeem);
        SIGNAL SQLSTATE '45023' SET MESSAGE_TEXT = 'Insufficient loyalty points';
    END IF;
    
    START TRANSACTION;
    
    -- Calculate discount (1 point = Rs. 1)
    SET p_discount_amount = p_points_to_redeem;
    SET p_remaining_points = v_current_points - p_points_to_redeem;
    
    -- Update customer
    UPDATE customers
    SET loyalty_points = p_remaining_points
    WHERE customer_id = p_customer_id;
    
    -- Log transaction
    INSERT INTO loyalty_transactions (
        customer_id, transaction_type, points,
        points_before, points_after, description
    ) VALUES (
        p_customer_id, 'REDEEMED', -p_points_to_redeem,
        v_current_points, p_remaining_points,
        CONCAT('Redeemed ', p_points_to_redeem, ' points for Rs. ', p_discount_amount, ' discount')
    );
    
    COMMIT;
END //

DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER //

/*
 * ============================================
 * TRIGGER: trg_prevent_negative_points
 * ============================================
 * Purpose: Ensures loyalty points never go negative
 * 
 * Timing: BEFORE UPDATE on customers table
 * 
 * Logic:
 *   - If NEW.loyalty_points < 0, set to 0
 *   - Prevents data integrity issues from calculation errors
 * 
 * Why Needed:
 *   - Manual adjustments might accidentally go negative
 *   - Concurrent transactions could cause race conditions
 *   - Provides last line of defense for data integrity
 */
CREATE TRIGGER trg_prevent_negative_points
BEFORE UPDATE ON customers
FOR EACH ROW
BEGIN
    IF NEW.loyalty_points < 0 THEN
        SET NEW.loyalty_points = 0;
    END IF;
END //

/*
 * ============================================
 * TRIGGER: trg_update_product_availability
 * ============================================
 * Purpose: Auto-updates is_available flag based on stock
 * 
 * Timing: BEFORE UPDATE on products table
 * 
 * Logic:
 *   - If stock becomes 0: is_available = FALSE
 *   - If stock was 0 and now > 0: is_available = TRUE
 * 
 * Business Impact:
 *   - Out-of-stock items automatically hidden from menu
 *   - Restocked items automatically appear on menu
 *   - No manual intervention needed
 * 
 * Note:
 *   - Only triggers when stock crosses the 0 threshold
 *   - Does not affect items manually marked unavailable
 */
CREATE TRIGGER trg_update_product_availability
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.stock_quantity = 0 THEN
        SET NEW.is_available = FALSE;
    ELSEIF NEW.stock_quantity > 0 AND OLD.stock_quantity = 0 THEN
        SET NEW.is_available = TRUE;
    END IF;
END //

/*
 * ============================================
 * TRIGGER: trg_calculate_order_subtotal
 * ============================================
 * Purpose: Auto-calculates subtotal when order item inserted
 * 
 * Timing: BEFORE INSERT on order_items table
 * 
 * Calculation:
 *   subtotal = quantity × unit_price
 * 
 * Benefits:
 *   - Ensures accurate calculations
 *   - Application doesn't need to calculate
 *   - Prevents mismatched data
 * 
 * Example:
 *   INSERT INTO order_items (bill_id, product_id, quantity, unit_price)
 *   VALUES (1, 5, 3, 100.00);
 *   -- subtotal automatically set to 300.00
 */
CREATE TRIGGER trg_calculate_order_subtotal
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //

/*
 * ============================================
 * TRIGGER: trg_update_order_subtotal
 * ============================================
 * Purpose: Recalculates subtotal when order item updated
 * 
 * Timing: BEFORE UPDATE on order_items table
 * 
 * Calculation:
 *   subtotal = quantity × unit_price
 * 
 * Use Cases:
 *   - Quantity changed (customer wants more/less)
 *   - Price adjusted (discount applied)
 * 
 * Maintains data consistency with insert trigger
 */
CREATE TRIGGER trg_update_order_subtotal
BEFORE UPDATE ON order_items
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //

/*
 * ============================================
 * TRIGGER: trg_reduce_stock_on_order
 * ============================================
 * Purpose: Automatically reduces product stock on order
 * 
 * Timing: AFTER INSERT on order_items table
 * 
 * Action:
 *   - Reduces product stock_quantity by ordered quantity
 *   - Updates product timestamp
 * 
 * Inventory Flow:
 *   1. Server adds item to bill
 *   2. Trigger fires immediately
 *   3. Stock reduced in real-time
 *   4. Low stock alerts update automatically
 * 
 * Warning:
 *   - Does not check if sufficient stock exists
 *   - Application should verify stock before ordering
 *   - Stock can go negative (handle in application)
 */
CREATE TRIGGER trg_reduce_stock_on_order
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity - NEW.quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = NEW.product_id;
END //

/*
 * ============================================
 * TRIGGER: trg_update_table_on_reservation
 * ============================================
 * Purpose: Auto-updates table status based on reservation changes
 * 
 * Timing: AFTER UPDATE on reservations table
 * 
 * Logic:
 *   - COMPLETED/CANCELLED/NO_SHOW: Table -> AVAILABLE
 *   - CONFIRMED (same day): Table -> RESERVED
 * 
 * Workflow Integration:
 *   1. Reservation confirmed for today
 *   2. Table automatically marked RESERVED
 *   3. Guests arrive, reservation COMPLETED
 *   4. Table automatically marked AVAILABLE
 * 
 * Handles No-Shows:
 *   - If customer doesn't arrive, mark NO_SHOW
 *   - Table becomes available for walk-ins
 */
CREATE TRIGGER trg_update_table_on_reservation
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' OR NEW.status = 'CANCELLED' OR NEW.status = 'NO_SHOW' THEN
        UPDATE restaurant_tables SET status = 'AVAILABLE' WHERE table_id = NEW.table_id;
    ELSEIF NEW.status = 'CONFIRMED' AND NEW.reservation_date = CURDATE() THEN
        UPDATE restaurant_tables SET status = 'RESERVED' WHERE table_id = NEW.table_id;
    END IF;
END //

DELIMITER ;

-- ============================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- ============================================

/*
 * ============================================
 * PERFORMANCE INDEXES
 * ============================================
 * These composite indexes optimize common query patterns
 * for faster data retrieval and reporting.
 * 
 * idx_bills_customer_date:
 *   - Speeds up: Customer purchase history queries
 *   - Example: SELECT * FROM bills WHERE customer_id = ? ORDER BY created_at
 * 
 * idx_bills_user_status:
 *   - Speeds up: Cashier performance reports by payment status
 *   - Example: SELECT * FROM bills WHERE user_id = ? AND payment_status = 'PAID'
 * 
 * idx_order_items_product_created:
 *   - Speeds up: Product sales over time queries
 *   - Example: SELECT * FROM order_items WHERE product_id = ? AND created_at > ?
 * 
 * idx_loyalty_trans_customer_type:
 *   - Speeds up: Loyalty transaction history by type
 *   - Example: SELECT * FROM loyalty_transactions WHERE customer_id = ? AND type = 'EARNED'
 * 
 * idx_reservations_date_status:
 *   - Speeds up: Daily reservation reports
 *   - Example: SELECT * FROM reservations WHERE date = ? AND status = 'CONFIRMED'
 * 
 * idx_products_category_available:
 *   - Speeds up: Menu display queries (available items by category)
 *   - Example: SELECT * FROM products WHERE category_id = ? AND is_available = TRUE
 */
CREATE INDEX idx_bills_customer_date ON bills(customer_id, created_at);
CREATE INDEX idx_bills_user_status ON bills(user_id, payment_status);
CREATE INDEX idx_order_items_product_created ON order_items(product_id, created_at);
CREATE INDEX idx_loyalty_trans_customer_type ON loyalty_transactions(customer_id, transaction_type);
CREATE INDEX idx_reservations_date_status ON reservations(reservation_date, status);
CREATE INDEX idx_products_category_available ON products(category_id, is_available);

-- ============================================
-- GRANT PERMISSIONS (Optional)
-- ============================================

-- Create application user (uncomment and set password as needed)
-- CREATE USER IF NOT EXISTS 'rms_app'@'localhost' IDENTIFIED BY 'your_secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON restaurant_db.* TO 'rms_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ============================================
-- DATABASE SETUP COMPLETE
-- ============================================

SELECT 'Database restaurant_db created successfully!' as status;
SELECT 'Total Tables Created:' as info, COUNT(*) as count FROM information_schema.tables WHERE table_schema = 'restaurant_db' AND table_type = 'BASE TABLE';
SELECT 'Total Views Created:' as info, COUNT(*) as count FROM information_schema.views WHERE table_schema = 'restaurant_db';
SELECT 'Total Procedures Created:' as info, COUNT(*) as count FROM information_schema.routines WHERE routine_schema = 'restaurant_db' AND routine_type = 'PROCEDURE';
SELECT 'Total Functions Created:' as info, COUNT(*) as count FROM information_schema.routines WHERE routine_schema = 'restaurant_db' AND routine_type = 'FUNCTION';
SELECT 'Total Triggers Created:' as info, COUNT(*) as count FROM information_schema.triggers WHERE trigger_schema = 'restaurant_db';
SELECT 'Total Events Created:' as info, COUNT(*) as count FROM information_schema.events WHERE event_schema = 'restaurant_db';
SELECT 'Total Indexes Created:' as info, COUNT(DISTINCT index_name) as count FROM information_schema.statistics WHERE table_schema = 'restaurant_db' AND index_name != 'PRIMARY';
