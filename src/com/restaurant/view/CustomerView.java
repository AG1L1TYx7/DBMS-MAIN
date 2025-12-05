package com.restaurant.view;

import com.restaurant.controller.CustomerController;
import com.restaurant.model.Customer;
import com.restaurant.model.Customer.MembershipTier;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

/**
 * Customer View - Customer Profile and Loyalty Points Management
 * Allows customers to view their profile, loyalty points, and membership status
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class CustomerView extends JFrame {
    
    private final CustomerController customerController;
    
    // UI Components
    private JTabbedPane tabbedPane;
    
    // Lookup Tab
    private JTextField phoneSearchField;
    private JButton searchButton;
    
    // Profile Display
    private JLabel customerNameLabel;
    private JLabel customerPhoneLabel;
    private JLabel customerEmailLabel;
    private JLabel memberSinceLabel;
    
    // Loyalty Points Display
    private JLabel loyaltyPointsLabel;
    private JLabel membershipTierLabel;
    private JPanel totalSpentPanel;
    private JPanel visitCountPanel;
    private JPanel lastVisitPanel;
    private JLabel discountPercentLabel;
    private JProgressBar tierProgressBar;
    private JLabel nextTierLabel;
    
    // Points Redemption
    private JSpinner redeemPointsSpinner;
    private JLabel redeemValueLabel;
    private JButton redeemButton;
    
    // Transaction History
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    
    // Current Customer
    private Customer currentCustomer = null;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color GOLD_COLOR = new Color(241, 196, 15);
    private static final Color SILVER_COLOR = new Color(149, 165, 166);
    private static final Color BRONZE_COLOR = new Color(205, 127, 50);
    private static final Color PLATINUM_COLOR = new Color(229, 228, 226);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color DARK_TEXT = new Color(44, 62, 80);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    
    public CustomerView() {
        this.customerController = new CustomerController();
        
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        setTitle("Customer Portal - Loyalty Program");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search Panel at top
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        
        // Tabbed content
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(WHITE);
        
        tabbedPane.addTab("🎯 My Loyalty Points", createLoyaltyPanel());
        tabbedPane.addTab("👤 My Profile", createProfilePanel());
        tabbedPane.addTab("📜 History", createHistoryPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Left - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("⭐");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel("Customer Loyalty Program");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        // Right - Close button
        JButton closeBtn = new JButton("← Back");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(closeBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel label = new JLabel("Enter your phone number:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        phoneSearchField = new JTextField(15);
        phoneSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneSearchField.setPreferredSize(new Dimension(200, 35));
        phoneSearchField.addActionListener(e -> searchCustomer());
        
        searchButton = new JButton("🔍 Find My Account");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> searchCustomer());
        
        panel.add(label);
        panel.add(phoneSearchField);
        panel.add(searchButton);
        
        return panel;
    }
    
    private JPanel createLoyaltyPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top - Points Overview Card
        JPanel pointsCard = createPointsOverviewCard();
        
        // Center - Membership Status
        JPanel membershipCard = createMembershipCard();
        
        // Bottom - Redeem Points
        JPanel redeemCard = createRedeemCard();
        
        // Layout
        JPanel topSection = new JPanel(new GridLayout(1, 2, 15, 0));
        topSection.setOpaque(false);
        topSection.add(pointsCard);
        topSection.add(membershipCard);
        
        panel.add(topSection, BorderLayout.NORTH);
        panel.add(redeemCard, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPointsOverviewCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("🎯 Your Loyalty Points");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_TEXT);
        
        // Points Display
        loyaltyPointsLabel = new JLabel("---");
        loyaltyPointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        loyaltyPointsLabel.setForeground(PRIMARY_COLOR);
        loyaltyPointsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel pointsSubLabel = new JLabel("Available Points");
        pointsSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pointsSubLabel.setForeground(Color.GRAY);
        pointsSubLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(loyaltyPointsLabel, BorderLayout.CENTER);
        centerPanel.add(pointsSubLabel, BorderLayout.SOUTH);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMembershipCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("🏆 Membership Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_TEXT);
        
        // Tier Display
        membershipTierLabel = new JLabel("---");
        membershipTierLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        membershipTierLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        discountPercentLabel = new JLabel("0% discount");
        discountPercentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discountPercentLabel.setForeground(ACCENT_COLOR);
        discountPercentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Progress to next tier
        tierProgressBar = new JProgressBar(0, 100);
        tierProgressBar.setStringPainted(true);
        tierProgressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tierProgressBar.setForeground(ACCENT_COLOR);
        
        nextTierLabel = new JLabel("Login to view progress");
        nextTierLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        nextTierLabel.setForeground(Color.GRAY);
        nextTierLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        membershipTierLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        discountPercentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tierProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextTierLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(membershipTierLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(discountPercentLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(tierProgressBar);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(nextTierLabel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRedeemCard() {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("💰 Redeem Points for Discount");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_TEXT);
        
        // Redemption Form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        formPanel.setOpaque(false);
        
        JLabel pointsLabel = new JLabel("Points to redeem:");
        pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(100, 100, 10000, 100);
        redeemPointsSpinner = new JSpinner(spinnerModel);
        redeemPointsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        redeemPointsSpinner.setPreferredSize(new Dimension(100, 35));
        redeemPointsSpinner.addChangeListener(e -> updateRedeemValue());
        
        JLabel equalsLabel = new JLabel("=");
        equalsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        redeemValueLabel = new JLabel("$1.00 discount");
        redeemValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        redeemValueLabel.setForeground(ACCENT_COLOR);
        
        redeemButton = new JButton("Redeem Now");
        redeemButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        redeemButton.setBackground(ACCENT_COLOR);
        redeemButton.setForeground(WHITE);
        redeemButton.setFocusPainted(false);
        redeemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        redeemButton.setEnabled(false);
        redeemButton.addActionListener(e -> redeemPoints());
        
        formPanel.add(pointsLabel);
        formPanel.add(redeemPointsSpinner);
        formPanel.add(equalsLabel);
        formPanel.add(redeemValueLabel);
        formPanel.add(Box.createHorizontalStrut(20));
        formPanel.add(redeemButton);
        
        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 20, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        totalSpentPanel = createInfoLabel("Total Spent:", "---");
        visitCountPanel = createInfoLabel("Total Visits:", "---");
        lastVisitPanel = createInfoLabel("Last Visit:", "---");
        JPanel conversionInfo = createInfoLabel("Conversion:", "100 pts = $1.00");
        
        infoPanel.add(totalSpentPanel);
        infoPanel.add(visitCountPanel);
        infoPanel.add(lastVisitPanel);
        infoPanel.add(conversionInfo);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComp.setForeground(Color.GRAY);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valueComp.setForeground(DARK_TEXT);
        
        panel.add(labelComp);
        panel.add(valueComp);
        
        return panel;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel profileCard = new JPanel(new GridBagLayout());
        profileCard.setBackground(WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Profile Icon
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        profileCard.add(iconLabel, gbc);
        
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        
        // Name
        gbc.gridy = 0;
        profileCard.add(createProfileField("Name:", customerNameLabel = new JLabel("---")), gbc);
        
        // Phone
        gbc.gridy = 1;
        profileCard.add(createProfileField("Phone:", customerPhoneLabel = new JLabel("---")), gbc);
        
        // Email
        gbc.gridy = 2;
        profileCard.add(createProfileField("Email:", customerEmailLabel = new JLabel("---")), gbc);
        
        // Member Since
        gbc.gridy = 3;
        profileCard.add(createProfileField("Member Since:", memberSinceLabel = new JLabel("---")), gbc);
        
        panel.add(profileCard, BorderLayout.NORTH);
        
        // Tier Benefits
        JPanel benefitsPanel = createTierBenefitsPanel();
        panel.add(benefitsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProfileField(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(Color.GRAY);
        labelComp.setPreferredSize(new Dimension(120, 25));
        
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(DARK_TEXT);
        
        panel.add(labelComp);
        panel.add(valueLabel);
        
        return panel;
    }
    
    private JPanel createTierBenefitsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("🎁 Membership Tier Benefits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_TEXT);
        
        // Benefits Table
        String[] columns = {"Tier", "Spending Required", "Discount", "Benefits"};
        Object[][] data = {
            {"🥉 Bronze", "$0 - $14,999", "5%", "Basic rewards, Birthday bonus"},
            {"🥈 Silver", "$15,000 - $39,999", "8%", "Priority reservations, Double points days"},
            {"🥇 Gold", "$40,000 - $79,999", "12%", "Exclusive events, Free dessert monthly"},
            {"💎 Platinum", "$80,000+", "15%", "VIP access, Personal concierge, All benefits"}
        };
        
        JTable benefitsTable = new JTable(data, columns);
        benefitsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        benefitsTable.setRowHeight(35);
        benefitsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        benefitsTable.setEnabled(false);
        
        JScrollPane scrollPane = new JScrollPane(benefitsTable);
        scrollPane.setPreferredSize(new Dimension(0, 180));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📜 Points Transaction History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_TEXT);
        
        // Table
        String[] columns = {"Date", "Type", "Points", "Description"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(historyTableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.setRowHeight(30);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBackground(WHITE);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void searchCustomer() {
        String phone = phoneSearchField.getText().trim();
        
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your phone number",
                "Phone Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentCustomer = customerController.findCustomerByPhone(phone);
        
        if (currentCustomer != null) {
            updateCustomerDisplay();
            JOptionPane.showMessageDialog(this,
                "Welcome back, " + currentCustomer.getFullName() + "!",
                "Account Found",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            clearCustomerDisplay();
            JOptionPane.showMessageDialog(this,
                "No account found with this phone number.\nPlease register at our front desk.",
                "Account Not Found",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updateCustomerDisplay() {
        if (currentCustomer == null) return;
        
        // Update Points Display
        int points = currentCustomer.getLoyaltyPoints() != null ? currentCustomer.getLoyaltyPoints() : 0;
        loyaltyPointsLabel.setText(String.format("%,d", points));
        
        // Update Membership Tier
        MembershipTier tier = currentCustomer.getMembershipTier();
        membershipTierLabel.setText(getTierEmoji(tier) + " " + tier.getDisplayName());
        membershipTierLabel.setForeground(getTierColor(tier));
        discountPercentLabel.setText(tier.getDiscountPercentage() + "% discount on all orders");
        
        // Update Progress Bar
        updateTierProgress();
        
        // Update Stats
        BigDecimal totalSpent = currentCustomer.getTotalSpent() != null ? currentCustomer.getTotalSpent() : BigDecimal.ZERO;
        ((JLabel)totalSpentPanel.getComponent(1)).setText(CURRENCY_FORMAT.format(totalSpent));
        
        int visits = currentCustomer.getVisitCount() != null ? currentCustomer.getVisitCount() : 0;
        ((JLabel)visitCountPanel.getComponent(1)).setText(String.valueOf(visits));
        
        String lastVisit = currentCustomer.getLastVisitDate() != null ? 
            currentCustomer.getLastVisitDate().format(DATE_FORMATTER) : "Never";
        ((JLabel)lastVisitPanel.getComponent(1)).setText(lastVisit);
        
        // Update Profile Tab
        customerNameLabel.setText(currentCustomer.getFullName());
        customerPhoneLabel.setText(currentCustomer.getPhone());
        customerEmailLabel.setText(currentCustomer.getEmail() != null ? currentCustomer.getEmail() : "Not provided");
        memberSinceLabel.setText(currentCustomer.getRegistrationDate() != null ? 
            currentCustomer.getRegistrationDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")) : "---");
        
        // Enable redemption
        redeemButton.setEnabled(points >= 100);
        
        // Update spinner max
        SpinnerNumberModel model = (SpinnerNumberModel) redeemPointsSpinner.getModel();
        model.setMaximum(Math.max(100, points));
        
        // Load history (sample data)
        loadTransactionHistory();
    }
    
    private void updateTierProgress() {
        if (currentCustomer == null) return;
        
        MembershipTier currentTier = currentCustomer.getMembershipTier();
        BigDecimal totalSpent = currentCustomer.getTotalSpent() != null ? currentCustomer.getTotalSpent() : BigDecimal.ZERO;
        int spent = totalSpent.intValue();
        
        if (currentTier == MembershipTier.PLATINUM) {
            tierProgressBar.setValue(100);
            tierProgressBar.setString("Maximum tier reached!");
            nextTierLabel.setText("🎉 You've reached the highest tier!");
        } else {
            MembershipTier nextTier = getNextTier(currentTier);
            int currentMin = currentTier.getMinSpending();
            int nextMin = nextTier.getMinSpending();
            
            int progress = (int) (((double)(spent - currentMin) / (nextMin - currentMin)) * 100);
            progress = Math.max(0, Math.min(100, progress));
            
            tierProgressBar.setValue(progress);
            tierProgressBar.setString(progress + "% to " + nextTier.getDisplayName());
            
            int remaining = nextMin - spent;
            nextTierLabel.setText("Spend $" + String.format("%,d", remaining) + " more to reach " + nextTier.getDisplayName());
        }
    }
    
    private MembershipTier getNextTier(MembershipTier current) {
        return switch (current) {
            case BRONZE -> MembershipTier.SILVER;
            case SILVER -> MembershipTier.GOLD;
            case GOLD -> MembershipTier.PLATINUM;
            case PLATINUM -> MembershipTier.PLATINUM;
        };
    }
    
    private String getTierEmoji(MembershipTier tier) {
        return switch (tier) {
            case BRONZE -> "🥉";
            case SILVER -> "🥈";
            case GOLD -> "🥇";
            case PLATINUM -> "💎";
        };
    }
    
    private Color getTierColor(MembershipTier tier) {
        return switch (tier) {
            case BRONZE -> BRONZE_COLOR;
            case SILVER -> SILVER_COLOR;
            case GOLD -> GOLD_COLOR;
            case PLATINUM -> PRIMARY_COLOR;
        };
    }
    
    private void clearCustomerDisplay() {
        currentCustomer = null;
        loyaltyPointsLabel.setText("---");
        membershipTierLabel.setText("---");
        membershipTierLabel.setForeground(DARK_TEXT);
        discountPercentLabel.setText("0% discount");
        tierProgressBar.setValue(0);
        tierProgressBar.setString("Login to view progress");
        nextTierLabel.setText("Login to view progress");
        
        ((JLabel)totalSpentPanel.getComponent(1)).setText("---");
        ((JLabel)visitCountPanel.getComponent(1)).setText("---");
        ((JLabel)lastVisitPanel.getComponent(1)).setText("---");
        
        customerNameLabel.setText("---");
        customerPhoneLabel.setText("---");
        customerEmailLabel.setText("---");
        memberSinceLabel.setText("---");
        
        redeemButton.setEnabled(false);
        historyTableModel.setRowCount(0);
    }
    
    private void updateRedeemValue() {
        int points = (Integer) redeemPointsSpinner.getValue();
        double value = points / 100.0; // 100 points = $1
        redeemValueLabel.setText(String.format("$%.2f discount", value));
    }
    
    private void redeemPoints() {
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this,
                "Please login first",
                "Not Logged In",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int pointsToRedeem = (Integer) redeemPointsSpinner.getValue();
        int availablePoints = currentCustomer.getLoyaltyPoints() != null ? currentCustomer.getLoyaltyPoints() : 0;
        
        if (pointsToRedeem > availablePoints) {
            JOptionPane.showMessageDialog(this,
                "You don't have enough points.\nAvailable: " + availablePoints,
                "Insufficient Points",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Redeem " + pointsToRedeem + " points for $" + String.format("%.2f", pointsToRedeem / 100.0) + " discount?",
            "Confirm Redemption",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            BigDecimal discount = customerController.redeemLoyaltyPoints(
                currentCustomer.getCustomerId(), pointsToRedeem);
            
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                JOptionPane.showMessageDialog(this,
                    "Successfully redeemed " + pointsToRedeem + " points!\n" +
                    "Your discount code: LOYALTY" + System.currentTimeMillis() % 10000 + "\n" +
                    "Value: $" + String.format("%.2f", discount),
                    "Redemption Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh customer data
                currentCustomer = customerController.getCustomerById(currentCustomer.getCustomerId());
                updateCustomerDisplay();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Redemption failed. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadTransactionHistory() {
        historyTableModel.setRowCount(0);
        
        // Sample transaction history (in real app, this would come from database)
        if (currentCustomer != null && currentCustomer.getVisitCount() != null && currentCustomer.getVisitCount() > 0) {
            // Add sample entries
            historyTableModel.addRow(new Object[]{"Dec 01, 2025", "Earned", "+150", "Order #1234"});
            historyTableModel.addRow(new Object[]{"Nov 28, 2025", "Earned", "+200", "Order #1230"});
            historyTableModel.addRow(new Object[]{"Nov 25, 2025", "Redeemed", "-500", "Discount applied"});
            historyTableModel.addRow(new Object[]{"Nov 20, 2025", "Earned", "+175", "Order #1225"});
            historyTableModel.addRow(new Object[]{"Nov 15, 2025", "Bonus", "+100", "Birthday reward"});
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CustomerView().setVisible(true);
        });
    }
}
