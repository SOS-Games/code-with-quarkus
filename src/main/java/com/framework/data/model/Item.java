package com.framework.data.model;

/**
 * Core item blueprint for everything in the game.
 * Immutable blueprint for item definitions.
 */
public class Item {
    private final String id;
    private final String name;
    private final int baseValue;
    private final boolean stackable;
    private final ItemType itemType;
    private final EquipmentDetails equipmentDetails; // null if not equipable

    public Item(String id, String name, int baseValue, boolean stackable, ItemType itemType) {
        this(id, name, baseValue, stackable, itemType, null);
    }

    public Item(String id, String name, int baseValue, boolean stackable, 
                ItemType itemType, EquipmentDetails equipmentDetails) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be null or blank");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("Item type cannot be null");
        }
        if (itemType == ItemType.EQUIPMENT && equipmentDetails == null) {
            throw new IllegalArgumentException("Equipment items must have equipment details");
        }
        if (itemType != ItemType.EQUIPMENT && equipmentDetails != null) {
            throw new IllegalArgumentException("Non-equipment items cannot have equipment details");
        }
        this.id = id;
        this.name = name;
        this.baseValue = baseValue;
        this.stackable = stackable;
        this.itemType = itemType;
        this.equipmentDetails = equipmentDetails;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public boolean isStackable() {
        return stackable;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public EquipmentDetails getEquipmentDetails() {
        return equipmentDetails;
    }

    public boolean isEquipable() {
        return equipmentDetails != null;
    }
}
