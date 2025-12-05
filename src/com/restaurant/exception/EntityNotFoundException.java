package com.restaurant.exception;

/**
 * Exception thrown when a requested entity cannot be found in the database.
 * This is a general-purpose exception for any type of entity lookup failure.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class EntityNotFoundException extends RestaurantException {

    private static final long serialVersionUID = 1L;
    
    private final String entityType;
    private final Object entityId;

    /**
     * Constructs a new EntityNotFoundException.
     *
     * @param entityType the type of entity that was not found
     * @param entityId the ID of the entity that was not found
     */
    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format("%s with ID '%s' not found", entityType, entityId), "REST-404");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Constructs a new EntityNotFoundException with a custom message.
     *
     * @param entityType the type of entity that was not found
     * @param entityId the ID of the entity that was not found
     * @param message custom error message
     */
    public EntityNotFoundException(String entityType, Object entityId, String message) {
        super(message, "REST-404");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Gets the type of entity that was not found.
     *
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the ID of the entity that was not found.
     *
     * @return the entity ID
     */
    public Object getEntityId() {
        return entityId;
    }
}
