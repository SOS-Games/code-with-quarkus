package com.framework.data.model;

import java.util.Map;

/**
 * Equipment details for items that can be equipped.
 * Immutable data structure.
 */
public class EquipmentDetails {
    private final EquipmentSlot slot;
    private final int attackBonus;
    private final int defenseBonus;
    private final int durabilityMax;
    private final Map<Float, Float> statReductionMultipliers;

    public EquipmentDetails(EquipmentSlot slot, int attackBonus, int defenseBonus, 
                           int durabilityMax, Map<Float, Float> statReductionMultipliers) {
        if (slot == null) {
            throw new IllegalArgumentException("Equipment slot cannot be null");
        }
        if (durabilityMax <= 0) {
            throw new IllegalArgumentException("Durability max must be positive");
        }
        if (statReductionMultipliers == null) {
            throw new IllegalArgumentException("Stat reduction multipliers cannot be null");
        }
        this.slot = slot;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.durabilityMax = durabilityMax;
        this.statReductionMultipliers = Map.copyOf(statReductionMultipliers); // Immutable copy
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getDurabilityMax() {
        return durabilityMax;
    }

    public Map<Float, Float> getStatReductionMultipliers() {
        return statReductionMultipliers; // Already immutable
    }
}
