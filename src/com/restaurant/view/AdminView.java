
package com.restaurant.view;

import com.restaurant.controller.AuthenticationController;
import com.restaurant.controller.OrderController;
import com.restaurant.controller.ProductController;
import com.restaurant.controller.UserController;
import com.restaurant.model.Product;
import com.restaurant.model.Product.ProductCategory;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;
import com.restaurant.model.Bill;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.time.format.DateTimeFormatter;

/**
 * Admin View - Dashboard and Management Interface
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class AdminView extends JFrame {
    
    private final ProductController productController;
    private final OrderController orderController;
    private final UserController userController;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable productsTable, usersTable, ordersTable;
    private DefaultTableModel productsTableModel, usersTableModel, ordersTableModel;
    private JLabel totalSalesLabel, todaysSalesLabel, totalOrdersLabel, todaysOrdersLabel;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public AdminView() {
        this.productController = new ProductController();
        this.orderController = new OrderController();
        this.userController = new UserController();
        
        initializeComponents();
        setupLayout();
        loadDashboardData();
        loadProductsTable();
        loadUsersTable();
        loadOrdersTable();
    }
    
    private void initializeComponents() {
        setTitle("Restaurant Management - Admin Dashboard");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(WHITE);
        
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        tabbedPane.addTab("🍽️ Products", createProductsPanel());
        tabbedPane.addTab("📦 Inventory", new InventoryView());
        tabbedPane.addTab("📝 Orders", createOrdersPanel());
        tabbedPane.addTab("👥 Users", createUsersPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Left - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🎯 Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(WHITE);
        leftPanel.add(titleLabel);
        
        // Right - Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JButton refreshBtn = createHeaderButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        
        JButton settingsBtn = createHeaderButton("⚙️ Settings");
        settingsBtn.setBackground(new Color(155, 89, 182));
        settingsBtn.addActionListener(e -> openSettings());
        
        JButton posBtn = createHeaderButton("💰 POS");
        posBtn.setBackground(ACCENT_COLOR);
        posBtn.addActionListener(e -> openPOS());
        
        JButton logoutBtn = createHeaderButton("🚪 Logout");
        logoutBtn.setBackground(DANGER_COLOR);
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(refreshBtn);
        rightPanel.add(settingsBtn);
        rightPanel.add(posBtn);
        rightPanel.add(logoutBtn);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(BACKGROUND);
        
        totalSalesLabel = new JLabel("$0.00");
        todaysSalesLabel = new JLabel("$0.00");
        totalOrdersLabel = new JLabel("0");
        todaysOrdersLabel = new JLabel("0");
        
        statsPanel.add(createStatCard("💰 Total Sales", totalSalesLabel, PRIMARY_COLOR));
        statsPanel.add(createStatCard("📅 Today's Sales", todaysSalesLabel, ACCENT_COLOR));
        statsPanel.add(createStatCard("📦 Total Orders", totalOrdersLabel, WARNING_COLOR));
        statsPanel.add(createStatCard("🎯 Today's Orders", todaysOrdersLabel, new Color(155, 89, 182)));
        
        // Welcome Message
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(WHITE);
        welcomePanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        String userName = AuthenticationController.getCurrentUser() != null ? 
            AuthenticationController.getCurrentUser().getFullName() : "Admin";
        
        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "! 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Here's what's happening with your restaurant today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(subtitleLabel);
        welcomePanel.add(Box.createVerticalStrut(30));
        
        // Quick Actions
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        quickActionsPanel.setOpaque(false);
        
        JButton addProductBtn = createQuickActionButton("➕ Add Product", ACCENT_COLOR);
        addProductBtn.addActionListener(e -> showAddProductDialog());
        
        JButton viewOrdersBtn = createQuickActionButton("📝 View Orders", PRIMARY_COLOR);
        viewOrdersBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        JButton manageProdBtn = createQuickActionButton("🍽️ Manage Products", WARNING_COLOR);
        manageProdBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        quickActionsPanel.add(addProductBtn);
        quickActionsPanel.add(viewOrdersBtn);
        quickActionsPanel.add(manageProdBtn);
        
        welcomePanel.add(quickActionsPanel);
        
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(welcomePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }
    
    private JButton createQuickActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 45));
        return button;
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Top - Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(BACKGROUND);
        
        JButton addBtn = createActionButton("➕ Add Product", ACCENT_COLOR);
        addBtn.addActionListener(e -> showAddProductDialog());
        
        JButton editBtn = createActionButton("✏️ Edit", WARNING_COLOR);
        editBtn.addActionListener(e -> showEditProductDialog());
        
        JButton deleteBtn = createActionButton("🗑️ Delete", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteProduct());
        
        JButton refreshBtn = createActionButton("🔄 Refresh", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> loadProductsTable());
        
        actionsPanel.add(addBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(deleteBtn);
        actionsPanel.add(refreshBtn);
        
        // Center - Table
        String[] columns = {"ID", "Name", "Category", "Price", "Description", "Available"};
        productsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        productsTable = new JTable(productsTableModel);
        productsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productsTable.setRowHeight(35);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        productsTable.getTableHeader().setForeground(WHITE);
        
        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 38));
        return button;
    }
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Top - Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(BACKGROUND);
        
        JButton refreshBtn = createActionButton("🔄 Refresh", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> loadOrdersTable());
        
        JButton viewDetailsBtn = createActionButton("👁️ View Details", ACCENT_COLOR);
        viewDetailsBtn.addActionListener(e -> viewOrderDetails());
        
        JButton exportBtn = createActionButton("📄 Export", WARNING_COLOR);
        exportBtn.addActionListener(e -> exportOrders());
        
        actionsPanel.add(refreshBtn);
        actionsPanel.add(viewDetailsBtn);
        actionsPanel.add(exportBtn);
        
        // Center - Table
        String[] columns = {"Bill #", "Date & Time", "Items", "Subtotal", "Tax", "Total", "Payment", "Cashier"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ordersTable.setRowHeight(35);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        ordersTable.getTableHeader().setBackground(PRIMARY_COLOR);
        ordersTable.getTableHeader().setForeground(WHITE);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Top - Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(BACKGROUND);
        
        JButton addBtn = createActionButton("➕ Add User", ACCENT_COLOR);
        addBtn.addActionListener(e -> showAddUserDialog());
        
        JButton editBtn = createActionButton("✏️ Edit", WARNING_COLOR);
        editBtn.addActionListener(e -> showEditUserDialog());
        
        JButton deleteBtn = createActionButton("🗑️ Delete", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteUser());
        
        JButton toggleBtn = createActionButton("🔄 Toggle Status", PRIMARY_COLOR);
        toggleBtn.addActionListener(e -> toggleUserStatus());
        
        JButton refreshBtn = createActionButton("🔄 Refresh", new Color(52, 73, 94));
        refreshBtn.addActionListener(e -> loadUsersTable());
        
        actionsPanel.add(addBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(deleteBtn);
        actionsPanel.add(toggleBtn);
        actionsPanel.add(refreshBtn);
        
        // Center - Table
        String[] columns = {"ID", "Full Name", "Username", "Email", "Phone", "Role", "Status", "Created"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(usersTableModel);
        usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usersTable.setRowHeight(35);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        usersTable.getTableHeader().setBackground(PRIMARY_COLOR);
        usersTable.getTableHeader().setForeground(WHITE);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            BigDecimal totalSales, todaysSales;
            int totalOrders, todaysOrders;
            
            @Override
            protected Void doInBackground() {
                totalSales = orderController.getTotalSales();
                todaysSales = orderController.getTodaysSales();
                totalOrders = orderController.getTotalOrders();
                todaysOrders = orderController.getTodaysOrders();
                return null;
            }
            
            @Override
            protected void done() {
                totalSalesLabel.setText("$" + totalSales);
                todaysSalesLabel.setText("$" + todaysSales);
                totalOrdersLabel.setText(String.valueOf(totalOrders));
                todaysOrdersLabel.setText(String.valueOf(todaysOrders));
            }
        };
        worker.execute();
    }
    
    private void loadProductsTable() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Product> products;
            
            @Override
            protected Void doInBackground() {
                products = productController.getAllProducts();
                return null;
            }
            
            @Override
            protected void done() {
                productsTableModel.setRowCount(0);
                for (Product product : products) {
                    Object[] row = {
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory().name(),
                        "$" + product.getPrice(),
                        product.getDescription(),
                        product.isAvailable() ? "✓" : "✗"
                    };
                    productsTableModel.addRow(row);
                }
            }
        };
        worker.execute();
    }
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add New Product", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        // Product Name
        formPanel.add(createLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nameField = createTextField();
        formPanel.add(nameField, gbc);
        
        // Category
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JComboBox<ProductCategory> categoryCombo = new JComboBox<>(ProductCategory.values());
        categoryCombo.setPreferredSize(new Dimension(0, 35));
        formPanel.add(categoryCombo, gbc);
        
        // Price
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Price:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField priceField = createTextField();
        formPanel.add(priceField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton saveBtn = createActionButton("💾 Save", ACCENT_COLOR);
        JButton cancelBtn = createActionButton("❌ Cancel", Color.GRAY);
        
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String category = ((ProductCategory) categoryCombo.getSelectedItem()).name();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                String description = descArea.getText().trim();
                
                if (productController.createProduct(name, category, price, description)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Product added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProductsTable();
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter valid price",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditProductDialog() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a product to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer productId = (Integer) productsTable.getValueAt(selectedRow, 0);
        String currentName = (String) productsTable.getValueAt(selectedRow, 1);
        String currentCategory = (String) productsTable.getValueAt(selectedRow, 2);
        String currentPrice = ((String) productsTable.getValueAt(selectedRow, 3)).replace("$", "");
        String currentDesc = (String) productsTable.getValueAt(selectedRow, 4);
        boolean currentAvailable = "✓".equals(productsTable.getValueAt(selectedRow, 5));
        
        JDialog dialog = new JDialog(this, "Edit Product", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        // Product Name
        formPanel.add(createLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nameField = createTextField();
        nameField.setText(currentName);
        formPanel.add(nameField, gbc);
        
        // Category
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JComboBox<ProductCategory> categoryCombo = new JComboBox<>(ProductCategory.values());
        categoryCombo.setSelectedItem(ProductCategory.valueOf(currentCategory));
        categoryCombo.setPreferredSize(new Dimension(0, 35));
        formPanel.add(categoryCombo, gbc);
        
        // Price
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Price:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField priceField = createTextField();
        priceField.setText(currentPrice);
        formPanel.add(priceField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setText(currentDesc);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll, gbc);
        
        // Available
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JCheckBox availableCheck = new JCheckBox("Product Available");
        availableCheck.setSelected(currentAvailable);
        availableCheck.setFont(new Font("Segoe UI", Font.BOLD, 13));
        availableCheck.setBackground(WHITE);
        formPanel.add(availableCheck, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton updateBtn = createActionButton("💾 Update", ACCENT_COLOR);
        JButton cancelBtn = createActionButton("❌ Cancel", Color.GRAY);
        
        updateBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String category = ((ProductCategory) categoryCombo.getSelectedItem()).name();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                String description = descArea.getText().trim();
                boolean available = availableCheck.isSelected();
                
                if (productController.updateProduct(productId, name, category, price, description, available)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Product updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProductsTable();
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter valid price",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a product to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer productId = (Integer) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete: " + productName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (productController.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this,
                    "Product deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProductsTable();
            }
        }
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }
    
    private void refreshData() {
        loadDashboardData();
        loadProductsTable();
        JOptionPane.showMessageDialog(this,
            "Data refreshed successfully!",
            "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openPOS() {
        dispose();
        new OrderView().setVisible(true);
    }
    
    private void openSettings() {
        new SettingsView().setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AuthenticationController.logout();
            dispose();
            new LoginView().setVisible(true);
        }
    }
    
    // ========== User Management Methods ==========
    
    private void loadUsersTable() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<User> users;
            
            @Override
            protected Void doInBackground() {
                users = userController.getAllUsers();
                return null;
            }
            
            @Override
            protected void done() {
                usersTableModel.setRowCount(0);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (User user : users) {
                    Object[] row = {
                        user.getUserId(),
                        user.getFullName(),
                        user.getUsername(),
                        user.getEmailAddress(),
                        user.getPhoneNumber(),
                        user.getRole().name(),
                        user.isActive() ? "✓ Active" : "✗ Inactive",
                        user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "N/A"
                    };
                    usersTableModel.addRow(row);
                }
            }
        };
        worker.execute();
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        // Full Name
        formPanel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField fullNameField = createTextField();
        formPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField emailField = createTextField();
        formPanel.add(emailField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField usernameField = createTextField();
        formPanel.add(usernameField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField phoneField = createTextField();
        formPanel.add(phoneField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(0, 35));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(passwordField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField addressField = createTextField();
        formPanel.add(addressField, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setPreferredSize(new Dimension(0, 35));
        formPanel.add(roleCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton saveBtn = createActionButton("💾 Save", ACCENT_COLOR);
        JButton cancelBtn = createActionButton("❌ Cancel", Color.GRAY);
        
        saveBtn.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String address = addressField.getText().trim();
            String role = ((UserRole) roleCombo.getSelectedItem()).name();
            
            if (userController.createUser(fullName, email, username, phone, password, address, role)) {
                JOptionPane.showMessageDialog(dialog,
                    "User created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable();
                dialog.dispose();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer userId = (Integer) usersTable.getValueAt(selectedRow, 0);
        String currentFullName = (String) usersTable.getValueAt(selectedRow, 1);
        String currentUsername = (String) usersTable.getValueAt(selectedRow, 2);
        String currentEmail = (String) usersTable.getValueAt(selectedRow, 3);
        String currentPhone = (String) usersTable.getValueAt(selectedRow, 4);
        String currentRole = (String) usersTable.getValueAt(selectedRow, 5);
        String statusStr = (String) usersTable.getValueAt(selectedRow, 6);
        boolean currentActive = statusStr.contains("Active");
        
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        
        // Full Name
        formPanel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField fullNameField = createTextField();
        fullNameField.setText(currentFullName);
        formPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField emailField = createTextField();
        emailField.setText(currentEmail);
        formPanel.add(emailField, gbc);
        
        // Username (read-only)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField usernameField = createTextField();
        usernameField.setText(currentUsername);
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(240, 240, 240));
        formPanel.add(usernameField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField phoneField = createTextField();
        phoneField.setText(currentPhone);
        formPanel.add(phoneField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField addressField = createTextField();
        var optUser = userController.getUserById(userId);
        if (optUser.isPresent()) {
            addressField.setText(optUser.get().getAddress());
        }
        formPanel.add(addressField, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setSelectedItem(UserRole.valueOf(currentRole));
        roleCombo.setPreferredSize(new Dimension(0, 35));
        formPanel.add(roleCombo, gbc);
        
        // Active Status
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JCheckBox activeCheck = new JCheckBox("Active");
        activeCheck.setSelected(currentActive);
        activeCheck.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeCheck.setBackground(WHITE);
        formPanel.add(activeCheck, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton updateBtn = createActionButton("💾 Update", ACCENT_COLOR);
        JButton cancelBtn = createActionButton("❌ Cancel", Color.GRAY);
        
        updateBtn.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String role = ((UserRole) roleCombo.getSelectedItem()).name();
            boolean active = activeCheck.isSelected();
            
            if (userController.updateUser(userId, fullName, email, phone, address, role, active)) {
                JOptionPane.showMessageDialog(dialog,
                    "User updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable();
                dialog.dispose();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer userId = (Integer) usersTable.getValueAt(selectedRow, 0);
        String userName = (String) usersTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + userName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this,
                    "User deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable();
            }
        }
    }
    
    private void toggleUserStatus() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to toggle status",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer userId = (Integer) usersTable.getValueAt(selectedRow, 0);
        String userName = (String) usersTable.getValueAt(selectedRow, 1);
        String status = (String) usersTable.getValueAt(selectedRow, 6);
        boolean currentlyActive = status.contains("Active");
        
        String action = currentlyActive ? "deactivate" : "activate";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + action + " user: " + userName + "?",
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.toggleUserStatus(userId)) {
                JOptionPane.showMessageDialog(this,
                    "User status updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable();
            }
        }
    }
    
    // ========== Order Management Methods ==========
    
    private void loadOrdersTable() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Bill> bills;
            
            @Override
            protected Void doInBackground() {
                bills = orderController.getAllBills();
                return null;
            }
            
            @Override
            protected void done() {
                ordersTableModel.setRowCount(0);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Bill bill : bills) {
                    Object[] row = {
                        bill.getBillNumber(),
                        bill.getBilledAt() != null ? bill.getBilledAt().format(formatter) : "N/A",
                        bill.getTotalItems(),
                        "$" + bill.getNetAmount(),
                        "$" + bill.getTaxAmount(),
                        "$" + bill.getTotalAmount(),
                        bill.getPaymentMethod().name(),
                        bill.getBilledByUser()
                    };
                    ordersTableModel.addRow(row);
                }
            }
        };
        worker.execute();
    }
    
    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to view details",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String billNumber = (String) ordersTable.getValueAt(selectedRow, 0);
        String dateTime = (String) ordersTable.getValueAt(selectedRow, 1);
        int items = (Integer) ordersTable.getValueAt(selectedRow, 2);
        String subtotal = (String) ordersTable.getValueAt(selectedRow, 3);
        String tax = (String) ordersTable.getValueAt(selectedRow, 4);
        String total = (String) ordersTable.getValueAt(selectedRow, 5);
        String payment = (String) ordersTable.getValueAt(selectedRow, 6);
        String cashier = (String) ordersTable.getValueAt(selectedRow, 7);
        
        String details = (
                """
                Bill Number: %s
                Date & Time: %s
                Total Items: %d
                Subtotal: %s
                Tax: %s
                Total: %s
                Payment Method: %s
                Cashier: %s""").formatted(
                billNumber, dateTime, items, subtotal, tax, total, payment, cashier
        );
        
        JOptionPane.showMessageDialog(this,
            details,
            "Order Details - " + billNumber,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportOrders() {
        JOptionPane.showMessageDialog(this,
            """
            Export functionality coming soon!
            This will allow exporting orders to CSV/PDF format.""",
            "Export Orders",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
