package com.restaurant;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.view.LoginView;

import javax.swing.*;

/**
 * Restaurant Management System Main Application
 * Entry point for the MVC-based Restaurant Management System
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class RestaurantManagementApp {
    
    /**
     * Main method - Application entry point
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Set system look and feel
        setLookAndFeel();
        
        // Test database connection
        if (!testDatabaseConnection()) {
            showDatabaseError();
            return;
        }
        
        // Start the application
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
    
    /**
     * Set the application look and feel
     */
    private static void setLookAndFeel() {
        try {
            // Try to use Nimbus look and feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            // Fallback to system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection on startup
     * 
     * @return true if connection successful
     */
    private static boolean testDatabaseConnection() {
        try {
            DatabaseConfiguration dbConfig = DatabaseConfiguration.getInstance();
            boolean isConnected = dbConfig.testConnection();
            
            if (isConnected) {
                System.out.println("✓ Database connection successful!");
                System.out.println("✓ Connected to: " + DatabaseConfiguration.getDatabaseName());
                return true;
            } else {
                System.err.println("✗ Database connection failed!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("✗ Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Show database error dialog
     */
    private static void showDatabaseError() {
        String errorMessage = "Failed to connect to the database!\n\n" +
                             "Please ensure:\n" +
                             "1. MySQL server is running\n" +
                             "2. Database 'restaurant_db' exists\n" +
                             "3. MySQL Connector/J driver is in classpath\n" +
                             "4. Database credentials in DatabaseConfiguration are correct\n\n" +
                             "To set up the database:\n" +
                             "1. Open MySQL Workbench\n" +
                             "2. Run the 'restaurant_db_schema.sql' script\n" +
                             "3. Restart the application";
        
        JOptionPane.showMessageDialog(null,
            errorMessage,
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Get application information
     * 
     * @return application info string
     */
    public static String getApplicationInfo() {
        return "Restaurant Management System v2.0\n" +
               "MVC Architecture Implementation\n" +
               "© 2025 Restaurant Management System";
    }
}
