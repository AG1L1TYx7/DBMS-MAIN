package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Bill DAO Implementation
 * Handles all database operations for bills and orders
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class BillDAOImpl implements BillDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public BillDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public Bill createBill(Bill bill) throws SQLException {
        Connection conn = null;
        PreparedStatement billStmt = null;
        PreparedStatement orderItemStmt = null;
        
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert bill
            String billSql = "INSERT INTO bills (user_id, bill_number, subtotal, tax_amount, " +
                           "total_amount, payment_method, customer_name) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            billStmt = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            billStmt.setInt(1, bill.getUserId());
            billStmt.setString(2, bill.getBillNumber());
            billStmt.setBigDecimal(3, bill.getNetAmount());
            billStmt.setBigDecimal(4, bill.getTaxAmount());
            billStmt.setBigDecimal(5, bill.getTotalAmount());
            billStmt.setString(6, bill.getPaymentMethod().name());
            billStmt.setString(7, bill.getBilledByUser());
            
            int affectedRows = billStmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating bill failed, no rows affected.");
            }
            
            // Get generated bill ID
            ResultSet generatedKeys = billStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                bill.setBillId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating bill failed, no ID obtained.");
            }
            
            // Insert order items
            String orderItemSql = "INSERT INTO order_items (bill_id, product_id, product_name, " +
                                "quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            
            orderItemStmt = conn.prepareStatement(orderItemSql);
            
            for (OrderItem item : bill.getOrderItems()) {
                orderItemStmt.setInt(1, bill.getBillId());
                orderItemStmt.setInt(2, item.getProductId());
                orderItemStmt.setString(3, item.getProductName());
                orderItemStmt.setInt(4, item.getQuantity());
                orderItemStmt.setBigDecimal(5, item.getUnitPrice());
                orderItemStmt.setBigDecimal(6, item.getSubtotal());
                orderItemStmt.addBatch();
            }
            
            orderItemStmt.executeBatch();
            
            conn.commit(); // Commit transaction
            
            return bill;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (orderItemStmt != null) orderItemStmt.close();
            if (billStmt != null) billStmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    @Override
    public Optional<Bill> findBillById(Integer billId) throws SQLException {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setOrderItems(getOrderItemsByBillId(billId));
                    return Optional.of(bill);
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Bill> getAllBills() throws SQLException {
        String sql = "SELECT * FROM bills ORDER BY created_at DESC";
        List<Bill> bills = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bill.setOrderItems(getOrderItemsByBillId(bill.getBillId()));
                bills.add(bill);
            }
        }
        
        return bills;
    }
    
    @Override
    public List<Bill> getBillsByUserId(Integer userId) throws SQLException {
        String sql = "SELECT * FROM bills WHERE user_id = ? ORDER BY created_at DESC";
        List<Bill> bills = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setOrderItems(getOrderItemsByBillId(bill.getBillId()));
                    bills.add(bill);
                }
            }
        }
        
        return bills;
    }
    
    @Override
    public Optional<Bill> findBillByNumber(String billNumber) throws SQLException {
        String sql = "SELECT * FROM bills WHERE bill_number = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, billNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setOrderItems(getOrderItemsByBillId(bill.getBillId()));
                    return Optional.of(bill);
                }
            }
        }
        
        return Optional.empty();
    }
    
    public List<Bill> getBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM bills WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        List<Bill> bills = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = mapResultSetToBill(rs);
                    bill.setOrderItems(getOrderItemsByBillId(bill.getBillId()));
                    bills.add(bill);
                }
            }
        }
        
        return bills;
    }
    
    @Override
    public BigDecimal getTotalSales() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM bills";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getLastSale() throws SQLException {
        String sql = "SELECT total_amount FROM bills ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getBigDecimal("total_amount");
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public Integer getTotalOrders() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM bills";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }
    
    public BigDecimal getTotalSalesToday() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM bills " +
                    "WHERE DATE(created_at) = CURDATE()";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    public int getTotalOrdersToday() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM bills WHERE DATE(created_at) = CURDATE()";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }
    
    public String generateBillNumber() throws SQLException {
        // Generate bill number format: BILL-YYYYMMDD-XXXX
        LocalDateTime now = LocalDateTime.now();
        String dateStr = "%04d%02d%02d".formatted(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        
        String sql = "SELECT COUNT(*) + 1 as next_num FROM bills " +
                    "WHERE DATE(created_at) = CURDATE()";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int nextNum = rs.getInt("next_num");
                return "BILL-%s-%04d".formatted(dateStr, nextNum);
            }
        }
        
        return "BILL-%s-0001".formatted(dateStr);
    }
    
    /**
     * Get order items for a specific bill
     */
    private List<OrderItem> getOrderItemsByBillId(Integer billId) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE bill_id = ?";
        List<OrderItem> orderItems = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("bill_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    orderItems.add(item);
                }
            }
        }
        
        return orderItems;
    }
    
    /**
     * Map ResultSet to Bill object
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setUserId(rs.getInt("user_id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setNetAmount(rs.getBigDecimal("subtotal"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")));
        bill.setBilledByUser(rs.getString("customer_name"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            bill.setBilledAt(createdAt.toLocalDateTime());
        }
        
        return bill;
    }
}
