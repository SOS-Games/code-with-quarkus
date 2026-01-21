package com.framework.data.staticdata;

import com.framework.data.model.Action;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static data factory for actions.
 * Holds the catalog of all action blueprints in the game.
 */
public class StaticActionData {
    private static final Map<String, Action> actions = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the static action data.
     * This should be called during application startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // TODO: Add actual action definitions here
        // Example:
        // actions.put("mine_copper", new Action("mine_copper", "Mine Copper", 
        //     "mining", "copper_mining_drops", 10.0, 0.0)); // Continuous action
        
        initialized = true;
    }

    /**
     * Gets an action by its ID.
     * @param id The action ID
     * @return The Action object
     * @throws IllegalArgumentException if the action is not found
     */
    public static Action getAction(String id) {
        if (!initialized) {
            initialize();
        }
        Action action = actions.get(id);
        if (action == null) {
            throw new IllegalArgumentException("Action not found: " + id);
        }
        return action;
    }

    /**
     * Gets all actions.
     * @return An unmodifiable collection of all actions
     */
    public static Map<String, Action> getAllActions() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableMap(actions);
    }

    /**
     * Checks if an action exists.
     * @param id The action ID
     * @return true if the action exists, false otherwise
     */
    public static boolean hasAction(String id) {
        if (!initialized) {
            initialize();
        }
        return actions.containsKey(id);
    }
}
