package com.restaurant.view;

import com.restaurant.controller.ScheduleController;
import com.restaurant.model.Schedule;
import com.restaurant.model.Schedule.ShiftType;
import com.restaurant.model.LeaveRequest;
import com.restaurant.model.LeaveRequest.LeaveStatus;
import com.restaurant.model.LeaveRequest.LeaveType;
import com.restaurant.model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Schedule View - Calendar view for employees to see their schedules and submit leave requests
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class ScheduleView extends JPanel {
    
    private final User currentUser;
    private final ScheduleController scheduleController;
    
    // Calendar components
    private JLabel monthYearLabel;
    private JPanel calendarGrid;
    private LocalDate currentMonth;
    private Map<LocalDate, List<Schedule>> scheduleMap;
    private Map<LocalDate, List<LeaveRequest>> leaveMap;
    
    // Leave request table
    private JTable leaveTable;
    private DefaultTableModel leaveTableModel;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color SCHEDULE_COLOR = new Color(52, 152, 219, 180);
    private static final Color LEAVE_COLOR = new Color(155, 89, 182, 180);
    private static final Color TODAY_COLOR = new Color(46, 204, 113, 50);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public ScheduleView(User user) {
        this.currentUser = user;
        this.scheduleController = new ScheduleController();
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        this.scheduleMap = new HashMap<>();
        this.leaveMap = new HashMap<>();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        initializeUI();
        loadScheduleData();
    }
    
    private void initializeUI() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content - split between calendar and leave requests
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.7);
        
        // Calendar Panel
        splitPane.setLeftComponent(createCalendarPanel());
        
        // Leave Requests Panel
        splitPane.setRightComponent(createLeaveRequestsPanel());
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📅 My Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        JButton requestLeaveBtn = new JButton("📝 Request Leave");
        requestLeaveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        requestLeaveBtn.setForeground(WHITE);
        requestLeaveBtn.setBackground(ACCENT_COLOR);
        requestLeaveBtn.setBorderPainted(false);
        requestLeaveBtn.setFocusPainted(false);
        requestLeaveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        requestLeaveBtn.addActionListener(e -> showLeaveRequestDialog());
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(requestLeaveBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(WHITE);
        
        JButton prevBtn = new JButton("◀ Previous");
        prevBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevBtn.addActionListener(e -> navigateMonth(-1));
        
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        monthYearLabel.setForeground(PRIMARY_COLOR);
        updateMonthYearLabel();
        
        JButton nextBtn = new JButton("Next ▶");
        nextBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextBtn.addActionListener(e -> navigateMonth(1));
        
        navPanel.add(prevBtn, BorderLayout.WEST);
        navPanel.add(monthYearLabel, BorderLayout.CENTER);
        navPanel.add(nextBtn, BorderLayout.EAST);
        
        panel.add(navPanel, BorderLayout.NORTH);
        
        // Day headers
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 2, 2));
        headerPanel.setBackground(WHITE);
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(PRIMARY_COLOR);
            label.setBorder(new EmptyBorder(5, 0, 5, 0));
            headerPanel.add(label);
        }
        
        // Calendar grid
        calendarGrid = new JPanel(new GridLayout(6, 7, 2, 2));
        calendarGrid.setBackground(WHITE);
        
        JPanel calendarWrapper = new JPanel(new BorderLayout(0, 5));
        calendarWrapper.setBackground(WHITE);
        calendarWrapper.add(headerPanel, BorderLayout.NORTH);
        calendarWrapper.add(calendarGrid, BorderLayout.CENTER);
        
        panel.add(calendarWrapper, BorderLayout.CENTER);
        
        // Legend
        panel.add(createLegendPanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        panel.add(createLegendItem("📅 Scheduled", SCHEDULE_COLOR));
        panel.add(createLegendItem("🏖️ On Leave", LEAVE_COLOR));
        panel.add(createLegendItem("📍 Today", TODAY_COLOR));
        
        return panel;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(WHITE);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(16, 16));
        colorBox.setBackground(color);
        colorBox.setBorder(new LineBorder(color.darker(), 1));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        item.add(colorBox);
        item.add(label);
        
        return item;
    }
    
    private JPanel createLeaveRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("📋 My Leave Requests");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Leave requests table
        String[] columns = {"Type", "From", "To", "Days", "Status"};
        leaveTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        leaveTable = new JTable(leaveTableModel);
        leaveTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leaveTable.setRowHeight(30);
        leaveTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Custom renderer for status
        leaveTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                
                if (value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "PENDING" -> label.setForeground(WARNING_COLOR);
                        case "APPROVED" -> label.setForeground(ACCENT_COLOR);
                        case "REJECTED" -> label.setForeground(DANGER_COLOR);
                        default -> label.setForeground(Color.GRAY);
                    }
                }
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(leaveTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        // Cancel button for pending requests
        JButton cancelBtn = new JButton("❌ Cancel Selected");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setForeground(WHITE);
        cancelBtn.setBackground(DANGER_COLOR);
        cancelBtn.setBorderPainted(false);
        cancelBtn.addActionListener(e -> cancelSelectedLeaveRequest());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(cancelBtn);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateMonthYearLabel() {
        String monthYear = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) 
                          + " " + currentMonth.getYear();
        monthYearLabel.setText(monthYear);
    }
    
    private void navigateMonth(int offset) {
        currentMonth = currentMonth.plusMonths(offset);
        updateMonthYearLabel();
        loadScheduleData();
    }
    
    private void loadScheduleData() {
        // Clear maps
        scheduleMap.clear();
        leaveMap.clear();
        
        // Load schedules for the month
        int year = currentMonth.getYear();
        int month = currentMonth.getMonthValue();
        
        List<Schedule> schedules = scheduleController.getSchedulesByUserAndDateRange(
            currentUser.getUserId(), 
            currentMonth, 
            currentMonth.plusMonths(1).minusDays(1)
        );
        
        for (Schedule schedule : schedules) {
            scheduleMap.computeIfAbsent(schedule.getScheduleDate(), k -> new ArrayList<>()).add(schedule);
        }
        
        // Load leave requests
        List<LeaveRequest> leaves = scheduleController.getLeaveRequestsByUserId(currentUser.getUserId());
        for (LeaveRequest leave : leaves) {
            if (leave.getStatus() == LeaveStatus.APPROVED || leave.getStatus() == LeaveStatus.PENDING) {
                LocalDate date = leave.getStartDate();
                while (!date.isAfter(leave.getEndDate())) {
                    leaveMap.computeIfAbsent(date, k -> new ArrayList<>()).add(leave);
                    date = date.plusDays(1);
                }
            }
        }
        
        // Refresh calendar
        refreshCalendar();
        
        // Refresh leave table
        refreshLeaveTable(leaves);
    }
    
    private void refreshCalendar() {
        calendarGrid.removeAll();
        
        LocalDate firstOfMonth = currentMonth;
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // Monday = 1
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();
        
        // Add empty cells for days before the first of month
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.add(createEmptyDayCell());
        }
        
        // Add day cells
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            calendarGrid.add(createDayCell(date, date.equals(today)));
        }
        
        // Add empty cells to complete the grid
        int totalCells = (dayOfWeek - 1) + daysInMonth;
        int remainingCells = 42 - totalCells; // 6 rows * 7 days
        for (int i = 0; i < remainingCells; i++) {
            calendarGrid.add(createEmptyDayCell());
        }
        
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
    
    private JPanel createEmptyDayCell() {
        JPanel cell = new JPanel();
        cell.setBackground(new Color(245, 245, 245));
        cell.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        return cell;
    }
    
    private JPanel createDayCell(LocalDate date, boolean isToday) {
        JPanel cell = new JPanel(new BorderLayout(2, 2));
        cell.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Background color
        if (isToday) {
            cell.setBackground(TODAY_COLOR);
        } else {
            cell.setBackground(WHITE);
        }
        
        // Day number
        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dayLabel.setBorder(new EmptyBorder(3, 5, 0, 0));
        cell.add(dayLabel, BorderLayout.NORTH);
        
        // Events panel
        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setOpaque(false);
        
        // Check for schedules
        List<Schedule> daySchedules = scheduleMap.get(date);
        if (daySchedules != null && !daySchedules.isEmpty()) {
            for (Schedule schedule : daySchedules) {
                JLabel scheduleLabel = new JLabel("📅 " + schedule.getStartTime().format(TIME_FORMATTER));
                scheduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                scheduleLabel.setForeground(PRIMARY_COLOR);
                scheduleLabel.setOpaque(true);
                scheduleLabel.setBackground(SCHEDULE_COLOR);
                scheduleLabel.setBorder(new EmptyBorder(1, 3, 1, 3));
                eventsPanel.add(scheduleLabel);
                eventsPanel.add(Box.createVerticalStrut(2));
            }
        }
        
        // Check for leaves
        List<LeaveRequest> dayLeaves = leaveMap.get(date);
        if (dayLeaves != null && !dayLeaves.isEmpty()) {
            LeaveRequest leave = dayLeaves.get(0);
            String icon = leave.getStatus() == LeaveStatus.APPROVED ? "🏖️" : "⏳";
            JLabel leaveLabel = new JLabel(icon + " Leave");
            leaveLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            leaveLabel.setForeground(new Color(142, 68, 173));
            leaveLabel.setOpaque(true);
            leaveLabel.setBackground(LEAVE_COLOR);
            leaveLabel.setBorder(new EmptyBorder(1, 3, 1, 3));
            eventsPanel.add(leaveLabel);
        }
        
        cell.add(eventsPanel, BorderLayout.CENTER);
        
        // Click handler to show details
        cell.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showDayDetails(date);
            }
        });
        
        return cell;
    }
    
    private void showDayDetails(LocalDate date) {
        StringBuilder details = new StringBuilder();
        details.append("📅 ").append(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))).append("\n\n");
        
        List<Schedule> schedules = scheduleMap.get(date);
        if (schedules != null && !schedules.isEmpty()) {
            details.append("🕐 SCHEDULES:\n");
            for (Schedule s : schedules) {
                details.append("   • ").append(s.getShiftType().getDisplayName())
                       .append(": ").append(s.getStartTime().format(TIME_FORMATTER))
                       .append(" - ").append(s.getEndTime().format(TIME_FORMATTER)).append("\n");
                if (s.getNotes() != null && !s.getNotes().isEmpty()) {
                    details.append("     Note: ").append(s.getNotes()).append("\n");
                }
            }
            details.append("\n");
        }
        
        List<LeaveRequest> leaves = leaveMap.get(date);
        if (leaves != null && !leaves.isEmpty()) {
            details.append("🏖️ LEAVE:\n");
            for (LeaveRequest l : leaves) {
                details.append("   • ").append(l.getLeaveType().getDisplayName())
                       .append(" (").append(l.getStatus().getDisplayName()).append(")\n");
            }
        }
        
        if (schedules == null && leaves == null) {
            details.append("No schedules or leave for this day.");
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), 
            "Schedule Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshLeaveTable(List<LeaveRequest> leaves) {
        leaveTableModel.setRowCount(0);
        
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd");
        
        for (LeaveRequest leave : leaves) {
            Object[] row = {
                leave.getLeaveType().getDisplayName(),
                leave.getStartDate().format(df),
                leave.getEndDate().format(df),
                leave.getTotalDays(),
                leave.getStatus().name()
            };
            leaveTableModel.addRow(row);
        }
    }
    
    private void showLeaveRequestDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Request Leave", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Leave Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Leave Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<LeaveType> typeCombo = new JComboBox<>(LeaveType.values());
        formPanel.add(typeCombo, gbc);
        
        // Start Date
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        startDateSpinner.setValue(java.util.Date.from(LocalDate.now().plusDays(1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        formPanel.add(startDateSpinner, gbc);
        
        // End Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endEditor);
        endDateSpinner.setValue(java.util.Date.from(LocalDate.now().plusDays(1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        formPanel.add(endDateSpinner, gbc);
        
        // Reason
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea reasonArea = new JTextArea(5, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        formPanel.add(reasonScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(WHITE);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton submitBtn = new JButton("Submit Request");
        submitBtn.setBackground(ACCENT_COLOR);
        submitBtn.setForeground(WHITE);
        submitBtn.addActionListener(e -> {
            LeaveType type = (LeaveType) typeCombo.getSelectedItem();
            LocalDate startDate = ((java.util.Date) startDateSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = ((java.util.Date) endDateSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String reason = reasonArea.getText().trim();
            
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please provide a reason for leave.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(dialog, "End date cannot be before start date.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (startDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(dialog, "Start date cannot be in the past.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check for overlapping
            if (scheduleController.hasOverlappingLeave(currentUser.getUserId(), startDate, endDate)) {
                JOptionPane.showMessageDialog(dialog, 
                    "You already have a leave request for these dates.",
                    "Overlap Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LeaveRequest request = scheduleController.submitLeaveRequest(
                currentUser.getUserId(), type, startDate, endDate, reason);
            
            if (request != null) {
                JOptionPane.showMessageDialog(dialog, 
                    "Leave request submitted successfully!\nYour request is pending admin approval.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadScheduleData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to submit leave request.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void cancelSelectedLeaveRequest() {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request to cancel.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String status = (String) leaveTableModel.getValueAt(selectedRow, 4);
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "Only pending leave requests can be cancelled.",
                "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this leave request?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Get the leave request from the user's list
            List<LeaveRequest> leaves = scheduleController.getLeaveRequestsByUserId(currentUser.getUserId());
            if (selectedRow < leaves.size()) {
                boolean success = scheduleController.cancelLeaveRequest(leaves.get(selectedRow).getLeaveId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Leave request cancelled.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadScheduleData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel leave request.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Refresh the view
     */
    public void refresh() {
        loadScheduleData();
    }
}
