package com.framework.service.logic;

import com.framework.data.entity.PlayerEntity;
import com.framework.service.core.StaticDataService;
import com.framework.service.repos.PlayerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing player-related business logic.
 */
@ApplicationScoped
public class PlayerService {
    
    @Inject
    PlayerRepository playerRepository;
    
    @Inject
    StaticDataService staticDataService;
    
    @Inject
    InventoryService inventoryService;
    
    /**
     * Gets or creates a player. The single entry point for player login.
     * @param id The unique player ID
     * @return The PlayerEntity (existing or newly created)
     */
    @Transactional
    public PlayerEntity getOrCreatePlayer(String id) {
        PlayerEntity player = playerRepository.findById(id);
        if (player == null) {
            // Create new player with default name
            player = new PlayerEntity(id, "Player_" + id);
            playerRepository.persist(player);
        }
        return player;
    }
    
    /**
     * Saves player state/inventory changes to the database.
     * @param player The PlayerEntity to save
     * @return The saved PlayerEntity
     */
    @Transactional
    public PlayerEntity savePlayer(PlayerEntity player) {
        playerRepository.persist(player);
        return player;
    }
    
    /**
     * Creates a new player.
     * @param id The unique player ID
     * @param name The player's name
     * @return The created PlayerEntity
     */
    @Transactional
    public PlayerEntity createPlayer(String id, String name) {
        PlayerEntity player = new PlayerEntity(id, name);
        playerRepository.persist(player);
        return player;
    }
    
    /**
     * Gets a player by ID.
     * @param id The player ID
     * @return The PlayerEntity, or null if not found
     */
    public PlayerEntity getPlayer(String id) {
        return playerRepository.findById(id);
    }
    
    /**
     * Gets a player by name.
     * @param name The player's name
     * @return The PlayerEntity, or null if not found
     */
    public PlayerEntity getPlayerByName(String name) {
        return playerRepository.findByName(name);
    }
    
    /**
     * Updates player experience and level.
     * @param playerId The player ID
     * @param skillId The skill ID
     * @param experienceGained The experience gained
     * @return The updated PlayerEntity
     */
    @Transactional
    public PlayerEntity addExperience(String playerId, String skillId, long experienceGained) {
        PlayerEntity player = playerRepository.findById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }
        
        // Update total experience (simplified - in real game, track per-skill)
        player.experience += experienceGained;
        
        // Calculate level based on experience (simplified)
        // In a real game, you'd check against skill-specific experience curves
        player.level = calculateLevel(player.experience);
        
        playerRepository.persist(player);
        return player;
    }
    
    private int calculateLevel(long totalExperience) {
        // Simplified level calculation
        // In a real game, use skill-specific experience curves
        return (int) Math.min(100, Math.floor(Math.sqrt(totalExperience / 100.0)) + 1);
    }
}
