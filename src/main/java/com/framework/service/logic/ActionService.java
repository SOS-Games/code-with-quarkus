package com.framework.service.logic;

import com.framework.data.model.Action;
import com.framework.data.model.Item;
import com.framework.data.model.LootTable;
import com.framework.data.model.LootTable.LootDrop;
import com.framework.data.staticdata.StaticActionData;
import com.framework.service.core.StaticDataService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing game actions (mining, crafting, etc.).
 */
@ApplicationScoped
public class ActionService {
    
    @Inject
    StaticDataService staticDataService;
    
    @Inject
    InventoryService inventoryService;
    
    @Inject
    PlayerService playerService;
    
    private final Random random = new Random();
    
    // Track active actions: playerId -> actionId
    private final Map<String, String> activeActions = new ConcurrentHashMap<>();
    
    /**
     * Initiates a task/action for a player.
     * @param playerId The player ID
     * @param actionId The action ID
     */
    @Transactional
    public void startAction(String playerId, String actionId) {
        // Validate action exists (will throw if not found)
        StaticActionData.getAction(actionId);
        
        // Check if player is already performing an action
        if (activeActions.containsKey(playerId)) {
            throw new IllegalStateException("Player is already performing an action: " + activeActions.get(playerId));
        }
        
        // Start the action
        activeActions.put(playerId, actionId);
    }
    
    /**
     * Runs the core idle logic (XP gain, calls InventoryService.addItem for loot).
     * @param playerId The player ID
     * @param actionId The action ID
     * @param elapsedSeconds The elapsed time in seconds since last tick
     */
    @Transactional
    public void processActionTick(String playerId, String actionId, double elapsedSeconds) {
        // Verify action is active
        String activeActionId = activeActions.get(playerId);
        if (activeActionId == null || !activeActionId.equals(actionId)) {
            throw new IllegalStateException("Action is not active for player: " + playerId);
        }
        
        // Get action data
        Action action = StaticActionData.getAction(actionId);
        
        // Grant experience
        double experienceGained = action.getExperiencePerSecond() * elapsedSeconds;
        if (experienceGained > 0) {
            playerService.addExperience(playerId, action.getSkillId(), (long) experienceGained);
        }
        
        // Process loot table (simplified - in real game, might want to limit frequency)
        if (action.getLootTableId() != null && !action.getLootTableId().isBlank()) {
            // Roll for loot based on elapsed time (e.g., once per second)
            int lootRolls = (int) Math.floor(elapsedSeconds);
            for (int i = 0; i < lootRolls; i++) {
                executeLootTable(playerId, action.getLootTableId());
            }
        }
    }
    
    /**
     * Stops an active action for a player.
     * @param playerId The player ID
     */
    public void stopAction(String playerId) {
        activeActions.remove(playerId);
    }
    
    /**
     * Gets the currently active action for a player.
     * @param playerId The player ID
     * @return The action ID, or null if no action is active
     */
    public String getActiveAction(String playerId) {
        return activeActions.get(playerId);
    }
    
    /**
     * Executes a loot table roll and adds items to player inventory.
     * @param playerId The player ID
     * @param lootTableId The loot table ID
     * @return List of items that were dropped
     */
    @Transactional
    public List<Item> executeLootTable(String playerId, String lootTableId) {
        LootTable lootTable = staticDataService.getLootTable(lootTableId);
        List<Item> droppedItems = new ArrayList<>();
        
        for (LootDrop drop : lootTable.getDrops()) {
            if (random.nextDouble() < drop.getChance()) {
                // Roll successful, determine quantity
                int quantity = drop.getMinQuantity() + 
                    random.nextInt(drop.getMaxQuantity() - drop.getMinQuantity() + 1);
                
                // Add item to inventory
                inventoryService.addItem(playerId, drop.getItemId(), quantity);
                
                // Add to return list
                Item item = staticDataService.getItem(drop.getItemId());
                droppedItems.add(item);
            }
        }
        
        return droppedItems;
    }
    
    /**
     * Executes a mining action (example action).
     * @param playerId The player ID
     * @param locationId The location/mine ID
     * @param lootTableId The loot table for this location
     * @return List of items obtained
     */
    @Transactional
    public List<Item> executeMiningAction(String playerId, String locationId, String lootTableId) {
        // Execute the loot table
        List<Item> items = executeLootTable(playerId, lootTableId);
        
        // Grant experience (simplified - in real game, use skill-specific experience)
        playerService.addExperience(playerId, "mining", 10);
        
        return items;
    }
    
    /**
     * Executes a crafting action.
     * @param playerId The player ID
     * @param recipeId The recipe ID (simplified - in real game, use Recipe model)
     * @param inputItems Map of item IDs to quantities required
     * @param outputItemId The item ID to craft
     * @param outputQuantity The quantity to craft
     * @return true if crafting was successful
     */
    @Transactional
    public boolean executeCraftingAction(String playerId, String recipeId, 
                                        Map<String, Integer> inputItems,
                                        String outputItemId, int outputQuantity) {
        // Check if player has all required items
        for (Map.Entry<String, Integer> entry : inputItems.entrySet()) {
            if (!inventoryService.hasItem(playerId, entry.getKey(), entry.getValue())) {
                return false; // Missing required items
            }
        }
        
        // Remove input items
        for (Map.Entry<String, Integer> entry : inputItems.entrySet()) {
            inventoryService.removeItem(playerId, entry.getKey(), entry.getValue());
        }
        
        // Add output item
        inventoryService.addItem(playerId, outputItemId, outputQuantity);
        
        // Grant experience
        playerService.addExperience(playerId, "crafting", 15);
        
        return true;
    }
}
