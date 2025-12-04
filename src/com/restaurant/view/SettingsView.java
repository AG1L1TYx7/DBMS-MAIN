package com.restaurant.view;

import com.restaurant.config.DatabaseConfiguration;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

/**
 * Settings View
 * Application settings and preferences
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class SettingsView extends JFrame {
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    // UI Components
    private JTextField restaurantNameField, addressField, phoneField, emailField;
    private JSpinner taxRateSpinner;
    private JComboBox<String> themeCombo;
    
    public SettingsView() {
        initializeComponents();
        setupLayout();
        loadSettings();
    }
    
    private void initializeComponents() {
        setTitle("Restaurant Management - Settings");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(WHITE);
        
        tabbedPane.addTab("🏪 Restaurant", createRestaurantPanel());
        tabbedPane.addTab("⚙️ Application", createApplicationPanel());
        tabbedPane.addTab("💾 Database", createDatabasePanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer Buttons
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("⚙️ Settings & Preferences");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(WHITE);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createRestaurantPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(PRIMARY_COLOR, 2), 
                "Restaurant Information", 
                TitledBorder.LEFT, 
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        // Restaurant Name
        formPanel.add(createLabel("Restaurant Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        restaurantNameField = createTextField("Delicious Restaurant");
        formPanel.add(restaurantNameField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        addressField = createTextField("123 Main Street, Kathmandu, Nepal");
        formPanel.add(addressField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phoneField = createTextField("+977 1-4567890");
        formPanel.add(phoneField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = createTextField("info@delicious.com.np");
        formPanel.add(emailField, gbc);
        
        // Tax Rate
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Tax Rate (%):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        taxRateSpinner = new JSpinner(new SpinnerNumberModel(13.0, 0.0, 100.0, 0.5));
        taxRateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) taxRateSpinner.getEditor();
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(taxRateSpinner, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(PRIMARY_COLOR, 2), 
                "Application Settings", 
                TitledBorder.LEFT, 
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        // Theme
        formPanel.add(createLabel("Theme:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        themeCombo = new JComboBox<>(new String[]{"Modern Light", "Dark Mode", "Classic"});
        themeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(themeCombo, gbc);
        
        // Language
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Language:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JComboBox<String> langCombo = new JComboBox<>(new String[]{"English", "Nepali"});
        langCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(langCombo, gbc);
        
        // Auto Backup
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JCheckBox autoBackupCheck = new JCheckBox("Enable automatic daily backup");
        autoBackupCheck.setFont(new Font("Segoe UI", Font.BOLD, 14));
        autoBackupCheck.setBackground(WHITE);
        formPanel.add(autoBackupCheck, gbc);
        
        // Receipt Printing
        gbc.gridy++;
        JCheckBox autoPrintCheck = new JCheckBox("Auto-print receipts after order");
        autoPrintCheck.setFont(new Font("Segoe UI", Font.BOLD, 14));
        autoPrintCheck.setBackground(WHITE);
        formPanel.add(autoPrintCheck, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(WHITE);
        infoPanel.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(PRIMARY_COLOR, 2), 
                "Database Information", 
                TitledBorder.LEFT, 
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        infoPanel.add(createLabel("Database:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel dbNameLabel = new JLabel(DatabaseConfiguration.getDatabaseName());
        dbNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(dbNameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        infoPanel.add(createLabel("Server:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel serverLabel = new JLabel("localhost:3306");
        serverLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(serverLabel, gbc);
        
        // Actions Panel
        JPanel actionsPanel = new JPanel(new GridBagLayout());
        actionsPanel.setBackground(WHITE);
        actionsPanel.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(ACCENT_COLOR, 2), 
                "Database Actions", 
                TitledBorder.LEFT, 
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                ACCENT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        JButton backupBtn = createActionButton("💾 Backup Database", ACCENT_COLOR);
        backupBtn.addActionListener(e -> backupDatabase());
        actionsPanel.add(backupBtn, gbc);
        
        gbc.gridy++;
        JButton restoreBtn = createActionButton("📥 Restore Database", PRIMARY_COLOR);
        restoreBtn.addActionListener(e -> restoreDatabase());
        actionsPanel.add(restoreBtn, gbc);
        
        gbc.gridy++;
        JButton testBtn = createActionButton("🔍 Test Connection", ACCENT_COLOR);
        testBtn.addActionListener(e -> testConnection());
        actionsPanel.add(testBtn, gbc);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(actionsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(BACKGROUND);
        
        JButton saveBtn = createActionButton("💾 Save Settings", ACCENT_COLOR);
        saveBtn.setPreferredSize(new Dimension(180, 45));
        saveBtn.addActionListener(e -> saveSettings());
        
        JButton cancelBtn = createActionButton("❌ Cancel", Color.GRAY);
        cancelBtn.setPreferredSize(new Dimension(180, 45));
        cancelBtn.addActionListener(e -> dispose());
        
        panel.add(saveBtn);
        panel.add(cancelBtn);
        
        return panel;
    }
    
    private JTextField createTextField(String initialValue) {
        JTextField field = new JTextField(initialValue);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 45));
        return button;
    }
    
    private void loadSettings() {
        // Load saved settings from preferences or database
        // This is a placeholder - implement actual loading logic
    }
    
    private void saveSettings() {
        String restaurantName = restaurantNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        double taxRate = (Double) taxRateSpinner.getValue();
        String theme = (String) themeCombo.getSelectedItem();
        
        // Save to preferences or configuration file
        // This is a placeholder - implement actual saving logic
        
        JOptionPane.showMessageDialog(this,
            "Settings saved successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void backupDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup Location");
        fileChooser.setSelectedFile(new File("restaurant_backup_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Execute mysqldump command
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        String command = "mysqldump -u root %s > %s".formatted(
                                DatabaseConfiguration.getDatabaseName(),
                                file.getAbsolutePath()
                        );
                        
                        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
                        process.waitFor();
                        
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(SettingsView.this,
                                "Database backup created successfully!\n" + file.getAbsolutePath(),
                                "Backup Complete", JOptionPane.INFORMATION_MESSAGE)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(SettingsView.this,
                                "Backup failed: " + e.getMessage(),
                                "Backup Error", JOptionPane.ERROR_MESSAGE)
                        );
                    }
                    return null;
                }
            };
            worker.execute();
        }
    }
    
    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File to Restore");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "This will overwrite the current database!\nAre you sure you want to continue?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            String command = "mysql -u root %s < %s".formatted(
                                    DatabaseConfiguration.getDatabaseName(),
                                    file.getAbsolutePath()
                            );
                            
                            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
                            process.waitFor();
                            
                            SwingUtilities.invokeLater(() -> 
                                JOptionPane.showMessageDialog(SettingsView.this,
                                    "Database restored successfully!",
                                    "Restore Complete", JOptionPane.INFORMATION_MESSAGE)
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            SwingUtilities.invokeLater(() -> 
                                JOptionPane.showMessageDialog(SettingsView.this,
                                    "Restore failed: " + e.getMessage(),
                                    "Restore Error", JOptionPane.ERROR_MESSAGE)
                            );
                        }
                        return null;
                    }
                };
                worker.execute();
            }
        }
    }
    
    private void testConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                DatabaseConfiguration dbConfig = DatabaseConfiguration.getInstance();
                return dbConfig.testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        JOptionPane.showMessageDialog(SettingsView.this,
                            "✓ Database connection successful!",
                            "Connection Test", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(SettingsView.this,
                            "✗ Database connection failed!",
                            "Connection Test", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SettingsView.this,
                        "Connection test error: " + e.getMessage(),
                        "Connection Test", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
