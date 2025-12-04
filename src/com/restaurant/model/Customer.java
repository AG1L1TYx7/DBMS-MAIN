package com.restaurant.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Customer Model Class
 * Represents a customer in the restaurant management system
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Integer customerId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private MembershipTier membershipTier;
    private Integer loyaltyPoints;
    private BigDecimal totalSpent;
    private Integer visitCount;
    private LocalDate lastVisitDate;
    private LocalDateTime registrationDate;
    private boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Membership Tier Enumeration
     */
    public enum MembershipTier {
        BRONZE("Bronze", 0, 15000, 5),
        SILVER("Silver", 15000, 40000, 8),
        GOLD("Gold", 40000, 80000, 12),
        PLATINUM("Platinum", 80000, Integer.MAX_VALUE, 15);
        
        private final String displayName;
        private final int minSpending;
        private final int maxSpending;
        private final int discountPercentage;
        
        MembershipTier(String displayName, int minSpending, int maxSpending, int discountPercentage) {
            this.displayName = displayName;
            this.minSpending = minSpending;
            this.maxSpending = maxSpending;
            this.discountPercentage = discountPercentage;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getMinSpending() {
            return minSpending;
        }
        
        public int getMaxSpending() {
            return maxSpending;
        }
        
        public int getDiscountPercentage() {
            return discountPercentage;
        }
        
        public static MembershipTier fromString(String tier) {
            for (MembershipTier membershipTier : MembershipTier.values()) {
                if (membershipTier.name().equalsIgnoreCase(tier) || 
                    membershipTier.displayName.equalsIgnoreCase(tier)) {
                    return membershipTier;
                }
            }
            return BRONZE;
        }
        
        public static MembershipTier fromSpending(BigDecimal totalSpent) {
            int spending = totalSpent.intValue();
            if (spending >= PLATINUM.minSpending) return PLATINUM;
            if (spending >= GOLD.minSpending) return GOLD;
            if (spending >= SILVER.minSpending) return SILVER;
            return BRONZE;
        }
    }
    
    /**
     * Default constructor
     */
    public Customer() {
        this.membershipTier = MembershipTier.BRONZE;
        this.loyaltyPoints = 0;
        this.totalSpent = BigDecimal.ZERO;
        this.visitCount = 0;
        this.isActive = true;
        this.registrationDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor
     */
    public Customer(String fullName, String email, String phone, String address) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    // Getters and Setters
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public MembershipTier getMembershipTier() {
        return membershipTier;
    }
    
    public void setMembershipTier(MembershipTier membershipTier) {
        this.membershipTier = membershipTier;
    }
    
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public Integer getVisitCount() {
        return visitCount;
    }
    
    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }
    
    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }
    
    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Calculate average order value
     */
    public BigDecimal getAverageOrderValue() {
        if (visitCount == null || visitCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalSpent.divide(BigDecimal.valueOf(visitCount), 2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Check if customer is a frequent visitor (>10 visits)
     */
    public boolean isFrequentVisitor() {
        return visitCount != null && visitCount > 10;
    }
    
    /**
     * Check if customer visited recently (within 7 days)
     */
    public boolean isRecentVisitor() {
        if (lastVisitDate == null) return false;
        return LocalDate.now().minusDays(7).isBefore(lastVisitDate);
    }
    
    /**
     * Get customer status based on last visit
     */
    public String getCustomerStatus() {
        if (lastVisitDate == null) return "NEW";
        
        long daysSinceVisit = java.time.temporal.ChronoUnit.DAYS.between(lastVisitDate, LocalDate.now());
        
        if (daysSinceVisit <= 7) return "ACTIVE";
        if (daysSinceVisit <= 30) return "REGULAR";
        if (daysSinceVisit <= 90) return "INACTIVE";
        return "DORMANT";
    }
    
    /**
     * Add loyalty points
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints = (this.loyaltyPoints != null ? this.loyaltyPoints : 0) + points;
    }
    
    /**
     * Redeem loyalty points
     */
    public boolean redeemLoyaltyPoints(int points) {
        if (this.loyaltyPoints == null || this.loyaltyPoints < points) {
            return false;
        }
        this.loyaltyPoints -= points;
        return true;
    }
    
    /**
     * Update visit statistics
     */
    public void recordVisit(BigDecimal amount) {
        this.visitCount = (this.visitCount != null ? this.visitCount : 0) + 1;
        this.totalSpent = (this.totalSpent != null ? this.totalSpent : BigDecimal.ZERO).add(amount);
        this.lastVisitDate = LocalDate.now();
        
        // Auto-upgrade tier based on spending
        this.membershipTier = MembershipTier.fromSpending(this.totalSpent);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", membershipTier=" + membershipTier +
                ", loyaltyPoints=" + loyaltyPoints +
                ", totalSpent=" + totalSpent +
                ", visitCount=" + visitCount +
                '}';
    }
}
