# 🚀 Quick Start Guide - Restaurant Management System (MVC)

This guide will help you get the MVC-based Restaurant Management System up and running in minutes.

## ⚡ Quick Setup (5 Minutes)

### Step 1: Database Setup (2 minutes)

1. **Open MySQL Workbench**
   - Launch MySQL Workbench on your computer
   - Connect to your local MySQL server (usually localhost:3306)

2. **Execute Database Script**
   - Click: File → Open SQL Script
   - Navigate to: `restaurant_db_schema.sql`
   - Click the ⚡ Lightning bolt icon to execute
   - Wait for "Database Setup Complete!" message

3. **Verify Installation**
   ```sql
   USE restaurant_db;
   SHOW TABLES;
   -- You should see: users, products, bills, order_items
   ```

### Step 2: Configure Database Connection (1 minute)

1. Open: `src/com/restaurant/config/DatabaseConfiguration.java`

2. Update if needed (default values work for most setups):
   ```java
   private static final String DB_PASSWORD = "";  // Change if you set a MySQL password
   ```

### Step 3: Add MySQL Driver (1 minute)

**NetBeans:**
- Right-click project → Properties → Libraries → Add JAR/Folder
- Select: `mysql-connector-j-8.0.33.jar`

**IntelliJ IDEA:**
- File → Project Structure → Libraries → + → Java
- Select: `mysql-connector-j-8.0.33.jar`

### Step 4: Run the Application (1 minute)

1. **Find the main class:**
   - Location: `src/com/restaurant/RestaurantManagementApp.java`

2. **Run:**
   - **NetBeans:** Right-click file → Run File (Shift+F6)
   - **IntelliJ:** Right-click file → Run 'RestaurantManagementApp.main()'

3. **Login:**
   - Username: `admin`
   - Password: `admin`
   - Role: Admin

## 🎯 What's New in MVC Version?

### Architecture Improvements
✅ **MVC Pattern** - Clean separation of Model, View, Controller
✅ **DAO Layer** - Abstracted database operations
✅ **Better Organization** - Logical package structure
✅ **Enhanced Database** - Improved schema with constraints

### New Features
✅ **Role-Based Access** - Admin, Manager, Employee roles
✅ **Better Validation** - Client and server-side validation
✅ **Tax Calculation** - Automatic tax computation
✅ **Payment Methods** - Cash, Card, UPI, Digital Wallet
✅ **Sales Analytics** - Comprehensive reporting
✅ **Audit Trail** - Track user actions

### Database Enhancements
✅ **Normalized Schema** - Better data integrity
✅ **Foreign Keys** - Referential integrity
✅ **Indexes** - Faster queries
✅ **Views** - Pre-built reports
✅ **Stored Procedures** - Optimized operations
✅ **Triggers** - Automatic validations

## 📁 Project Structure

```
Restaurant-management-system/
│
├── src/
│   ├── com/restaurant/              ✨ NEW MVC STRUCTURE
│   │   ├── RestaurantManagementApp.java
│   │   ├── model/                   # Data models
│   │   ├── view/                    # UI components
│   │   ├── controller/              # Business logic
│   │   ├── dao/                     # Database access
│   │   ├── config/                  # Configuration
│   │   └── util/                    # Utilities
│   │
│   ├── cafe/management/system/      📦 OLD STRUCTURE (Compatible)
│   │   ├── Login.java
│   │   ├── Signup.java
│   │   ├── Order.java
│   │   └── ...
│   │
│   └── Assets/                      # Images and resources
│
├── restaurant_db_schema.sql         ✨ NEW DATABASE SCHEMA
├── rms.sql                          📦 OLD DATABASE SCHEMA
├── README_MVC.md                    ✨ NEW README
├── README.md                        📦 OLD README
└── QUICKSTART.md                    ✨ THIS FILE
```

## 🔄 Migration from Old Version

Already using the old version? Here's how to migrate:

### Option 1: Start Fresh (Recommended)

1. Backup your data:
   ```bash
   mysqldump -u root -p rms > backup.sql
   ```

2. Run new schema:
   ```sql
   -- In MySQL Workbench
   source restaurant_db_schema.sql
   ```

3. Use the new MVC version

### Option 2: Keep Both Versions

Both versions can coexist! The old version uses `rms` database, new uses `restaurant_db`.

**Run Old Version:**
```java
// Main class: cafe.management.system.CafeManagementSystem
```

**Run New Version:**
```java
// Main class: com.restaurant.RestaurantManagementApp
```

## 🎓 Learning the MVC Structure

### Model (Data Layer)
```
model/User.java          - User entity
model/Product.java       - Product entity
model/Bill.java          - Bill entity
model/OrderItem.java     - Order item entity
```

### View (Presentation Layer)
```
view/LoginView.java      - Login screen
view/SignupView.java     - Registration screen
view/OrderView.java      - Order management
view/AdminView.java      - Admin dashboard
```

### Controller (Business Logic)
```
controller/AuthenticationController.java  - Login/Signup logic
controller/ProductController.java         - Product operations
controller/OrderController.java           - Order processing
```

### DAO (Data Access)
```
dao/UserDAO.java         - User database interface
dao/UserDAOImpl.java     - User database implementation
dao/ProductDAO.java      - Product database interface
dao/BillDAO.java         - Bill database interface
```

## 🛠️ Configuration Guide

### Database Settings

Edit: `src/com/restaurant/config/DatabaseConfiguration.java`

```java
// Change these if your MySQL setup is different:
private static final String DB_HOST = "localhost";  // MySQL host
private static final String DB_PORT = "3306";       // MySQL port
private static final String DB_NAME = "restaurant_db";  // Database name
private static final String DB_USER = "root";       // MySQL user
private static final String DB_PASSWORD = "";       // MySQL password
```

### Connection Pool

```java
// Adjust for performance:
private static final int MAX_POOL_SIZE = 10;
private static final int INITIAL_POOL_SIZE = 3;
```

## 🐛 Common Issues & Solutions

### Issue 1: "Database connection failed"

**Solution:**
```bash
# Check MySQL is running
# Windows:
net start MySQL80

# Mac/Linux:
sudo systemctl status mysql

# Then verify in MySQL Workbench:
USE restaurant_db;
```

### Issue 2: "JDBC Driver not found"

**Solution:**
- Download: https://dev.mysql.com/downloads/connector/j/
- Add to project libraries (see Step 3 above)

### Issue 3: "Package com.restaurant does not exist"

**Solution:**
- Ensure you're in the correct directory
- Rebuild project: Build → Clean and Build

### Issue 4: "Port 3306 already in use"

**Solution:**
```java
// Change port in DatabaseConfiguration.java
private static final String DB_PORT = "3307";  // Or any available port
```

### Issue 5: "Access denied for user 'root'"

**Solution:**
```java
// Update credentials in DatabaseConfiguration.java
private static final String DB_USER = "your_mysql_user";
private static final String DB_PASSWORD = "your_mysql_password";
```

## 📊 Sample Data

The database comes with sample data:

### Users
- Admin: `admin` / `admin`
- Employee 1: `krishna` / `Krishna@123`
- Employee 2: `sita` / `Sita@123`

### Products
- 4 Burgers (₹280 - ₹420)
- 4 Rice Meals (₹280 - ₹550)
- 5 Beverages (₹50 - ₹180)
- 3 Fries (₹150 - ₹220)
- 4 Desserts (₹100 - ₹180)
- 4 Milkshakes (₹200 - ₹250)
- 3 Soft Cocktails (₹180 - ₹220)
- 2 Chicken Rolls (₹180 - ₹200)

## 🎯 Next Steps

1. **Explore the Admin Dashboard**
   - Login as admin
   - View sales statistics
   - Manage products
   - View user list

2. **Try Employee Features**
   - Login as `krishna`
   - Create an order
   - Add multiple items
   - Generate bill

3. **Customize the System**
   - Add your own products
   - Change color themes
   - Add new categories
   - Modify validation rules

4. **Read Full Documentation**
   - See: `README_MVC.md`
   - API documentation
   - Database schema details

## 📞 Need Help?

- **GitHub Issues:** Create an issue on the repository
- **Email:** work.nischalpandey@gmail.com
- **Documentation:** See README_MVC.md

## ✅ Checklist

Before running, ensure:

- [ ] MySQL Server is running
- [ ] MySQL Workbench is installed
- [ ] Database schema is executed
- [ ] MySQL Connector/J is added to project
- [ ] Database credentials are configured
- [ ] JDK 17+ is installed
- [ ] IDE (NetBeans/IntelliJ) is set up

## 🎉 You're Ready!

Your Restaurant Management System is now set up and ready to use!

**Run the application:**
```
Main Class: com.restaurant.RestaurantManagementApp
```

**First login:**
- Username: `admin`
- Password: `admin`

Enjoy managing your restaurant! 🍔🍕🍰

---

**Version:** 2.0 MVC  
**Last Updated:** 2025
