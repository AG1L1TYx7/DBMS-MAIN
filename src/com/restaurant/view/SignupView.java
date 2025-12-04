package com.restaurant.view;

import com.restaurant.controller.AuthenticationController;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern Signup View with validation
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class SignupView extends JFrame {
    
    private final AuthenticationController authController;
    
    // UI Components
    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton backButton;
    private JLabel errorLabel;
    private JLabel passwordStrengthLabel;
    private JCheckBox showPasswordCheckbox;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public SignupView() {
        this.authController = new AuthenticationController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("Restaurant Management System - Sign Up");
        setSize(1000, 700);
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
        
        // Right Panel - Signup Form
        JPanel rightPanel = createSignupPanel();
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 700));
        panel.setBackground(ACCENT_COLOR);
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Logo Icon
        JLabel logoLabel = new JLabel("👥");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        logoLabel.setForeground(WHITE);
        panel.add(logoLabel, gbc);
        
        // Title
        gbc.gridy++;
        JLabel titleLabel = new JLabel("Join Our Team");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(WHITE);
        panel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Create Your Account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        panel.add(subtitleLabel, gbc);
        
        // Benefits
        gbc.gridy++;
        gbc.insets = new Insets(40, 20, 5, 20);
        String[] benefits = {
            "✓ Easy Access Control",
            "✓ Secure Authentication",
            "✓ Role-Based Access",
            "✓ Quick Registration"
        };
        
        JPanel benefitsPanel = new JPanel();
        benefitsPanel.setLayout(new BoxLayout(benefitsPanel, BoxLayout.Y_AXIS));
        benefitsPanel.setOpaque(false);
        
        for (String benefit : benefits) {
            JLabel benefitLabel = new JLabel(benefit);
            benefitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            benefitLabel.setForeground(new Color(255, 255, 255, 180));
            benefitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            benefitLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            benefitsPanel.add(benefitLabel);
        }
        
        panel.add(benefitsPanel, gbc);
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 40, 8, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(SECONDARY_COLOR);
        gbc.insets = new Insets(20, 40, 5, 40);
        panel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Please fill in the information below");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 40, 20, 40);
        panel.add(subtitleLabel, gbc);
        
        // Full Name
        gbc.gridy++;
        gbc.insets = new Insets(5, 40, 3, 40);
        panel.add(createLabel("Full Name *"), gbc);
        
        gbc.gridy++;
        fullNameField = createTextField();
        gbc.insets = new Insets(0, 40, 8, 40);
        panel.add(fullNameField, gbc);
        
        // Username
        gbc.gridy++;
        gbc.insets = new Insets(5, 40, 3, 40);
        panel.add(createLabel("Username * (5-10 alphanumeric)"), gbc);
        
        gbc.gridy++;
        usernameField = createTextField();
        gbc.insets = new Insets(0, 40, 8, 40);
        panel.add(usernameField, gbc);
        
        // Email
        gbc.gridy++;
        gbc.insets = new Insets(5, 40, 3, 40);
        panel.add(createLabel("Email Address *"), gbc);
        
        gbc.gridy++;
        emailField = createTextField();
        gbc.insets = new Insets(0, 40, 8, 40);
        panel.add(emailField, gbc);
        
        // Phone
        gbc.gridy++;
        gbc.insets = new Insets(5, 40, 3, 40);
        panel.add(createLabel("Phone Number * (10 digits)"), gbc);
        
        gbc.gridy++;
        phoneField = createTextField();
        gbc.insets = new Insets(0, 40, 8, 40);
        panel.add(phoneField, gbc);
        
        // Password
        gbc.gridy++;
        gbc.insets = new Insets(5, 40, 3, 40);
        panel.add(createLabel("Password *"), gbc);
        
        gbc.gridy++;
        passwordField = createPasswordField();
        gbc.insets = new Insets(0, 40, 3, 40);
        panel.add(passwordField, gbc);
        
        // Password Strength
        gbc.gridy++;
        passwordStrengthLabel = new JLabel("Min 8 chars, uppercase, lowercase, number, special char");
        passwordStrengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passwordStrengthLabel.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 40, 8, 40);
        panel.add(passwordStrengthLabel, gbc);
        
        // Confirm Password
        gbc.gridy++;
        gbc.insets = new Insets(5, 40, 3, 40);
        panel.add(createLabel("Confirm Password *"), gbc);
        
        gbc.gridy++;
        confirmPasswordField = createPasswordField();
        gbc.insets = new Insets(0, 40, 8, 40);
        panel.add(confirmPasswordField, gbc);
        
        // Show Password
        gbc.gridy++;
        showPasswordCheckbox = new JCheckBox("Show Passwords");
        showPasswordCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckbox.setBackground(WHITE);
        showPasswordCheckbox.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 40, 10, 40);
        panel.add(showPasswordCheckbox, gbc);
        
        // Error Label
        gbc.gridy++;
        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(5, 40, 8, 40);
        panel.add(errorLabel, gbc);
        
        // Buttons Panel
        gbc.gridy++;
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setBackground(WHITE);
        
        backButton = new JButton("BACK");
        backButton.setPreferredSize(new Dimension(170, 40));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setForeground(SECONDARY_COLOR);
        backButton.setBackground(new Color(236, 240, 241));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signupButton = new JButton("SIGN UP");
        signupButton.setPreferredSize(new Dimension(170, 40));
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signupButton.setForeground(WHITE);
        signupButton.setBackground(ACCENT_COLOR);
        signupButton.setBorderPainted(false);
        signupButton.setFocusPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonsPanel.add(backButton);
        buttonsPanel.add(signupButton);
        gbc.insets = new Insets(10, 40, 20, 40);
        panel.add(buttonsPanel, gbc);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(SECONDARY_COLOR);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(350, 38));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(350, 38));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }
    
    private void setupEventHandlers() {
        // Show/Hide Password
        showPasswordCheckbox.addActionListener(e -> {
            char echoChar = showPasswordCheckbox.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        
        // Password strength indicator
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updatePasswordStrength();
            }
        });
        
        // Signup Button
        signupButton.addActionListener(e -> handleSignup());
        
        // Back Button
        backButton.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
        
        // Hover effects
        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(39, 174, 96));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(ACCENT_COLOR);
            }
        });
        
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(220, 225, 227));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(236, 240, 241));
            }
        });
    }
    
    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("Min 8 chars, uppercase, lowercase, number, special char");
            passwordStrengthLabel.setForeground(Color.GRAY);
            return;
        }
        
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[@#$%^&+=].*")) strength++;
        
        switch (strength) {
            case 0:
            case 1:
                passwordStrengthLabel.setText("Weak Password");
                passwordStrengthLabel.setForeground(ERROR_COLOR);
                break;
            case 2:
            case 3:
                passwordStrengthLabel.setText("Medium Password");
                passwordStrengthLabel.setForeground(WARNING_COLOR);
                break;
            case 4:
                passwordStrengthLabel.setText("Strong Password");
                passwordStrengthLabel.setForeground(ACCENT_COLOR);
                break;
            case 5:
                passwordStrengthLabel.setText("Very Strong Password ✓");
                passwordStrengthLabel.setForeground(ACCENT_COLOR);
                break;
        }
    }
    
    private void handleSignup() {
        errorLabel.setText("");
        
        // Get input
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate all fields
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }
        
        if (!authController.validateUsername(username)) {
            showError("Username must be 5-10 alphanumeric characters");
            usernameField.requestFocus();
            return;
        }
        
        if (!authController.validateEmail(email)) {
            showError("Invalid email format");
            emailField.requestFocus();
            return;
        }
        
        if (!authController.validatePhoneNumber(phone)) {
            showError("Phone number must be 10 digits");
            phoneField.requestFocus();
            return;
        }
        
        if (!authController.validatePassword(password)) {
            showError("Password doesn't meet requirements");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return;
        }
        
        // Disable button during signup
        signupButton.setEnabled(false);
        signupButton.setText("CREATING ACCOUNT...");
        
        // Perform signup in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setUsername(username);
                newUser.setPasswordHash(password);
                newUser.setEmailAddress(email);
                newUser.setPhoneNumber(phone);
                newUser.setRole(UserRole.EMPLOYEE); // Default role
                
                return authController.registerUser(newUser);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(SignupView.this,
                            "Account created successfully!\nYou can now login with your credentials.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginView().setVisible(true);
                    }
                } catch (Exception ex) {
                    showError("Registration failed: " + ex.getMessage());
                } finally {
                    signupButton.setEnabled(true);
                    signupButton.setText("SIGN UP");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        Timer timer = new Timer(5000, e -> errorLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }
}
