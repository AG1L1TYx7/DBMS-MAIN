package com.restaurant.view;

import com.restaurant.controller.AuthenticationController;
import com.restaurant.dao.TimeClockDAO;
import com.restaurant.dao.TimeClockDAOImpl;
import com.restaurant.dao.UserDAO;
import com.restaurant.dao.UserDAOImpl;
import com.restaurant.model.TimeClock;
import com.restaurant.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Modern Login View with professional UI design
 * Features: Clean design, validation, error handling, responsive layout
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class LoginView extends JFrame {
    
    // Controllers
    private final AuthenticationController authController;
    
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel errorLabel;
    private JCheckBox showPasswordCheckbox;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public LoginView() {
        this.authController = new AuthenticationController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("Restaurant Management System - Login");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Left Panel - Branding
        JPanel leftPanel = createBrandingPanel();
        add(leftPanel, BorderLayout.WEST);
        
        // Right Panel - Login Form
        JPanel rightPanel = createLoginPanel();
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 650));
        panel.setBackground(PRIMARY_COLOR);
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Logo Icon
        JLabel logoLabel = new JLabel("🍽️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        logoLabel.setForeground(WHITE);
        panel.add(logoLabel, gbc);
        
        // App Title
        gbc.gridy++;
        JLabel titleLabel = new JLabel("Restaurant POS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(WHITE);
        panel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        panel.add(subtitleLabel, gbc);
        
        // Features
        gbc.gridy++;
        gbc.insets = new Insets(40, 20, 5, 20);
        String[] features = {
            "✓ Order Management",
            "✓ Inventory Tracking",
            "✓ Sales Analytics",
            "✓ User Management"
        };
        
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            featureLabel.setForeground(new Color(255, 255, 255, 180));
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featureLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            featuresPanel.add(featureLabel);
        }
        
        panel.add(featuresPanel, gbc);
        
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 40, 10, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(SECONDARY_COLOR);
        gbc.insets = new Insets(40, 40, 10, 40);
        panel.add(welcomeLabel, gbc);
        
        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Please login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 40, 30, 40);
        panel.add(subtitleLabel, gbc);
        
        // Username Label
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(SECONDARY_COLOR);
        gbc.insets = new Insets(10, 40, 5, 40);
        panel.add(usernameLabel, gbc);
        
        // Username Field
        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(350, 45));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        gbc.insets = new Insets(0, 40, 15, 40);
        panel.add(usernameField, gbc);
        
        // Password Label
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(SECONDARY_COLOR);
        gbc.insets = new Insets(10, 40, 5, 40);
        panel.add(passwordLabel, gbc);
        
        // Password Field
        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        gbc.insets = new Insets(0, 40, 10, 40);
        panel.add(passwordField, gbc);
        
        // Show Password Checkbox
        gbc.gridy++;
        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckbox.setBackground(WHITE);
        showPasswordCheckbox.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 40, 15, 40);
        panel.add(showPasswordCheckbox, gbc);
        
        // Error Label
        gbc.gridy++;
        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(5, 40, 10, 40);
        panel.add(errorLabel, gbc);
        
        // Login Button
        gbc.gridy++;
        loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(350, 45));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(WHITE);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(10, 40, 15, 40);
        panel.add(loginButton, gbc);
        
        // Signup Panel
        gbc.gridy++;
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setBackground(WHITE);
        
        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noAccountLabel.setForeground(Color.GRAY);
        
        signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signupButton.setForeground(PRIMARY_COLOR);
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setFocusPainted(false);
        
        signupPanel.add(noAccountLabel);
        signupPanel.add(signupButton);
        gbc.insets = new Insets(10, 40, 20, 40);
        panel.add(signupPanel, gbc);
        
        // Reservation Link for guests
        gbc.gridy++;
        JPanel reservationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        reservationPanel.setBackground(WHITE);
        
        JLabel reserveLabel = new JLabel("Want to reserve a table?");
        reserveLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reserveLabel.setForeground(Color.GRAY);
        
        JButton reservationButton = new JButton("Make Reservation");
        reservationButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        reservationButton.setForeground(ACCENT_COLOR);
        reservationButton.setBorderPainted(false);
        reservationButton.setContentAreaFilled(false);
        reservationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reservationButton.setFocusPainted(false);
        reservationButton.addActionListener(e -> {
            new CustomerReservationView().setVisible(true);
        });
        
        reservationPanel.add(reserveLabel);
        reservationPanel.add(reservationButton);
        gbc.insets = new Insets(5, 40, 10, 40);
        panel.add(reservationPanel, gbc);
        
        // Employee Clock In/Out Panel
        gbc.gridy++;
        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        clockPanel.setBackground(WHITE);
        
        JLabel clockLabel = new JLabel("Employee?");
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clockLabel.setForeground(Color.GRAY);
        
        JButton clockButton = new JButton("⏱️ Clock In/Out");
        clockButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clockButton.setForeground(new Color(155, 89, 182));  // Purple color
        clockButton.setBorderPainted(false);
        clockButton.setContentAreaFilled(false);
        clockButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clockButton.setFocusPainted(false);
        clockButton.addActionListener(e -> showClockInOutDialog());
        
        clockPanel.add(clockLabel);
        clockPanel.add(clockButton);
        gbc.insets = new Insets(5, 40, 10, 40);
        panel.add(clockPanel, gbc);
        
        // Version Label
        gbc.gridy++;
        JLabel versionLabel = new JLabel("Version 2.0 MVC");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(Color.LIGHT_GRAY);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(10, 40, 20, 40);
        panel.add(versionLabel, gbc);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Show/Hide Password
        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        
        // Login Button
        loginButton.addActionListener(e -> handleLogin());
        
        // Signup Button
        signupButton.addActionListener(e -> {
            dispose();
            new SignupView().setVisible(true);
        });
        
        // Enter key on password field
        passwordField.addActionListener(e -> handleLogin());
        
        // Hover effects
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(31, 97, 141));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(PRIMARY_COLOR);
            }
        });
    }
    
    private void handleLogin() {
        // Clear previous error
        errorLabel.setText("");
        
        // Get input
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validation
        if (username.isEmpty()) {
            showError("Please enter username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter password");
            passwordField.requestFocus();
            return;
        }
        
        // Disable button during login
        loginButton.setEnabled(false);
        loginButton.setText("LOGGING IN...");
        
        // Perform login in background thread
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return authController.authenticateUser(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        // Success - open appropriate view based on role
                        dispose();
                        switch (user.getRole()) {
                            case ADMIN:
                                new AdminView().setVisible(true);
                                break;
                            case SERVER:
                                new ServerView(user).setVisible(true);
                                break;
                            case CHEF:
                                new KitchenView(user).setVisible(true);
                                break;
                            case CUSTOMER:
                                // Customer view - show reservation view
                                new CustomerReservationView().setVisible(true);
                                break;
                        }
                    } else {
                        showError("Invalid username or password");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Login failed: " + ex.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        // Animate error label
        Timer timer = new Timer(3000, e -> errorLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Show Clock In/Out dialog for employees
     */
    private void showClockInOutDialog() {
        JDialog dialog = new JDialog(this, "Employee Clock In/Out", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(155, 89, 182));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("⏱️ Employee Time Clock");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        dialog.add(titlePanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Employee ID Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Employee ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);
        
        // Employee ID Field
        gbc.gridx = 1;
        JTextField employeeIdField = new JTextField(10);
        employeeIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(employeeIdField, gbc);
        
        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
        formPanel.add(statusLabel, gbc);
        
        // Buttons Panel
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton clockInBtn = new JButton("🟢 Clock In");
        clockInBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clockInBtn.setBackground(new Color(46, 204, 113));
        clockInBtn.setForeground(Color.WHITE);
        clockInBtn.setFocusPainted(false);
        clockInBtn.setPreferredSize(new Dimension(130, 40));
        
        JButton clockOutBtn = new JButton("🔴 Clock Out");
        clockOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clockOutBtn.setBackground(new Color(231, 76, 60));
        clockOutBtn.setForeground(Color.WHITE);
        clockOutBtn.setFocusPainted(false);
        clockOutBtn.setPreferredSize(new Dimension(130, 40));
        
        buttonPanel.add(clockInBtn);
        buttonPanel.add(clockOutBtn);
        formPanel.add(buttonPanel, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Check status when employee ID changes
        employeeIdField.addActionListener(e -> {
            String empId = employeeIdField.getText().trim();
            if (!empId.isEmpty()) {
                checkEmployeeStatus(empId, statusLabel);
            }
        });
        
        // Clock In Action
        clockInBtn.addActionListener(e -> {
            String empId = employeeIdField.getText().trim();
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter your Employee ID", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                UserDAO userDAO = new UserDAOImpl();
                TimeClockDAO timeClockDAO = new TimeClockDAOImpl();
                
                Optional<User> userOpt = userDAO.findUserByEmployeeId(empId);
                if (userOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Employee ID not found", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User user = userOpt.get();
                
                // Check if already clocked in
                if (timeClockDAO.isClockedIn(user.getUserId())) {
                    JOptionPane.showMessageDialog(dialog, "You are already clocked in!", 
                        "Already Clocked In", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Clock in
                TimeClock record = timeClockDAO.clockIn(user.getUserId(), empId);
                if (record != null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Clock In Successful!\n\n" +
                        "Employee: " + user.getFullName() + "\n" +
                        "ID: " + empId + "\n" +
                        "Time: " + record.getClockInTime().format(DateTimeFormatter.ofPattern("hh:mm a")),
                        "Clocked In", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Clock Out Action
        clockOutBtn.addActionListener(e -> {
            String empId = employeeIdField.getText().trim();
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter your Employee ID", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                UserDAO userDAO = new UserDAOImpl();
                TimeClockDAO timeClockDAO = new TimeClockDAOImpl();
                
                Optional<User> userOpt = userDAO.findUserByEmployeeId(empId);
                if (userOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Employee ID not found", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User user = userOpt.get();
                
                // Check if clocked in
                Optional<TimeClock> activeRecord = timeClockDAO.getActiveClockIn(user.getUserId());
                if (activeRecord.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "You are not clocked in!", 
                        "Not Clocked In", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Clock out
                TimeClock record = activeRecord.get();
                if (timeClockDAO.clockOut(record.getClockId())) {
                    record = timeClockDAO.findById(record.getClockId()).orElse(record);
                    JOptionPane.showMessageDialog(dialog, 
                        "Clock Out Successful!\n\n" +
                        "Employee: " + user.getFullName() + "\n" +
                        "ID: " + empId + "\n" +
                        "Duration: " + record.getFormattedDuration(),
                        "Clocked Out", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Check and display employee clock status
     */
    private void checkEmployeeStatus(String employeeId, JLabel statusLabel) {
        try {
            UserDAO userDAO = new UserDAOImpl();
            TimeClockDAO timeClockDAO = new TimeClockDAOImpl();
            
            Optional<User> userOpt = userDAO.findUserByEmployeeId(employeeId);
            if (userOpt.isEmpty()) {
                statusLabel.setText("Employee not found");
                statusLabel.setForeground(Color.RED);
                return;
            }
            
            User user = userOpt.get();
            boolean isClockedIn = timeClockDAO.isClockedIn(user.getUserId());
            
            if (isClockedIn) {
                Optional<TimeClock> active = timeClockDAO.getActiveClockIn(user.getUserId());
                if (active.isPresent()) {
                    statusLabel.setText("✓ " + user.getFullName() + " - Currently clocked in (" + 
                        active.get().getFormattedDuration() + ")");
                    statusLabel.setForeground(new Color(46, 204, 113));
                }
            } else {
                statusLabel.setText("○ " + user.getFullName() + " - Not clocked in");
                statusLabel.setForeground(Color.GRAY);
            }
        } catch (SQLException ex) {
            statusLabel.setText("Error checking status");
            statusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginView().setVisible(true);
        });
    }
}
