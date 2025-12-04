package com.restaurant.controller;

import com.restaurant.dao.BillDAO;
import com.restaurant.dao.BillDAOImpl;
import com.restaurant.model.Bill;
import com.restaurant.model.Bill.PaymentMethod;
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
    
    public OrderController() {
        this.billDAO = new BillDAOImpl();
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
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message,
            "Order Error", JOptionPane.ERROR_MESSAGE);
    }
}
