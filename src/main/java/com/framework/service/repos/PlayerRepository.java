package com.framework.service.repos;

import com.framework.data.entity.PlayerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for PlayerEntity.
 * PanacheRepositoryBase provides methods like persist(), findById(), findAll(), etc., automatically.
 */
@ApplicationScoped
public class PlayerRepository implements PanacheRepositoryBase<PlayerEntity, String> {
    
    /**
     * Finds a player by name.
     * @param name The player's name
     * @return The PlayerEntity, or null if not found
     */
    public PlayerEntity findByName(String name) {
        return find("name", name).firstResult();
    }
}
