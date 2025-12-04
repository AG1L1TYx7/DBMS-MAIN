#!/bin/bash

# Restaurant Management System - Database Setup Script
# This script sets up the complete database with all tables, views, procedures, and sample data

echo "========================================"
echo "Restaurant Management System"
echo "Database Setup Script"
echo "========================================"
echo ""

# Prompt for MySQL credentials
read -p "Enter MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -sp "Enter MySQL password: " MYSQL_PASSWORD
echo ""

# Database name
DB_NAME="restaurant_db"

echo ""
echo "Creating database: $DB_NAME"

# Create database
mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ Database created successfully"
else
    echo "✗ Failed to create database. Please check your credentials."
    exit 1
fi

# Execute main schema
echo ""
echo "Importing main schema (rms.sql)..."
mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$DB_NAME" < rms.sql 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ Main schema imported successfully"
else
    echo "✗ Failed to import main schema"
    exit 1
fi

# Execute inventory schema
if [ -f "database/inventory_schema.sql" ]; then
    echo ""
    echo "Importing inventory management schema..."
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$DB_NAME" < database/inventory_schema.sql 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "✓ Inventory schema imported successfully"
    else
        echo "✗ Failed to import inventory schema"
    fi
fi

# Execute table reservation schema
if [ -f "database/table_reservation_schema.sql" ]; then
    echo ""
    echo "Importing table & reservation schema..."
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$DB_NAME" < database/table_reservation_schema.sql 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "✓ Table & reservation schema imported successfully"
    else
        echo "✗ Failed to import table & reservation schema"
    fi
fi

# Execute customer management schema
if [ -f "database/customer_management_schema.sql" ]; then
    echo ""
    echo "Importing customer management schema..."
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$DB_NAME" < database/customer_management_schema.sql 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "✓ Customer management schema imported successfully"
    else
        echo "✗ Failed to import customer management schema"
    fi
fi

echo ""
echo "========================================"
echo "Database Setup Complete!"
echo "========================================"
echo ""
echo "Database Name: $DB_NAME"
echo "You can now run the application with these credentials."
echo ""
echo "To verify the setup, run:"
echo "mysql -u $MYSQL_USER -p $DB_NAME -e 'SHOW TABLES;'"
echo ""
