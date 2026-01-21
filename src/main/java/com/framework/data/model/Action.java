package com.framework.data.model;

/**
 * Defines a game action (mining, crafting, etc.).
 * Immutable blueprint for action definitions.
 */
public class Action {
    private final String id;
    private final String name;
    private final String skillId; // The skill this action trains
    private final String lootTableId; // The loot table for this action
    private final double experiencePerSecond; // Experience gained per second
    private final double durationSeconds; // How long the action takes (0 for continuous)
    
    public Action(String id, String name, String skillId, String lootTableId, 
                  double experiencePerSecond, double durationSeconds) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Action id cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Action name cannot be null or blank");
        }
        this.id = id;
        this.name = name;
        this.skillId = skillId;
        this.lootTableId = lootTableId;
        this.experiencePerSecond = experiencePerSecond;
        this.durationSeconds = durationSeconds;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getLootTableId() {
        return lootTableId;
    }
    
    public double getExperiencePerSecond() {
        return experiencePerSecond;
    }
    
    public double getDurationSeconds() {
        return durationSeconds;
    }
    
    public boolean isContinuous() {
        return durationSeconds <= 0;
    }
}
