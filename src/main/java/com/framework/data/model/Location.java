package com.framework.data.model;

import java.util.List;

/**
 * Defines a game location/zone.
 * Immutable blueprint for location definitions.
 */
public class Location {
    private final String id;
    private final String name;
    private final String description;
    private final List<String> availableActionIds; // List of action IDs available at this location
    
    public Location(String id, String name, String description, List<String> availableActionIds) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Location id cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be null or blank");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.availableActionIds = availableActionIds != null ? List.copyOf(availableActionIds) : List.of();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<String> getAvailableActionIds() {
        return availableActionIds;
    }
}
