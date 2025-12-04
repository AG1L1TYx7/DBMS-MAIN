package com.restaurant.dao;

import com.restaurant.model.Order;
import com.restaurant.model.Order.OrderStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Order DAO Interface
 * Defines data access operations for Order entity
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public interface OrderDAO {
    
    /**
     * Create a new order
     */
    Order createOrder(Order order) throws SQLException;
    
    /**
     * Find order by ID
     */
    Optional<Order> findOrderById(Integer orderId) throws SQLException;
    
    /**
     * Find order by order number
     */
    Optional<Order> findOrderByNumber(String orderNumber) throws SQLException;
    
    /**
     * Get all orders
     */
    List<Order> getAllOrders() throws SQLException;
    
    /**
     * Get orders by status (for kitchen display)
     */
    List<Order> getOrdersByStatus(OrderStatus status) throws SQLException;
    
    /**
     * Get pending and preparing orders (for kitchen)
     */
    List<Order> getKitchenOrders() throws SQLException;
    
    /**
     * Get orders by server
     */
    List<Order> getOrdersByServer(Integer serverId) throws SQLException;
    
    /**
     * Get orders by table
     */
    List<Order> getOrdersByTable(String tableNumber) throws SQLException;
    
    /**
     * Update order status
     */
    boolean updateOrderStatus(Integer orderId, OrderStatus status) throws SQLException;
    
    /**
     * Update order
     */
    boolean updateOrder(Order order) throws SQLException;
    
    /**
     * Get today's orders
     */
    List<Order> getTodaysOrders() throws SQLException;
    
    /**
     * Get active orders (not completed or cancelled)
     */
    List<Order> getActiveOrders() throws SQLException;
    
    /**
     * Generate order number
     */
    String generateOrderNumber() throws SQLException;
    
    /**
     * Add item to existing order
     */
    boolean addItemToOrder(Integer orderId, com.restaurant.model.OrderItem item) throws SQLException;
}
