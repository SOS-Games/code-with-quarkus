package com.framework.data.staticdata;

import com.framework.data.model.Location;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static data factory for locations.
 * Holds the catalog of all location blueprints in the game.
 */
public class StaticLocationData {
    private static final Map<String, Location> locations = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the static location data.
     * This should be called during application startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // TODO: Add actual location definitions here
        // Example:
        // locations.put("varrock_square", new Location("varrock_square", "Varrock Square", 
        //     "A bustling town square", List.of("mine_copper", "chop_tree")));
        
        initialized = true;
    }

    /**
     * Gets a location by its ID.
     * @param id The location ID
     * @return The Location object
     * @throws IllegalArgumentException if the location is not found
     */
    public static Location getLocation(String id) {
        if (!initialized) {
            initialize();
        }
        Location location = locations.get(id);
        if (location == null) {
            throw new IllegalArgumentException("Location not found: " + id);
        }
        return location;
    }

    /**
     * Gets all locations.
     * @return An unmodifiable collection of all locations
     */
    public static Map<String, Location> getAllLocations() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableMap(locations);
    }

    /**
     * Checks if a location exists.
     * @param id The location ID
     * @return true if the location exists, false otherwise
     */
    public static boolean hasLocation(String id) {
        if (!initialized) {
            initialize();
        }
        return locations.containsKey(id);
    }
}
