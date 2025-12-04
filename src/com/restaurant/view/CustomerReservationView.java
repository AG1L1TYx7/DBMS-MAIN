package com.restaurant.view;

import com.restaurant.controller.ReservationController;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Customer Reservation View
 * Allows customers to view tables, make reservations, and manage their bookings
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class CustomerReservationView extends JFrame {
    
    private final ReservationController reservationController;
    
    // UI Components
    private JTabbedPane tabbedPane;
    
    // Make Reservation Tab
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField customerEmailField;
    private JSpinner dateSpinner;
    private JComboBox<String> timeComboBox;
    private JSpinner partySizeSpinner;
    private JTextArea specialRequestsArea;
    private JPanel tablesPanel;
    private JTable tablesTable;
    private DefaultTableModel tablesTableModel;
    private Integer selectedTableId = null;
    private JLabel selectedTableLabel;
    
    // My Reservations Tab
    private JTextField lookupPhoneField;
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color TABLE_AVAILABLE = new Color(46, 204, 113);
    private static final Color TABLE_RESERVED = new Color(231, 76, 60);
    private static final Color TABLE_SELECTED = new Color(52, 152, 219);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    
    public CustomerReservationView() {
        this.reservationController = new ReservationController();
        
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        setTitle("Restaurant - Table Reservations");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        
        tabbedPane.addTab("📅 Make a Reservation", createMakeReservationPanel());
        tabbedPane.addTab("📋 My Reservations", createMyReservationsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Left - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🍽️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel("Table Reservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        
        // Right - Back button
        JButton backBtn = new JButton("← Back to Menu");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> dispose());
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(backBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createMakeReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Left - Form
        JPanel formPanel = createReservationFormPanel();
        formPanel.setPreferredSize(new Dimension(350, 0));
        
        // Right - Table Selection
        JPanel tableSelectionPanel = createTableSelectionPanel();
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tableSelectionPanel);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.35);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReservationFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel formTitle = new JLabel("Reservation Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(20));
        
        // Customer Name
        panel.add(createFormLabel("Your Name *"));
        customerNameField = new JTextField();
        customerNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(customerNameField);
        panel.add(Box.createVerticalStrut(15));
        
        // Phone
        panel.add(createFormLabel("Phone Number *"));
        customerPhoneField = new JTextField();
        customerPhoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(customerPhoneField);
        panel.add(Box.createVerticalStrut(15));
        
        // Email
        panel.add(createFormLabel("Email (optional)"));
        customerEmailField = new JTextField();
        customerEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(customerEmailField);
        panel.add(Box.createVerticalStrut(15));
        
        // Date
        panel.add(createFormLabel("Reservation Date *"));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MMM dd, yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.util.Date.from(LocalDate.now().plusDays(1)
            .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        dateSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        dateSpinner.addChangeListener(e -> refreshAvailableTables());
        panel.add(dateSpinner);
        panel.add(Box.createVerticalStrut(15));
        
        // Time
        panel.add(createFormLabel("Reservation Time *"));
        String[] times = generateTimeSlots();
        timeComboBox = new JComboBox<>(times);
        timeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        timeComboBox.addActionListener(e -> refreshAvailableTables());
        panel.add(timeComboBox);
        panel.add(Box.createVerticalStrut(15));
        
        // Party Size
        panel.add(createFormLabel("Party Size *"));
        SpinnerNumberModel partySizeModel = new SpinnerNumberModel(2, 1, 20, 1);
        partySizeSpinner = new JSpinner(partySizeModel);
        partySizeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        partySizeSpinner.addChangeListener(e -> refreshAvailableTables());
        panel.add(partySizeSpinner);
        panel.add(Box.createVerticalStrut(15));
        
        // Special Requests
        panel.add(createFormLabel("Special Requests (optional)"));
        specialRequestsArea = new JTextArea(3, 20);
        specialRequestsArea.setLineWrap(true);
        specialRequestsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(specialRequestsArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(20));
        
        // Selected Table Display
        selectedTableLabel = new JLabel("No table selected");
        selectedTableLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        selectedTableLabel.setForeground(WARNING_COLOR);
        selectedTableLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(selectedTableLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Submit Button
        JButton submitBtn = new JButton("✓ Confirm Reservation");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBackground(ACCENT_COLOR);
        submitBtn.setForeground(WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> submitReservation());
        panel.add(submitBtn);
        
        return panel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private String[] generateTimeSlots() {
        String[] slots = new String[28]; // 11:00 AM to 10:30 PM (30 min intervals)
        LocalTime time = LocalTime.of(11, 0);
        for (int i = 0; i < slots.length; i++) {
            slots[i] = time.format(TIME_FORMATTER);
            time = time.plusMinutes(30);
        }
        return slots;
    }
    
    private JPanel createTableSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel title = new JLabel("🪑 Select a Table");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);
        
        // Tables Grid Panel
        tablesPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        tablesPanel.setBackground(WHITE);
        
        JScrollPane scrollPane = new JScrollPane(tablesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legendPanel.setBackground(WHITE);
        legendPanel.add(createLegendItem("Available", TABLE_AVAILABLE));
        legendPanel.add(createLegendItem("Reserved", TABLE_RESERVED));
        legendPanel.add(createLegendItem("Selected", TABLE_SELECTED));
        panel.add(legendPanel, BorderLayout.SOUTH);
        
        // Load tables
        refreshAvailableTables();
        
        return panel;
    }
    
    private JPanel createLegendItem(String label, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(WHITE);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(16, 16));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        panel.add(colorBox);
        panel.add(textLabel);
        return panel;
    }
    
    private void refreshAvailableTables() {
        tablesPanel.removeAll();
        selectedTableId = null;
        selectedTableLabel.setText("No table selected");
        selectedTableLabel.setForeground(WARNING_COLOR);
        
        LocalDate selectedDate = getSelectedDate();
        LocalTime selectedTime = getSelectedTime();
        int partySize = (int) partySizeSpinner.getValue();
        
        List<RestaurantTable> allTables = reservationController.getAllTables();
        List<RestaurantTable> availableTables = reservationController.getAvailableTables(
            selectedDate, selectedTime, partySize);
        
        for (RestaurantTable table : allTables) {
            boolean isAvailable = availableTables.stream()
                .anyMatch(t -> t.getTableId().equals(table.getTableId()));
            
            JPanel tableCard = createTableCard(table, isAvailable);
            tablesPanel.add(tableCard);
        }
        
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }
    
    private JPanel createTableCard(RestaurantTable table, boolean isAvailable) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(150, 120));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        Color bgColor = isAvailable ? TABLE_AVAILABLE : TABLE_RESERVED;
        card.setBackground(bgColor);
        
        // Table number
        JLabel numberLabel = new JLabel("Table " + table.getTableNumber(), SwingConstants.CENTER);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        numberLabel.setForeground(WHITE);
        card.add(numberLabel, BorderLayout.NORTH);
        
        // Capacity icon
        JLabel capacityLabel = new JLabel("👥 " + table.getCapacity() + " seats", SwingConstants.CENTER);
        capacityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        capacityLabel.setForeground(WHITE);
        card.add(capacityLabel, BorderLayout.CENTER);
        
        // Location
        JLabel locationLabel = new JLabel(table.getLocation() != null ? table.getLocation() : "", SwingConstants.CENTER);
        locationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        locationLabel.setForeground(new Color(255, 255, 255, 200));
        card.add(locationLabel, BorderLayout.SOUTH);
        
        if (isAvailable) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectTable(table, card);
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!table.getTableId().equals(selectedTableId)) {
                        card.setBackground(TABLE_AVAILABLE.darker());
                    }
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!table.getTableId().equals(selectedTableId)) {
                        card.setBackground(TABLE_AVAILABLE);
                    }
                }
            });
        } else {
            // Add "Reserved" overlay
            JLabel reservedLabel = new JLabel("RESERVED", SwingConstants.CENTER);
            reservedLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            reservedLabel.setForeground(WHITE);
            card.add(reservedLabel, BorderLayout.SOUTH);
        }
        
        return card;
    }
    
    private void selectTable(RestaurantTable table, JPanel card) {
        // Deselect previous
        for (Component comp : tablesPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBackground().equals(TABLE_SELECTED)) {
                    panel.setBackground(TABLE_AVAILABLE);
                }
            }
        }
        
        // Select new
        selectedTableId = table.getTableId();
        card.setBackground(TABLE_SELECTED);
        selectedTableLabel.setText("✓ Selected: Table " + table.getTableNumber() + 
                                   " (" + table.getCapacity() + " seats)");
        selectedTableLabel.setForeground(ACCENT_COLOR);
    }
    
    private LocalDate getSelectedDate() {
        java.util.Date date = (java.util.Date) dateSpinner.getValue();
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    private LocalTime getSelectedTime() {
        String timeStr = (String) timeComboBox.getSelectedItem();
        if (timeStr != null) {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        }
        return LocalTime.of(18, 0);
    }
    
    private void submitReservation() {
        String name = customerNameField.getText().trim();
        String phone = customerPhoneField.getText().trim();
        String email = customerEmailField.getText().trim();
        LocalDate date = getSelectedDate();
        LocalTime time = getSelectedTime();
        int partySize = (int) partySizeSpinner.getValue();
        String specialRequests = specialRequestsArea.getText().trim();
        
        if (selectedTableId == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a table for your reservation.",
                "No Table Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Reservation reservation = reservationController.createReservation(
            name, phone, email.isEmpty() ? null : email, 
            selectedTableId, date, time, partySize, 
            specialRequests.isEmpty() ? null : specialRequests);
        
        if (reservation != null) {
            // Clear form
            clearReservationForm();
            refreshAvailableTables();
        }
    }
    
    private void clearReservationForm() {
        customerNameField.setText("");
        customerPhoneField.setText("");
        customerEmailField.setText("");
        specialRequestsArea.setText("");
        partySizeSpinner.setValue(2);
        selectedTableId = null;
        selectedTableLabel.setText("No table selected");
        selectedTableLabel.setForeground(WARNING_COLOR);
    }
    
    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel searchLabel = new JLabel("Enter your phone number to view reservations:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        lookupPhoneField = new JTextField(15);
        lookupPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton searchBtn = new JButton("🔍 Find My Reservations");
        searchBtn.setBackground(PRIMARY_COLOR);
        searchBtn.setForeground(WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchReservations());
        
        searchPanel.add(searchLabel);
        searchPanel.add(lookupPhoneField);
        searchPanel.add(searchBtn);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Reservations Table
        String[] columns = {"ID", "Date", "Time", "Table", "Party Size", "Status", "Special Requests"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setRowHeight(35);
        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reservationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Status column renderer
        reservationsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if (status.equals("CONFIRMED")) {
                    c.setForeground(ACCENT_COLOR);
                } else if (status.equals("CANCELLED")) {
                    c.setForeground(DANGER_COLOR);
                } else if (status.equals("COMPLETED")) {
                    c.setForeground(PRIMARY_COLOR);
                } else {
                    c.setForeground(WARNING_COLOR);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton cancelBtn = new JButton("❌ Cancel Reservation");
        cancelBtn.setBackground(DANGER_COLOR);
        cancelBtn.setForeground(WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> cancelSelectedReservation());
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(PRIMARY_COLOR);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> searchReservations());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void searchReservations() {
        String phone = lookupPhoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your phone number.",
                "Phone Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        reservationsTableModel.setRowCount(0);
        
        List<Reservation> reservations = reservationController.getReservationsByPhone(phone);
        
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No reservations found for this phone number.",
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        for (Reservation res : reservations) {
            reservationsTableModel.addRow(new Object[]{
                res.getReservationId(),
                res.getReservationDate() != null ? res.getReservationDate().format(DATE_FORMATTER) : "",
                res.getReservationTime() != null ? res.getReservationTime().format(TIME_FORMATTER) : "",
                "Table " + (res.getTableNumber() != null ? res.getTableNumber() : res.getTableId()),
                res.getNumberOfGuests(),
                res.getStatus().name(),
                res.getSpecialRequests() != null ? res.getSpecialRequests() : ""
            });
        }
    }
    
    private void cancelSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to cancel.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        String status = (String) reservationsTableModel.getValueAt(selectedRow, 5);
        
        if (status.equals("CANCELLED") || status.equals("COMPLETED") || status.equals("NO_SHOW")) {
            JOptionPane.showMessageDialog(this,
                "This reservation cannot be cancelled.",
                "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (reservationController.cancelReservation(reservationId)) {
                JOptionPane.showMessageDialog(this,
                    "Reservation cancelled successfully.",
                    "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                searchReservations();
            }
        }
    }
}
