package com.genericsim.backend.model;

/**
 * Enumeration of lifestyle types for communities.
 * Represents different stages of societal development.
 */
public enum LifestyleType {
    HUNTER_GATHERER("Hunter-Gatherer", "Mobile communities that gather and hunt for food"),
    NOMADIC("Nomadic", "Mobile herding communities that follow resources"),
    SETTLED("Settled", "Permanent settlements with agriculture");

    private final String displayName;
    private final String description;

    LifestyleType(String displayName, String description) {
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
