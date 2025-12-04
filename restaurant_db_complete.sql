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

-- Users Table (3NF: No transitive dependencies)
-- Roles: ADMIN, SERVER (waiter/waitress), CHEF, CUSTOMER
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

-- Product Categories Table (Normalized)
CREATE TABLE product_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Products Table (3NF: Category separated to eliminate redundancy)
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

-- Customers Table (3NF: All attributes depend only on customer_id)
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

-- Restaurant Tables (Physical tables in restaurant)
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

-- Order Types Table (3NF: Normalized order type info)
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

-- Orders Table (3NF: Main order tracking for kitchen/servers)
-- This is separate from bills - orders track what kitchen needs to prepare
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

-- Kitchen Order Items Table (3NF: Items for kitchen to prepare)
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

-- Order Status History Table (3NF: Audit trail for order status changes)
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

-- Bills Table (3NF: Links to order, customer and user info referenced)
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

-- Order Items Table (3NF: Weak entity depending on bill)
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

-- Reservations Table (3NF: Related to table and customer)
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

-- Loyalty Transactions Table (3NF: Transaction history normalized)
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

-- Inventory Adjustments Table (3NF: Stock movement tracking)
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

-- Customer Preferences Table (3NF: Separate preferences from customer)
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

-- Employee Schedules Table (3NF: Schedule info depends only on schedule_id)
-- Stores work schedules for servers and chefs
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

-- Leave Requests Table (3NF: Leave request info depends only on request_id)
-- Stores leave/time-off requests from employees
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

-- Insert Sample Customers
INSERT INTO customers (full_name, email, phone, date_of_birth, address, membership_tier, loyalty_points, total_spent, visit_count, last_visit_date) VALUES
('Rajesh Kumar', 'rajesh.kumar@email.com', '9841111111', '1985-05-15', 'Kathmandu, Nepal', 'GOLD', 2500, 55000.00, 45, CURDATE()),
('Sita Sharma', 'sita.sharma@email.com', '9841222222', '1990-08-20', 'Lalitpur, Nepal', 'SILVER', 1200, 28000.00, 28, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('Ramesh Thapa', 'ramesh.thapa@email.com', '9841333333', '1988-03-10', 'Bhaktapur, Nepal', 'BRONZE', 450, 8000.00, 12, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('Gita Poudel', 'gita.poudel@email.com', '9841444444', '1992-11-25', 'Pokhara, Nepal', 'PLATINUM', 5000, 95000.00, 78, CURDATE()),
('Krishna Adhikari', 'krishna.adhikari@email.com', '9841555555', '1987-07-30', 'Chitwan, Nepal', 'SILVER', 1500, 32000.00, 35, DATE_SUB(CURDATE(), INTERVAL 3 DAY));

-- Insert Sample Orders (for Kitchen Display)
INSERT INTO orders (order_number, order_type_id, table_id, customer_id, server_id, customer_name, guest_count, order_status, kitchen_notes, priority) VALUES
('ORD-20251204-001', 1, 3, 1, 2, 'Rajesh Kumar', 2, 'PREPARING', 'No onions in burger', 'NORMAL'),
('ORD-20251204-002', 1, 5, 2, 2, 'Sita Sharma', 4, 'PENDING', NULL, 'NORMAL'),
('ORD-20251204-003', 2, NULL, NULL, 3, 'Walk-in Customer', 1, 'READY', 'Extra napkins', 'RUSH'),
('ORD-20251204-004', 1, 7, 4, 3, 'Gita Poudel', 6, 'CONFIRMED', 'VIP Guest - Priority service', 'VIP'),
('ORD-20251204-005', 3, NULL, 5, 2, 'Krishna Adhikari', 1, 'PENDING', 'Delivery to Chitwan office', 'NORMAL');

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

-- Kitchen Display View (Shows pending orders for chefs)
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

-- Kitchen Order Items Detail View
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

-- Daily Sales Summary View
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

-- Product Sales Summary View
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

-- User Performance View
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

-- Low Stock Alert View
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

-- Customer Loyalty Summary View
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

-- Table Utilization View
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

-- Reservation Status View
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

-- Procedure: Create New Bill
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

-- Procedure: Add Loyalty Points
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

-- Procedure: Redeem Loyalty Points
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

-- Procedure: Check and Update Membership Tier
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

-- Procedure: Adjust Inventory
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

-- Procedure: Create Reservation
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

DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER //

-- Trigger: Prevent negative loyalty points
CREATE TRIGGER trg_prevent_negative_points
BEFORE UPDATE ON customers
FOR EACH ROW
BEGIN
    IF NEW.loyalty_points < 0 THEN
        SET NEW.loyalty_points = 0;
    END IF;
END //

-- Trigger: Update product availability based on stock
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

-- Trigger: Auto-calculate order item subtotal
CREATE TRIGGER trg_calculate_order_subtotal
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //

-- Trigger: Update order item subtotal on update
CREATE TRIGGER trg_update_order_subtotal
BEFORE UPDATE ON order_items
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //

-- Trigger: Reduce stock when order is placed
CREATE TRIGGER trg_reduce_stock_on_order
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity - NEW.quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = NEW.product_id;
END //

-- Trigger: Update table status when reservation is confirmed/completed
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

-- Additional composite indexes for common queries
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
