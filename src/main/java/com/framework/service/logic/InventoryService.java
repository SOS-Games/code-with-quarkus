package com.framework.service.logic;

import com.framework.data.entity.PlayerItemEntity;
import com.framework.data.model.Item;
import com.framework.service.core.StaticDataService;
import com.framework.service.repos.PlayerItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Service for managing player inventory.
 */
@ApplicationScoped
public class InventoryService {
    
    @Inject
    PlayerItemRepository playerItemRepository;
    
    @Inject
    StaticDataService staticDataService;
    
    /**
     * Adds an item to a player's inventory. Adds or stacks an item, creates a new PlayerItemEntity if needed.
     * @param playerId The player ID
     * @param itemId The item blueprint ID
     * @param quantity The quantity to add
     * @return The created or updated PlayerItemEntity
     */
    @Transactional
    public PlayerItemEntity addItem(String playerId, String itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Validate item exists
        Item item = staticDataService.getItem(itemId);
        
        // Check if player already has this item (and it's stackable) in inventory
        PlayerItemEntity existing = playerItemRepository.findByOwnerIdAndItemId(playerId, itemId);
        
        // Only stack if item is in inventory (not equipped) and is stackable
        if (existing != null && "INVENTORY".equals(existing.slot) && item.isStackable()) {
            // Stack with existing item
            existing.quantity += quantity;
            playerItemRepository.persist(existing);
            return existing;
        } else {
            // Create new item instance
            PlayerItemEntity newItem = new PlayerItemEntity(playerId, itemId, quantity);
            
            // Set initial durability for equipment
            if (item.isEquipable() && item.getEquipmentDetails() != null) {
                newItem.currentDurability = (float) item.getEquipmentDetails().getDurabilityMax();
            }
            
            playerItemRepository.persist(newItem);
            return newItem;
        }
    }
    
    /**
     * Removes an item from a player's inventory. Decrements stack or removes the entity entirely.
     * @param playerId The player ID
     * @param itemId The item blueprint ID
     * @param quantity The quantity to remove
     * @return true if the item was removed, false if not found or insufficient quantity
     */
    @Transactional
    public boolean removeItem(String playerId, String itemId, int quantity) {
        PlayerItemEntity item = playerItemRepository.findByOwnerIdAndItemId(playerId, itemId);
        if (item == null) {
            return false;
        }
        
        if (item.quantity < quantity) {
            return false; // Insufficient quantity
        }
        
        item.quantity -= quantity;
        if (item.quantity <= 0) {
            playerItemRepository.delete(item);
        } else {
            playerItemRepository.persist(item);
        }
        return true;
    }
    
    /**
     * Gets all items in a player's inventory.
     * @param playerId The player ID
     * @return List of PlayerItemEntity objects
     */
    public List<PlayerItemEntity> getInventory(String playerId) {
        return playerItemRepository.findByOwnerId(playerId);
    }
    
    /**
     * Checks if a player has a sufficient quantity of an item.
     * @param playerId The player ID
     * @param itemId The item blueprint ID
     * @param requiredQuantity The required quantity
     * @return true if the player has at least the required quantity
     */
    public boolean hasItem(String playerId, String itemId, int requiredQuantity) {
        PlayerItemEntity item = playerItemRepository.findByOwnerIdAndItemId(playerId, itemId);
        return item != null && item.quantity >= requiredQuantity;
    }
    
    /**
     * Equips an item. Moves the item from INVENTORY to the target slot in the DB.
     * @param playerId The player ID
     * @param playerItemId The PlayerItemEntity ID (Long)
     * @param slot The equipment slot (WEAPON, HEAD, BODY, etc.)
     * @return The equipped PlayerItemEntity
     */
    @Transactional
    public PlayerItemEntity equipItem(String playerId, Long playerItemId, String slot) {
        PlayerItemEntity item = playerItemRepository.findById(playerItemId);
        if (item == null || !item.ownerId.equals(playerId)) {
            throw new IllegalArgumentException("Item not found or not owned by player");
        }
        
        if (!"INVENTORY".equals(item.slot)) {
            throw new IllegalArgumentException("Item is already equipped in slot: " + item.slot);
        }
        
        Item itemBlueprint = staticDataService.getItem(item.itemId);
        if (!itemBlueprint.isEquipable()) {
            throw new IllegalArgumentException("Item is not equipable: " + item.itemId);
        }
        
        // Validate slot matches item's equipment slot
        String itemSlot = itemBlueprint.getEquipmentDetails().getSlot().name();
        if (!itemSlot.equals(slot)) {
            throw new IllegalArgumentException("Item slot mismatch: item is " + itemSlot + ", requested " + slot);
        }
        
        // Unequip any item in the same slot
        List<PlayerItemEntity> equippedInSlot = playerItemRepository.find("ownerId = ?1 and slot = ?2", 
            playerId, slot).list();
        for (PlayerItemEntity equipped : equippedInSlot) {
            equipped.slot = "INVENTORY";
            playerItemRepository.persist(equipped);
        }
        
        // Equip the new item
        item.slot = slot;
        playerItemRepository.persist(item);
        
        return item;
    }
    
    /**
     * Gets all equipped items for a player. Retrieves all PlayerItemEntity objects where slot is not INVENTORY.
     * @param playerId The player ID
     * @return List of equipped PlayerItemEntity objects
     */
    public List<PlayerItemEntity> getEquippedItems(String playerId) {
        return playerItemRepository.findEquippedByOwnerId(playerId);
    }
}
