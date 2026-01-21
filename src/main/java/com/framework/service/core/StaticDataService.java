package com.framework.service.core;

import com.framework.data.model.Item;
import com.framework.data.model.LootTable;
import com.framework.data.model.Skill;
import java.util.Collection;

/**
 * Main access point for all static data in the game.
 * Provides a unified interface for retrieving immutable game blueprints.
 */
public interface StaticDataService {
    
    /**
     * Retrieves an Item object by its ID.
     * @param id The item ID
     * @return The Item object
     * @throws IllegalArgumentException if the ID is not found
     */
    Item getItem(String id);
    
    /**
     * Retrieves a Skill object by its ID.
     * @param id The skill ID
     * @return The Skill object
     * @throws IllegalArgumentException if the ID is not found
     */
    Skill getSkill(String id);
    
    /**
     * Retrieves a LootTable object by its ID.
     * @param id The loot table ID
     * @return The LootTable object
     * @throws IllegalArgumentException if the ID is not found
     */
    LootTable getLootTable(String id);
    
    /**
     * Returns all items (e.g., for store restock logic).
     * @return A collection of all items
     */
    Collection<Item> getAllItems();
}
