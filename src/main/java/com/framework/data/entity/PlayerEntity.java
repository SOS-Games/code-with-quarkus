// src/main/java/com/framework/data/entity/PlayerEntity.java

package com.framework.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

// 1. Mark as a JPA Entity
@Entity
// 2. Map to a specific table name in Postgres
@Table(name = "player") 
public class PlayerEntity extends PanacheEntityBase {

    // 3. Mark the primary key. For a player, the ID can be a simple UUID or a String
    @Id
    public String id; 

    // Basic mutable fields for the player state
    public String name;
    public int level;
    public long experience;
    
    // Add the complex relations later (like one-to-many for Inventory)

    // Hibernate requires a no-argument constructor
    public PlayerEntity() {} 

    public PlayerEntity(String id, String name) {
        this.id = id;
        this.name = name;
        this.level = 1;
        this.experience = 0;
    }
    
    // Getters and setters (omitted for brevity, but needed for proper encapsulation)
}