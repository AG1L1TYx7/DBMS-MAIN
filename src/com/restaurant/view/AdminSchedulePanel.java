package com.restaurant.view;

import com.restaurant.controller.ScheduleController;
import com.restaurant.controller.UserController;
import com.restaurant.controller.AuthenticationController;
import com.restaurant.model.Schedule;
import com.restaurant.model.Schedule.ShiftType;
import com.restaurant.model.LeaveRequest;
import com.restaurant.model.LeaveRequest.LeaveStatus;
import com.restaurant.model.User;
import com.restaurant.model.User.UserRole;

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
 * Admin Schedule Management Panel - For admins to create and manage employee schedules
 * 
 * @author Restaurant Management System
 * @version 1.0
 */
public class AdminSchedulePanel extends JPanel {
    
    private final ScheduleController scheduleController;
    private final UserController userController;
    
    // Calendar components
    private JLabel monthYearLabel;
    private JPanel calendarGrid;
    private LocalDate currentMonth;
    private Map<LocalDate, List<Schedule>> scheduleMap;
    
    // Tables
    private JTable scheduleTable;
    private DefaultTableModel scheduleTableModel;
    private JTable leaveTable;
    private DefaultTableModel leaveTableModel;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color SERVER_COLOR = new Color(52, 152, 219, 180);
    private static final Color CHEF_COLOR = new Color(230, 126, 34, 180);
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public AdminSchedulePanel() {
        this.scheduleController = new ScheduleController();
        this.userController = new UserController();
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        this.scheduleMap = new HashMap<>();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content - tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tabs.addTab("📅 Calendar View", createCalendarPanel());
        tabs.addTab("📋 Schedule List", createScheduleListPanel());
        tabs.addTab("📝 Leave Requests", createLeaveRequestsPanel());
        
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📅 Employee Schedule Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton addScheduleBtn = new JButton("➕ Add Schedule");
        addScheduleBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addScheduleBtn.setForeground(WHITE);
        addScheduleBtn.setBackground(ACCENT_COLOR);
        addScheduleBtn.setBorderPainted(false);
        addScheduleBtn.setFocusPainted(false);
        addScheduleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addScheduleBtn.addActionListener(e -> showAddScheduleDialog(null));
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setForeground(WHITE);
        refreshBtn.setBackground(new Color(52, 73, 94));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadData());
        
        buttonPanel.add(addScheduleBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
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
        prevBtn.addActionListener(e -> navigateMonth(-1));
        
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        monthYearLabel.setForeground(PRIMARY_COLOR);
        updateMonthYearLabel();
        
        JButton nextBtn = new JButton("Next ▶");
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
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(WHITE);
        legendPanel.add(createLegendItem("🍽️ Server", SERVER_COLOR));
        legendPanel.add(createLegendItem("👨‍🍳 Chef", CHEF_COLOR));
        panel.add(legendPanel, BorderLayout.SOUTH);
        
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
    
    private JPanel createScheduleListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"ID", "Employee", "Role", "Date", "Start", "End", "Shift", "Notes"};
        scheduleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scheduleTable = new JTable(scheduleTableModel);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scheduleTable.setRowHeight(30);
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(WHITE);
        
        JButton editBtn = new JButton("✏️ Edit");
        editBtn.addActionListener(e -> editSelectedSchedule());
        
        JButton deleteBtn = new JButton("🗑️ Delete");
        deleteBtn.setForeground(DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteSelectedSchedule());
        
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLeaveRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("📝 Pending Leave Requests");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        String[] columns = {"ID", "Employee", "Role", "Type", "From", "To", "Days", "Reason", "Status"};
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
        leaveTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        leaveTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        
        // Status renderer
        leaveTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                
                if (value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "PENDING" -> {
                            label.setForeground(WARNING_COLOR);
                            label.setFont(label.getFont().deriveFont(Font.BOLD));
                        }
                        case "APPROVED" -> label.setForeground(ACCENT_COLOR);
                        case "REJECTED" -> label.setForeground(DANGER_COLOR);
                        default -> label.setForeground(Color.GRAY);
                    }
                }
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(leaveTable);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(WHITE);
        
        JButton approveBtn = new JButton("✅ Approve");
        approveBtn.setForeground(WHITE);
        approveBtn.setBackground(ACCENT_COLOR);
        approveBtn.addActionListener(e -> handleLeaveAction(true));
        
        JButton rejectBtn = new JButton("❌ Reject");
        rejectBtn.setForeground(WHITE);
        rejectBtn.setBackground(DANGER_COLOR);
        rejectBtn.addActionListener(e -> handleLeaveAction(false));
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        
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
        loadData();
    }
    
    private void loadData() {
        loadScheduleData();
        loadScheduleTable();
        loadLeaveRequests();
    }
    
    private void loadScheduleData() {
        scheduleMap.clear();
        
        List<Schedule> schedules = scheduleController.getMonthSchedules(
            currentMonth.getYear(), currentMonth.getMonthValue());
        
        for (Schedule schedule : schedules) {
            scheduleMap.computeIfAbsent(schedule.getScheduleDate(), k -> new ArrayList<>()).add(schedule);
        }
        
        refreshCalendar();
    }
    
    private void loadScheduleTable() {
        scheduleTableModel.setRowCount(0);
        
        List<Schedule> schedules = scheduleController.getAllSchedules();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Schedule s : schedules) {
            Object[] row = {
                s.getScheduleId(),
                s.getFullName(),
                s.getUserRole(),
                s.getScheduleDate().format(df),
                s.getStartTime().format(TIME_FORMATTER),
                s.getEndTime().format(TIME_FORMATTER),
                s.getShiftType().getDisplayName(),
                s.getNotes() != null ? s.getNotes() : ""
            };
            scheduleTableModel.addRow(row);
        }
    }
    
    private void loadLeaveRequests() {
        leaveTableModel.setRowCount(0);
        
        List<LeaveRequest> requests = scheduleController.getAllLeaveRequests();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd");
        
        for (LeaveRequest lr : requests) {
            Object[] row = {
                lr.getLeaveId(),
                lr.getFullName(),
                lr.getUserRole(),
                lr.getLeaveType().getDisplayName(),
                lr.getStartDate().format(df),
                lr.getEndDate().format(df),
                lr.getTotalDays(),
                lr.getReason(),
                lr.getStatus().name()
            };
            leaveTableModel.addRow(row);
        }
    }
    
    private void refreshCalendar() {
        calendarGrid.removeAll();
        
        LocalDate firstOfMonth = currentMonth;
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
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
        int remainingCells = 42 - totalCells;
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
        
        if (isToday) {
            cell.setBackground(new Color(46, 204, 113, 50));
        } else {
            cell.setBackground(WHITE);
        }
        
        // Day number
        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dayLabel.setBorder(new EmptyBorder(2, 4, 0, 0));
        cell.add(dayLabel, BorderLayout.NORTH);
        
        // Events panel
        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setOpaque(false);
        
        List<Schedule> daySchedules = scheduleMap.get(date);
        if (daySchedules != null) {
            for (Schedule schedule : daySchedules) {
                Color bgColor = "SERVER".equals(schedule.getUserRole()) ? SERVER_COLOR : CHEF_COLOR;
                String icon = "SERVER".equals(schedule.getUserRole()) ? "🍽️" : "👨‍🍳";
                
                JLabel scheduleLabel = new JLabel(icon + " " + schedule.getFullName().split(" ")[0]);
                scheduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 8));
                scheduleLabel.setOpaque(true);
                scheduleLabel.setBackground(bgColor);
                scheduleLabel.setBorder(new EmptyBorder(1, 2, 1, 2));
                eventsPanel.add(scheduleLabel);
                eventsPanel.add(Box.createVerticalStrut(1));
            }
        }
        
        cell.add(eventsPanel, BorderLayout.CENTER);
        
        // Double-click to add schedule
        cell.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showAddScheduleDialog(date);
                } else {
                    showDaySchedules(date);
                }
            }
        });
        
        return cell;
    }
    
    private void showDaySchedules(LocalDate date) {
        List<Schedule> schedules = scheduleMap.get(date);
        
        StringBuilder msg = new StringBuilder();
        msg.append("📅 Schedules for ").append(date.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))).append("\n\n");
        
        if (schedules == null || schedules.isEmpty()) {
            msg.append("No schedules for this day.\nDouble-click to add a schedule.");
        } else {
            for (Schedule s : schedules) {
                msg.append("• ").append(s.getFullName()).append(" (").append(s.getUserRole()).append(")\n");
                msg.append("  ").append(s.getStartTime().format(TIME_FORMATTER))
                   .append(" - ").append(s.getEndTime().format(TIME_FORMATTER))
                   .append(" [").append(s.getShiftType().getDisplayName()).append("]\n");
                if (s.getNotes() != null && !s.getNotes().isEmpty()) {
                    msg.append("  Note: ").append(s.getNotes()).append("\n");
                }
                msg.append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, msg.toString(), "Day Schedules", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAddScheduleDialog(LocalDate preselectedDate) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Add Schedule", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        
        // Employee selection (only SERVER and CHEF)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee:"), gbc);
        gbc.gridx = 1;
        List<User> employees = userController.getAllUsers().stream()
            .filter(u -> u.getRole() == UserRole.SERVER || u.getRole() == UserRole.CHEF)
            .toList();
        JComboBox<String> employeeCombo = new JComboBox<>();
        Map<String, User> employeeMap = new HashMap<>();
        for (User u : employees) {
            String display = u.getFullName() + " (" + u.getRole().getDisplayName() + ")";
            employeeCombo.addItem(display);
            employeeMap.put(display, u);
        }
        formPanel.add(employeeCombo, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        if (preselectedDate != null) {
            dateSpinner.setValue(java.util.Date.from(preselectedDate
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        formPanel.add(dateSpinner, gbc);
        
        // Shift Type
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Shift Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<ShiftType> shiftCombo = new JComboBox<>(ShiftType.values());
        formPanel.add(shiftCombo, gbc);
        
        // Start Time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JSpinner startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        formPanel.add(startTimeSpinner, gbc);
        
        // End Time
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        JSpinner endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        formPanel.add(endTimeSpinner, gbc);
        
        // Update times when shift type changes
        shiftCombo.addActionListener(e -> {
            ShiftType selected = (ShiftType) shiftCombo.getSelectedItem();
            if (selected != null) {
                try {
                    LocalTime start = LocalTime.parse(selected.getDefaultStart());
                    LocalTime end = LocalTime.parse(selected.getDefaultEnd());
                    startTimeSpinner.setValue(java.util.Date.from(start
                        .atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()));
                    endTimeSpinner.setValue(java.util.Date.from(end
                        .atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()));
                } catch (Exception ex) {
                    // Ignore parsing errors
                }
            }
        });
        
        // Trigger initial time update
        shiftCombo.setSelectedIndex(0);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(WHITE);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = new JButton("Save Schedule");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(WHITE);
        saveBtn.addActionListener(e -> {
            String selectedEmployee = (String) employeeCombo.getSelectedItem();
            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(dialog, "Please select an employee.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User employee = employeeMap.get(selectedEmployee);
            LocalDate date = ((java.util.Date) dateSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            ShiftType shiftType = (ShiftType) shiftCombo.getSelectedItem();
            LocalTime startTime = ((java.util.Date) startTimeSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime endTime = ((java.util.Date) endTimeSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            String notes = notesArea.getText().trim();
            
            User currentUser = AuthenticationController.getCurrentUser();
            int createdBy = currentUser != null ? currentUser.getUserId() : 1;
            
            // Validate: end time must be after start time
            if (!endTime.isAfter(startTime)) {
                JOptionPane.showMessageDialog(dialog, "End time must be after start time.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check for overlapping schedules
            if (scheduleController.hasOverlappingSchedule(employee.getUserId(), date, startTime, endTime, 0)) {
                JOptionPane.showMessageDialog(dialog, 
                    "This employee already has a schedule that overlaps with the selected time.\n" +
                    "Please choose a different time or date.",
                    "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Schedule schedule = scheduleController.createSchedule(
                employee.getUserId(), date, startTime, endTime, shiftType, 
                notes.isEmpty() ? null : notes, createdBy);
            
            if (schedule != null) {
                JOptionPane.showMessageDialog(dialog, "Schedule created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to create schedule. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int scheduleId = (int) scheduleTableModel.getValueAt(selectedRow, 0);
        // TODO: Implement edit dialog
        JOptionPane.showMessageDialog(this, "Edit functionality for schedule #" + scheduleId,
            "Edit Schedule", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int scheduleId = (int) scheduleTableModel.getValueAt(selectedRow, 0);
        String employee = (String) scheduleTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the schedule for " + employee + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = scheduleController.deleteSchedule(scheduleId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Schedule deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete schedule.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleLeaveAction(boolean approve) {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String status = (String) leaveTableModel.getValueAt(selectedRow, 8);
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only pending requests can be " + (approve ? "approved" : "rejected") + ".",
                "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int leaveId = (int) leaveTableModel.getValueAt(selectedRow, 0);
        String employee = (String) leaveTableModel.getValueAt(selectedRow, 1);
        
        String notes = JOptionPane.showInputDialog(this,
            (approve ? "Approval" : "Rejection") + " notes for " + employee + "'s leave request:",
            (approve ? "Approve" : "Reject") + " Leave Request", JOptionPane.QUESTION_MESSAGE);
        
        if (notes == null) return; // Cancelled
        
        User currentUser = AuthenticationController.getCurrentUser();
        int reviewedBy = currentUser != null ? currentUser.getUserId() : 0;
        
        boolean success;
        if (approve) {
            success = scheduleController.approveLeaveRequest(leaveId, reviewedBy, notes);
        } else {
            success = scheduleController.rejectLeaveRequest(leaveId, reviewedBy, notes);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Leave request " + (approve ? "approved" : "rejected") + " successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process leave request.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Refresh the panel data
     */
    public void refresh() {
        loadData();
    }
}
