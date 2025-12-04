package com.restaurant.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Database Configuration Manager
 * Handles MySQL Workbench database connections with connection pooling support
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class DatabaseConfiguration {
    
    // Database connection parameters - Configure these for MySQL Workbench
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "restaurant_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "A9851040557@123a";
    
    // JDBC URL construction
    private static final String DB_URL = "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true".formatted(
            DB_HOST, DB_PORT, DB_NAME
    );
    
    // Connection pool settings
    private static final int MAX_POOL_SIZE = 10;
    private static final int INITIAL_POOL_SIZE = 3;
    
    private static DatabaseConfiguration instance;
    private Connection connection;
    
    /**
     * Private constructor for singleton pattern
     */
    private DatabaseConfiguration() {
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
}
