package com.framework.data.model;

import com.framework.data.staticdata.StaticItemData;
import java.util.List;

/**
 * Defines a reusable drop list (referenced by Actions and Creatures).
 * Immutable blueprint for loot table definitions.
 */
public class LootTable {
    private final String id;
    private final List<LootDrop> drops;

    /**
     * Creates a LootTable with validation of all item references.
     * @param id The unique identifier for this loot table
     * @param drops The list of loot drops
     * @throws IllegalArgumentException if any drop references a non-existent item
     */
    public LootTable(String id, List<LootDrop> drops) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("LootTable id cannot be null or blank");
        }
        if (drops == null) {
            throw new IllegalArgumentException("Drops list cannot be null");
        }
        
        // Cross-reference validation: ensure all itemIds exist
        for (LootDrop drop : drops) {
            if (drop.getItemId() == null || drop.getItemId().isBlank()) {
                throw new IllegalArgumentException("LootDrop itemId cannot be null or blank");
            }
            try {
                StaticItemData.getItem(drop.getItemId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "LootDrop references non-existent item: " + drop.getItemId() + 
                    " in LootTable: " + id, e);
            }
            
            // Validate chance is between 0 and 1
            if (drop.getChance() < 0.0 || drop.getChance() > 1.0) {
                throw new IllegalArgumentException(
                    "LootDrop chance must be between 0.0 and 1.0, got: " + drop.getChance());
            }
            
            // Validate quantity ranges
            if (drop.getMinQuantity() < 0) {
                throw new IllegalArgumentException("LootDrop minQuantity cannot be negative");
            }
            if (drop.getMaxQuantity() < drop.getMinQuantity()) {
                throw new IllegalArgumentException(
                    "LootDrop maxQuantity must be >= minQuantity");
            }
        }
        
        this.id = id;
        this.drops = List.copyOf(drops); // Immutable copy
    }

    public String getId() {
        return id;
    }

    public List<LootDrop> getDrops() {
        return drops; // Already immutable
    }

    /**
     * Nested class representing a single loot drop entry.
     */
    public static class LootDrop {
        private final String itemId;
        private final double chance;
        private final int minQuantity;
        private final int maxQuantity;

        public LootDrop(String itemId, double chance, int minQuantity, int maxQuantity) {
            if (itemId == null || itemId.isBlank()) {
                throw new IllegalArgumentException("LootDrop itemId cannot be null or blank");
            }
            if (chance < 0.0 || chance > 1.0) {
                throw new IllegalArgumentException("LootDrop chance must be between 0.0 and 1.0");
            }
            if (minQuantity < 0) {
                throw new IllegalArgumentException("LootDrop minQuantity cannot be negative");
            }
            if (maxQuantity < minQuantity) {
                throw new IllegalArgumentException("LootDrop maxQuantity must be >= minQuantity");
            }
            this.itemId = itemId;
            this.chance = chance;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
        }

        public String getItemId() {
            return itemId;
        }

        public double getChance() {
            return chance;
        }

        public int getMinQuantity() {
            return minQuantity;
        }

        public int getMaxQuantity() {
            return maxQuantity;
        }
    }
}
