# 🍽️ Restaurant Management System - MVC Edition

A modern, full-featured Restaurant Management System built with Java Swing and MySQL, following MVC architecture pattern.

![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## ✨ Features

### 🔐 Authentication & Authorization
- **Secure Login System** with role-based access control
- **User Registration** with real-time validation
- **Password Strength Indicator**
- **Multiple User Roles**: Admin, Manager, Employee

### 💰 Point of Sale (POS)
- **Intuitive Order Interface** with product grid view
- **Category-based Filtering**
- **Real-time Cart Management**
- **Multiple Payment Methods**: Cash, Card, Digital Wallet, UPI
- **Automatic Change Calculation**
- **Digital Receipt Generation**

### 📊 Admin Dashboard
- **Real-time Sales Analytics**
- **Today's Sales & Orders Tracking**
- **Total Revenue Overview**
- **Quick Action Buttons**

### 🍽️ Product Management
- **Add/Edit/Delete Products**
- **Category-based Organization** (9 categories)
- **Price Management**
- **Availability Toggle**
- **Product Search & Filter**

## 🚀 Quick Start

### Prerequisites

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/mysql/)
- **Gradle** (optional - wrapper included)

### Installation Steps

#### 1. Clone/Download the Project
```bash
cd restaurant-management-system-project-in-Java-master
```

#### 2. Set Up Database
```bash
mysql -u root -p < restaurant_db_schema.sql
```

#### 3. Configure Database (if needed)
Edit `src/com/restaurant/config/DatabaseConfiguration.java`:
```java
private static final String DB_PASSWORD = "your_password";
```

#### 4. Run with Gradle

**Build the project:**
```bash
./gradlew build
```

**Run the application:**
```bash
./gradlew run
```

**Create executable JAR:**
```bash
./gradlew fatJar
java -jar build/libs/restaurant-management-system-2.0.0-all.jar
```

## 🎯 Default Login

- **Username:** `admin`
- **Password:** `admin`

## 📋 Gradle Tasks

```bash
./gradlew build      # Build project
./gradlew run        # Run application
./gradlew fatJar     # Create fat JAR
./gradlew clean      # Clean build
./gradlew test       # Run tests
```

## 🏗️ Architecture

```
src/com/restaurant/
├── RestaurantManagementApp.java  # Entry point
├── model/          # Data models
├── view/           # UI components
├── controller/     # Business logic
├── dao/            # Database access
└── config/         # Configuration
```

## 🛠️ Technology Stack

- **Java 17** - Programming language
- **Swing** - GUI framework
- **MySQL 8.0** - Database
- **Gradle** - Build tool
- **JDBC** - Database connectivity
- **MVC Pattern** - Architecture

## 📖 Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Quick setup guide
- **[README_MVC.md](README_MVC.md)** - Architecture details
- **[CONVERSION_SUMMARY.md](CONVERSION_SUMMARY.md)** - Changes overview

## 🔧 Troubleshooting

**Database connection failed?**
- Ensure MySQL is running
- Check credentials in `DatabaseConfiguration.java`
- Verify `restaurant_db` exists

**Build errors?**
- Ensure Java 17+ installed: `java -version`
- Run: `./gradlew clean build`

## 🎨 UI Features

- Modern, clean interface
- Responsive layouts
- Color-coded components
- Real-time updates
- Professional design

---

**Version:** 2.0.0 | **Status:** ✅ Ready to Use | Made with ❤️
# JAVARESTROAPP
