package com.framework.data.model;

import java.util.List;

/**
 * Defines the permanent skills in the game.
 * Immutable blueprint for skill definitions.
 */
public class Skill {
    private final String id;
    private final String name;
    private final List<Integer> experienceCurve;

    public Skill(String id, String name, List<Integer> experienceCurve) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Skill id cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be null or blank");
        }
        if (experienceCurve == null || experienceCurve.isEmpty()) {
            throw new IllegalArgumentException("Experience curve cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.experienceCurve = List.copyOf(experienceCurve); // Immutable copy
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getExperienceCurve() {
        return experienceCurve; // Already immutable
    }

    /**
     * Gets the total XP required to reach a specific level.
     * @param level The level (1-indexed)
     * @return The total XP required, or -1 if level is out of bounds
     */
    public int getExperienceForLevel(int level) {
        if (level < 1 || level > experienceCurve.size()) {
            return -1;
        }
        return experienceCurve.get(level - 1);
    }
}
