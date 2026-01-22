package com.framework.data.staticdata;

import com.framework.data.model.EquipmentDetails;
import com.framework.data.model.Item;
import com.framework.data.model.ItemType;
import com.framework.data.model.EquipmentSlot;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static data factory for items.
 * Holds the catalog of all item blueprints in the game.
 */
public class StaticItemData {
    private static final Map<String, Item> items = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the static item data.
     * This should be called during application startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // TODO: Add actual item definitions here
        items.put("copper_ore", new Item("copper_ore", "Copper Ore", 10, true, ItemType.RESOURCE));

        items.put("bronze_sword", new Item("bronze_sword", "Bronze Sword", 100, false, 
            ItemType.EQUIPMENT, new EquipmentDetails(EquipmentSlot.WEAPON, 10, 10, 100, Map.of(1.0f, 1.0f))));
        
        initialized = true;
    }

    /**
     * Gets an item by its ID.
     * @param id The item ID
     * @return The Item object
     * @throws IllegalArgumentException if the item is not found
     */
    public static Item getItem(String id) {
        if (!initialized) {
            initialize();
        }
        Item item = items.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + id);
        }
        return item;
    }

    /**
     * Gets all items.
     * @return An unmodifiable collection of all items
     */
    public static Map<String, Item> getAllItems() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableMap(items);
    }

    /**
     * Checks if an item exists.
     * @param id The item ID
     * @return true if the item exists, false otherwise
     */
    public static boolean hasItem(String id) {
        if (!initialized) {
            initialize();
        }
        return items.containsKey(id);
    }
}
