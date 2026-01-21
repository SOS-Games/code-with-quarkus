package com.framework.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 * Represents an item instance owned by a player.
 * Mutable entity persisted to the database.
 */
@Entity
@Table(name = "player_item")
public class PlayerItemEntity extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id; // Auto-generated primary key
    
    public String ownerId; // Foreign key to PlayerEntity (renamed from playerId)
    
    public String itemId; // Reference to static Item blueprint
    
    public int quantity; // For stackable items
    
    public String slot; // Equipment slot: WEAPON, HEAD, BODY, etc., or "INVENTORY" if not equipped
    
    public Float currentDurability; // The item's current health (for equipable items), null if not equipment
    
    // Hibernate requires a no-argument constructor
    public PlayerItemEntity() {}
    
    public PlayerItemEntity(String ownerId, String itemId, int quantity) {
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.slot = "INVENTORY"; // Default to inventory
        this.currentDurability = null;
    }
}
