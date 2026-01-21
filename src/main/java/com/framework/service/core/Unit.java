package com.framework.service.core;

/**
 * Interface/Abstract base class for game units.
 * This can be extended for different types of units (players, NPCs, creatures, etc.).
 */
public interface Unit {
    /**
     * Gets the unique identifier of this unit.
     * @return The unit's ID
     */
    String getId();
    
    /**
     * Gets the name of this unit.
     * @return The unit's name
     */
    String getName();
    
    /**
     * Gets the current level of this unit.
     * @return The unit's level
     */
    int getLevel();
}
