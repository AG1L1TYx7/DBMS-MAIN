# How to Run the Restaurant Management System

A quick guide to get the application up and running.

---

## Prerequisites

Before running the application, ensure you have:

- ✅ **Java 21** or higher installed
- ✅ **MySQL 8.0** or higher installed and running
- ✅ **Git** (optional, for cloning)

### Verify Java Installation
```bash
java -version
# Expected output: openjdk version "21.x.x" or similar
```

### Verify MySQL Installation
```bash
mysql --version
# Expected output: mysql Ver 8.x.x
```

---

## Step 1: Database Setup

### 1.1 Start MySQL Server

**macOS:**
```bash
brew services start mysql
# OR
mysql.server start
```

**Linux:**
```bash
sudo systemctl start mysql
```

**Windows:**
```bash
net start mysql
```

### 1.2 Create Database and Import Schema

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE restaurant_db;

# Exit MySQL
exit;

# Import the complete database (includes schema + sample data)
mysql -u root -p restaurant_db < restaurant_db_complete.sql
```

### 1.3 Update Database Credentials (if needed)

Edit `src/com/restaurant/config/DatabaseConfiguration.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";  // Change this
```

---

## Step 2: Build the Application

```bash
# Navigate to project directory
cd Restaurant-management-system-project-in-Java-master

# Build the project (skip tests for faster build)
./gradlew build -x test

# Expected output: BUILD SUCCESSFUL
```

---

## Step 3: Run the Application

```bash
# Run the application
./gradlew run
```

**Expected Output:**
```
> Task :run
Database connection established successfully!
✓ Database connection successful!
✓ Connected to: restaurant_db
```

The login window should appear.

---

## Step 4: Login

### Test Accounts

| Username | Password | Role | Access |
|----------|----------|------|--------|
| `admin` | `password123` | ADMIN | Full admin dashboard |
| `server1` | `password123` | SERVER | Order management |
| `chef1` | `password123` | CHEF | Kitchen display |

### Employee IDs (for Clock In/Out)

| Employee ID | Name | Role |
|-------------|------|------|
| `1001` | Sarah Server | SERVER |
| `1002` | Mike Waiter | SERVER |
| `1003` | Chef Marco | CHEF |
| `1004` | Chef Rita | CHEF |

---

## Quick Commands Reference

```bash
# Build only
./gradlew build -x test

# Run application
./gradlew run

# Clean and rebuild
./gradlew clean build -x test

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies
```

---

## Using the Application

### As Admin
1. Login with `admin` / `password123`
2. Access all tabs: Dashboard, Products, Inventory, Orders, Users, Time Clock, Schedules

### As Server
1. Login with `server1` / `password123`
2. Create and manage orders
3. Clock in/out from login screen using Employee ID `1001`

### As Chef
1. Login with `chef1` / `password123`
2. View kitchen orders
3. Update order status (PENDING → IN_PROGRESS → READY)
4. Clock in/out using Employee ID `1003`

### Clock In/Out (from Login Screen)
1. Click "⏰ Clock In/Out" button
2. Enter your 4-digit Employee ID
3. Click "Clock In" or "Clock Out"

### Make a Reservation (Guest)
1. On login screen, click "Make Reservation"
2. Fill in reservation details
3. Select available table and time

---

## Troubleshooting

### "Database connection failed"
- Ensure MySQL is running: `brew services start mysql`
- Check credentials in `DatabaseConfiguration.java`
- Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`

### "BUILD FAILED"
```bash
# Clean and rebuild
./gradlew clean build -x test --info
```

### "Invalid username or password"
- Use test credentials: `admin` / `password123`
- Or reset password in database:
```sql
UPDATE users SET password_hash = '$2a$12$lWAr.sVvfmaw8JudpcPOneCIl8wMXkybLpquQrJuaCZTheSgWA39e' 
WHERE username = 'admin';
```

### "Employee ID not found"
- Only SERVER and CHEF have employee IDs
- Check assigned IDs:
```sql
SELECT employee_id, username, role FROM users WHERE employee_id IS NOT NULL;
```

### Application won't start
```bash
# Check Java version
java -version

# Should be Java 21+
# If not, install Java 21 and set JAVA_HOME
export JAVA_HOME=/path/to/java21
```

---

## Project Files

| File | Purpose |
|------|---------|
| `build.gradle` | Build configuration |
| `gradlew` | Gradle wrapper (Unix) |
| `gradlew.bat` | Gradle wrapper (Windows) |
| `restaurant_db_complete.sql` | Full database with sample data |
| `restaurant_db_schema.sql` | Database schema only |

---

## Support

For issues, check:
1. README.md for full documentation
2. Database connection settings
3. Java version compatibility (requires Java 21+)

---

**Happy Restaurant Managing! 🍽️**
