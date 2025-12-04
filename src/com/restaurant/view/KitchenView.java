package com.restaurant.view;

import com.restaurant.controller.AuthenticationController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Kitchen View - Interface for chefs to view and manage orders
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class KitchenView extends JFrame {
    
    private final User currentUser;
    private final OrderController orderController;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable pendingOrdersTable;
    private JTable preparingOrdersTable;
    private DefaultTableModel pendingTableModel;
    private DefaultTableModel preparingTableModel;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public KitchenView(User user) {
        this.currentUser = user;
        this.orderController = new OrderController();
        
        initializeComponents();
        setupLayout();
        loadOrdersData();
        
        // Auto-refresh every 30 seconds
        Timer refreshTimer = new Timer(30000, e -> loadOrdersData());
        refreshTimer.start();
    }
    
    private void initializeComponents() {
        setTitle("Restaurant Management - Kitchen Dashboard");
        setSize(1200, 800);
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
        
        tabbedPane.addTab("🔥 Kitchen Queue", createKitchenQueuePanel());
        tabbedPane.addTab("📅 My Schedule", new ScheduleView(currentUser));
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 126, 34)); // Orange for kitchen
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("👨‍🍳");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLabel = new JLabel("Kitchen Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        // Right - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Chef: " + currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(WHITE);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(DANGER_COLOR);
        logoutBtn.setForeground(WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createKitchenQueuePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Split into two sections: Pending and Preparing
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(580);
        splitPane.setResizeWeight(0.5);
        
        splitPane.setLeftComponent(createPendingOrdersPanel());
        splitPane.setRightComponent(createPreparingOrdersPanel());
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Refresh button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BACKGROUND);
        
        JButton refreshBtn = new JButton("🔄 Refresh Orders");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setBackground(PRIMARY_COLOR);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadOrdersData());
        
        bottomPanel.add(refreshBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createPendingOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING_COLOR, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("📋 Pending Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(WARNING_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Order #", "Table", "Items", "Time", "Notes"};
        pendingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        pendingOrdersTable = new JTable(pendingTableModel);
        pendingOrdersTable.setRowHeight(40);
        pendingOrdersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pendingOrdersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        pendingOrdersTable.getTableHeader().setBackground(WARNING_COLOR);
        pendingOrdersTable.getTableHeader().setForeground(WHITE);
        
        JScrollPane scrollPane = new JScrollPane(pendingOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button
        JButton startBtn = new JButton("▶️ Start Preparing");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startBtn.setBackground(ACCENT_COLOR);
        startBtn.setForeground(WHITE);
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> startPreparingOrder());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(WHITE);
        btnPanel.add(startBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPreparingOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("🔥 Now Preparing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Order #", "Table", "Items", "Started", "Progress"};
        preparingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        preparingOrdersTable = new JTable(preparingTableModel);
        preparingOrdersTable.setRowHeight(40);
        preparingOrdersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        preparingOrdersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        preparingOrdersTable.getTableHeader().setBackground(PRIMARY_COLOR);
        preparingOrdersTable.getTableHeader().setForeground(WHITE);
        
        JScrollPane scrollPane = new JScrollPane(preparingOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button
        JButton readyBtn = new JButton("✅ Mark Ready");
        readyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        readyBtn.setBackground(ACCENT_COLOR);
        readyBtn.setForeground(WHITE);
        readyBtn.setFocusPainted(false);
        readyBtn.addActionListener(e -> markOrderReady());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(WHITE);
        btnPanel.add(readyBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadOrdersData() {
        // Load pending orders
        pendingTableModel.setRowCount(0);
        List<Order> pendingOrders = orderController.getOrdersByStatus("PENDING");
        for (Order order : pendingOrders) {
            String items = "";
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                items = order.getOrderItems().stream()
                    .map(item -> item.getProductName() + " x" + item.getQuantity())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            }
            
            pendingTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getTableNumber() != null ? "T-" + order.getTableNumber() : "Takeaway",
                items.length() > 40 ? items.substring(0, 37) + "..." : items,
                order.getCreatedAt() != null ? order.getCreatedAt().toLocalTime().toString() : "",
                order.getSpecialInstructions() != null ? order.getSpecialInstructions() : ""
            });
        }
        
        // Load preparing orders
        preparingTableModel.setRowCount(0);
        List<Order> preparingOrders = orderController.getOrdersByStatus("PREPARING");
        for (Order order : preparingOrders) {
            String items = "";
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                items = order.getOrderItems().stream()
                    .map(item -> item.getProductName() + " x" + item.getQuantity())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            }
            
            preparingTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getTableNumber() != null ? "T-" + order.getTableNumber() : "Takeaway",
                items.length() > 40 ? items.substring(0, 37) + "..." : items,
                order.getCreatedAt() != null ? order.getCreatedAt().toLocalTime().toString() : "",
                "In Progress"
            });
        }
    }
    
    private void startPreparingOrder() {
        int selectedRow = pendingOrdersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to start preparing.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) pendingTableModel.getValueAt(selectedRow, 0);
        
        boolean success = orderController.updateOrderStatus(orderId, "PREPARING");
        if (success) {
            JOptionPane.showMessageDialog(this, "Order #" + orderId + " is now being prepared!",
                "Order Started", JOptionPane.INFORMATION_MESSAGE);
            loadOrdersData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update order status.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void markOrderReady() {
        int selectedRow = preparingOrdersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to mark as ready.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) preparingTableModel.getValueAt(selectedRow, 0);
        
        boolean success = orderController.updateOrderStatus(orderId, "READY");
        if (success) {
            JOptionPane.showMessageDialog(this, "Order #" + orderId + " is ready for pickup!",
                "Order Ready", JOptionPane.INFORMATION_MESSAGE);
            loadOrdersData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update order status.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AuthenticationController.logout();
            dispose();
            new LoginView().setVisible(true);
        }
    }
}
