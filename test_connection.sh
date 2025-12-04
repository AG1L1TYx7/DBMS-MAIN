#!/bin/bash

# Restaurant Management System - Database Connection Test

echo "============================================"
echo "Restaurant Management System"
echo "Database Connection Test"
echo "============================================"
echo ""

# Test MySQL connection
mysql -u root -p << 'EOF'
-- Show current database status
SELECT 'DATABASE STATUS:' as info;
SHOW DATABASES LIKE 'restaurant_db';

-- Use the database
USE restaurant_db;

-- Show tables
SELECT 'TABLES IN DATABASE:' as info;
SELECT COUNT(*) as total_tables FROM information_schema.tables 
WHERE table_schema = 'restaurant_db' AND table_type = 'BASE TABLE';

-- Show views
SELECT 'VIEWS IN DATABASE:' as info;
SELECT COUNT(*) as total_views FROM information_schema.views 
WHERE table_schema = 'restaurant_db';

-- Show stored procedures
SELECT 'STORED PROCEDURES:' as info;
SELECT COUNT(*) as total_procedures FROM information_schema.routines 
WHERE routine_schema = 'restaurant_db' AND routine_type = 'PROCEDURE';

-- Show sample data counts
SELECT 'DATA SUMMARY:' as info;
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL SELECT 'Products', COUNT(*) FROM products
UNION ALL SELECT 'Customers', COUNT(*) FROM customers
UNION ALL SELECT 'Tables', COUNT(*) FROM restaurant_tables
UNION ALL SELECT 'Product Categories', COUNT(*) FROM product_categories;

-- Show foreign key relationships
SELECT 'FOREIGN KEY RELATIONSHIPS:' as info;
SELECT COUNT(DISTINCT CONSTRAINT_NAME) as total_foreign_keys
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'restaurant_db' 
AND REFERENCED_TABLE_NAME IS NOT NULL;

EOF

echo ""
echo "============================================"
echo "Connection test complete!"
echo "============================================"
echo ""
echo "To migrate the complete database, run:"
echo "  mysql -u root -p < restaurant_db_complete.sql"
echo ""
