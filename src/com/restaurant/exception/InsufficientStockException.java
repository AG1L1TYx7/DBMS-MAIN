package com.restaurant.exception;

/**
 * Exception thrown when there is insufficient stock for a product.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class InsufficientStockException extends RestaurantException {

    private static final long serialVersionUID = 1L;
    
    private final Integer productId;
    private final String productName;
    private final int requestedQuantity;
    private final int availableQuantity;

    /**
     * Constructs a new InsufficientStockException.
     *
     * @param productId the product ID
     * @param productName the product name
     * @param requestedQuantity the quantity requested
     * @param availableQuantity the quantity available
     */
    public InsufficientStockException(Integer productId, String productName, 
            int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for '%s': requested %d, available %d", 
                productName, requestedQuantity, availableQuantity), "REST-409");
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Constructs a new InsufficientStockException with just product name.
     *
     * @param productName the product name
     * @param requestedQuantity the quantity requested
     * @param availableQuantity the quantity available
     */
    public InsufficientStockException(String productName, int requestedQuantity, int availableQuantity) {
        this(null, productName, requestedQuantity, availableQuantity);
    }

    /**
     * Gets the product ID.
     *
     * @return the product ID
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Gets the requested quantity.
     *
     * @return the requested quantity
     */
    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    /**
     * Gets the available quantity.
     *
     * @return the available quantity
     */
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Gets the shortage amount.
     *
     * @return the difference between requested and available
     */
    public int getShortage() {
        return requestedQuantity - availableQuantity;
    }
}
