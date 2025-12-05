-- ============================================
-- Database Performance Optimization Script
-- Restaurant Management System
-- Run this after restaurant_db_complete.sql
-- ============================================

/*
 * ============================================
 * DATABASE OPTIMIZATION SUMMARY
 * ============================================
 * 
 * This script implements the following optimizations:
 * 
 * 1. HIKARICP CONNECTION POOL (Java Layer)
 *    - Pool Size: 5-20 connections (reuses instead of creating new)
 *    - Prepared Statement Cache: 250 statements
 *    - Batch Rewriting: Enabled (10-50x faster bulk inserts)
 *    - Server-side Prepared Statements: Better query plan caching
 * 
 * 2. COMPOSITE INDEXES
 *    - idx_orders_date_status: Orders by date + status (reporting)
 *    - idx_orders_server_date: Server lookup with date
 *    - idx_bills_date_range: Date range queries for reporting
 *    - idx_bills_customer_date: Customer spending history
 *    - idx_products_category_available: Products by category/availability
 *    - idx_products_low_stock: Low stock alerts
 *    - idx_reservations_datetime: Reservation date/time lookups
 *    - idx_reservations_phone: Customer phone search
 *    - idx_customers_name: Name search (partial matching)
 *    - idx_kitchen_items_status_order: Kitchen status tracking
 * 
 * 3. STORED PROCEDURES
 *    - sp_daily_sales_summary: Fast daily revenue reports
 *    - sp_top_products: Best-selling items analysis
 *    - sp_low_stock_alert: Inventory warnings
 *    - sp_update_stock: Atomic stock updates with audit trail
 *    - sp_customer_history: Customer order history lookup
 *    - sp_create_order: Transactional order creation
 * 
 * 4. DATABASE VIEWS (Pre-joined Queries)
 *    - v_active_orders: Kitchen display - orders in progress
 *    - v_todays_reservations: Host/front desk - today's bookings
 *    - v_inventory_status: Stock status with LOW/OUT indicators
 *    - v_daily_revenue: Sales dashboard data
 * 
 * 5. TABLE MAINTENANCE PROCEDURES
 *    - sp_analyze_tables: Update query optimizer statistics
 *    - sp_optimize_tables: Reclaim space and defragment
 * 
 * PERFORMANCE IMPACT:
 *    Before: New connection per query, full table scans, multiple round-trips
 *    After:  Connection reuse, index lookups, single stored procedure calls
 * 
 * TO APPLY: mysql -u root -p restaurant_db < database_optimization.sql
 * ============================================
 */

USE restaurant_db;

-- ============================================
-- ADDITIONAL INDEXES FOR QUERY OPTIMIZATION
-- ============================================

/*
 * INDEX: idx_orders_date_status
 * Table: orders
 * Columns: (created_at, order_status)
 * 
 * Purpose: Optimizes queries that filter orders by date range and status.
 * Common use cases:
 *   - Daily/weekly/monthly sales reports
 *   - Finding pending orders for a specific date
 *   - Order history filtering by status and time period
 * 
 * Example query optimized:
 *   SELECT * FROM orders WHERE created_at BETWEEN '2024-01-01' AND '2024-01-31' 
 *   AND order_status = 'COMPLETED';
 */
CREATE INDEX IF NOT EXISTS idx_orders_date_status 
ON orders(created_at, order_status);

/*
 * INDEX: idx_orders_server_date
 * Table: orders
 * Columns: (server_id, created_at)
 * 
 * Purpose: Optimizes queries for server performance tracking.
 * Common use cases:
 *   - Server shift reports
 *   - Individual server order history
 *   - Commission calculations per server
 */
CREATE INDEX IF NOT EXISTS idx_orders_server_date 
ON orders(server_id, created_at);

/*
 * INDEX: idx_bills_date_range
 * Table: bills
 * Columns: (created_at, payment_status)
 * 
 * Purpose: Optimizes financial reporting queries.
 * Common use cases:
 *   - Daily revenue reports
 *   - End-of-day cash reconciliation
 *   - Finding unpaid bills for follow-up
 */
CREATE INDEX IF NOT EXISTS idx_bills_date_range 
ON bills(created_at, payment_status);

/*
 * INDEX: idx_bills_customer_date
 * Table: bills
 * Columns: (customer_id, created_at)
 * 
 * Purpose: Optimizes customer loyalty and history queries.
 * Common use cases:
 *   - Customer spending history lookup
 *   - Loyalty points calculation
 *   - VIP customer identification
 *   - Personalized marketing based on purchase history
 */
CREATE INDEX IF NOT EXISTS idx_bills_customer_date 
ON bills(customer_id, created_at);

/*
 * INDEX: idx_products_category_available
 * Table: products
 * Columns: (category_id, is_available)
 * 
 * Purpose: Optimizes menu display and product listing queries.
 * Common use cases:
 *   - Displaying available items by category
 *   - Menu filtering in the ordering system
 *   - Hiding out-of-stock items from customers
 */
CREATE INDEX IF NOT EXISTS idx_products_category_available 
ON products(category_id, is_available);

/*
 * INDEX: idx_products_low_stock
 * Table: products
 * Columns: (stock_quantity, reorder_level)
 * 
 * Purpose: Optimizes inventory management and alert queries.
 * Common use cases:
 *   - Low stock alerts dashboard
 *   - Automatic reorder triggers
 *   - Inventory status reports
 *   - WHERE stock_quantity <= reorder_level queries
 */
CREATE INDEX IF NOT EXISTS idx_products_low_stock 
ON products(stock_quantity, reorder_level);

/*
 * INDEX: idx_reservations_datetime
 * Table: reservations
 * Columns: (reservation_date, reservation_time)
 * 
 * Purpose: Optimizes reservation lookup and availability checks.
 * Common use cases:
 *   - Checking table availability for a specific date/time
 *   - Today's reservations list for host station
 *   - Upcoming reservations calendar view
 */
CREATE INDEX IF NOT EXISTS idx_reservations_datetime 
ON reservations(reservation_date, reservation_time);

/*
 * INDEX: idx_reservations_phone
 * Table: reservations
 * Columns: (customer_phone)
 * 
 * Purpose: Optimizes customer lookup by phone number.
 * Common use cases:
 *   - Finding reservation when customer calls
 *   - Quick lookup at host station
 *   - Returning customer identification
 */
CREATE INDEX IF NOT EXISTS idx_reservations_phone 
ON reservations(customer_phone);

/*
 * INDEX: idx_customers_name
 * Table: customers
 * Columns: (full_name) - prefix index on first 50 chars
 * 
 * Purpose: Optimizes customer name searches.
 * Common use cases:
 *   - Search customer by name at checkout
 *   - Autocomplete in customer lookup fields
 *   - Customer directory searches
 */
CREATE INDEX IF NOT EXISTS idx_customers_name 
ON customers(full_name(50));

/*
 * INDEX: idx_kitchen_items_status_order
 * Table: kitchen_order_items
 * Columns: (item_status, order_id)
 * 
 * Purpose: Optimizes kitchen display system queries.
 * Common use cases:
 *   - Kitchen display showing pending items
 *   - Tracking items ready for serving
 *   - Order completion status checks
 */
CREATE INDEX IF NOT EXISTS idx_kitchen_items_status_order 
ON kitchen_order_items(item_status, order_id);

-- ============================================
-- STORED PROCEDURES FOR COMMON OPERATIONS
-- ============================================

DELIMITER //

/*
 * PROCEDURE: sp_daily_sales_summary
 * 
 * Purpose: Generates a comprehensive daily sales report.
 * 
 * Parameters:
 *   @report_date (DATE) - The date to generate report for
 * 
 * Returns:
 *   - total_orders: Number of completed orders
 *   - total_revenue: Sum of all paid bills
 *   - avg_order_value: Average transaction amount
 *   - cash_sales: Revenue from cash payments
 *   - card_sales: Revenue from card payments
 *   - digital_sales: Revenue from digital wallet payments
 * 
 * Usage: CALL sp_daily_sales_summary('2024-12-05');
 */
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

/*
 * PROCEDURE: sp_top_products
 * 
 * Purpose: Retrieves best-selling products for analysis and menu optimization.
 * 
 * Parameters:
 *   @days_back (INT) - Number of days to analyze (e.g., 7 for weekly, 30 for monthly)
 *   @limit_count (INT) - Number of top products to return
 * 
 * Returns:
 *   - product_id, product_name, category_name
 *   - total_quantity: Units sold in the period
 *   - total_revenue: Revenue generated by the product
 * 
 * Usage: CALL sp_top_products(30, 10); -- Top 10 products in last 30 days
 */
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

/*
 * PROCEDURE: sp_low_stock_alert
 * 
 * Purpose: Returns all products that need restocking.
 * 
 * Parameters: None
 * 
 * Returns:
 *   - product_id, product_name, category_name
 *   - stock_quantity: Current stock level
 *   - reorder_level: Minimum threshold
 *   - units_needed: How many units to order
 * 
 * Usage: CALL sp_low_stock_alert();
 * 
 * Note: Results sorted by urgency (most needed first)
 */
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

/*
 * PROCEDURE: sp_update_stock
 * 
 * Purpose: Safely updates product stock with full audit trail.
 * Uses transactions and row locking to prevent race conditions.
 * 
 * Parameters:
 *   @p_product_id (INT) - Product to update
 *   @p_adjustment_type (VARCHAR) - Type: 'ADD', 'REMOVE', 'SET', 'RESTOCK', 'DAMAGE', 'EXPIRED'
 *   @p_quantity_change (INT) - Amount to adjust
 *   @p_reason (TEXT) - Reason for adjustment (for audit)
 *   @p_adjusted_by (INT) - User ID making the change
 * 
 * Features:
 *   - Row-level locking prevents concurrent modification issues
 *   - Automatic audit trail in inventory_adjustments table
 *   - Stock cannot go below zero (uses GREATEST function)
 * 
 * Usage: CALL sp_update_stock(1, 'ADD', 50, 'Weekly restock delivery', 1);
 */
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

/*
 * PROCEDURE: sp_customer_history
 * 
 * Purpose: Retrieves a customer's order history for CRM and loyalty programs.
 * 
 * Parameters:
 *   @p_customer_id (INT) - Customer to look up
 *   @limit_count (INT) - Maximum number of orders to return
 * 
 * Returns:
 *   - bill_id, bill_number, created_at
 *   - total_amount, payment_method
 *   - item_count: Number of items in each order
 * 
 * Usage: CALL sp_customer_history(42, 10); -- Last 10 orders for customer 42
 */
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

/*
 * PROCEDURE: sp_create_order
 * 
 * Purpose: Creates a new order with automatic order number generation.
 * Handles table status updates in a single transaction.
 * 
 * Parameters:
 *   @p_order_type_id (INT) - Dine-in, Takeout, or Delivery
 *   @p_table_id (INT) - Restaurant table (NULL for takeout/delivery)
 *   @p_customer_id (INT) - Linked customer account (optional)
 *   @p_server_id (INT) - Server handling the order
 *   @p_customer_name (VARCHAR) - Customer name for the order
 *   @p_customer_phone (VARCHAR) - Contact phone
 *   @p_guest_count (INT) - Number of guests
 *   @p_kitchen_notes (TEXT) - Special instructions for kitchen
 * 
 * Output Parameters:
 *   @p_order_id (INT) - Generated order ID
 *   @p_order_number (VARCHAR) - Generated order number (ORD-YYYYMMDD-XXXX)
 * 
 * Features:
 *   - Auto-generates unique order number
 *   - Automatically marks table as OCCUPIED for dine-in orders
 *   - Full transaction support (all-or-nothing)
 * 
 * Usage:
 *   CALL sp_create_order(1, 5, NULL, 2, 'John Doe', '555-1234', 4, 'No peanuts', @id, @num);
 *   SELECT @id, @num;
 */
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

/*
 * VIEW: v_active_orders
 * 
 * Purpose: Real-time view for kitchen display and server stations.
 * Shows all orders that are currently being processed.
 * 
 * Columns:
 *   - order_id, order_number, order_type
 *   - table_number, customer_name
 *   - order_status, priority, created_at
 *   - server_name: Who is handling the order
 *   - total_items: Number of items in the order
 *   - items_ready: How many items are ready to serve
 * 
 * Excludes: COMPLETED and CANCELLED orders
 * 
 * Usage: SELECT * FROM v_active_orders WHERE order_status = 'PREPARING';
 */
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

/*
 * VIEW: v_todays_reservations
 * 
 * Purpose: Quick lookup for host/hostess station.
 * Shows all reservations for the current day.
 * 
 * Columns:
 *   - reservation_id, customer_name, customer_phone
 *   - party_size, reservation_time, status
 *   - table_number, table_capacity
 *   - special_requests
 * 
 * Auto-filtered: Only shows CURDATE() reservations
 * Sorted: By reservation time (earliest first)
 * 
 * Usage: SELECT * FROM v_todays_reservations WHERE status = 'CONFIRMED';
 */
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

/*
 * VIEW: v_inventory_status
 * 
 * Purpose: Inventory dashboard showing stock levels with status indicators.
 * 
 * Columns:
 *   - product_id, product_name, category_name
 *   - stock_quantity, reorder_level, max_stock_level, unit
 *   - stock_status: Calculated field with values:
 *       'OUT_OF_STOCK' - stock = 0
 *       'LOW_STOCK' - stock <= reorder_level
 *       'OVERSTOCKED' - stock >= max_stock_level
 *       'NORMAL' - everything else
 *   - is_available
 * 
 * Sorted: Out of stock first, then low stock, then normal
 * 
 * Usage:
 *   SELECT * FROM v_inventory_status WHERE stock_status = 'LOW_STOCK';
 *   SELECT * FROM v_inventory_status WHERE category_name = 'Beverages';
 */
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

/*
 * VIEW: v_daily_revenue
 * 
 * Purpose: Financial dashboard showing daily sales metrics.
 * Only includes PAID bills for accurate revenue reporting.
 * 
 * Columns:
 *   - sale_date: The date
 *   - order_count: Number of paid orders
 *   - gross_sales: Subtotal before discounts/tax
 *   - total_discounts: Sum of all discounts applied
 *   - total_tax: Tax collected
 *   - net_revenue: Final revenue (after discounts, including tax)
 *   - avg_order_value: Average transaction size
 * 
 * Sorted: Most recent date first
 * 
 * Usage:
 *   SELECT * FROM v_daily_revenue WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
 *   SELECT SUM(net_revenue) FROM v_daily_revenue WHERE MONTH(sale_date) = MONTH(CURDATE());
 */
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

/*
 * PROCEDURE: sp_analyze_tables
 * 
 * Purpose: Updates table statistics for the query optimizer.
 * Run this periodically to ensure optimal query execution plans.
 * 
 * When to run:
 *   - After bulk data imports
 *   - After significant data changes (>10% of table size)
 *   - Weekly as general maintenance
 * 
 * Impact: Low - only reads data, doesn't lock tables
 * 
 * Usage: CALL sp_analyze_tables();
 */
DROP PROCEDURE IF EXISTS sp_analyze_tables//
CREATE PROCEDURE sp_analyze_tables()
BEGIN
    ANALYZE TABLE users, products, product_categories, customers, 
                  restaurant_tables, orders, kitchen_order_items, 
                  bills, order_items, reservations;
END//

/*
 * PROCEDURE: sp_optimize_tables
 * 
 * Purpose: Reclaims disk space and defragments tables.
 * Run this periodically for tables with frequent updates/deletes.
 * 
 * When to run:
 *   - After bulk deletions
 *   - Weekly/monthly as general maintenance
 *   - When table fragmentation is high
 * 
 * Impact: MEDIUM - may lock tables briefly during optimization
 * Best run during low-traffic periods (e.g., 3 AM)
 * 
 * Usage: CALL sp_optimize_tables();
 */
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
