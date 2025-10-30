package com.genericsim.backend.model;

/**
 * Enumeration of resource types available in the simulation.
 * New resource types can be easily added to this enum.
 */
public enum ResourceType {
    FOOD("Food", "Basic sustenance for survival"),
    WATER("Water", "Essential liquid for hydration"),
    STONE("Stone", "Basic building and tool material"),
    WOOD("Wood", "Basic construction and fuel material");

    private final String displayName;
    private final String description;

    ResourceType(String displayName, String description) {
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
