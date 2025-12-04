# Restaurant Management System - MVC Architecture

![Java](https://img.shields.io/badge/Java-17+-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange)
![License](https://img.shields.io/badge/License-MIT-green)

A complete Restaurant Management System built with Java Swing following MVC (Model-View-Controller) architecture pattern, designed to work seamlessly with MySQL Workbench.

## 🌟 Features

### Core Functionality
- **User Authentication & Authorization**
  - Role-based access control (Admin, Manager, Employee)
  - Secure password handling
  - Session management

- **Product Management**
  - Add, update, delete menu items
  - Category-based product organization
  - Product availability tracking
  - Image upload support

- **Order Management**
  - Real-time order processing
  - Multi-item order support
  - Order history tracking
  - Category-wise product filtering

- **Billing System**
  - Automatic bill generation
  - Tax calculation
  - Multiple payment methods (Cash, Card, Digital Wallet, UPI)
  - Bill printing and export

- **Admin Dashboard**
  - Sales analytics
  - User management
  - Product inventory overview
  - Performance metrics

### Technical Features
- **MVC Architecture** - Clean separation of concerns
- **DAO Pattern** - Database abstraction layer
- **Connection Pooling** - Optimized database connections
- **Input Validation** - Client-side and server-side validation
- **Error Handling** - Comprehensive exception management
- **SQL Views & Procedures** - Optimized database queries

## 🏗️ Architecture

```
src/
├── com/
│   └── restaurant/
│       ├── RestaurantManagementApp.java  # Main entry point
│       ├── model/                         # Domain models
│       │   ├── User.java
│       │   ├── Product.java
│       │   ├── OrderItem.java
│       │   └── Bill.java
│       ├── view/                          # UI components
│       │   ├── LoginView.java
│       │   ├── SignupView.java
│       │   ├── OrderView.java
│       │   ├── ProductView.java
│       │   └── AdminView.java
│       ├── controller/                    # Business logic
│       │   ├── AuthenticationController.java
│       │   ├── ProductController.java
│       │   ├── OrderController.java
│       │   └── AdminController.java
│       ├── dao/                           # Data access layer
│       │   ├── UserDAO.java
│       │   ├── UserDAOImpl.java
│       │   ├── ProductDAO.java
│       │   ├── ProductDAOImpl.java
│       │   ├── BillDAO.java
│       │   └── BillDAOImpl.java
│       ├── config/                        # Configuration
│       │   └── DatabaseConfiguration.java
│       └── util/                          # Utilities
└── Assets/                                # Images and resources
```

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
  - Download from: https://www.oracle.com/java/technologies/downloads/
  - Verify installation: `java -version`

- **MySQL Server** 8.0 or higher
  - Download from: https://dev.mysql.com/downloads/mysql/
  - Verify installation: `mysql --version`

- **MySQL Workbench** (Recommended)
  - Download from: https://dev.mysql.com/downloads/workbench/

- **Apache NetBeans IDE** or **IntelliJ IDEA**
  - NetBeans: https://netbeans.apache.org/download/
  - IntelliJ IDEA: https://www.jetbrains.com/idea/download/

- **MySQL Connector/J** (JDBC Driver)
  - Download from: https://dev.mysql.com/downloads/connector/j/
  - Version: 8.0.33 or higher

### Installation Steps

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd Restaurant-management-system-project-in-Java-master
```

#### 2. Set Up MySQL Database

**Option A: Using MySQL Workbench (Recommended)**

1. Open MySQL Workbench
2. Connect to your MySQL server (default: localhost:3306)
3. Open the SQL script file: `restaurant_db_schema.sql`
4. Execute the script: Click on ⚡ (Execute) button
5. Verify database creation:
   ```sql
   USE restaurant_db;
   SHOW TABLES;
   ```

**Option B: Using MySQL Command Line**

```bash
# Login to MySQL
mysql -u root -p

# Execute the schema script
source /path/to/restaurant_db_schema.sql

# Verify
USE restaurant_db;
SHOW TABLES;
```

#### 3. Configure Database Connection

Open the file: `src/com/restaurant/config/DatabaseConfiguration.java`

Update the following constants if needed:

```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "restaurant_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password_here";
```

#### 4. Add MySQL Connector/J to Project

**For NetBeans:**

1. Right-click on the project → Properties
2. Select "Libraries" → "Compile" tab
3. Click "Add JAR/Folder"
4. Navigate to MySQL Connector/J JAR file (e.g., `mysql-connector-j-8.0.33.jar`)
5. Click "Open" → "OK"

**For IntelliJ IDEA:**

1. File → Project Structure → Modules
2. Click "+" → JARs or directories
3. Select MySQL Connector/J JAR file
4. Click "OK"

**For Maven Projects:**

Add to `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

#### 5. Build and Run

**Using NetBeans:**

1. Open the project in NetBeans
2. Clean and Build: Right-click project → Clean and Build
3. Run: Press F6 or Right-click project → Run

**Using IntelliJ IDEA:**

1. Open the project in IntelliJ IDEA
2. Build Project: Build → Build Project
3. Run: Right-click `RestaurantManagementApp.java` → Run

**Using Command Line:**

```bash
# Compile
javac -cp ".:mysql-connector-j-8.0.33.jar" -d bin src/com/restaurant/**/*.java

# Run
java -cp "bin:mysql-connector-j-8.0.33.jar" com.restaurant.RestaurantManagementApp
```

## 🔐 Default Login Credentials

### Administrator
- **Username:** `admin`
- **Password:** `admin`
- **Role:** Admin

### Sample Employees
- **Username:** `krishna` | **Password:** `Krishna@123`
- **Username:** `sita` | **Password:** `Sita@123`
- **Username:** `ramthapa` | **Password:** `Ram@1234`

## 📊 Database Schema

### Tables

1. **users** - User accounts and authentication
2. **products** - Menu items and product catalog
3. **bills** - Order invoices and payments
4. **order_items** - Individual items in each order

### Views

1. **v_daily_sales_summary** - Daily sales analytics
2. **v_product_sales_summary** - Product performance metrics
3. **v_user_performance** - Employee performance tracking

### Stored Procedures

1. **sp_get_sales_statistics()** - Overall sales statistics
2. **sp_get_top_selling_products(limit_count)** - Top selling items

## 🛠️ Troubleshooting

### Database Connection Issues

**Problem:** "Failed to connect to the database"

**Solutions:**
1. Verify MySQL server is running:
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl status mysql
   ```

2. Check database exists:
   ```sql
   SHOW DATABASES;
   ```

3. Verify credentials in `DatabaseConfiguration.java`

4. Ensure MySQL Connector/J is in classpath

### JDBC Driver Not Found

**Problem:** "MySQL JDBC Driver not found"

**Solution:**
- Download MySQL Connector/J from official website
- Add JAR to project libraries
- Verify classpath includes the driver

### Port Already in Use

**Problem:** MySQL default port 3306 is in use

**Solution:**
1. Change MySQL port in configuration
2. Update `DatabaseConfiguration.java` with new port
3. Restart MySQL server

### Build Errors

**Problem:** Compilation errors

**Solutions:**
1. Ensure JDK 17+ is being used
2. Clean and rebuild project
3. Verify all dependencies are added
4. Check for syntax errors in code

## 📝 Usage Guide

### For Employees

1. **Login**
   - Select "Employee" role
   - Enter username and password

2. **Take Orders**
   - Browse products by category
   - Add items to order
   - Specify quantities
   - View order summary

3. **Generate Bill**
   - Review order items
   - Enter cash received
   - Calculate change
   - Print bill

### For Administrators

1. **Login**
   - Select "Admin" role
   - Use admin credentials

2. **Manage Products**
   - Add new menu items
   - Update product details
   - Delete discontinued items
   - Set product availability

3. **View Analytics**
   - Total sales overview
   - Order statistics
   - Employee performance
   - Product sales reports

4. **Manage Users**
   - View all registered users
   - Monitor employee activity

## 🔄 Migration from Old Version

If you're upgrading from the previous non-MVC version:

1. **Backup your database:**
   ```bash
   mysqldump -u root -p rms > rms_backup.sql
   ```

2. **Data Migration:**
   ```sql
   -- Migrate users
   INSERT INTO restaurant_db.users (full_name, email_address, username, phone_number, password_hash, address)
   SELECT name, email, username, phone, password, address FROM rms.usertable;
   
   -- Migrate products
   INSERT INTO restaurant_db.products (product_name, category, price, description, image_path)
   SELECT product_name, category, price, description, image_path FROM rms.products;
   ```

3. Update application to use new MVC structure

## 🎨 Customization

### Changing Theme Colors

Edit view classes to customize color schemes:

```java
// Example in LoginView
private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
private static final Color SECONDARY_COLOR = new Color(255, 255, 255);
```

### Adding New Product Categories

1. Update `Product.ProductCategory` enum
2. Add category to database schema
3. Update UI category dropdowns

### Modifying Database Connection

Edit `DatabaseConfiguration.java` to change:
- Connection pool size
- Timeout settings
- SSL configuration
- Server timezone

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributors

- **Original Author:** @nischalsir
- **MVC Refactoring:** Restaurant Management System Team

## 📧 Support

For issues and questions:
- Create an issue on GitHub
- Email: work.nischalpandey@gmail.com

## 🔗 Links

- **GitHub Repository:** [Restaurant Management System](https://github.com/nischalsir/Restaurant-management-system-project-in-Java)
- **MySQL Documentation:** https://dev.mysql.com/doc/
- **Java Swing Tutorial:** https://docs.oracle.com/javase/tutorial/uiswing/

## 📈 Future Enhancements

- [ ] Online ordering integration
- [ ] Table reservation system
- [ ] Inventory management
- [ ] Multi-location support
- [ ] Mobile app integration
- [ ] Real-time reporting dashboard
- [ ] Email/SMS notifications
- [ ] Loyalty program integration

## 🙏 Acknowledgments

- Apache NetBeans Community
- MySQL Development Team
- Java Swing Community

---

**Made with ❤️ for Restaurant Management**
