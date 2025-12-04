package com.restaurant.dao;

import com.restaurant.config.DatabaseConfiguration;
import com.restaurant.model.Order;
import com.restaurant.model.Order.OrderStatus;
import com.restaurant.model.Order.OrderType;
import com.restaurant.model.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Order DAO Implementation
 * Handles all database operations for orders
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class OrderDAOImpl implements OrderDAO {
    
    private final DatabaseConfiguration dbConfig;
    
    public OrderDAOImpl() {
        this.dbConfig = DatabaseConfiguration.getInstance();
    }
    
    @Override
    public Order createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_number, order_type_id, order_type, table_number, " +
                    "order_status, status, customer_name, customer_phone, server_id, server_name, " +
                    "subtotal, tax_amount, total_amount, special_instructions, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Map order type to order_type_id
            int orderTypeId = switch (order.getOrderType()) {
                case DINE_IN -> 1;
                case TAKEOUT -> 2;
                case DELIVERY -> 3;
            };
            
            stmt.setString(1, order.getOrderNumber());
            stmt.setInt(2, orderTypeId);
            stmt.setString(3, order.getOrderType().name());
            stmt.setString(4, order.getTableNumber());
            stmt.setString(5, order.getStatus().name());
            stmt.setString(6, order.getStatus().name());
            stmt.setString(7, order.getCustomerName());
            stmt.setString(8, order.getCustomerPhone());
            stmt.setInt(9, order.getServerId());
            stmt.setString(10, order.getServerName());
            stmt.setBigDecimal(11, order.getSubtotal());
            stmt.setBigDecimal(12, order.getTaxAmount());
            stmt.setBigDecimal(13, order.getTotalAmount());
            stmt.setString(14, order.getSpecialInstructions());
            stmt.setTimestamp(15, Timestamp.valueOf(order.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                }
                
                // Insert order items
                insertOrderItems(order);
            }
            
            return order;
        }
    }
    
    private void insertOrderItems(Order order) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (OrderItem item : order.getOrderItems()) {
                stmt.setInt(1, order.getOrderId());
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setBigDecimal(4, item.getUnitPrice());
                stmt.setBigDecimal(5, item.getSubtotal());
                stmt.setString(6, item.getNotes());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    @Override
    public Optional<Order> findOrderById(Integer orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setOrderItems(getOrderItemsByOrderId(orderId));
                    return Optional.of(order);
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Order> findOrderByNumber(String orderNumber) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_number = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                    return Optional.of(order);
                }
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        return fetchOrders(sql);
    }
    
    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_status = ? ORDER BY created_at ASC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        
        // Fetch order items for each order
        for (Order order : orders) {
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
        }
        
        return orders;
    }
    
    @Override
    public List<Order> getKitchenOrders() throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_status IN ('PENDING', 'PREPARING') " +
                    "ORDER BY created_at ASC";
        return fetchOrders(sql);
    }
    
    @Override
    public List<Order> getOrdersByServer(Integer serverId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE server_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        
        for (Order order : orders) {
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
        }
        
        return orders;
    }
    
    @Override
    public List<Order> getOrdersByTable(String tableNumber) throws SQLException {
        String sql = "SELECT * FROM orders WHERE table_number = ? AND order_status NOT IN ('COMPLETED', 'CANCELLED') " +
                    "ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tableNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        
        for (Order order : orders) {
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
        }
        
        return orders;
    }
    
    @Override
    public boolean updateOrderStatus(Integer orderId, OrderStatus status) throws SQLException {
        String sql = "UPDATE orders SET order_status = ?, updated_at = ? WHERE order_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            
            stmt.setString(1, status.name());
            stmt.setTimestamp(2, now);
            stmt.setInt(3, orderId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET order_type = ?, table_number = ?, status = ?, " +
                    "customer_name = ?, customer_phone = ?, subtotal = ?, tax_amount = ?, " +
                    "total_amount = ?, special_instructions = ?, updated_at = ? WHERE order_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getOrderType().name());
            stmt.setString(2, order.getTableNumber());
            stmt.setString(3, order.getStatus().name());
            stmt.setString(4, order.getCustomerName());
            stmt.setString(5, order.getCustomerPhone());
            stmt.setBigDecimal(6, order.getSubtotal());
            stmt.setBigDecimal(7, order.getTaxAmount());
            stmt.setBigDecimal(8, order.getTotalAmount());
            stmt.setString(9, order.getSpecialInstructions());
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(11, order.getOrderId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public List<Order> getTodaysOrders() throws SQLException {
        String sql = "SELECT * FROM orders WHERE DATE(created_at) = CURDATE() ORDER BY created_at DESC";
        return fetchOrders(sql);
    }
    
    @Override
    public List<Order> getActiveOrders() throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_status NOT IN ('COMPLETED', 'CANCELLED') " +
                    "ORDER BY created_at ASC";
        return fetchOrders(sql);
    }
    
    @Override
    public String generateOrderNumber() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = "%04d%02d%02d".formatted(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        
        String sql = "SELECT COUNT(*) + 1 as next_num FROM orders WHERE DATE(created_at) = CURDATE()";
        
        try (Connection conn = dbConfig.createNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int nextNum = rs.getInt("next_num");
                return "ORD-%s-%04d".formatted(dateStr, nextNum);
            }
        }
        
        return "ORD-%s-0001".formatted(dateStr);
    }
    
    private List<Order> fetchOrders(String sql) throws SQLException {
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        
        // Fetch order items for each order
        for (Order order : orders) {
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
        }
        
        return orders;
    }
    
    private List<OrderItem> getOrderItemsByOrderId(Integer orderId) throws SQLException {
        String sql = "SELECT oi.*, p.product_name FROM order_items oi " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = dbConfig.createNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        
        return items;
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderNumber(rs.getString("order_number"));
        
        // Handle order_type - can be null, default to DINE_IN
        String orderType = rs.getString("order_type");
        if (orderType != null) {
            order.setOrderType(OrderType.valueOf(orderType));
        } else {
            order.setOrderType(OrderType.DINE_IN);
        }
        
        order.setTableNumber(rs.getString("table_number"));
        
        // Handle status - use order_status column which is the primary status field
        String status = rs.getString("order_status");
        if (status != null) {
            order.setStatus(OrderStatus.valueOf(status));
        } else {
            order.setStatus(OrderStatus.PENDING);
        }
        
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerPhone(rs.getString("customer_phone"));
        
        int serverId = rs.getInt("server_id");
        if (!rs.wasNull()) {
            order.setServerId(serverId);
        }
        order.setServerName(rs.getString("server_name"));
        
        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setTaxAmount(rs.getBigDecimal("tax_amount"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setSpecialInstructions(rs.getString("special_instructions"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp preparedAt = rs.getTimestamp("prepared_at");
        if (preparedAt != null) {
            order.setPreparedAt(preparedAt.toLocalDateTime());
        }
        
        Timestamp servedAt = rs.getTimestamp("served_at");
        if (servedAt != null) {
            order.setServedAt(servedAt.toLocalDateTime());
        }
        
        return order;
    }
    
    @Override
    public boolean addItemToOrder(Integer orderId, OrderItem item) throws SQLException {
        String insertSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) " +
                          "VALUES (?, ?, ?, ?, ?)";
        String updateOrderSql = "UPDATE orders SET subtotal = subtotal + ?, total_amount = total_amount + ? WHERE order_id = ?";
        
        try (Connection conn = dbConfig.createNewConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Insert the new order item
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, item.getProductId());
                    stmt.setInt(3, item.getQuantity());
                    stmt.setBigDecimal(4, item.getUnitPrice());
                    stmt.setBigDecimal(5, item.getSubtotal());
                    stmt.executeUpdate();
                }
                
                // Update order totals
                try (PreparedStatement stmt = conn.prepareStatement(updateOrderSql)) {
                    stmt.setBigDecimal(1, item.getSubtotal());
                    stmt.setBigDecimal(2, item.getSubtotal());
                    stmt.setInt(3, orderId);
                    stmt.executeUpdate();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
