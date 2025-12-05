package com.restaurant.util;

import com.restaurant.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Report Generator Utility Class.
 * Generates various reports for the restaurant management system
 * including sales reports, inventory reports, and customer analytics.
 *
 * @author Restaurant Management System
 * @version 2.0
 * @since 2024-12-04
 */
public class ReportGenerator {

    /** Default report directory. */
    private static final String DEFAULT_REPORT_DIR = "reports";

    /** Report header line separator. */
    private static final String LINE_SEPARATOR = "=".repeat(80);

    /** Report sub-header line separator. */
    private static final String SUB_SEPARATOR = "-".repeat(60);

    /** Restaurant name for report headers. */
    private static final String RESTAURANT_NAME = "Delicious Restaurant";

    /**
     * Default constructor.
     */
    public ReportGenerator() {
        ensureReportDirectoryExists();
    }

    /**
     * Ensures the report directory exists.
     */
    private void ensureReportDirectoryExists() {
        try {
            Path reportDir = Path.of(DEFAULT_REPORT_DIR);
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create report directory: " + e.getMessage());
        }
    }

    // ==================== Sales Reports ====================

    /**
     * Generates a daily sales report.
     *
     * @param bills the list of bills for the day
     * @param reportDate the date of the report
     * @return the generated report as a string
     */
    public String generateDailySalesReport(List<Bill> bills, LocalDate reportDate) {
        StringBuilder report = new StringBuilder();

        // Header
        appendReportHeader(report, "DAILY SALES REPORT");
        report.append("Date: ").append(DateTimeUtils.formatDateForDisplay(reportDate)).append("\n");
        report.append(LINE_SEPARATOR).append("\n\n");

        if (bills == null || bills.isEmpty()) {
            report.append("No sales recorded for this date.\n");
            return report.toString();
        }

        // Summary Statistics
        BigDecimal totalSales = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = bills.stream()
                .map(Bill::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = bills.stream()
                .map(Bill::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageBill = totalSales.divide(
                BigDecimal.valueOf(bills.size()), 2, RoundingMode.HALF_UP);

        report.append("SUMMARY\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-30s: %d\n", "Total Transactions", bills.size()));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Gross Sales", totalSales));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Total Discounts", totalDiscount));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Total Tax", totalTax));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Net Sales", 
                totalSales.subtract(totalDiscount)));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Average Bill Value", averageBill));
        report.append("\n");

        // Payment Method Breakdown
        report.append("PAYMENT METHODS\n");
        report.append(SUB_SEPARATOR).append("\n");
        Map<String, Long> paymentCounts = bills.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getPaymentMethod() != null ? b.getPaymentMethod().getDisplayName() : "Unknown",
                        Collectors.counting()));

        Map<String, BigDecimal> paymentTotals = bills.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getPaymentMethod() != null ? b.getPaymentMethod().getDisplayName() : "Unknown",
                        Collectors.reducing(BigDecimal.ZERO, Bill::getTotalAmount, BigDecimal::add)));

        for (Map.Entry<String, Long> entry : paymentCounts.entrySet()) {
            report.append(String.format("%-20s: %d transactions (Rs. %,.2f)\n",
                    entry.getKey(), entry.getValue(), paymentTotals.get(entry.getKey())));
        }
        report.append("\n");

        // Transaction Details
        report.append("TRANSACTION DETAILS\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-12s %-10s %-15s %-15s %-15s\n",
                "Bill #", "Table", "Time", "Amount", "Payment"));
        report.append(SUB_SEPARATOR).append("\n");

        for (Bill bill : bills) {
            report.append(String.format("%-12s %-10s %-15s Rs. %-11.2f %-15s\n",
                    bill.getBillId() != null ? bill.getBillId() : "N/A",
                    bill.getTableNumber() != null ? bill.getTableNumber() : "N/A",
                    bill.getCreatedAt() != null 
                            ? DateTimeUtils.formatTimeShort(bill.getCreatedAt().toLocalTime()) : "N/A",
                    bill.getTotalAmount(),
                    bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "N/A"));
        }

        appendReportFooter(report);
        return report.toString();
    }

    /**
     * Generates a sales summary report for a date range.
     *
     * @param bills the list of bills
     * @param startDate the start date
     * @param endDate the end date
     * @return the generated report as a string
     */
    public String generateSalesSummaryReport(List<Bill> bills, LocalDate startDate, LocalDate endDate) {
        StringBuilder report = new StringBuilder();

        appendReportHeader(report, "SALES SUMMARY REPORT");
        report.append(String.format("Period: %s to %s\n",
                DateTimeUtils.formatDateForDisplay(startDate),
                DateTimeUtils.formatDateForDisplay(endDate)));
        report.append(LINE_SEPARATOR).append("\n\n");

        if (bills == null || bills.isEmpty()) {
            report.append("No sales recorded for this period.\n");
            return report.toString();
        }

        // Overall Statistics
        BigDecimal totalRevenue = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalDays = DateTimeUtils.daysBetween(startDate, endDate) + 1;
        BigDecimal avgDailyRevenue = totalRevenue.divide(
                BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

        report.append("OVERALL STATISTICS\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-30s: %d days\n", "Report Period", totalDays));
        report.append(String.format("%-30s: %d\n", "Total Transactions", bills.size()));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Total Revenue", totalRevenue));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Average Daily Revenue", avgDailyRevenue));
        report.append(String.format("%-30s: %.1f\n", "Avg Transactions/Day",
                (double) bills.size() / totalDays));
        report.append("\n");

        appendReportFooter(report);
        return report.toString();
    }

    // ==================== Inventory Reports ====================

    /**
     * Generates an inventory status report.
     *
     * @param products the list of products
     * @return the generated report as a string
     */
    public String generateInventoryReport(List<Product> products) {
        StringBuilder report = new StringBuilder();

        appendReportHeader(report, "INVENTORY STATUS REPORT");
        report.append("Generated: ").append(DateTimeUtils.formatDateTimeForDisplay(
                DateTimeUtils.getCurrentDateTime())).append("\n");
        report.append(LINE_SEPARATOR).append("\n\n");

        if (products == null || products.isEmpty()) {
            report.append("No products in inventory.\n");
            return report.toString();
        }

        // Summary
        long totalProducts = products.size();
        long availableProducts = products.stream()
                .filter(Product::isAvailable)
                .count();
        long lowStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() < 10)
                .count();
        long outOfStock = products.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() == 0)
                .count();

        report.append("SUMMARY\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-30s: %d\n", "Total Products", totalProducts));
        report.append(String.format("%-30s: %d\n", "Available Products", availableProducts));
        report.append(String.format("%-30s: %d\n", "Low Stock (< 10)", lowStockProducts));
        report.append(String.format("%-30s: %d\n", "Out of Stock", outOfStock));
        report.append("\n");

        // Category Breakdown
        report.append("BY CATEGORY\n");
        report.append(SUB_SEPARATOR).append("\n");
        Map<String, Long> categoryCount = products.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory().getDisplayName() : "Uncategorized",
                        Collectors.counting()));

        for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
            report.append(String.format("%-25s: %d items\n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");

        // Low Stock Alert
        if (lowStockProducts > 0) {
            report.append("LOW STOCK ALERT\n");
            report.append(SUB_SEPARATOR).append("\n");
            report.append(String.format("%-30s %-15s %-15s\n", "Product", "Stock", "Category"));
            report.append(SUB_SEPARATOR).append("\n");

            products.stream()
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() < 10)
                    .forEach(p -> report.append(String.format("%-30s %-15d %-15s\n",
                            truncate(p.getProductName(), 30),
                            p.getStockQuantity(),
                            p.getCategory() != null ? p.getCategory().getDisplayName() : "N/A")));
            report.append("\n");
        }

        // Full Product List
        report.append("PRODUCT DETAILS\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-25s %-12s %-10s %-10s %-10s\n",
                "Product", "Price", "Stock", "Category", "Status"));
        report.append(SUB_SEPARATOR).append("\n");

        for (Product product : products) {
            report.append(String.format("%-25s Rs. %-8.2f %-10s %-10s %-10s\n",
                    truncate(product.getProductName(), 25),
                    product.getPrice(),
                    product.getStockQuantity() != null ? product.getStockQuantity() : "N/A",
                    truncate(product.getCategory() != null ? product.getCategory().getDisplayName() : "N/A", 10),
                    product.isAvailable() ? "Available" : "Unavailable"));
        }

        appendReportFooter(report);
        return report.toString();
    }

    // ==================== Customer Reports ====================

    /**
     * Generates a customer analytics report.
     *
     * @param customers the list of customers
     * @return the generated report as a string
     */
    public String generateCustomerReport(List<Customer> customers) {
        StringBuilder report = new StringBuilder();

        appendReportHeader(report, "CUSTOMER ANALYTICS REPORT");
        report.append("Generated: ").append(DateTimeUtils.formatDateTimeForDisplay(
                DateTimeUtils.getCurrentDateTime())).append("\n");
        report.append(LINE_SEPARATOR).append("\n\n");

        if (customers == null || customers.isEmpty()) {
            report.append("No customer data available.\n");
            return report.toString();
        }

        // Summary Statistics
        long totalCustomers = customers.size();
        long activeCustomers = customers.stream()
                .filter(Customer::isActive)
                .count();

        BigDecimal totalRevenue = customers.stream()
                .map(Customer::getTotalSpent)
                .filter(spent -> spent != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalLoyaltyPoints = customers.stream()
                .mapToInt(c -> c.getLoyaltyPoints() != null ? c.getLoyaltyPoints() : 0)
                .sum();

        report.append("SUMMARY\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-30s: %d\n", "Total Customers", totalCustomers));
        report.append(String.format("%-30s: %d\n", "Active Customers", activeCustomers));
        report.append(String.format("%-30s: Rs. %,.2f\n", "Total Revenue from Customers", totalRevenue));
        report.append(String.format("%-30s: %,d\n", "Total Loyalty Points Issued", totalLoyaltyPoints));
        report.append("\n");

        // Membership Tier Distribution
        report.append("MEMBERSHIP DISTRIBUTION\n");
        report.append(SUB_SEPARATOR).append("\n");
        Map<Customer.MembershipTier, Long> tierCount = customers.stream()
                .filter(c -> c.getMembershipTier() != null)
                .collect(Collectors.groupingBy(Customer::getMembershipTier, Collectors.counting()));

        for (Customer.MembershipTier tier : Customer.MembershipTier.values()) {
            long count = tierCount.getOrDefault(tier, 0L);
            double percentage = (double) count / totalCustomers * 100;
            report.append(String.format("%-15s: %5d customers (%5.1f%%)\n",
                    tier.getDisplayName(), count, percentage));
        }
        report.append("\n");

        // Top Customers
        report.append("TOP 10 CUSTOMERS BY SPENDING\n");
        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("%-25s %-15s %-10s %-10s\n",
                "Name", "Total Spent", "Visits", "Tier"));
        report.append(SUB_SEPARATOR).append("\n");

        customers.stream()
                .filter(c -> c.getTotalSpent() != null)
                .sorted((c1, c2) -> c2.getTotalSpent().compareTo(c1.getTotalSpent()))
                .limit(10)
                .forEach(c -> report.append(String.format("%-25s Rs. %-11.2f %-10d %-10s\n",
                        truncate(c.getFullName(), 25),
                        c.getTotalSpent(),
                        c.getVisitCount() != null ? c.getVisitCount() : 0,
                        c.getMembershipTier() != null ? c.getMembershipTier().getDisplayName() : "N/A")));

        appendReportFooter(report);
        return report.toString();
    }

    // ==================== Staff Reports ====================

    /**
     * Generates a staff schedule report.
     *
     * @param schedules the list of schedules
     * @param reportDate the date for the schedule
     * @return the generated report as a string
     */
    public String generateStaffScheduleReport(List<Schedule> schedules, LocalDate reportDate) {
        StringBuilder report = new StringBuilder();

        appendReportHeader(report, "STAFF SCHEDULE REPORT");
        report.append("Date: ").append(DateTimeUtils.formatDateForDisplay(reportDate)).append("\n");
        report.append("Day: ").append(DateTimeUtils.getDayOfWeekName(reportDate)).append("\n");
        report.append(LINE_SEPARATOR).append("\n\n");

        if (schedules == null || schedules.isEmpty()) {
            report.append("No schedules for this date.\n");
            return report.toString();
        }

        report.append(String.format("%-25s %-12s %-12s %-15s %-15s\n",
                "Staff Member", "Start Time", "End Time", "Shift Type", "Status"));
        report.append(SUB_SEPARATOR).append("\n");

        for (Schedule schedule : schedules) {
            report.append(String.format("%-25s %-12s %-12s %-15s %-15s\n",
                    "Staff #" + schedule.getUserId(),
                    schedule.getStartTime() != null 
                            ? DateTimeUtils.formatTimeShort(schedule.getStartTime()) : "N/A",
                    schedule.getEndTime() != null 
                            ? DateTimeUtils.formatTimeShort(schedule.getEndTime()) : "N/A",
                    schedule.getShiftType() != null ? schedule.getShiftType().getDisplayName() : "N/A",
                    schedule.isActive() ? "Active" : "Inactive"));
        }

        appendReportFooter(report);
        return report.toString();
    }

    // ==================== Utility Methods ====================

    /**
     * Appends the standard report header.
     *
     * @param report the StringBuilder to append to
     * @param title the report title
     */
    private void appendReportHeader(StringBuilder report, String title) {
        report.append("\n");
        report.append(LINE_SEPARATOR).append("\n");
        report.append(centerText(RESTAURANT_NAME, 80)).append("\n");
        report.append(centerText(title, 80)).append("\n");
        report.append(LINE_SEPARATOR).append("\n");
    }

    /**
     * Appends the standard report footer.
     *
     * @param report the StringBuilder to append to
     */
    private void appendReportFooter(StringBuilder report) {
        report.append("\n");
        report.append(LINE_SEPARATOR).append("\n");
        report.append(centerText("*** End of Report ***", 80)).append("\n");
        report.append(centerText("Generated by Restaurant Management System", 80)).append("\n");
        report.append(LINE_SEPARATOR).append("\n");
    }

    /**
     * Centers text within a specified width.
     *
     * @param text the text to center
     * @param width the total width
     * @return the centered text
     */
    private String centerText(String text, int width) {
        if (text == null || text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }

    /**
     * Truncates a string to a maximum length.
     *
     * @param text the text to truncate
     * @param maxLength the maximum length
     * @return the truncated text
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Saves a report to a file.
     *
     * @param report the report content
     * @param filename the filename (without path)
     * @return the path to the saved file
     * @throws IOException if an I/O error occurs
     */
    public String saveReport(String report, String filename) throws IOException {
        Path filePath = Path.of(DEFAULT_REPORT_DIR, filename);
        Files.writeString(filePath, report, StandardCharsets.UTF_8);
        return filePath.toString();
    }

    /**
     * Generates a filename for a report.
     *
     * @param reportType the type of report (e.g., "sales", "inventory")
     * @param date the date for the report
     * @return the generated filename
     */
    public String generateReportFilename(String reportType, LocalDate date) {
        return String.format("%s_report_%s.txt",
                reportType.toLowerCase().replace(" ", "_"),
                DateTimeUtils.formatDate(date));
    }

    /**
     * Generates a timestamp-based filename for a report.
     *
     * @param reportType the type of report
     * @return the generated filename with timestamp
     */
    public String generateTimestampedFilename(String reportType) {
        LocalDateTime now = DateTimeUtils.getCurrentDateTime();
        return String.format("%s_report_%s_%s.txt",
                reportType.toLowerCase().replace(" ", "_"),
                DateTimeUtils.formatDate(now.toLocalDate()),
                now.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HHmmss")));
    }
}
