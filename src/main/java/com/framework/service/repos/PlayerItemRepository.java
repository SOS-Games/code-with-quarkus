package com.framework.service.repos;

import com.framework.data.entity.PlayerItemEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repository for PlayerItemEntity.
 * PanacheRepositoryBase provides methods like persist(), findById(), findAll(), etc., automatically.
 */
@ApplicationScoped
public class PlayerItemRepository implements PanacheRepositoryBase<PlayerItemEntity, Long> {
    
    /**
     * Finds all items owned by a specific player.
     * @param ownerId The player's ID
     * @return List of PlayerItemEntity objects
     */
    public List<PlayerItemEntity> findByOwnerId(String ownerId) {
        return find("ownerId", ownerId).list();
    }
    
    /**
     * Finds a specific item instance for a player.
     * @param ownerId The player's ID
     * @param itemId The item blueprint ID
     * @return The PlayerItemEntity, or null if not found
     */
    public PlayerItemEntity findByOwnerIdAndItemId(String ownerId, String itemId) {
        return find("ownerId = ?1 and itemId = ?2", ownerId, itemId).firstResult();
    }
    
    /**
     * Finds all equipped items for a player (where slot is not INVENTORY).
     * @param ownerId The player's ID
     * @return List of equipped PlayerItemEntity objects
     */
    public List<PlayerItemEntity> findEquippedByOwnerId(String ownerId) {
        return find("ownerId = ?1 and slot != 'INVENTORY'", ownerId).list();
    }
}
