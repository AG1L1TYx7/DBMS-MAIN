package com.restaurant.view;

import com.restaurant.controller.InventoryController;
import com.restaurant.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Inventory Management View
 * Provides interface for managing product inventory, stock levels,
 * and generating restock alerts
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class InventoryView extends JPanel {
    
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    private final InventoryController inventoryController;
    
    // UI Components
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JLabel totalItemsLabel;
    private JLabel lowStockLabel;
    private JLabel outOfStockLabel;
    private JLabel inStockLabel;
    private JComboBox<String> filterCombo;
    private Timer autoRefreshTimer;
    
    /**
     * Constructor
     */
    public InventoryView() {
        this.inventoryController = new InventoryController();
        initializeUI();
        loadInventoryData();
        updateStatistics();
        startAutoRefresh();
    }
    
    /**
     * Start auto-refresh timer (every 30 seconds)
     */
    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(30000, e -> {
            loadInventoryData();
            updateStatistics();
        });
        autoRefreshTimer.start();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Statistics Panel
        add(createStatisticsPanel(), BorderLayout.CENTER);
    }
    
    /**
     * Create header panel with title and actions
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Filter combo box
        filterCombo = new JComboBox<>(new String[]{
            "All Products", "Low Stock", "Out of Stock", "In Stock"
        });
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterCombo.addActionListener(e -> applyFilter());
        
        // Buttons
        JButton refreshBtn = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> refreshInventory());
        
        JButton adjustStockBtn = createStyledButton("Adjust Stock", SUCCESS_COLOR);
        adjustStockBtn.addActionListener(e -> showAdjustStockDialog());
        
        JButton initInventoryBtn = createStyledButton("Initialize Inventory", WARNING_COLOR);
        initInventoryBtn.addActionListener(e -> showInitializeInventoryDialog());
        
        buttonPanel.add(new JLabel("Filter: "));
        buttonPanel.add(filterCombo);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(adjustStockBtn);
        buttonPanel.add(initInventoryBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create statistics panel with cards
     */
    private JPanel createStatisticsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Stats Cards Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Create stat cards
        totalItemsLabel = new JLabel("0");
        lowStockLabel = new JLabel("0");
        outOfStockLabel = new JLabel("0");
        inStockLabel = new JLabel("0");
        
        statsPanel.add(createStatCard("Total Items", totalItemsLabel, PRIMARY_COLOR));
        statsPanel.add(createStatCard("Low Stock", lowStockLabel, WARNING_COLOR));
        statsPanel.add(createStatCard("Out of Stock", outOfStockLabel, DANGER_COLOR));
        statsPanel.add(createStatCard("In Stock", inStockLabel, SUCCESS_COLOR));
        
        // Table Panel
        JPanel tablePanel = createInventoryTablePanel();
        
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    /**
     * Create a statistics card
     */
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(20, 15, 20, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create inventory table panel
     */
    private JPanel createInventoryTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Table columns
        String[] columns = {
            "ID", "Product Name", "Category", "Stock", "Reorder Level", 
            "Max Stock", "Unit", "Status", "Available"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inventoryTable.setRowHeight(35);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        inventoryTable.getTableHeader().setBackground(PRIMARY_COLOR);
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        
        // Custom cell renderer for status column
        inventoryTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                
                if (!isSelected) {
                    if ("OUT OF STOCK".equals(status)) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(DANGER_COLOR);
                    } else if ("LOW STOCK".equals(status)) {
                        c.setBackground(new Color(255, 240, 200));
                        c.setForeground(WARNING_COLOR);
                    } else {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(SUCCESS_COLOR);
                    }
                }
                
                setHorizontalAlignment(CENTER);
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        });
        
        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        inventoryTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        
        // Column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create styled button
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Load inventory data into table
     */
    private void loadInventoryData() {
        tableModel.setRowCount(0);
        List<Product> products = inventoryController.getAllInventoryItems();
        
        for (Product product : products) {
            String status = getStockStatus(product);
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getCategory() != null ? product.getCategory().getDisplayName() : "N/A",
                product.getStockQuantity() != null ? product.getStockQuantity() : 0,
                product.getReorderLevel() != null ? product.getReorderLevel() : 0,
                product.getMaxStockLevel() != null ? product.getMaxStockLevel() : 0,
                product.getUnit() != null ? product.getUnit() : "pcs",
                status,
                product.isAvailable() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Get stock status for a product
     */
    private String getStockStatus(Product product) {
        if (product.isOutOfStock()) {
            return "OUT OF STOCK";
        } else if (product.isLowStock()) {
            return "LOW STOCK";
        } else {
            return "IN STOCK";
        }
    }
    
    /**
     * Update statistics
     */
    private void updateStatistics() {
        Map<String, Integer> stats = inventoryController.getInventoryStatistics();
        totalItemsLabel.setText(String.valueOf(stats.get("total")));
        lowStockLabel.setText(String.valueOf(stats.get("lowStock")));
        outOfStockLabel.setText(String.valueOf(stats.get("outOfStock")));
        inStockLabel.setText(String.valueOf(stats.get("inStock")));
    }
    
    /**
     * Apply filter to inventory table
     */
    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);
        
        List<Product> products;
        switch (filter) {
            case "Low Stock":
                products = inventoryController.getLowStockProducts();
                break;
            case "Out of Stock":
                products = inventoryController.getOutOfStockProducts();
                break;
            case "In Stock":
                products = inventoryController.getAllInventoryItems();
                products.removeIf(p -> p.isLowStock() || p.isOutOfStock());
                break;
            default:
                products = inventoryController.getAllInventoryItems();
        }
        
        for (Product product : products) {
            String status = getStockStatus(product);
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getCategory() != null ? product.getCategory().getDisplayName() : "N/A",
                product.getStockQuantity() != null ? product.getStockQuantity() : 0,
                product.getReorderLevel() != null ? product.getReorderLevel() : 0,
                product.getMaxStockLevel() != null ? product.getMaxStockLevel() : 0,
                product.getUnit() != null ? product.getUnit() : "pcs",
                status,
                product.isAvailable() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Refresh inventory data
     */
    private void refreshInventory() {
        loadInventoryData();
        updateStatistics();
        JOptionPane.showMessageDialog(this, "Inventory refreshed successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show adjust stock dialog
     */
    private void showAdjustStockDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to adjust stock.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        Integer currentStock = (Integer) tableModel.getValueAt(selectedRow, 3);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Adjust Stock - " + productName, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel currentStockLabel = new JLabel("Current Stock: " + currentStock);
        currentStockLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"ADD", "REMOVE", "SET"});
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        
        formPanel.add(currentStockLabel);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel("Adjustment Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantitySpinner);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            Integer quantity = (Integer) quantitySpinner.getValue();
            String reason = "Manual adjustment";
            
            if (inventoryController.updateStock(productId, quantity, type, reason)) {
                JOptionPane.showMessageDialog(dialog, "Stock updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshInventory();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update stock.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Show initialize inventory dialog
     */
    private void showInitializeInventoryDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to initialize inventory.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Initialize Inventory - " + productName, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JSpinner initialStockSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 9999, 1));
        JSpinner reorderLevelSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 999, 1));
        JSpinner maxStockSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 9999, 1));
        JComboBox<String> unitCombo = new JComboBox<>(new String[]{"pcs", "kg", "liters", "boxes", "bottles"});
        
        formPanel.add(new JLabel("Initial Stock:"));
        formPanel.add(initialStockSpinner);
        formPanel.add(new JLabel("Reorder Level:"));
        formPanel.add(reorderLevelSpinner);
        formPanel.add(new JLabel("Max Stock:"));
        formPanel.add(maxStockSpinner);
        formPanel.add(new JLabel("Unit:"));
        formPanel.add(unitCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Initialize");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            Integer initialStock = (Integer) initialStockSpinner.getValue();
            Integer reorderLevel = (Integer) reorderLevelSpinner.getValue();
            Integer maxStock = (Integer) maxStockSpinner.getValue();
            String unit = (String) unitCombo.getSelectedItem();
            
            if (inventoryController.initializeInventory(productId, initialStock, reorderLevel, maxStock, unit)) {
                JOptionPane.showMessageDialog(dialog, "Inventory initialized successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshInventory();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to initialize inventory.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
