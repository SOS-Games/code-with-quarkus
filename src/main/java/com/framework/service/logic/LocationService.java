package com.framework.service.logic;

import com.framework.data.entity.AgentStateEntity;
import com.framework.data.model.Location;
import com.framework.data.staticdata.StaticLocationData;
import com.framework.service.repos.AgentStateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing player locations.
 */
@ApplicationScoped
public class LocationService {
    
    @Inject
    AgentStateRepository agentStateRepository;
    
    /**
     * Gets the current location for a player.
     * @param playerId The player ID
     * @return The Location object, or null if not found
     */
    public Location getCurrentLocation(String playerId) {
        AgentStateEntity agentState = agentStateRepository.findById(playerId);
        if (agentState == null || agentState.currentLocation == null) {
            return null;
        }
        try {
            return StaticLocationData.getLocation(agentState.currentLocation);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Moves a player to a new location.
     * @param playerId The player ID
     * @param targetLocationId The destination location ID
     * @return The new Location object
     */
    @Transactional
    public Location moveToLocation(String playerId, String targetLocationId) {
        // Validate location exists
        Location location = StaticLocationData.getLocation(targetLocationId);
        
        // Get or create agent state
        AgentStateEntity agentState = agentStateRepository.findById(playerId);
        if (agentState == null) {
            agentState = new AgentStateEntity(playerId, "player", targetLocationId);
            agentStateRepository.persist(agentState);
        } else {
            agentState.currentLocation = targetLocationId;
            agentStateRepository.persist(agentState);
        }
        
        return location;
    }
    
    /**
     * Gets or creates agent state for a player.
     * @param playerId The player ID
     * @return The AgentStateEntity
     */
    @Transactional
    public AgentStateEntity getOrCreateAgentState(String playerId) {
        AgentStateEntity agentState = agentStateRepository.findById(playerId);
        if (agentState == null) {
            agentState = new AgentStateEntity(playerId, "player", "starting_location");
            agentStateRepository.persist(agentState);
        }
        return agentState;
    }
}
