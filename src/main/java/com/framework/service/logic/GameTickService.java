package com.framework.service.logic;

import com.framework.api.GameWebSocket;
import com.framework.data.entity.PlayerEntity;
import com.framework.data.entity.PlayerItemEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing game ticks and sending delta updates to players.
 */
@ApplicationScoped
public class GameTickService {
    
    @Inject
    ActionService actionService;
    
    @Inject
    PlayerService playerService;
    
    @Inject
    InventoryService inventoryService;
    
    @Inject
    GameWebSocket gameWebSocket;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, Map<String, Integer>> lastXpState = new HashMap<>(); // playerId -> skillId -> xp
    private final Map<String, Map<String, Integer>> lastInventoryState = new HashMap<>(); // playerId -> itemId -> quantity
    
    /**
     * Starts the game tick scheduler.
     */
    public void start() {
        // Run game tick every second
        scheduler.scheduleAtFixedRate(this::processGameTick, 1, 1, TimeUnit.SECONDS);
    }
    
    /**
     * Stops the game tick scheduler.
     */
    public void stop() {
        scheduler.shutdown();
    }
    
    private void processGameTick() {
        // Get all active actions and process them
        // Note: This is a simplified implementation
        // In a real game, you'd track all active players and their actions
        
        // For now, we'll process ticks when players send processActionTick messages
        // This method can be extended to automatically process all active actions
    }
    
    /**
     * Processes a game tick for a specific player and sends delta update.
     * @param playerId The player ID
     * @param elapsedSeconds The elapsed time since last tick
     */
    public void processPlayerTick(String playerId, double elapsedSeconds) {
        com.framework.service.logic.ActionState actionState = actionService.getActiveAction(playerId);
        if (actionState == null) {
            return; // No active action
        }
        
        // Process the action tick
        actionService.processActionTick(playerId, actionState.actionId, elapsedSeconds);
        
        // Calculate deltas
        PlayerEntity player = playerService.getPlayer(playerId);
        Map<String, Integer> xpDelta = calculateXpDelta(playerId, player);
        Map<String, Integer> inventoryDelta = calculateInventoryDelta(playerId);
        
        // Get agent state for health
        int health = 100; // Default, should come from AgentStateEntity
        
        // Send delta update
        gameWebSocket.sendStateDelta(playerId, xpDelta, inventoryDelta, health, null);
        
        // Update last known state
        updateLastState(playerId, player, inventoryDelta);
    }
    
    private Map<String, Integer> calculateXpDelta(String playerId, PlayerEntity player) {
        Map<String, Integer> delta = new HashMap<>();
        
        // Simplified: track total XP changes
        // In a real game, you'd track per-skill XP
        @SuppressWarnings("unused")
        Map<String, Integer> lastXp = lastXpState.getOrDefault(playerId, new HashMap<>());
        
        // For now, we'll return empty delta as XP tracking per skill needs more implementation
        // This is a placeholder that can be extended
        
        return delta;
    }
    
    private Map<String, Integer> calculateInventoryDelta(String playerId) {
        Map<String, Integer> delta = new HashMap<>();
        
        List<PlayerItemEntity> currentInventory = inventoryService.getInventory(playerId);
        Map<String, Integer> lastInventory = lastInventoryState.getOrDefault(playerId, new HashMap<>());
        
        // Calculate changes
        for (PlayerItemEntity item : currentInventory) {
            int lastQuantity = lastInventory.getOrDefault(item.itemId, 0);
            int currentQuantity = item.quantity;
            int change = currentQuantity - lastQuantity;
            if (change != 0) {
                delta.put(item.itemId, change);
            }
        }
        
        // Check for removed items
        for (Map.Entry<String, Integer> entry : lastInventory.entrySet()) {
            if (!delta.containsKey(entry.getKey())) {
                boolean stillExists = currentInventory.stream()
                    .anyMatch(item -> item.itemId.equals(entry.getKey()));
                if (!stillExists) {
                    delta.put(entry.getKey(), -entry.getValue());
                }
            }
        }
        
        return delta;
    }
    
    private void updateLastState(String playerId, PlayerEntity player, Map<String, Integer> inventoryDelta) {
        // Update inventory state
        List<PlayerItemEntity> currentInventory = inventoryService.getInventory(playerId);
        Map<String, Integer> inventoryState = new HashMap<>();
        for (PlayerItemEntity item : currentInventory) {
            inventoryState.put(item.itemId, item.quantity);
        }
        lastInventoryState.put(playerId, inventoryState);
    }
}
