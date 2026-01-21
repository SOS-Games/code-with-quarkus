package com.framework.service.logic;

import com.framework.data.entity.PlayerEntity;
import com.framework.data.model.Item;
import com.framework.service.core.StaticDataService;
import com.framework.service.repos.PlayerItemRepository;
import com.framework.service.repos.PlayerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Service for managing combat-related business logic.
 */
@ApplicationScoped
public class CombatService {
    
    @Inject
    PlayerRepository playerRepository;
    
    @Inject
    PlayerItemRepository playerItemRepository;
    
    @Inject
    StaticDataService staticDataService;
    
    /**
     * Calculates the total attack bonus for a player from equipped items.
     * @param playerId The player ID
     * @return The total attack bonus
     */
    public int calculateAttackBonus(String playerId) {
        List<com.framework.data.entity.PlayerItemEntity> equipped = 
            playerItemRepository.findEquippedByOwnerId(playerId);
        
        int totalAttack = 0;
        for (com.framework.data.entity.PlayerItemEntity itemEntity : equipped) {
            Item item = staticDataService.getItem(itemEntity.itemId);
            if (item.isEquipable() && item.getEquipmentDetails() != null) {
                // Apply durability reduction if applicable
                int attackBonus = item.getEquipmentDetails().getAttackBonus();
                if (itemEntity.currentDurability != null) {
                    float durabilityRatio = (float) itemEntity.currentDurability / 
                        item.getEquipmentDetails().getDurabilityMax();
                    attackBonus = applyDurabilityReduction(attackBonus, durabilityRatio, 
                        item.getEquipmentDetails().getStatReductionMultipliers());
                }
                totalAttack += attackBonus;
            }
        }
        return totalAttack;
    }
    
    /**
     * Calculates the total defense bonus for a player from equipped items.
     * @param playerId The player ID
     * @return The total defense bonus
     */
    public int calculateDefenseBonus(String playerId) {
        List<com.framework.data.entity.PlayerItemEntity> equipped = 
            playerItemRepository.findEquippedByOwnerId(playerId);
        
        int totalDefense = 0;
        for (com.framework.data.entity.PlayerItemEntity itemEntity : equipped) {
            Item item = staticDataService.getItem(itemEntity.itemId);
            if (item.isEquipable() && item.getEquipmentDetails() != null) {
                // Apply durability reduction if applicable
                int defenseBonus = item.getEquipmentDetails().getDefenseBonus();
                if (itemEntity.currentDurability != null) {
                    float durabilityRatio = (float) itemEntity.currentDurability / 
                        item.getEquipmentDetails().getDurabilityMax();
                    defenseBonus = applyDurabilityReduction(defenseBonus, durabilityRatio, 
                        item.getEquipmentDetails().getStatReductionMultipliers());
                }
                totalDefense += defenseBonus;
            }
        }
        return totalDefense;
    }
    
    /**
     * Applies durability-based stat reduction.
     * @param baseStat The base stat value
     * @param durabilityRatio The current durability ratio (0.0 to 1.0)
     * @param multipliers The stat reduction multipliers map
     * @return The adjusted stat value
     */
    private int applyDurabilityReduction(int baseStat, float durabilityRatio, 
                                        java.util.Map<Float, Float> multipliers) {
        for (java.util.Map.Entry<Float, Float> entry : multipliers.entrySet()) {
            if (durabilityRatio <= entry.getKey()) {
                return (int) (baseStat * entry.getValue());
            }
        }
        return baseStat;
    }
    
    /**
     * Processes a combat action between two entities.
     * @param attackerId The attacker's ID
     * @param defenderId The defender's ID
     * @return The damage dealt
     */
    @Transactional
    public int processCombat(String attackerId, String defenderId) {
        PlayerEntity attacker = playerRepository.findById(attackerId);
        PlayerEntity defender = playerRepository.findById(defenderId);
        
        if (attacker == null || defender == null) {
            throw new IllegalArgumentException("Attacker or defender not found");
        }
        
        int attackBonus = calculateAttackBonus(attackerId);
        int defenseBonus = calculateDefenseBonus(defenderId);
        
        // Simplified combat calculation
        int baseDamage = 10 + attacker.level;
        int damage = Math.max(1, baseDamage + attackBonus - defenseBonus);
        
        // Apply damage (simplified - in real game, update health in AgentStateEntity)
        // For now, just return the damage
        return damage;
    }
}
