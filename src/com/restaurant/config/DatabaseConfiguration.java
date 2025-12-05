package com.restaurant.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Database Configuration Manager
 * Handles MySQL database connections with configuration from properties file
 * 
 * SETUP: Edit src/resources/database.properties with your MySQL credentials
 * 
 * @author Restaurant Management System
 * @version 2.1
 */
public class DatabaseConfiguration {
    
    // Configuration loaded from database.properties
    private static String DB_HOST;
    private static String DB_PORT;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DB_URL;
    
    // Connection pool settings
    private static final int MAX_POOL_SIZE = 10;
    private static final int INITIAL_POOL_SIZE = 3;
    
    private static DatabaseConfiguration instance;
    private Connection connection;
    private static boolean configLoaded = false;
    
    /**
     * Load database configuration from properties file
     */
    private static void loadConfiguration() {
        if (configLoaded) return;
        
        Properties props = new Properties();
        
        // Try multiple locations for the properties file
        String[] possiblePaths = {
            "src/resources/database.properties",
            "resources/database.properties",
            "database.properties",
            "../resources/database.properties"
        };
        
        boolean loaded = false;
        
        // First try loading from classpath
        try (InputStream is = DatabaseConfiguration.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
                loaded = true;
                System.out.println("Loaded database config from classpath");
            }
        } catch (IOException e) {
            // Continue to try file paths
        }
        
        // Try file paths if classpath loading failed
        if (!loaded) {
            for (String path : possiblePaths) {
                try (FileInputStream fis = new FileInputStream(path)) {
                    props.load(fis);
                    loaded = true;
                    System.out.println("Loaded database config from: " + path);
                    break;
                } catch (IOException e) {
                    // Try next path
                }
            }
        }
        
        // Use defaults if no config file found
        if (!loaded) {
            System.out.println("No database.properties found, using defaults");
            System.out.println("Create src/resources/database.properties to customize");
        }
        
        // Load values with defaults
        DB_HOST = props.getProperty("db.host", "localhost");
        DB_PORT = props.getProperty("db.port", "3306");
        DB_NAME = props.getProperty("db.name", "restaurant_db");
        DB_USER = props.getProperty("db.username", "root");
        DB_PASSWORD = props.getProperty("db.password", "");
        
        // Build JDBC URL
        String useSSL = props.getProperty("db.useSSL", "false");
        String serverTimezone = props.getProperty("db.serverTimezone", "UTC");
        String allowPublicKeyRetrieval = props.getProperty("db.allowPublicKeyRetrieval", "true");
        
        DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=%s&allowPublicKeyRetrieval=%s",
            DB_HOST, DB_PORT, DB_NAME, useSSL, serverTimezone, allowPublicKeyRetrieval
        );
        
        configLoaded = true;
        System.out.println("Database configured: " + DB_HOST + ":" + DB_PORT + "/" + DB_NAME);
    }
    
    /**
     * Private constructor for singleton pattern
     */
    private DatabaseConfiguration() {
        loadConfiguration();
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializeConnection();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database driver not found. Please add MySQL Connector/J to your project.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Get singleton instance of DatabaseConfiguration
     * 
     * @return DatabaseConfiguration instance
     */
    public static synchronized DatabaseConfiguration getInstance() {
        if (instance == null) {
            instance = new DatabaseConfiguration();
        }
        return instance;
    }
    
    /**
     * Initialize database connection
     */
    private void initializeConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            props.setProperty("password", DB_PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("autoReconnect", "true");
            props.setProperty("characterEncoding", "UTF-8");
            
            connection = DriverManager.getConnection(DB_URL, props);
            System.out.println("Database connection established successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection!");
            System.err.println("Please check your database.properties configuration:");
            System.err.println("  - Is MySQL running on " + DB_HOST + ":" + DB_PORT + "?");
            System.err.println("  - Does database '" + DB_NAME + "' exist?");
            System.err.println("  - Are username/password correct?");
            e.printStackTrace();
        }
    }
    
    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
        return connection;
    }
    
    /**
     * Create a new connection (for multi-threading scenarios)
     * 
     * @return new Connection object
     * @throws SQLException if connection fails
     */
    public Connection createNewConnection() throws SQLException {
        loadConfiguration(); // Ensure config is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Get a static connection (for controllers that don't use singleton)
     * 
     * @return new Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getStaticConnection() throws SQLException {
        loadConfiguration(); // Ensure config is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection testConn = createNewConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get database connection URL
     * 
     * @return database URL
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Get database name
     * 
     * @return database name
     */
    public static String getDatabaseName() {
        return DB_NAME;
    }

    // ==================== Health Check Methods ====================

    /**
     * Performs a comprehensive health check on the database connection.
     * Checks connectivity, validates the connection, and measures response time.
     *
     * @return DatabaseHealthStatus containing health information
     */
    public DatabaseHealthStatus performHealthCheck() {
        DatabaseHealthStatus status = new DatabaseHealthStatus();
        long startTime = System.currentTimeMillis();

        try (Connection healthConn = createNewConnection()) {
            status.setConnected(healthConn != null && healthConn.isValid(5));
            
            // Execute a simple query to test database responsiveness
            if (status.isConnected()) {
                try (var stmt = healthConn.createStatement();
                     var rs = stmt.executeQuery("SELECT 1")) {
                    status.setQueryExecutable(rs.next());
                }
            }

            // Get database metadata
            if (status.isConnected()) {
                var metaData = healthConn.getMetaData();
                status.setDatabaseProductName(metaData.getDatabaseProductName());
                status.setDatabaseProductVersion(metaData.getDatabaseProductVersion());
                status.setDriverName(metaData.getDriverName());
                status.setDriverVersion(metaData.getDriverVersion());
            }

        } catch (SQLException e) {
            status.setConnected(false);
            status.setErrorMessage(e.getMessage());
        }

        status.setResponseTimeMs(System.currentTimeMillis() - startTime);
        status.setCheckTimestamp(java.time.LocalDateTime.now());
        return status;
    }

    /**
     * Checks if the database is currently available.
     *
     * @return true if database is available and responding, false otherwise
     */
    public boolean isDatabaseAvailable() {
        try (Connection testConn = createNewConnection()) {
            return testConn != null && testConn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Gets the current connection status information.
     *
     * @return a string describing the connection status
     */
    public String getConnectionStatus() {
        try {
            if (connection == null) {
                return "No connection established";
            }
            if (connection.isClosed()) {
                return "Connection is closed";
            }
            if (connection.isValid(2)) {
                return "Connection is active and valid";
            }
            return "Connection exists but may be invalid";
        } catch (SQLException e) {
            return "Error checking connection status: " + e.getMessage();
        }
    }

    /**
     * Attempts to reconnect to the database.
     *
     * @return true if reconnection was successful, false otherwise
     */
    public boolean reconnect() {
        try {
            closeConnection();
            initializeConnection();
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            System.err.println("Reconnection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Executes a query with automatic retry on failure.
     *
     * @param maxRetries the maximum number of retry attempts
     * @param operation the database operation to execute
     * @param <T> the return type of the operation
     * @return the result of the operation
     * @throws SQLException if all retry attempts fail
     */
    public <T> T executeWithRetry(int maxRetries, DatabaseOperation<T> operation) throws SQLException {
        int attempts = 0;
        SQLException lastException = null;

        while (attempts < maxRetries) {
            try {
                return operation.execute(getConnection());
            } catch (SQLException e) {
                lastException = e;
                attempts++;
                System.err.println("Database operation failed (attempt " + attempts + "/" + maxRetries + "): " + e.getMessage());
                
                if (attempts < maxRetries) {
                    // Try to reconnect before next attempt
                    reconnect();
                    try {
                        Thread.sleep(1000L * attempts); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Operation interrupted", ie);
                    }
                }
            }
        }

        throw new SQLException("Operation failed after " + maxRetries + " attempts", lastException);
    }

    // ==================== Statistics Methods ====================

    /**
     * Gets basic database statistics.
     *
     * @return DatabaseStatistics containing database metrics
     */
    public DatabaseStatistics getDatabaseStatistics() {
        DatabaseStatistics stats = new DatabaseStatistics();
        
        try (Connection statsConn = createNewConnection();
             var stmt = statsConn.createStatement()) {

            // Get table count
            try (var rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '" + DB_NAME + "'")) {
                if (rs.next()) {
                    stats.setTableCount(rs.getInt(1));
                }
            }

            // Get total row count estimate
            try (var rs = stmt.executeQuery(
                    "SELECT SUM(table_rows) FROM information_schema.tables WHERE table_schema = '" + DB_NAME + "'")) {
                if (rs.next()) {
                    stats.setTotalRowCount(rs.getLong(1));
                }
            }

            // Get database size
            try (var rs = stmt.executeQuery(
                    "SELECT SUM(data_length + index_length) / 1024 / 1024 AS size_mb " +
                    "FROM information_schema.tables WHERE table_schema = '" + DB_NAME + "'")) {
                if (rs.next()) {
                    stats.setDatabaseSizeMB(rs.getDouble(1));
                }
            }

            stats.setCollectionTimestamp(java.time.LocalDateTime.now());

        } catch (SQLException e) {
            System.err.println("Failed to collect database statistics: " + e.getMessage());
        }

        return stats;
    }

    // ==================== Inner Classes ====================

    /**
     * Functional interface for database operations with retry support.
     *
     * @param <T> the return type of the operation
     */
    @FunctionalInterface
    public interface DatabaseOperation<T> {
        /**
         * Executes the database operation.
         *
         * @param connection the database connection to use
         * @return the result of the operation
         * @throws SQLException if the operation fails
         */
        T execute(Connection connection) throws SQLException;
    }

    /**
     * Health status information for database connection.
     */
    public static class DatabaseHealthStatus {
        private boolean connected;
        private boolean queryExecutable;
        private String databaseProductName;
        private String databaseProductVersion;
        private String driverName;
        private String driverVersion;
        private String errorMessage;
        private long responseTimeMs;
        private java.time.LocalDateTime checkTimestamp;

        // Getters and Setters
        public boolean isConnected() { return connected; }
        public void setConnected(boolean connected) { this.connected = connected; }
        
        public boolean isQueryExecutable() { return queryExecutable; }
        public void setQueryExecutable(boolean queryExecutable) { this.queryExecutable = queryExecutable; }
        
        public String getDatabaseProductName() { return databaseProductName; }
        public void setDatabaseProductName(String databaseProductName) { this.databaseProductName = databaseProductName; }
        
        public String getDatabaseProductVersion() { return databaseProductVersion; }
        public void setDatabaseProductVersion(String databaseProductVersion) { this.databaseProductVersion = databaseProductVersion; }
        
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        
        public String getDriverVersion() { return driverVersion; }
        public void setDriverVersion(String driverVersion) { this.driverVersion = driverVersion; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public long getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
        
        public java.time.LocalDateTime getCheckTimestamp() { return checkTimestamp; }
        public void setCheckTimestamp(java.time.LocalDateTime checkTimestamp) { this.checkTimestamp = checkTimestamp; }

        /**
         * Checks if the database is healthy (connected and queries executable).
         *
         * @return true if healthy, false otherwise
         */
        public boolean isHealthy() {
            return connected && queryExecutable;
        }

        @Override
        public String toString() {
            return String.format(
                "DatabaseHealthStatus{connected=%s, queryExecutable=%s, " +
                "product=%s %s, driver=%s %s, responseTime=%dms, timestamp=%s%s}",
                connected, queryExecutable, databaseProductName, databaseProductVersion,
                driverName, driverVersion, responseTimeMs, checkTimestamp,
                errorMessage != null ? ", error=" + errorMessage : ""
            );
        }
    }

    /**
     * Statistics information about the database.
     */
    public static class DatabaseStatistics {
        private int tableCount;
        private long totalRowCount;
        private double databaseSizeMB;
        private java.time.LocalDateTime collectionTimestamp;

        // Getters and Setters
        public int getTableCount() { return tableCount; }
        public void setTableCount(int tableCount) { this.tableCount = tableCount; }
        
        public long getTotalRowCount() { return totalRowCount; }
        public void setTotalRowCount(long totalRowCount) { this.totalRowCount = totalRowCount; }
        
        public double getDatabaseSizeMB() { return databaseSizeMB; }
        public void setDatabaseSizeMB(double databaseSizeMB) { this.databaseSizeMB = databaseSizeMB; }
        
        public java.time.LocalDateTime getCollectionTimestamp() { return collectionTimestamp; }
        public void setCollectionTimestamp(java.time.LocalDateTime collectionTimestamp) { 
            this.collectionTimestamp = collectionTimestamp; 
        }

        @Override
        public String toString() {
            return String.format(
                "DatabaseStatistics{tables=%d, rows=%d, size=%.2fMB, collected=%s}",
                tableCount, totalRowCount, databaseSizeMB, collectionTimestamp
            );
        }
    }
}
