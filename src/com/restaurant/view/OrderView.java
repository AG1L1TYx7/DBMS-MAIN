package com.restaurant.view;

import com.restaurant.controller.AuthenticationController;
import com.restaurant.controller.OrderController;
import com.restaurant.controller.ProductController;
import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Product;
import com.restaurant.model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Order View - Point of Sale Interface
 * Complete POS system for taking orders
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class OrderView extends JFrame {
    
    private final ProductController productController;
    private final OrderController orderController;
    private final User currentUser;
    
    // UI Components
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JComboBox<String> categoryComboBox;
    private JPanel productsPanel;
    private JLabel totalLabel;
    private JLabel taxLabel;
    private JLabel grandTotalLabel;
    private JComboBox<PaymentMethod> paymentMethodCombo;
    private JTextField cashReceivedField;
    private JLabel changeLabel;
    
    // Cart data
    private List<OrderItem> cartItems;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public OrderView() {
        this.productController = new ProductController();
        this.orderController = new OrderController();
        this.currentUser = AuthenticationController.getCurrentUser();
        this.cartItems = new ArrayList<>();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProducts("ALL");
    }
    
    private void initializeComponents() {
        setTitle("Restaurant POS - Order Management");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top Panel - Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Center - Products & Cart
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createProductsPanel());
        splitPane.setRightComponent(createCartPanel());
        splitPane.setDividerLocation(850);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left - Title
        JLabel titleLabel = new JLabel("🍽️ Point of Sale");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        
        // Center - User info
        String userName = currentUser != null ? currentUser.getFullName() : "User";
        JLabel userLabel = new JLabel("Cashier: " + userName);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(255, 255, 255, 200));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Right - Logout
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(DANGER_COLOR);
        logoutBtn.setForeground(WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userLabel, BorderLayout.CENTER);
        panel.add(logoutBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        
        // Top - Category filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(WHITE);
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        List<String> categories = new ArrayList<>();
        categories.add("ALL");
        categories.addAll(productController.getAllCategories());
        categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryComboBox.setPreferredSize(new Dimension(200, 35));
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        filterPanel.add(categoryLabel);
        filterPanel.add(categoryComboBox);
        
        // Center - Products grid
        productsPanel = new JPanel();
        productsPanel.setLayout(new GridLayout(0, 3, 10, 10));
        productsPanel.setBackground(BACKGROUND);
        
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 5, 10, 10),
            new LineBorder(new Color(189, 195, 199), 1)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("🛒 Cart");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Cart Table
        String[] columns = {"Item", "Price", "Qty", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.setRowHeight(30);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(PRIMARY_COLOR);
        cartTable.getTableHeader().setForeground(WHITE);
        
        JScrollPane tableScroll = new JScrollPane(cartTable);
        tableScroll.setBorder(null);
        
        // Totals Panel
        JPanel totalsPanel = createTotalsPanel();
        
        // Actions Panel
        JPanel actionsPanel = createActionsPanel();
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(WHITE);
        bottomPanel.add(totalsPanel, BorderLayout.NORTH);
        bottomPanel.add(actionsPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 8));
        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new LineBorder(new Color(220, 220, 220), 1)
        ));
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 14);
        
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(valueFont);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        taxLabel = new JLabel("$0.00");
        taxLabel.setFont(valueFont);
        taxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        grandTotalLabel = new JLabel("$0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        grandTotalLabel.setForeground(PRIMARY_COLOR);
        grandTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        changeLabel = new JLabel("$0.00");
        changeLabel.setFont(valueFont);
        changeLabel.setForeground(ACCENT_COLOR);
        changeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(createLabel("Subtotal:", labelFont));
        panel.add(totalLabel);
        panel.add(createLabel("Tax (10%):", labelFont));
        panel.add(taxLabel);
        panel.add(createLabel("Grand Total:", new Font("Segoe UI", Font.BOLD, 16)));
        panel.add(grandTotalLabel);
        panel.add(createLabel("Change:", labelFont));
        panel.add(changeLabel);
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 15, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        
        // Payment Method
        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(paymentLabel, gbc);
        
        gbc.gridy++;
        paymentMethodCombo = new JComboBox<>(PaymentMethod.values());
        paymentMethodCombo.setPreferredSize(new Dimension(0, 35));
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(paymentMethodCombo, gbc);
        
        // Cash Received
        gbc.gridy++;
        JLabel cashLabel = new JLabel("Cash Received:");
        cashLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(cashLabel, gbc);
        
        gbc.gridy++;
        cashReceivedField = new JTextField();
        cashReceivedField.setPreferredSize(new Dimension(0, 35));
        cashReceivedField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cashReceivedField.setBorder(new CompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(cashReceivedField, gbc);
        
        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 5, 0);
        JButton clearBtn = createButton("Clear Cart", DANGER_COLOR);
        clearBtn.addActionListener(e -> clearCart());
        panel.add(clearBtn, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JButton placeOrderBtn = createButton("Place Order", ACCENT_COLOR);
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        placeOrderBtn.setPreferredSize(new Dimension(0, 45));
        placeOrderBtn.addActionListener(e -> placeOrder());
        panel.add(placeOrderBtn, gbc);
        
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }
    
    private void setupEventHandlers() {
        // Category filter
        categoryComboBox.addActionListener(e -> {
            String category = (String) categoryComboBox.getSelectedItem();
            loadProducts(category);
        });
        
        // Cash received change calculation
        cashReceivedField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });
        
        // Payment method change
        paymentMethodCombo.addActionListener(e -> {
            boolean isCash = paymentMethodCombo.getSelectedItem() == PaymentMethod.CASH;
            cashReceivedField.setEnabled(isCash);
            if (!isCash) {
                cashReceivedField.setText("");
                changeLabel.setText("$0.00");
            }
        });
        
        // Cart table - remove item on double click
        cartTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = cartTable.getSelectedRow();
                    if (row >= 0) {
                        removeItemFromCart(row);
                    }
                }
            }
        });
    }
    
    private void loadProducts(String category) {
        productsPanel.removeAll();
        
        List<Product> products;
        if ("ALL".equals(category)) {
            products = productController.getAvailableProducts();
        } else {
            products = productController.getProductsByCategory(category);
        }
        
        for (Product product : products) {
            if (product.isAvailable()) {
                productsPanel.add(createProductCard(product));
            }
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Product info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(WHITE);
        
        JLabel nameLabel = new JLabel(product.getProductName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("$" + product.getPrice());
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        priceLabel.setForeground(ACCENT_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel categoryLabel = new JLabel(product.getCategory().name());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        categoryLabel.setForeground(Color.GRAY);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(categoryLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Add to cart on click
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addToCart(product);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
                infoPanel.setBackground(new Color(240, 248, 255));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(WHITE);
                infoPanel.setBackground(WHITE);
            }
        });
        
        return card;
    }
    
    private void addToCart(Product product) {
        // Check if product already in cart
        for (OrderItem item : cartItems) {
            if (item.getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + 1);
                updateCartTable();
                return;
            }
        }
        
        // Add new item
        OrderItem item = new OrderItem(
            product.getProductId(),
            product.getProductName(),
            1,
            product.getPrice()
        );
        cartItems.add(item);
        updateCartTable();
    }
    
    private void removeItemFromCart(int index) {
        if (index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
            updateCartTable();
        }
    }
    
    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (OrderItem item : cartItems) {
            Object[] row = {
                item.getProductName(),
                "$" + item.getUnitPrice(),
                item.getQuantity(),
                "$" + item.getSubtotal()
            };
            cartTableModel.addRow(row);
            subtotal = subtotal.add(item.getSubtotal());
        }
        
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
        BigDecimal grandTotal = subtotal.add(tax);
        
        totalLabel.setText("$" + subtotal);
        taxLabel.setText("$" + tax);
        grandTotalLabel.setText("$" + grandTotal);
        
        calculateChange();
    }
    
    private void calculateChange() {
        try {
            String cashText = cashReceivedField.getText().trim();
            if (cashText.isEmpty()) {
                changeLabel.setText("$0.00");
                return;
            }
            
            BigDecimal cash = new BigDecimal(cashText);
            String grandTotalText = grandTotalLabel.getText().replace("$", "");
            BigDecimal grandTotal = new BigDecimal(grandTotalText);
            
            BigDecimal change = cash.subtract(grandTotal);
            changeLabel.setText("$" + change);
            
            if (change.compareTo(BigDecimal.ZERO) < 0) {
                changeLabel.setForeground(DANGER_COLOR);
            } else {
                changeLabel.setForeground(ACCENT_COLOR);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Invalid");
            changeLabel.setForeground(DANGER_COLOR);
        }
    }
    
    private void clearCart() {
        if (cartItems.isEmpty()) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Clear all items from cart?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartTable();
        }
    }
    
    private void placeOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Cart is empty. Please add items to order.",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PaymentMethod paymentMethod = (PaymentMethod) paymentMethodCombo.getSelectedItem();
        BigDecimal cashReceived = null;
        
        if (paymentMethod == PaymentMethod.CASH) {
            try {
                cashReceived = new BigDecimal(cashReceivedField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter valid cash amount",
                    "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Create order
        Bill bill = orderController.createBill(currentUser, cartItems, paymentMethod, cashReceived);
        
        if (bill != null) {
            // Clear cart
            cartItems.clear();
            updateCartTable();
            cashReceivedField.setText("");
            
            // Show receipt dialog
            showReceipt(bill);
        }
    }
    
    private void showReceipt(Bill bill) {
        JDialog receiptDialog = new JDialog(this, "Order Receipt", true);
        receiptDialog.setSize(400, 500);
        receiptDialog.setLocationRelativeTo(this);
        
        JTextArea receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        receiptArea.setMargin(new Insets(10, 10, 10, 10));
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("================================\n");
        receipt.append("    RESTAURANT MANAGEMENT\n");
        receipt.append("================================\n\n");
        receipt.append("Bill No: ").append(bill.getBillNumber()).append("\n");
        receipt.append("Date: ").append(bill.getBilledAt()).append("\n");
        receipt.append("Cashier: ").append(bill.getBilledByUser()).append("\n");
        receipt.append("--------------------------------\n\n");
        
        for (OrderItem item : bill.getOrderItems()) {
            receipt.append(String.format("%-20s x%d\n", item.getProductName(), item.getQuantity()));
            receipt.append(String.format("  $%-8s    $%s\n", item.getUnitPrice(), item.getSubtotal()));
        }
        
        receipt.append("\n--------------------------------\n");
        receipt.append(String.format("Subtotal:        $%s\n", bill.getNetAmount()));
        receipt.append(String.format("Tax (10%%):       $%s\n", bill.getTaxAmount()));
        receipt.append(String.format("TOTAL:           $%s\n", bill.getTotalAmount()));
        receipt.append("\n");
        receipt.append("Payment: ").append(bill.getPaymentMethod().getDisplayName()).append("\n");
        
        if (bill.getCashReceived() != null) {
            receipt.append(String.format("Cash:            $%s\n", bill.getCashReceived()));
            receipt.append(String.format("Change:          $%s\n", bill.getChangeAmount()));
        }
        
        receipt.append("\n================================\n");
        receipt.append("   Thank you for your order!\n");
        receipt.append("================================\n");
        
        receiptArea.setText(receipt.toString());
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> receiptDialog.dispose());
        
        receiptDialog.setLayout(new BorderLayout());
        receiptDialog.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        receiptDialog.add(closeBtn, BorderLayout.SOUTH);
        receiptDialog.setVisible(true);
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
}
