package com.restaurant.controller;

import com.restaurant.dao.BillDAO;
import com.restaurant.dao.BillDAOImpl;
import com.restaurant.dao.OrderDAO;
import com.restaurant.dao.OrderDAOImpl;
import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
import com.restaurant.model.Order;
import com.restaurant.model.Order.OrderStatus;
import com.restaurant.model.OrderItem;
import com.restaurant.model.User;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Order Controller
 * Handles order and billing business logic
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class OrderController {
    
    private final BillDAO billDAO;
    private final OrderDAO orderDAO;
    
    public OrderController() {
        this.billDAO = new BillDAOImpl();
        this.orderDAO = new OrderDAOImpl();
    }
    
    /**
     * Create a new bill/order
     */
    public Bill createBill(User currentUser, List<OrderItem> items, PaymentMethod paymentMethod,
                          BigDecimal cashReceived) {
        try {
            // Validation
            if (items == null || items.isEmpty()) {
                showError("Cart is empty. Please add items to order.");
                return null;
            }
            
            if (currentUser == null) {
                showError("User not authenticated");
                return null;
            }
            
            // Create bill
            Bill bill = new Bill();
            bill.setUserId(currentUser.getUserId());
            bill.setBilledByUser(currentUser.getFullName());
            bill.setPaymentMethod(paymentMethod);
            bill.setOrderItems(items);
            bill.calculateTotals(); // This calculates netAmount, tax, and total
            
            // Validate cash payment
            if (paymentMethod == PaymentMethod.CASH) {
                if (cashReceived == null || cashReceived.compareTo(bill.getTotalAmount()) < 0) {
                    showError("Insufficient cash amount");
                    return null;
                }
                bill.setCashReceived(cashReceived);
            }
            
            // Generate bill number
            BillDAOImpl impl = (BillDAOImpl) billDAO;
            String billNumber = impl.generateBillNumber();
            bill.setBillNumber(billNumber);
            
            // Save to database
            Bill savedBill = billDAO.createBill(bill);
            
            if (savedBill != null) {
                JOptionPane.showMessageDialog(null,
                    "Order placed successfully!\nBill Number: " + savedBill.getBillNumber(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            return savedBill;
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to create order: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get bill by ID
     */
    public Optional<Bill> getBillById(Integer billId) {
        try {
            return billDAO.findBillById(billId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load bill: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Get all bills
     */
    public List<Bill> getAllBills() {
        try {
            return billDAO.getAllBills();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load bills: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get bills by user
     */
    public List<Bill> getBillsByUser(Integer userId) {
        try {
            return billDAO.getBillsByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load bills: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get total sales
     */
    public BigDecimal getTotalSales() {
        try {
            return billDAO.getTotalSales();
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get last sale amount
     */
    public BigDecimal getLastSale() {
        try {
            return billDAO.getLastSale();
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get total orders count
     */
    public int getTotalOrders() {
        try {
            return billDAO.getTotalOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get today's sales
     */
    public BigDecimal getTodaysSales() {
        try {
            BillDAOImpl impl = (BillDAOImpl) billDAO;
            return impl.getTotalSalesToday();
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get today's orders count
     */
    public int getTodaysOrders() {
        try {
            BillDAOImpl impl = (BillDAOImpl) billDAO;
            return impl.getTotalOrdersToday();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get today's order list (as Bills)
     */
    public List<Bill> getTodaysOrderList() {
        try {
            BillDAOImpl impl = (BillDAOImpl) billDAO;
            return impl.getTodaysBills();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get order by ID
     */
    public Bill getOrderById(int orderId) {
        try {
            return billDAO.findBillById(orderId).orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get orders by payment status (for bills)
     */
    public List<Bill> getBillsByStatus(String status) {
        try {
            BillDAOImpl impl = (BillDAOImpl) billDAO;
            return impl.getBillsByPaymentStatus(status);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get kitchen orders by status (PENDING, PREPARING, etc.)
     */
    public List<Order> getKitchenOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            return orderDAO.getOrdersByStatus(orderStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Update kitchen order status
     */
    public boolean updateKitchenOrderStatus(int orderId, String newStatus) {
        try {
            OrderStatus status = OrderStatus.valueOf(newStatus);
            return orderDAO.updateOrderStatus(orderId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update order/bill status (legacy - uses bills table)
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        // Try to update in orders table first (for kitchen orders)
        try {
            OrderStatus status = OrderStatus.valueOf(newStatus);
            boolean updated = orderDAO.updateOrderStatus(orderId, status);
            if (updated) {
                return true;
            }
        } catch (Exception e) {
            // Ignore - may not be in orders table
        }
        
        // Fall back to bills table
        try {
            BillDAOImpl impl = (BillDAOImpl) billDAO;
            return impl.updateBillStatus(orderId, newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get orders by status - returns Orders from orders table for kitchen
     */
    public List<Order> getOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            return orderDAO.getOrdersByStatus(orderStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Create a new order and send to kitchen
     */
    public Order createOrderForKitchen(User currentUser, List<OrderItem> items, String tableNumber) {
        try {
            if (items == null || items.isEmpty()) {
                showError("Cart is empty. Please add items to order.");
                return null;
            }
            
            if (currentUser == null) {
                showError("User not authenticated");
                return null;
            }
            
            // Create order
            Order order = new Order();
            order.setOrderNumber(orderDAO.generateOrderNumber());
            order.setOrderType(Order.OrderType.DINE_IN);
            order.setTableNumber(tableNumber);
            order.setStatus(Order.OrderStatus.PENDING);
            order.setServerId(currentUser.getUserId());
            order.setServerName(currentUser.getFullName());
            order.setOrderItems(items);
            order.calculateTotals();
            
            // Save to database
            Order savedOrder = orderDAO.createOrder(order);
            
            if (savedOrder != null) {
                JOptionPane.showMessageDialog(null,
                    "Order sent to kitchen!\nOrder #: " + savedOrder.getOrderNumber() + 
                    "\nTable: " + tableNumber,
                    "Order Sent", JOptionPane.INFORMATION_MESSAGE);
            }
            
            return savedOrder;
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to create order: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Add items to an existing order
     */
    public boolean addItemsToOrder(int orderId, List<OrderItem> newItems) {
        try {
            for (OrderItem item : newItems) {
                orderDAO.addItemToOrder(orderId, item);
            }
            
            // Reset order status to PENDING so kitchen sees the new items
            orderDAO.updateOrderStatus(orderId, Order.OrderStatus.PENDING);
            
            JOptionPane.showMessageDialog(null,
                "Items added to order successfully!\nKitchen has been notified.",
                "Items Added", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to add items to order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get active orders for a server (not completed or cancelled)
     */
    public List<Order> getActiveOrdersForServer(int serverId) {
        try {
            return orderDAO.getOrdersByServer(serverId).stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.COMPLETED && 
                             o.getStatus() != Order.OrderStatus.CANCELLED)
                .toList();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get all active orders
     */
    public List<Order> getAllActiveOrders() {
        try {
            return orderDAO.getActiveOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get order by ID
     */
    public Order getKitchenOrderById(int orderId) {
        try {
            return orderDAO.findOrderById(orderId).orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Mark order as served by server
     */
    public boolean markOrderAsServed(int orderId) {
        try {
            return orderDAO.updateOrderStatus(orderId, Order.OrderStatus.SERVED);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mark order as completed (after bill is created)
     */
    public boolean markOrderAsCompleted(int orderId) {
        try {
            return orderDAO.updateOrderStatus(orderId, Order.OrderStatus.COMPLETED);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message,
            "Order Error", JOptionPane.ERROR_MESSAGE);
    }
}
