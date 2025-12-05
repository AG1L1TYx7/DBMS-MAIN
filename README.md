# Restaurant Management System

A comprehensive Java-based restaurant management application with a modern GUI built using Java Swing. This system provides complete functionality for managing restaurant operations including orders, inventory, employees, reservations, and more.

---

## 🚀 Quick Start (5 Minutes Setup)

### Prerequisites
- Java 21+ installed
- MySQL 8.0+ running
- Git (to clone)

### Step 1: Clone & Navigate
```bash
git clone https://github.com/AG1L1TYx7/DBMS-MAIN.git
cd DBMS-MAIN
```

### Step 2: Configure Database Connection
Edit `src/resources/database.properties`:
```properties
db.host=localhost
db.port=3306
db.name=restaurant_db
db.username=root
db.password=          # Leave empty for XAMPP, or enter your MySQL password
```

### Step 3: Import Database
```bash
mysql -u root -p < restaurant_db_complete.sql
```

### Step 4: Run the Application
```bash
./gradlew run
```

### Step 5: Login
| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Admin Dashboard |
| `server1` | `server123` | Server View |
| `chef1` | `chef123` | Kitchen Display |

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Requirements](#system-requirements)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Architecture](#architecture)
- [API Reference](#api-reference)
- [Configuration](#configuration)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### User Management
- **Role-based access control**: ADMIN, SERVER, CHEF, CUSTOMER
- **Secure authentication** with BCrypt password hashing
- **Employee ID system**: Auto-generated 4-digit IDs for SERVER and CHEF roles
- **User registration and login**

### Order Management
- **Create, view, and manage orders**
- **Real-time order status tracking**: PENDING → IN_PROGRESS → READY → COMPLETED
- **Kitchen display system** for chefs
- **Order history and reporting**

### Inventory Management
- **Real-time stock tracking**
- **Low stock alerts** (when stock ≤ reorder level)
- **Out of stock notifications**
- **Stock adjustment with audit trail**
- **Auto-refresh every 30 seconds**

### Time Clock System
- **Employee clock in/out** using 4-digit Employee ID
- **Automatic hours calculation**
- **Admin dashboard for time tracking**
- **Filter by Today, This Week, This Month**

### Reservation System
- **Table reservation management**
- **Customer reservation interface**
- **Table availability tracking**

### Additional Features
- **Product/Menu management**
- **Bill generation and receipt printing**
- **Employee scheduling**
- **Leave request management**
- **Sales reporting and analytics**

---

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 LTS |
| GUI Framework | Java Swing |
| Build Tool | Gradle 8.x |
| Database | MySQL 8.0 |
| Password Hashing | BCrypt (jBCrypt 0.4) |
| Architecture | MVC (Model-View-Controller) |

---

## System Requirements

- **Java**: JDK 21 or higher
- **MySQL**: 8.0 or higher
- **Gradle**: 8.x (wrapper included)
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 512MB RAM
- **Disk Space**: 100MB for application

---

## Project Structure

```
Restaurant-management-system-project-in-Java-master/
├── src/
│   └── com/
│       └── restaurant/
│           ├── RestaurantManagementApp.java    # Main entry point
│           ├── config/
│           │   └── DatabaseConfiguration.java  # Database connection config
│           ├── controller/
│           │   ├── AuthenticationController.java
│           │   ├── CustomerController.java
│           │   ├── InventoryController.java
│           │   ├── OrderController.java
│           │   ├── ProductController.java
│           │   ├── ReservationController.java
│           │   ├── ScheduleController.java
│           │   └── UserController.java
│           ├── dao/
│           │   ├── BillDAO.java / BillDAOImpl.java
│           │   ├── ProductDAO.java / ProductDAOImpl.java
│           │   ├── ReservationDAO.java / ReservationDAOImpl.java
│           │   ├── TimeClockDAO.java / TimeClockDAOImpl.java
│           │   └── UserDAO.java / UserDAOImpl.java
│           ├── model/
│           │   ├── Bill.java
│           │   ├── Customer.java
│           │   ├── OrderItem.java
│           │   ├── Product.java
│           │   ├── Reservation.java
│           │   ├── RestaurantTable.java
│           │   ├── TimeClock.java
│           │   └── User.java
│           ├── util/
│           │   └── ReceiptPrinter.java
│           └── view/
│               ├── AdminView.java
│               ├── ChefKitchenView.java
│               ├── CustomerReservationView.java
│               ├── InventoryView.java
│               ├── LoginView.java
│               ├── OrderView.java
│               ├── SettingsView.java
│               └── SignupView.java
├── build.gradle                    # Gradle build configuration
├── settings.gradle                 # Gradle settings
├── gradle.properties               # Gradle properties
├── restaurant_db_schema.sql        # Database schema
├── restaurant_db_complete.sql      # Complete database with data
├── README.md                       # This file
└── use.md                          # Quick start guide
```

---

## Database Schema

### Core Tables

#### users
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(4) UNIQUE,          -- 4-digit ID for SERVER/CHEF
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,    -- BCrypt hashed
    full_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(255),
    role ENUM('ADMIN', 'SERVER', 'CHEF', 'CUSTOMER') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);
```

#### products
```sql
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL UNIQUE,
    category_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    image_path VARCHAR(500),
    stock_quantity INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    max_stock_level INT DEFAULT 100,
    unit VARCHAR(20) DEFAULT 'pcs',
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES product_categories(category_id)
);
```

#### orders
```sql
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) NOT NULL UNIQUE,
    table_id INT,
    order_type_id INT NOT NULL,
    order_status ENUM('PENDING', 'IN_PROGRESS', 'READY', 'COMPLETED', 'CANCELLED'),
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);
```

#### time_clock
```sql
CREATE TABLE time_clock (
    clock_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    employee_id VARCHAR(4) NOT NULL,
    clock_in_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    clock_out_time TIMESTAMP,
    total_hours DECIMAL(5,2),
    status ENUM('CLOCKED_IN', 'CLOCKED_OUT') DEFAULT 'CLOCKED_IN',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

#### reservations
```sql
CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    table_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(100),
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    party_size INT NOT NULL,
    special_requests TEXT,
    status ENUM('PENDING', 'CONFIRMED', 'SEATED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id)
);
```

### Additional Tables
- `product_categories` - Product categorization
- `order_items` - Items in each order
- `bills` - Payment records
- `restaurant_tables` - Table management
- `employee_schedules` - Work schedules
- `leave_requests` - Employee leave management
- `inventory_adjustments` - Stock change audit trail

---

## Architecture

### MVC Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                        VIEW LAYER                            │
│  LoginView | AdminView | OrderView | InventoryView | etc.   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                          │
│  AuthenticationController | OrderController | UserController │
│  InventoryController | ProductController | etc.              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DAO LAYER                              │
│  UserDAO | ProductDAO | BillDAO | TimeClockDAO | etc.       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MODEL LAYER                             │
│  User | Product | Order | Bill | TimeClock | Reservation    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE                                │
│                    MySQL 8.0                                 │
└─────────────────────────────────────────────────────────────┘
```

### User Roles & Permissions

| Role | Permissions |
|------|-------------|
| ADMIN | Full access - user management, inventory, reports, schedules, time clock viewing |
| SERVER | Order management, table service, clock in/out |
| CHEF | Kitchen display, order status updates, clock in/out |
| CUSTOMER | Make reservations, view menu |

---

## API Reference

### UserDAO

```java
// Create a new user
boolean createUser(User user) throws SQLException;

// Find user by username
Optional<User> findUserByUsername(String username) throws SQLException;

// Find user by employee ID
Optional<User> findUserByEmployeeId(String employeeId) throws SQLException;

// Authenticate user
Optional<User> authenticate(String username, String password) throws SQLException;

// Generate unique 4-digit employee ID
String generateEmployeeId() throws SQLException;
```

### TimeClockDAO

```java
// Clock in an employee
TimeClock clockIn(Integer userId, String employeeId) throws SQLException;

// Clock out an employee
boolean clockOut(Integer clockId) throws SQLException;

// Check if employee is clocked in
boolean isClockedIn(Integer userId) throws SQLException;

// Get today's clock records
List<TimeClock> getTodaysRecords() throws SQLException;

// Get currently clocked-in employees
List<TimeClock> getCurrentlyClockedIn() throws SQLException;
```

### InventoryController

```java
// Get all inventory items
List<Product> getAllInventoryItems();

// Get low stock products
List<Product> getLowStockProducts();

// Get out of stock products
List<Product> getOutOfStockProducts();

// Update stock quantity
boolean updateStock(Integer productId, Integer quantity, String type, String reason);

// Get inventory statistics
Map<String, Integer> getInventoryStatistics();
```

---

## Configuration

### Database Configuration (IMPORTANT - Read First!)

This project uses a **configuration file** for database settings. You **must** configure it before running.

#### Step 1: Edit the Database Properties File

Open `src/resources/database.properties` and update with YOUR MySQL credentials:

```properties
# Database Host (usually localhost)
db.host=localhost

# Database Port (default MySQL port is 3306)
db.port=3306

# Database Name (don't change unless you renamed the database)
db.name=restaurant_db

# MySQL Username (default is 'root')
db.username=root

# MySQL Password
# - Leave empty if using XAMPP/MAMP with no password: db.password=
# - Or enter your MySQL root password: db.password=yourpassword
db.password=
```

#### Step 2: Common Configurations

| Setup | db.username | db.password |
|-------|-------------|-------------|
| **XAMPP (Windows/Mac)** | `root` | (leave empty) |
| **MAMP (Mac)** | `root` | `root` |
| **MySQL Fresh Install** | `root` | (whatever you set during install) |
| **MySQL Workbench** | `root` | (your MySQL root password) |

#### Step 3: Load the Database

```bash
# Using MySQL command line
mysql -u root -p < restaurant_db_complete.sql

# Or import via MySQL Workbench:
# File → Open SQL Script → Select restaurant_db_complete.sql → Execute
```

### Gradle Configuration

`build.gradle`:
```groovy
plugins {
    id 'java'
    id 'application'
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
    implementation 'org.mindrot:jbcrypt:0.4'
}

application {
    mainClass = 'com.restaurant.RestaurantManagementApp'
}
```

---

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "UserDAOImplTest"

# Run with verbose output
./gradlew test --info
```

### Test Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| server1 | password123 | SERVER |
| chef1 | password123 | CHEF |

### Employee IDs (for Clock In/Out)
| Employee ID | Name | Role |
|-------------|------|------|
| 1001 | Sarah Server | SERVER |
| 1002 | Mike Waiter | SERVER |
| 1003 | Chef Marco | CHEF |
| 1004 | Chef Rita | CHEF |

---

## Troubleshooting

### Common Issues

#### Database Connection Failed
```
Error: Communications link failure
```
**Solution**: Ensure MySQL is running and credentials are correct in `DatabaseConfiguration.java`

#### Password Authentication Failed
```
Error: Invalid username or password
```
**Solution**: Ensure passwords are BCrypt hashed. Run:
```sql
UPDATE users SET password_hash = '$2a$12$...' WHERE user_id = X;
```

#### Employee ID Not Found (Clock In/Out)
```
Error: Employee ID not found
```
**Solution**: Only SERVER and CHEF roles have employee IDs. Check:
```sql
SELECT user_id, employee_id, username, role FROM users WHERE employee_id IS NOT NULL;
```

#### Java Version Mismatch
```
Error: UnsupportedClassVersionError
```
**Solution**: Ensure Java 21 is installed and JAVA_HOME is set correctly:
```bash
java -version
export JAVA_HOME=/path/to/java21
```

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public methods
- Keep methods focused and small

---

## License

This project is licensed under the MIT License.

---

## Authors

- Restaurant Management System Development Team

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.0 | Dec 2025 | Added time clock, reservations, improved inventory |
| 1.0 | Initial | Basic restaurant management features |


