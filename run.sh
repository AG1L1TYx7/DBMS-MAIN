#!/bin/bash

# Restaurant Management System - Quick Run Script

echo "🍽️  Restaurant Management System"
echo "================================"
echo ""

# Set Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Check if JAR exists
if [ ! -f "build/libs/restaurant-management-system-2.0.0-all.jar" ]; then
    echo "📦 Building project..."
    gradle fatJar --no-daemon
    echo ""
fi

echo "🚀 Starting Restaurant Management System..."
echo ""
echo "Default Login:"
echo "  Username: admin"
echo "  Password: admin"
echo ""
echo "Make sure MySQL is running and database is set up!"
echo "  Setup: mysql -u root -p < restaurant_db_schema.sql"
echo ""

java -jar build/libs/restaurant-management-system-2.0.0-all.jar
