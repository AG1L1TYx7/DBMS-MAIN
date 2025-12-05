-- ============================================
-- Database Performance Optimization Script
-- Restaurant Management System
-- Run this after restaurant_db_complete.sql
-- ============================================

USE restaurant_db;

-- ============================================
-- ADDITIONAL INDEXES FOR QUERY OPTIMIZATION
-- ============================================

-- Composite indexes for common queries
-- Orders: Often queried by date range and status
CREATE INDEX IF NOT EXISTS idx_orders_date_status 
ON orders(created_at, order_status);

-- Orders: Server lookup with date
CREATE INDEX IF NOT EXISTS idx_orders_server_date 
ON orders(server_id, created_at);

-- Bills: Date range queries for reporting
CREATE INDEX IF NOT EXISTS idx_bills_date_range 
ON bills(created_at, payment_status);

-- Bills: Customer spending history
CREATE INDEX IF NOT EXISTS idx_bills_customer_date 
ON bills(customer_id, created_at);

-- Products: Category and availability queries
CREATE INDEX IF NOT EXISTS idx_products_category_available 
ON products(category_id, is_available);

-- Products: Low stock alerts
CREATE INDEX IF NOT EXISTS idx_products_low_stock 
ON products(stock_quantity, reorder_level);

-- Reservations: Date and time lookup
CREATE INDEX IF NOT EXISTS idx_reservations_datetime 
ON reservations(reservation_date, reservation_time);

-- Reservations: Customer phone lookup (common search)
CREATE INDEX IF NOT EXISTS idx_reservations_phone 
ON reservations(customer_phone);

-- Customers: Name search (partial matching)
CREATE INDEX IF NOT EXISTS idx_customers_name 
ON customers(full_name(50));

-- Kitchen order items: Status tracking
CREATE INDEX IF NOT EXISTS idx_kitchen_items_status_order 
ON kitchen_order_items(item_status, order_id);

-- ============================================
-- STORED PROCEDURES FOR COMMON OPERATIONS
-- ============================================

DELIMITER //

-- Get daily sales summary
DROP PROCEDURE IF EXISTS sp_daily_sales_summary//
CREATE PROCEDURE sp_daily_sales_summary(IN report_date DATE)
BEGIN
    SELECT 
        COUNT(*) as total_orders,
        SUM(total_amount) as total_revenue,
        AVG(total_amount) as avg_order_value,
        SUM(CASE WHEN payment_method = 'CASH' THEN total_amount ELSE 0 END) as cash_sales,
        SUM(CASE WHEN payment_method = 'CARD' THEN total_amount ELSE 0 END) as card_sales,
        SUM(CASE WHEN payment_method = 'DIGITAL_WALLET' THEN total_amount ELSE 0 END) as digital_sales
    FROM bills 
    WHERE DATE(created_at) = report_date 
    AND payment_status = 'PAID';
END//

-- Get top selling products
DROP PROCEDURE IF EXISTS sp_top_products//
CREATE PROCEDURE sp_top_products(IN days_back INT, IN limit_count INT)
BEGIN
    SELECT 
        p.product_id,
        p.product_name,
        pc.category_name,
        SUM(oi.quantity) as total_quantity,
        SUM(oi.subtotal) as total_revenue
    FROM order_items oi
    JOIN products p ON oi.product_id = p.product_id
    JOIN product_categories pc ON p.category_id = pc.category_id
    JOIN bills b ON oi.bill_id = b.bill_id
    WHERE b.created_at >= DATE_SUB(CURDATE(), INTERVAL days_back DAY)
    AND b.payment_status = 'PAID'
    GROUP BY p.product_id, p.product_name, pc.category_name
    ORDER BY total_quantity DESC
    LIMIT limit_count;
END//

-- Get low stock products
DROP PROCEDURE IF EXISTS sp_low_stock_alert//
CREATE PROCEDURE sp_low_stock_alert()
BEGIN
    SELECT 
        p.product_id,
        p.product_name,
        pc.category_name,
        p.stock_quantity,
        p.reorder_level,
        (p.reorder_level - p.stock_quantity) as units_needed
    FROM products p
    JOIN product_categories pc ON p.category_id = pc.category_id
    WHERE p.stock_quantity <= p.reorder_level
    AND p.is_available = TRUE
    ORDER BY (p.reorder_level - p.stock_quantity) DESC;
END//

-- Update product stock with audit
DROP PROCEDURE IF EXISTS sp_update_stock//
CREATE PROCEDURE sp_update_stock(
    IN p_product_id INT,
    IN p_adjustment_type VARCHAR(20),
    IN p_quantity_change INT,
    IN p_reason TEXT,
    IN p_adjusted_by INT
)
BEGIN
    DECLARE current_stock INT;
    
    START TRANSACTION;
    
    -- Get current stock with row lock
    SELECT stock_quantity INTO current_stock 
    FROM products 
    WHERE product_id = p_product_id 
    FOR UPDATE;
    
    -- Update stock
    UPDATE products 
    SET stock_quantity = CASE 
        WHEN p_adjustment_type IN ('ADD', 'RESTOCK') THEN stock_quantity + p_quantity_change
        WHEN p_adjustment_type IN ('REMOVE', 'DAMAGE', 'EXPIRED') THEN GREATEST(0, stock_quantity - p_quantity_change)
        WHEN p_adjustment_type = 'SET' THEN p_quantity_change
        ELSE stock_quantity
    END,
    updated_at = NOW()
    WHERE product_id = p_product_id;
    
    -- Record audit
    INSERT INTO inventory_adjustments 
        (product_id, adjustment_type, quantity_change, quantity_before, quantity_after, reason, adjusted_by)
    VALUES 
        (p_product_id, p_adjustment_type, p_quantity_change, current_stock,
         (SELECT stock_quantity FROM products WHERE product_id = p_product_id),
         p_reason, p_adjusted_by);
    
    COMMIT;
END//

-- Get customer order history
DROP PROCEDURE IF EXISTS sp_customer_history//
CREATE PROCEDURE sp_customer_history(IN p_customer_id INT, IN limit_count INT)
BEGIN
    SELECT 
        b.bill_id,
        b.bill_number,
        b.created_at,
        b.total_amount,
        b.payment_method,
        COUNT(oi.order_item_id) as item_count
    FROM bills b
    LEFT JOIN order_items oi ON b.bill_id = oi.bill_id
    WHERE b.customer_id = p_customer_id
    GROUP BY b.bill_id, b.bill_number, b.created_at, b.total_amount, b.payment_method
    ORDER BY b.created_at DESC
    LIMIT limit_count;
END//

-- Create new order with items (transaction)
DROP PROCEDURE IF EXISTS sp_create_order//
CREATE PROCEDURE sp_create_order(
    IN p_order_type_id INT,
    IN p_table_id INT,
    IN p_customer_id INT,
    IN p_server_id INT,
    IN p_customer_name VARCHAR(100),
    IN p_customer_phone VARCHAR(20),
    IN p_guest_count INT,
    IN p_kitchen_notes TEXT,
    OUT p_order_id INT,
    OUT p_order_number VARCHAR(50)
)
BEGIN
    DECLARE v_order_number VARCHAR(50);
    
    START TRANSACTION;
    
    -- Generate order number
    SET v_order_number = CONCAT('ORD-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', 
                                LPAD(FLOOR(RAND() * 10000), 4, '0'));
    
    -- Create order
    INSERT INTO orders (order_number, order_type_id, table_id, customer_id, server_id,
                       customer_name, customer_phone, guest_count, kitchen_notes, order_status)
    VALUES (v_order_number, p_order_type_id, p_table_id, p_customer_id, p_server_id,
            p_customer_name, p_customer_phone, p_guest_count, p_kitchen_notes, 'PENDING');
    
    SET p_order_id = LAST_INSERT_ID();
    SET p_order_number = v_order_number;
    
    -- Update table status if dine-in
    IF p_table_id IS NOT NULL THEN
        UPDATE restaurant_tables SET status = 'OCCUPIED' WHERE table_id = p_table_id;
    END IF;
    
    COMMIT;
END//

DELIMITER ;

-- ============================================
-- VIEWS FOR COMMON QUERIES
-- ============================================

-- Active orders view
CREATE OR REPLACE VIEW v_active_orders AS
SELECT 
    o.order_id,
    o.order_number,
    ot.type_name as order_type,
    rt.table_number,
    o.customer_name,
    o.order_status,
    o.priority,
    o.created_at,
    u.full_name as server_name,
    COUNT(koi.kitchen_item_id) as total_items,
    SUM(CASE WHEN koi.item_status = 'READY' THEN 1 ELSE 0 END) as items_ready
FROM orders o
JOIN order_types ot ON o.order_type_id = ot.order_type_id
LEFT JOIN restaurant_tables rt ON o.table_id = rt.table_id
JOIN users u ON o.server_id = u.user_id
LEFT JOIN kitchen_order_items koi ON o.order_id = koi.order_id
WHERE o.order_status NOT IN ('COMPLETED', 'CANCELLED')
GROUP BY o.order_id, o.order_number, ot.type_name, rt.table_number, 
         o.customer_name, o.order_status, o.priority, o.created_at, u.full_name;

-- Today's reservations view
CREATE OR REPLACE VIEW v_todays_reservations AS
SELECT 
    r.reservation_id,
    r.customer_name,
    r.customer_phone,
    r.party_size,
    r.reservation_time,
    r.status,
    rt.table_number,
    rt.capacity as table_capacity,
    r.special_requests
FROM reservations r
JOIN restaurant_tables rt ON r.table_id = rt.table_id
WHERE r.reservation_date = CURDATE()
ORDER BY r.reservation_time;

-- Product inventory status view
CREATE OR REPLACE VIEW v_inventory_status AS
SELECT 
    p.product_id,
    p.product_name,
    pc.category_name,
    p.stock_quantity,
    p.reorder_level,
    p.max_stock_level,
    p.unit,
    CASE 
        WHEN p.stock_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN p.stock_quantity <= p.reorder_level THEN 'LOW_STOCK'
        WHEN p.stock_quantity >= p.max_stock_level THEN 'OVERSTOCKED'
        ELSE 'NORMAL'
    END as stock_status,
    p.is_available
FROM products p
JOIN product_categories pc ON p.category_id = pc.category_id
ORDER BY 
    CASE 
        WHEN p.stock_quantity = 0 THEN 1
        WHEN p.stock_quantity <= p.reorder_level THEN 2
        ELSE 3
    END,
    p.product_name;

-- Daily revenue summary view
CREATE OR REPLACE VIEW v_daily_revenue AS
SELECT 
    DATE(b.created_at) as sale_date,
    COUNT(*) as order_count,
    SUM(b.subtotal) as gross_sales,
    SUM(b.discount_amount) as total_discounts,
    SUM(b.tax_amount) as total_tax,
    SUM(b.total_amount) as net_revenue,
    AVG(b.total_amount) as avg_order_value
FROM bills b
WHERE b.payment_status = 'PAID'
GROUP BY DATE(b.created_at)
ORDER BY sale_date DESC;

-- ============================================
-- QUERY OPTIMIZATION CONFIGURATION
-- ============================================

-- Set recommended MySQL configuration (run as admin)
-- These are suggestions - actual values depend on server resources

-- Buffer pool size (should be ~70-80% of available RAM for dedicated servers)
-- SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB

-- Query cache (disabled by default in MySQL 8.0+)
-- Enabling if using MySQL 5.7
-- SET GLOBAL query_cache_type = 1;
-- SET GLOBAL query_cache_size = 67108864; -- 64MB

-- ============================================
-- TABLE MAINTENANCE PROCEDURES
-- ============================================

DELIMITER //

-- Analyze tables for query optimization
DROP PROCEDURE IF EXISTS sp_analyze_tables//
CREATE PROCEDURE sp_analyze_tables()
BEGIN
    ANALYZE TABLE users, products, product_categories, customers, 
                  restaurant_tables, orders, kitchen_order_items, 
                  bills, order_items, reservations;
END//

-- Optimize tables (reclaim space and defragment)
DROP PROCEDURE IF EXISTS sp_optimize_tables//
CREATE PROCEDURE sp_optimize_tables()
BEGIN
    OPTIMIZE TABLE users, products, customers, orders, bills, order_items, reservations;
END//

DELIMITER ;

-- ============================================
-- SCHEDULED EVENT FOR MAINTENANCE (Optional)
-- ============================================

-- Enable event scheduler if not enabled
-- SET GLOBAL event_scheduler = ON;

-- Weekly table optimization
-- CREATE EVENT IF NOT EXISTS evt_weekly_optimization
-- ON SCHEDULE EVERY 1 WEEK
-- STARTS (TIMESTAMP(CURRENT_DATE, '03:00:00') + INTERVAL 1 DAY)
-- DO CALL sp_optimize_tables();

-- Daily table analysis
-- CREATE EVENT IF NOT EXISTS evt_daily_analysis
-- ON SCHEDULE EVERY 1 DAY
-- STARTS (TIMESTAMP(CURRENT_DATE, '04:00:00') + INTERVAL 1 DAY)
-- DO CALL sp_analyze_tables();

SELECT 'Database optimization script executed successfully!' as status;
