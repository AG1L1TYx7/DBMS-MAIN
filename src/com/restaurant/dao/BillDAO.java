package com.restaurant.dao;

import com.restaurant.model.Bill;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * Bill Data Access Object Interface
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public interface BillDAO {
    
    Bill createBill(Bill bill) throws SQLException;
    Optional<Bill> findBillById(Integer billId) throws SQLException;
    Optional<Bill> findBillByNumber(String billNumber) throws SQLException;
    List<Bill> getAllBills() throws SQLException;
    List<Bill> getBillsByUserId(Integer userId) throws SQLException;
    BigDecimal getTotalSales() throws SQLException;
    BigDecimal getLastSale() throws SQLException;
    Integer getTotalOrders() throws SQLException;
}
