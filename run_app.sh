#!/bin/bash

# Restaurant Management System - Application Launcher
# This script sets up environment and runs the application

echo "============================================"
echo "Restaurant Management System"
echo "Application Launcher"
echo "============================================"
echo ""

# Set Java 17
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

echo "Starting application..."
echo ""

# Run the application
./gradlew run
