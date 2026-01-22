package com.framework.data.staticdata;

import com.framework.data.model.Skill;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static data factory for skills.
 * Holds the catalog of all skill blueprints in the game.
 */
public class StaticSkillData {
    private static final Map<String, Skill> skills = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the static skill data.
     * This should be called during application startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // TODO: Add actual skill definitions here
        
        ArrayList<Integer> miningCurve = new ArrayList<Integer>(Arrays.asList(0, 100, 200, 400, 800, 1600));
        skills.put("mining", new Skill("mining", "Mining", miningCurve));
        
        initialized = true;
    }

    /**
     * Gets a skill by its ID.
     * @param id The skill ID
     * @return The Skill object
     * @throws IllegalArgumentException if the skill is not found
     */
    public static Skill getSkill(String id) {
        if (!initialized) {
            initialize();
        }
        Skill skill = skills.get(id);
        if (skill == null) {
            throw new IllegalArgumentException("Skill not found: " + id);
        }
        return skill;
    }

    /**
     * Gets all skills.
     * @return An unmodifiable collection of all skills
     */
    public static Map<String, Skill> getAllSkills() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableMap(skills);
    }

    /**
     * Checks if a skill exists.
     * @param id The skill ID
     * @return true if the skill exists, false otherwise
     */
    public static boolean hasSkill(String id) {
        if (!initialized) {
            initialize();
        }
        return skills.containsKey(id);
    }
}
