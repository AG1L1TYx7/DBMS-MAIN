package com.restaurant.util;

import com.restaurant.model.Bill;
import com.restaurant.model.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;

/**
 * Receipt Printer Utility
 * Handles receipt generation and printing
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class ReceiptPrinter implements Printable {
    
    private final Bill bill;
    private static final String RESTAURANT_NAME = "Delicious Restaurant";
    private static final String RESTAURANT_ADDRESS = "123 Main Street, Kathmandu, Nepal";
    private static final String RESTAURANT_PHONE = "+977 1-4567890";
    private static final String RESTAURANT_EMAIL = "info@delicious.com.np";
    
    public ReceiptPrinter(Bill bill) {
        this.bill = bill;
    }
    
    /**
     * Print receipt
     */
    public void printReceipt() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        
        // Set up page format (thermal printer size)
        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        // 80mm thermal paper (approximately 226.77 points)
        double width = 226.77;
        double height = 842; // A4 height for variable length
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        pf.setPaper(paper);
        
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null,
                    "Failed to print receipt: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Show print preview
     */
    public void showPrintPreview() {
        JDialog dialog = new JDialog((Frame) null, "Receipt Preview", true);
        dialog.setSize(350, 600);
        dialog.setLocationRelativeTo(null);
        
        JTextArea receiptArea = new JTextArea(generateReceiptText());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.WHITE);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton printBtn = new JButton("🖨️ Print");
        JButton closeBtn = new JButton("❌ Close");
        
        printBtn.addActionListener(e -> {
            printReceipt();
            dialog.dispose();
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Generate receipt text
     */
    private String generateReceiptText() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Header
        sb.append(centerText(RESTAURANT_NAME, 40)).append("\n");
        sb.append(centerText(RESTAURANT_ADDRESS, 40)).append("\n");
        sb.append(centerText(RESTAURANT_PHONE, 40)).append("\n");
        sb.append(centerText(RESTAURANT_EMAIL, 40)).append("\n");
        sb.append(line(40)).append("\n");
        
        // Bill Information
        sb.append("Bill No: ").append(bill.getBillNumber()).append("\n");
        sb.append("Date: ").append(bill.getBilledAt() != null ? 
            bill.getBilledAt().format(formatter) : "N/A").append("\n");
        sb.append("Cashier: ").append(bill.getBilledByUser()).append("\n");
        sb.append(line(40)).append("\n");
        
        // Items Header
        sb.append("%-20s %4s %7s %7s\n".formatted("Item", "Qty", "Price", "Total"));
        sb.append(line(40)).append("\n");
        
        // Items
        if (bill.getOrderItems() != null) {
            for (OrderItem item : bill.getOrderItems()) {
                String itemName = truncate(item.getProductName(), 20);
                sb.append("%-20s %4d %7.2f %7.2f\n".formatted(
                        itemName,
                        item.getQuantity(),
                        item.getUnitPrice().doubleValue(),
                        item.getSubtotal().doubleValue()));
            }
        }
        
        sb.append(line(40)).append("\n");
        
        // Totals
        sb.append("%-28s %10.2f\n".formatted("Subtotal:", bill.getNetAmount().doubleValue()));
        sb.append("%-28s %10.2f\n".formatted("Tax (13%):", bill.getTaxAmount().doubleValue()));
        sb.append(line(40)).append("\n");
        sb.append("%-28s %10.2f\n".formatted("TOTAL:", bill.getTotalAmount().doubleValue()));
        
        // Payment Details
        if (bill.getPaymentMethod() != null) {
            sb.append(line(40)).append("\n");
            sb.append("Payment Method: ").append(bill.getPaymentMethod().name()).append("\n");
            
            if (bill.getCashReceived() != null) {
                sb.append("%-28s %10.2f\n".formatted("Cash Received:",
                        bill.getCashReceived().doubleValue()));
                sb.append("%-28s %10.2f\n".formatted("Change:",
                        bill.getChangeAmount().doubleValue()));
            }
        }
        
        // Footer
        sb.append(line(40)).append("\n");
        sb.append(centerText("Thank you for dining with us!", 40)).append("\n");
        sb.append(centerText("Please visit again!", 40)).append("\n");
        sb.append(line(40)).append("\n");
        
        return sb.toString();
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        
        // Set font
        Font font = new Font("Monospaced", Font.PLAIN, 9);
        g2d.setFont(font);
        
        // Draw receipt text
        String[] lines = generateReceiptText().split("\n");
        int y = 20;
        for (String line : lines) {
            g2d.drawString(line, 5, y);
            y += 12;
        }
        
        return PAGE_EXISTS;
    }
    
    /**
     * Center text
     */
    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }
    
    /**
     * Generate line separator
     */
    private String line(int width) {
        return "=".repeat(width);
    }
    
    /**
     * Truncate text to specified length
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
}
