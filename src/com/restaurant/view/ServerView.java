package com.restaurant.view;

import com.restaurant.controller.AuthenticationController;
import com.restaurant.controller.OrderController;
import com.restaurant.controller.ProductController;
import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.Order;
import com.restaurant.model.Order.OrderStatus;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Product;
import com.restaurant.model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Server View - Interface for servers to take orders and view their schedule
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class ServerView extends JFrame {
    
    private final User currentUser;
    private final ProductController productController;
    private final OrderController orderController;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    
    // Active Orders Panel
    private JTable activeOrdersTable;
    private DefaultTableModel activeOrdersTableModel;
    private Timer refreshTimer;
    
    // POS Components
    private JPanel productsPanel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;
    private JLabel taxLabel;
    private JLabel grandTotalLabel;
    private JTextField tableNumberField;
    private JComboBox<String> categoryComboBox;
    private List<OrderItem> cartItems = new ArrayList<>();
    
    // For adding items to existing order
    private JComboBox<String> existingOrderComboBox;
    private Integer selectedExistingOrderId = null;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public ServerView(User user) {
        this.currentUser = user;
        this.productController = new ProductController();
        this.orderController = new OrderController();
        
        try {
            initializeComponents();
            setupLayout();
            loadOrdersData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize ServerView: " + e.getMessage(), e);
        }
    }
    
    private void initializeComponents() {
        setTitle("Restaurant Management - Server Dashboard");
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
        
        tabbedPane.addTab("🍽️ New Order", createNewOrderPanel());
        tabbedPane.addTab("📋 Active Orders", createActiveOrdersPanel());
        tabbedPane.addTab("💰 Bills", createOrdersPanel());
        tabbedPane.addTab("📅 My Schedule", new ScheduleView(currentUser));
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Start auto-refresh timer for active orders
        startRefreshTimer();
    }
    
    private void startRefreshTimer() {
        refreshTimer = new Timer(10000, e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Active Orders tab
                loadActiveOrdersData();
            }
        });
        refreshTimer.start();
    }
    
    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🍽️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLabel = new JLabel("Server Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        // Right - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
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
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("📋 My Orders Today");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Orders Table
        String[] columns = {"Order ID", "Table", "Items", "Total", "Status", "Time"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setRowHeight(35);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(PRIMARY_COLOR);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadOrdersData());
        
        JButton viewDetailsBtn = new JButton("👁️ View Details");
        viewDetailsBtn.setBackground(ACCENT_COLOR);
        viewDetailsBtn.setForeground(WHITE);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.addActionListener(e -> viewOrderDetails());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewDetailsBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadOrdersData() {
        ordersTableModel.setRowCount(0);
        
        List<Bill> orders = orderController.getTodaysOrderList();
        for (Bill order : orders) {
            ordersTableModel.addRow(new Object[]{
                order.getBillId(),
                order.getTableNumber() != null ? "Table " + order.getTableNumber() : "Takeaway",
                order.getItems() != null ? order.getItems().size() + " items" : "0 items",
                String.format("$%.2f", order.getTotalAmount()),
                order.getPaymentStatus(),
                order.getCreatedAt() != null ? order.getCreatedAt().toLocalTime().toString() : ""
            });
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to view.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) ordersTableModel.getValueAt(selectedRow, 0);
        Bill order = orderController.getOrderById(orderId);
        
        if (order != null) {
            StringBuilder details = new StringBuilder();
            details.append("Order #").append(order.getBillId()).append("\n");
            details.append("Table: ").append(order.getTableNumber() != null ? order.getTableNumber() : "Takeaway").append("\n");
            details.append("Status: ").append(order.getPaymentStatus()).append("\n");
            details.append("Payment: ").append(order.getPaymentMethod()).append("\n\n");
            details.append("Items:\n");
            
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    details.append("  - ").append(item.getProductName())
                        .append(" x").append(item.getQuantity())
                        .append(" = $").append(item.getSubtotal()).append("\n");
                }
            }
            
            details.append("\nTotal: $").append(order.getTotalAmount());
            
            JOptionPane.showMessageDialog(this, details.toString(),
                "Order Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Create Active Orders Panel - Shows orders sent to kitchen with status
     */
    private JPanel createActiveOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title with notification
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("📋 Active Kitchen Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel hintLabel = new JLabel("🔔 Orders marked READY need to be served!");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(WARNING_COLOR);
        titlePanel.add(hintLabel, BorderLayout.EAST);
        
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Active Orders Table
        String[] columns = {"Order ID", "Order #", "Table", "Items", "Total", "Status", "Wait Time"};
        activeOrdersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        activeOrdersTable = new JTable(activeOrdersTableModel);
        activeOrdersTable.setRowHeight(40);
        activeOrdersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeOrdersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeOrdersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom renderer for status column to highlight READY orders
        activeOrdersTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if (status.contains("READY")) {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(WHITE);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("PREPARING")) {
                    c.setBackground(WARNING_COLOR);
                    c.setForeground(WHITE);
                } else if (status.contains("PENDING")) {
                    c.setBackground(new Color(189, 195, 199));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(WHITE);
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(activeOrdersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(PRIMARY_COLOR);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadActiveOrdersData());
        
        JButton addItemsBtn = new JButton("➕ Add Items to Order");
        addItemsBtn.setBackground(WARNING_COLOR);
        addItemsBtn.setForeground(WHITE);
        addItemsBtn.setFocusPainted(false);
        addItemsBtn.addActionListener(e -> addItemsToExistingOrder());
        
        JButton markServedBtn = new JButton("🍽️ Mark as Served");
        markServedBtn.setBackground(ACCENT_COLOR);
        markServedBtn.setForeground(WHITE);
        markServedBtn.setFocusPainted(false);
        markServedBtn.addActionListener(e -> markOrderAsServed());
        
        JButton createBillBtn = new JButton("💰 Create Bill");
        createBillBtn.setBackground(new Color(155, 89, 182)); // Purple
        createBillBtn.setForeground(WHITE);
        createBillBtn.setFocusPainted(false);
        createBillBtn.addActionListener(e -> createBillForServedOrder());
        
        JButton viewDetailsBtn = new JButton("👁️ View Details");
        viewDetailsBtn.setBackground(new Color(52, 73, 94));
        viewDetailsBtn.setForeground(WHITE);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.addActionListener(e -> viewActiveOrderDetails());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addItemsBtn);
        buttonPanel.add(markServedBtn);
        buttonPanel.add(createBillBtn);
        buttonPanel.add(viewDetailsBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadActiveOrdersData();
        
        return panel;
    }
    
    private void loadActiveOrdersData() {
        activeOrdersTableModel.setRowCount(0);
        
        List<Order> activeOrders = orderController.getAllActiveOrders();
        int readyCount = 0;
        
        for (Order order : activeOrders) {
            String statusDisplay = order.getStatus().getIcon() + " " + order.getStatus().getDisplayName();
            
            if (order.getStatus() == OrderStatus.READY) {
                readyCount++;
            }
            
            activeOrdersTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderNumber(),
                order.getTableNumber() != null ? "Table " + order.getTableNumber() : "Takeaway",
                order.getOrderItems() != null ? order.getOrderItems().size() + " items" : "0 items",
                String.format("$%.2f", order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO),
                statusDisplay,
                order.getWaitTime()
            });
        }
        
        // Alert for ready orders - only update tab title if tabbedPane is fully initialized
        if (tabbedPane != null && tabbedPane.getTabCount() > 1) {
            if (readyCount > 0) {
                // Flash the tab title or show notification
                tabbedPane.setTitleAt(1, "📋 Active Orders (" + readyCount + " READY!)");
            } else {
                tabbedPane.setTitleAt(1, "📋 Active Orders");
            }
        }
    }
    
    private void addItemsToExistingOrder() {
        int selectedRow = activeOrdersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to add items to.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) activeOrdersTableModel.getValueAt(selectedRow, 0);
        String orderNumber = (String) activeOrdersTableModel.getValueAt(selectedRow, 1);
        String table = (String) activeOrdersTableModel.getValueAt(selectedRow, 2);
        
        // Store the selected order ID and switch to New Order tab
        selectedExistingOrderId = orderId;
        tableNumberField.setText(table.replace("Table ", ""));
        tableNumberField.setEnabled(false);
        
        // Update New Order tab title to indicate we're adding to existing order
        tabbedPane.setTitleAt(0, "🍽️ Adding to " + orderNumber);
        tabbedPane.setSelectedIndex(0);
        
        JOptionPane.showMessageDialog(this, 
            "Add items to the cart, then click 'Add to Order' to send them to kitchen.\n" +
            "Adding items to: " + orderNumber + " - " + table,
            "Add Items Mode", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void markOrderAsServed() {
        int selectedRow = activeOrdersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to mark as served.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) activeOrdersTableModel.getValueAt(selectedRow, 0);
        String status = (String) activeOrdersTableModel.getValueAt(selectedRow, 5);
        
        // Check for "Ready" (case-insensitive) since status display is like "✅ Ready"
        if (!status.toLowerCase().contains("ready")) {
            JOptionPane.showMessageDialog(this, 
                "Only orders with READY status can be marked as served.",
                "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Mark this order as SERVED?", "Confirm",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (orderController.markOrderAsServed(orderId)) {
                JOptionPane.showMessageDialog(this, "Order marked as served!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadActiveOrdersData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewActiveOrderDetails() {
        int selectedRow = activeOrdersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to view.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) activeOrdersTableModel.getValueAt(selectedRow, 0);
        Order order = orderController.getKitchenOrderById(orderId);
        
        if (order != null) {
            StringBuilder details = new StringBuilder();
            details.append("Order #: ").append(order.getOrderNumber()).append("\n");
            details.append("Table: ").append(order.getLocationDisplay()).append("\n");
            details.append("Status: ").append(order.getStatus().getIcon()).append(" ")
                   .append(order.getStatus().getDisplayName()).append("\n");
            details.append("Server: ").append(order.getServerName()).append("\n");
            details.append("Time: ").append(order.getFormattedCreatedTime()).append("\n\n");
            details.append("Items:\n");
            
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    details.append("  • ").append(item.getProductName())
                        .append(" x").append(item.getQuantity())
                        .append(" - $").append(String.format("%.2f", item.getSubtotal())).append("\n");
                }
            }
            
            details.append("\nSubtotal: $").append(String.format("%.2f", order.getSubtotal()));
            details.append("\nTax: $").append(String.format("%.2f", order.getTaxAmount()));
            details.append("\nTotal: $").append(String.format("%.2f", order.getTotalAmount()));
            
            JOptionPane.showMessageDialog(this, details.toString(),
                "Order Details - " + order.getOrderNumber(), JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Create bill for a served order
     */
    private void createBillForServedOrder() {
        int selectedRow = activeOrdersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to create a bill for.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (int) activeOrdersTableModel.getValueAt(selectedRow, 0);
        String status = (String) activeOrdersTableModel.getValueAt(selectedRow, 5);
        
        // Only allow bill creation for SERVED orders
        if (!status.toLowerCase().contains("served")) {
            JOptionPane.showMessageDialog(this, 
                "Bills can only be created for orders that have been served.\n" +
                "Please mark the order as served first.",
                "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Order order = orderController.getKitchenOrderById(orderId);
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order not found or has no items.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show payment dialog
        String[] paymentOptions = {"Cash", "Card", "Cancel"};
        int paymentChoice = JOptionPane.showOptionDialog(this,
            "Order #" + order.getOrderNumber() + "\n" +
            "Total: $" + String.format("%.2f", order.getTotalAmount()) + "\n\n" +
            "Select payment method:",
            "Create Bill",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentOptions,
            paymentOptions[0]);
        
        if (paymentChoice == 2 || paymentChoice == JOptionPane.CLOSED_OPTION) {
            return; // Cancelled
        }
        
        PaymentMethod paymentMethod = (paymentChoice == 0) ? PaymentMethod.CASH : PaymentMethod.CARD;
        BigDecimal cashReceived = null;
        
        if (paymentMethod == PaymentMethod.CASH) {
            String cashInput = JOptionPane.showInputDialog(this,
                "Total: $" + String.format("%.2f", order.getTotalAmount()) + "\n\nEnter cash received:",
                "Cash Payment",
                JOptionPane.QUESTION_MESSAGE);
            
            if (cashInput == null || cashInput.trim().isEmpty()) {
                return; // Cancelled
            }
            
            try {
                cashReceived = new BigDecimal(cashInput.trim());
                if (cashReceived.compareTo(order.getTotalAmount()) < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient cash amount. Required: $" + String.format("%.2f", order.getTotalAmount()),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Create the bill
        Bill bill = orderController.createBill(currentUser, new ArrayList<>(order.getOrderItems()), 
                                               paymentMethod, cashReceived);
        
        if (bill != null) {
            // Mark order as completed
            orderController.markOrderAsCompleted(orderId);
            
            // Show receipt
            showBillReceipt(bill, order, cashReceived);
            
            // Refresh the orders list
            loadActiveOrdersData();
        }
    }
    
    /**
     * Show the bill receipt
     */
    private void showBillReceipt(Bill bill, Order order, BigDecimal cashReceived) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("═══════════════════════════════════\n");
        receipt.append("         RESTAURANT RECEIPT         \n");
        receipt.append("═══════════════════════════════════\n\n");
        receipt.append("Bill #: ").append(bill.getBillNumber()).append("\n");
        receipt.append("Order #: ").append(order.getOrderNumber()).append("\n");
        receipt.append("Table: ").append(order.getLocationDisplay()).append("\n");
        receipt.append("Server: ").append(currentUser.getFullName()).append("\n");
        receipt.append("Date: ").append(bill.getBilledAt() != null ? 
            bill.getBilledAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "").append("\n\n");
        receipt.append("───────────────────────────────────\n");
        receipt.append("ITEMS:\n");
        
        for (OrderItem item : order.getOrderItems()) {
            receipt.append(String.format("%-20s x%d  $%.2f\n", 
                truncateString(item.getProductName(), 20),
                item.getQuantity(), 
                item.getSubtotal()));
        }
        
        receipt.append("───────────────────────────────────\n");
        receipt.append(String.format("Subtotal:           $%.2f\n", bill.getNetAmount()));
        receipt.append(String.format("Tax:                $%.2f\n", bill.getTaxAmount()));
        receipt.append(String.format("TOTAL:              $%.2f\n", bill.getTotalAmount()));
        receipt.append("───────────────────────────────────\n");
        receipt.append("Payment: ").append(bill.getPaymentMethod()).append("\n");
        
        if (bill.getPaymentMethod() == PaymentMethod.CASH && cashReceived != null) {
            receipt.append(String.format("Cash Received:      $%.2f\n", cashReceived));
            BigDecimal change = cashReceived.subtract(bill.getTotalAmount());
            receipt.append(String.format("Change:             $%.2f\n", change));
        }
        
        receipt.append("\n═══════════════════════════════════\n");
        receipt.append("      Thank you for dining!        \n");
        receipt.append("═══════════════════════════════════\n");
        
        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Receipt - " + bill.getBillNumber(), JOptionPane.PLAIN_MESSAGE);
    }
    
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
    
    private JPanel createNewOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top - Table Number and Category Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topPanel.setBackground(WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        topPanel.add(new JLabel("Table #:"));
        tableNumberField = new JTextField(5);
        tableNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(tableNumberField);
        
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>(new String[]{"ALL", "APPETIZER", "MAIN_COURSE", "DESSERT", "BEVERAGE", "SIDE"});
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.addActionListener(e -> loadProductsForCategory((String) categoryComboBox.getSelectedItem()));
        topPanel.add(categoryComboBox);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center - Split between Products and Cart
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.6);
        
        // Left - Products Grid
        JPanel productsContainer = new JPanel(new BorderLayout());
        productsContainer.setBackground(WHITE);
        productsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel productsTitle = new JLabel("📦 Menu Items");
        productsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productsContainer.add(productsTitle, BorderLayout.NORTH);
        
        productsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        productsPanel.setBackground(WHITE);
        JScrollPane productsScroll = new JScrollPane(productsPanel);
        productsScroll.setBorder(null);
        productsScroll.getVerticalScrollBar().setUnitIncrement(16);
        productsContainer.add(productsScroll, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(productsContainer);
        
        // Right - Cart
        JPanel cartContainer = new JPanel(new BorderLayout(10, 10));
        cartContainer.setBackground(WHITE);
        cartContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel cartTitle = new JLabel("🛒 Current Order");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cartContainer.add(cartTitle, BorderLayout.NORTH);
        
        // Cart Table
        String[] cartColumns = {"Item", "Qty", "Price", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartContainer.add(cartScroll, BorderLayout.CENTER);
        
        // Cart Bottom - Totals and Actions
        JPanel cartBottomPanel = new JPanel(new BorderLayout(10, 10));
        cartBottomPanel.setBackground(WHITE);
        
        // Totals
        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        totalsPanel.setBackground(WHITE);
        totalsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        totalsPanel.add(new JLabel("Subtotal:"));
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalsPanel.add(totalLabel);
        
        totalsPanel.add(new JLabel("Tax (10%):"));
        taxLabel = new JLabel("$0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalsPanel.add(taxLabel);
        
        totalsPanel.add(new JLabel("Grand Total:"));
        grandTotalLabel = new JLabel("$0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        grandTotalLabel.setForeground(PRIMARY_COLOR);
        totalsPanel.add(grandTotalLabel);
        
        cartBottomPanel.add(totalsPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        buttonsPanel.setBackground(WHITE);
        
        JButton clearBtn = new JButton("🗑️ Clear");
        clearBtn.setBackground(DANGER_COLOR);
        clearBtn.setForeground(WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearCart());
        
        JButton removeBtn = new JButton("➖ Remove");
        removeBtn.setBackground(WARNING_COLOR);
        removeBtn.setForeground(WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.addActionListener(e -> removeFromCart());
        
        JButton sendToKitchenBtn = new JButton("👨‍🍳 Send to Kitchen");
        sendToKitchenBtn.setBackground(PRIMARY_COLOR);
        sendToKitchenBtn.setForeground(WHITE);
        sendToKitchenBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendToKitchenBtn.setFocusPainted(false);
        sendToKitchenBtn.addActionListener(e -> sendOrderToKitchen());
        
        JButton addToOrderBtn = new JButton("➕ Add to Existing Order");
        addToOrderBtn.setBackground(new Color(142, 68, 173));
        addToOrderBtn.setForeground(WHITE);
        addToOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToOrderBtn.setFocusPainted(false);
        addToOrderBtn.addActionListener(e -> addItemsToSelectedOrder());
        
        buttonsPanel.add(clearBtn);
        buttonsPanel.add(removeBtn);
        buttonsPanel.add(sendToKitchenBtn);
        buttonsPanel.add(addToOrderBtn);
        
        cartBottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
        cartContainer.add(cartBottomPanel, BorderLayout.SOUTH);
        
        splitPane.setRightComponent(cartContainer);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load products
        loadProductsForCategory("ALL");
        
        return panel;
    }
    
    private void loadProductsForCategory(String category) {
        productsPanel.removeAll();
        
        List<Product> products;
        if ("ALL".equals(category)) {
            products = productController.getAllProducts();
        } else {
            products = productController.getProductsByCategory(category);
        }
        
        for (Product product : products) {
            JButton productBtn = createProductButton(product);
            productsPanel.add(productBtn);
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private JButton createProductButton(Product product) {
        JButton btn = new JButton("<html><center>" + product.getProductName() + "<br>$" + 
            String.format("%.2f", product.getPrice()) + "</center></html>");
        btn.setPreferredSize(new Dimension(140, 80));
        btn.setBackground(WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> addToCart(product));
        
        return btn;
    }
    
    private void addToCart(Product product) {
        // Check if already in cart
        for (OrderItem item : cartItems) {
            if (item.getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + 1);
                updateCartDisplay();
                return;
            }
        }
        
        // Add new item
        OrderItem item = new OrderItem();
        item.setProductId(product.getProductId());
        item.setProductName(product.getProductName());
        item.setQuantity(1);
        item.setUnitPrice(product.getPrice());
        cartItems.add(item);
        
        updateCartDisplay();
    }
    
    private void updateCartDisplay() {
        cartTableModel.setRowCount(0);
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (OrderItem item : cartItems) {
            cartTableModel.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                String.format("$%.2f", item.getUnitPrice()),
                String.format("$%.2f", item.getSubtotal())
            });
            subtotal = subtotal.add(item.getSubtotal());
        }
        
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10"));
        BigDecimal grandTotal = subtotal.add(tax);
        
        totalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        grandTotalLabel.setText(String.format("$%.2f", grandTotal));
    }
    
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < cartItems.size()) {
            cartItems.remove(selectedRow);
            updateCartDisplay();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void clearCart() {
        if (!cartItems.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Clear all items from the order?", "Confirm Clear",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cartItems.clear();
                updateCartDisplay();
                resetOrderMode();
            }
        }
    }
    
    private void resetOrderMode() {
        selectedExistingOrderId = null;
        tableNumberField.setEnabled(true);
        tableNumberField.setText("");
        tabbedPane.setTitleAt(0, "🍽️ New Order");
    }
    
    /**
     * Send order to kitchen (new order)
     */
    private void sendOrderToKitchen() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add items first.",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tableNumber = tableNumberField.getText().trim();
        if (tableNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a table number.",
                "Missing Table", JOptionPane.WARNING_MESSAGE);
            tableNumberField.requestFocus();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Send order to kitchen?\n\nTable: " + tableNumber + "\nItems: " + cartItems.size(),
            "Confirm Order", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Order order = orderController.createOrderForKitchen(
                currentUser, new ArrayList<>(cartItems), tableNumber);
            
            if (order != null) {
                // Clear cart and refresh
                cartItems.clear();
                updateCartDisplay();
                resetOrderMode();
                loadActiveOrdersData();
                
                // Switch to Active Orders tab
                tabbedPane.setSelectedIndex(1);
            }
        }
    }
    
    /**
     * Add items to an existing order that was selected from Active Orders tab
     */
    private void addItemsToSelectedOrder() {
        if (selectedExistingOrderId == null) {
            JOptionPane.showMessageDialog(this, 
                "No existing order selected.\n\n" +
                "To add items to an existing order:\n" +
                "1. Go to 'Active Orders' tab\n" +
                "2. Select an order\n" +
                "3. Click 'Add Items to Order'\n" +
                "4. Add items to cart\n" +
                "5. Click this button",
                "No Order Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add items first.",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Add " + cartItems.size() + " item(s) to the existing order?",
            "Confirm Add Items", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = orderController.addItemsToOrder(
                selectedExistingOrderId, new ArrayList<>(cartItems));
            
            if (success) {
                // Clear cart and refresh
                cartItems.clear();
                updateCartDisplay();
                resetOrderMode();
                loadActiveOrdersData();
                
                // Switch to Active Orders tab
                tabbedPane.setSelectedIndex(1);
            }
        }
    }
    
    /**
     * Create a bill for payment (for completed/served orders)
     */
    private void createBillForOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add items first.",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Select payment method
        PaymentMethod[] methods = PaymentMethod.values();
        PaymentMethod selectedMethod = (PaymentMethod) JOptionPane.showInputDialog(
            this, "Select Payment Method:", "Payment",
            JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
        
        if (selectedMethod == null) return;
        
        BigDecimal cashReceived = null;
        if (selectedMethod == PaymentMethod.CASH) {
            String cashStr = JOptionPane.showInputDialog(this, "Enter cash received:");
            if (cashStr == null || cashStr.trim().isEmpty()) return;
            try {
                cashReceived = new BigDecimal(cashStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Create the bill
        Bill bill = orderController.createBill(currentUser, new ArrayList<>(cartItems), 
            selectedMethod, cashReceived);
        
        if (bill != null) {
            // Clear cart and refresh orders
            cartItems.clear();
            updateCartDisplay();
            resetOrderMode();
            loadOrdersData();
            
            // Switch to Bills tab
            tabbedPane.setSelectedIndex(2);
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
