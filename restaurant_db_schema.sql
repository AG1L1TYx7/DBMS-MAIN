-- =========================================================
-- Restaurant Management System Database Schema
-- Version: 2.0 - MVC Architecture
-- Database: restaurant_db
-- Compatible with MySQL Workbench 8.0+
-- =========================================================

-- Drop database if exists and create new
DROP DATABASE IF EXISTS restaurant_db;
CREATE DATABASE restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE restaurant_db;

-- =========================================================
-- Table: users
-- Description: Stores employee and admin user information
-- =========================================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    role ENUM('EMPLOYEE', 'ADMIN', 'MANAGER') DEFAULT 'EMPLOYEE',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email_address),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- Table: products
-- Description: Stores menu items and products
-- =========================================================
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL UNIQUE,
    category ENUM('BURGER', 'CHICKEN_ROLL', 'RICE_MEALS', 'BEVERAGE', 
                  'FRIES', 'DESSERTS', 'SOFT_COCKTAIL', 'MILKSHAKE', 
                  'APPETIZER', 'SPECIAL') NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    image_path VARCHAR(500),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_product_name (product_name),
    INDEX idx_is_available (is_available),
    CONSTRAINT chk_price CHECK (price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- Table: bills
-- Description: Stores order bills and invoices
-- =========================================================
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) NOT NULL UNIQUE,
    net_amount DECIMAL(10,2) NOT NULL,
    total_items INT NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    cash_received DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    payment_method ENUM('CASH', 'CARD', 'DIGITAL_WALLET', 'UPI') DEFAULT 'CASH',
    user_id INT,
    billed_by_user VARCHAR(100),
    billed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bill_number (bill_number),
    INDEX idx_billed_at (billed_at),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_net_amount CHECK (net_amount >= 0),
    CONSTRAINT chk_total_amount CHECK (total_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- Table: order_items
-- Description: Stores individual items in each order/bill
-- =========================================================
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bill_id (bill_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    CONSTRAINT chk_quantity CHECK (quantity > 0),
    CONSTRAINT chk_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_subtotal CHECK (subtotal >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- Sample Data Insertion
-- =========================================================

-- Insert sample users
INSERT INTO users (full_name, email_address, username, phone_number, password_hash, address, role) VALUES
('Admin User', 'admin@restaurant.com', 'admin', '9800000000', 'admin', 'Kathmandu, Nepal', 'ADMIN'),
('Krishna Sharma', 'krishna.sharma@restaurant.com', 'krishna', '9801234567', 'Krishna@123', 'Lalitpur, Nepal', 'EMPLOYEE'),
('Sita Adhikari', 'sita.adhikari@restaurant.com', 'sita', '9809876543', 'Sita@123', 'Bhaktapur, Nepal', 'EMPLOYEE'),
('Ram Thapa', 'ram.thapa@restaurant.com', 'ramthapa', '9812345678', 'Ram@1234', 'Pokhara, Nepal', 'MANAGER');

-- Insert sample products - Burgers
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Classic Beef Burger', 'BURGER', 350.00, 'Juicy beef patty with fresh vegetables, cheese, and special sauce', '/images/classic_burger.jpg', TRUE),
('Chicken Supreme Burger', 'BURGER', 320.00, 'Grilled chicken breast with lettuce, tomato, and mayo', '/images/chicken_burger.jpg', TRUE),
('Vegetarian Delight Burger', 'BURGER', 280.00, 'Plant-based patty with avocado and fresh veggies', '/images/veg_burger.jpg', TRUE),
('Double Cheese Burger', 'BURGER', 420.00, 'Two beef patties with double cheese and special sauce', '/images/double_burger.jpg', TRUE);

-- Insert sample products - Rice Meals
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Chicken Biryani Deluxe', 'RICE_MEALS', 450.00, 'Aromatic basmati rice cooked with tender chicken and exotic spices', '/images/chicken_biryani.jpg', TRUE),
('Vegetable Pulao Supreme', 'RICE_MEALS', 320.00, 'Fragrant rice with mixed vegetables and aromatic spices', '/images/veg_pulao.jpg', TRUE),
('Mutton Biryani Special', 'RICE_MEALS', 550.00, 'Premium mutton pieces cooked with saffron rice', '/images/mutton_biryani.jpg', TRUE),
('Egg Fried Rice', 'RICE_MEALS', 280.00, 'Stir-fried rice with eggs and vegetables', '/images/egg_fried_rice.jpg', TRUE);

-- Insert sample products - Beverages
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Fresh Lemon Soda', 'BEVERAGE', 120.00, 'Refreshing lemon soda with ice', '/images/lemon_soda.jpg', TRUE),
('Mango Lassi', 'BEVERAGE', 150.00, 'Creamy yogurt drink with fresh mango', '/images/mango_lassi.jpg', TRUE),
('Mineral Water', 'BEVERAGE', 50.00, '500ml mineral water bottle', '/images/water.jpg', TRUE),
('Cola', 'BEVERAGE', 100.00, 'Chilled cola drink', '/images/cola.jpg', TRUE),
('Orange Juice', 'BEVERAGE', 180.00, 'Freshly squeezed orange juice', '/images/orange_juice.jpg', TRUE);

-- Insert sample products - Fries
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Classic French Fries', 'FRIES', 150.00, 'Crispy golden french fries', '/images/french_fries.jpg', TRUE),
('Cheese Loaded Fries', 'FRIES', 220.00, 'Fries topped with melted cheese', '/images/cheese_fries.jpg', TRUE),
('Peri-Peri Fries', 'FRIES', 180.00, 'Spicy peri-peri seasoned fries', '/images/peri_fries.jpg', TRUE);

-- Insert sample products - Desserts
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Chocolate Brownie', 'DESSERTS', 180.00, 'Warm chocolate brownie with ice cream', '/images/brownie.jpg', TRUE),
('Vanilla Ice Cream', 'DESSERTS', 120.00, 'Premium vanilla ice cream', '/images/ice_cream.jpg', TRUE),
('Fruit Salad', 'DESSERTS', 150.00, 'Fresh seasonal fruits with honey', '/images/fruit_salad.jpg', TRUE),
('Gulab Jamun', 'DESSERTS', 100.00, 'Traditional sweet dumplings in syrup', '/images/gulab_jamun.jpg', TRUE);

-- Insert sample products - Milkshakes
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Chocolate Milkshake', 'MILKSHAKE', 220.00, 'Rich chocolate milkshake with whipped cream', '/images/choco_shake.jpg', TRUE),
('Strawberry Milkshake', 'MILKSHAKE', 220.00, 'Fresh strawberry milkshake', '/images/strawberry_shake.jpg', TRUE),
('Vanilla Milkshake', 'MILKSHAKE', 200.00, 'Classic vanilla milkshake', '/images/vanilla_shake.jpg', TRUE),
('Oreo Milkshake', 'MILKSHAKE', 250.00, 'Oreo cookies blended with ice cream', '/images/oreo_shake.jpg', TRUE);

-- Insert sample products - Soft Cocktails
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Virgin Mojito', 'SOFT_COCKTAIL', 180.00, 'Refreshing mint and lime mocktail', '/images/mojito.jpg', TRUE),
('Blue Lagoon Mocktail', 'SOFT_COCKTAIL', 200.00, 'Blue curacao flavored mocktail', '/images/blue_lagoon.jpg', TRUE),
('Tropical Paradise', 'SOFT_COCKTAIL', 220.00, 'Mixed tropical fruits mocktail', '/images/tropical.jpg', TRUE);

-- Insert sample products - Chicken Rolls
INSERT INTO products (product_name, category, price, description, image_path, is_available) VALUES
('Spicy Chicken Roll', 'CHICKEN_ROLL', 180.00, 'Grilled chicken with spicy sauce wrapped in soft bread', '/images/spicy_roll.jpg', TRUE),
('Tandoori Chicken Roll', 'CHICKEN_ROLL', 200.00, 'Tandoori chicken with onions and mint chutney', '/images/tandoori_roll.jpg', TRUE);

-- =========================================================
-- Views for Reporting
-- =========================================================

-- View: Daily Sales Summary
CREATE VIEW v_daily_sales_summary AS
SELECT 
    DATE(billed_at) as sale_date,
    COUNT(bill_id) as total_orders,
    SUM(total_items) as total_items_sold,
    SUM(net_amount) as total_net_amount,
    SUM(tax_amount) as total_tax_amount,
    SUM(total_amount) as total_sales_amount
FROM bills
GROUP BY DATE(billed_at)
ORDER BY sale_date DESC;

-- View: Product Sales Summary
CREATE VIEW v_product_sales_summary AS
SELECT 
    p.product_id,
    p.product_name,
    p.category,
    COUNT(oi.order_item_id) as times_ordered,
    SUM(oi.quantity) as total_quantity_sold,
    SUM(oi.subtotal) as total_revenue
FROM products p
LEFT JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.product_name, p.category
ORDER BY total_revenue DESC;

-- View: User Performance
CREATE VIEW v_user_performance AS
SELECT 
    u.user_id,
    u.full_name,
    u.username,
    u.role,
    COUNT(b.bill_id) as total_bills_created,
    SUM(b.total_amount) as total_sales_amount
FROM users u
LEFT JOIN bills b ON u.user_id = b.user_id
GROUP BY u.user_id, u.full_name, u.username, u.role
ORDER BY total_sales_amount DESC;

-- =========================================================
-- Stored Procedures
-- =========================================================

DELIMITER $$

-- Procedure: Get Sales Statistics
CREATE PROCEDURE sp_get_sales_statistics()
BEGIN
    SELECT 
        (SELECT SUM(total_amount) FROM bills) as total_sales,
        (SELECT SUM(total_amount) FROM bills ORDER BY bill_id DESC LIMIT 1) as last_sale,
        (SELECT SUM(total_items) FROM bills) as total_orders,
        (SELECT COUNT(*) FROM bills) as total_bills,
        (SELECT COUNT(*) FROM products WHERE is_available = TRUE) as available_products;
END$$

-- Procedure: Get Top Selling Products
CREATE PROCEDURE sp_get_top_selling_products(IN limit_count INT)
BEGIN
    SELECT 
        p.product_id,
        p.product_name,
        p.category,
        p.price,
        SUM(oi.quantity) as total_sold,
        SUM(oi.subtotal) as total_revenue
    FROM products p
    INNER JOIN order_items oi ON p.product_id = oi.product_id
    GROUP BY p.product_id, p.product_name, p.category, p.price
    ORDER BY total_sold DESC
    LIMIT limit_count;
END$$

DELIMITER ;

-- =========================================================
-- Triggers
-- =========================================================

DELIMITER $$

-- Trigger: Update product updated_at timestamp
CREATE TRIGGER trg_product_updated
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

-- Trigger: Validate order item subtotal
CREATE TRIGGER trg_validate_order_item
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    DECLARE calculated_subtotal DECIMAL(10,2);
    SET calculated_subtotal = NEW.quantity * NEW.unit_price;
    IF NEW.subtotal != calculated_subtotal THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Subtotal does not match quantity × unit_price';
    END IF;
END$$

DELIMITER ;

-- =========================================================
-- Grant Permissions (for MySQL Workbench users)
-- =========================================================

-- Create application user (change password as needed)
-- CREATE USER IF NOT EXISTS 'restaurant_app'@'localhost' IDENTIFIED BY 'RestaurantApp@2025';
-- GRANT ALL PRIVILEGES ON restaurant_db.* TO 'restaurant_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =========================================================
-- Display Summary
-- =========================================================

SELECT 'Database Setup Complete!' as Status;
SELECT COUNT(*) as Total_Users FROM users;
SELECT COUNT(*) as Total_Products FROM products;
SELECT COUNT(*) as Total_Categories FROM (SELECT DISTINCT category FROM products) as categories;

-- =========================================================
-- End of Database Schema
-- =========================================================
