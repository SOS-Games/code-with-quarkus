package com.framework.data.staticdata;

import com.framework.data.model.LootTable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static data factory for loot tables.
 * Holds the catalog of all loot table blueprints in the game.
 */
public class StaticLootTableData {
    private static final Map<String, LootTable> lootTables = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the static loot table data.
     * This should be called during application startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // TODO: Add actual loot table definitions here
        // Example:
        // List<LootTable.LootDrop> drops = List.of(
        //     new LootTable.LootDrop("copper_ore", 0.5, 1, 3),
        //     new LootTable.LootDrop("iron_ore", 0.2, 1, 2)
        // );
        // lootTables.put("common_mining_drops", new LootTable("common_mining_drops", drops));
        
        initialized = true;
    }

    /**
     * Gets a loot table by its ID.
     * @param id The loot table ID
     * @return The LootTable object
     * @throws IllegalArgumentException if the loot table is not found
     */
    public static LootTable getLootTable(String id) {
        if (!initialized) {
            initialize();
        }
        LootTable lootTable = lootTables.get(id);
        if (lootTable == null) {
            throw new IllegalArgumentException("LootTable not found: " + id);
        }
        return lootTable;
    }

    /**
     * Gets all loot tables.
     * @return An unmodifiable collection of all loot tables
     */
    public static Map<String, LootTable> getAllLootTables() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableMap(lootTables);
    }

    /**
     * Checks if a loot table exists.
     * @param id The loot table ID
     * @return true if the loot table exists, false otherwise
     */
    public static boolean hasLootTable(String id) {
        if (!initialized) {
            initialize();
        }
        return lootTables.containsKey(id);
    }
}
