// src/main/java/com/framework/service/PlayerRepository.java

package com.framework.service;

import com.framework.data.entity.PlayerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

// PanacheRepository<EntityClass, IdType>
@ApplicationScoped
public class PlayerRepository implements PanacheRepository<PlayerEntity> {
    // Panache provides methods like persist(), findById(), findAll(), etc., automatically.
    
    // You can add custom finders if needed, e.g.:
    public PlayerEntity findByName(String name) {
        return find("name", name).firstResult();
    }
}