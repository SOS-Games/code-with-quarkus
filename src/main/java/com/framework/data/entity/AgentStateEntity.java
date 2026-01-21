package com.framework.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 * Represents the state of an agent (player or NPC).
 * Mutable entity persisted to the database.
 */
@Entity
@Table(name = "agent_state")
public class AgentStateEntity extends PanacheEntityBase {
    
    @Id
    public String id; // Unique identifier for the agent
    
    public String agentType; // "player", "npc", "creature", etc.
    
    public String currentLocation; // Current location/zone ID
    
    public int health;
    
    public int maxHealth;
    
    public int mana; // Optional, depending on game design
    
    public int maxMana;
    
    // JSON or serialized state for complex data
    public String stateData; // JSON string for flexible state storage
    
    // Hibernate requires a no-argument constructor
    public AgentStateEntity() {}
    
    public AgentStateEntity(String id, String agentType, String currentLocation) {
        this.id = id;
        this.agentType = agentType;
        this.currentLocation = currentLocation;
        this.health = 100;
        this.maxHealth = 100;
        this.mana = 0;
        this.maxMana = 0;
        this.stateData = null;
    }
}
