// src/main/java/com/framework/service/TestService.java

package com.framework.service;

import com.framework.data.entity.PlayerEntity;
import com.framework.service.repos.PlayerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TestService {

    @Inject
    PlayerRepository playerRepository;

    // The @Transactional annotation ensures this method runs within a database transaction.
    @Transactional
    public PlayerEntity createAndFindTestPlayer() {
        String testId = "test_player_1";
        
        // 1. Delete old record (for clean test runs)
        playerRepository.delete("id", testId); 
        
        // 2. Create the new entity
        PlayerEntity newPlayer = new PlayerEntity(testId, "TestPlayerA");
        
        // 3. Persist (Save) the entity to the database
        playerRepository.persist(newPlayer);
        
        // 4. Find the entity from the database
        return playerRepository.find("id", testId).firstResult();
    }
}