package com.genericsim.backend.model;

/**
 * Enumeration of technology types available in the simulation.
 * New technologies can be easily added to this enum.
 */
public enum TechnologyType {
    FIRE("Fire", "Basic fire-making technology for cooking and warmth"),
    STONE_TOOLS("Stone Tools", "Basic tools made from stone"),
    AGRICULTURE("Agriculture", "Basic farming and crop cultivation"),
    ANIMAL_HUSBANDRY("Animal Husbandry", "Domestication and raising of animals");

    private final String displayName;
    private final String description;

    TechnologyType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
