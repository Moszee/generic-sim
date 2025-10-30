package com.genericsim.backend.model;

/**
 * Type of a generic resource or coefficient.
 */
public enum ResourceOrCoefficientType {
    /**
     * A tangible resource that can be gathered, stored, and consumed (e.g., food, water, wood).
     */
    RESOURCE,
    
    /**
     * An abstract coefficient representing community metrics (e.g., stability, morale, cohesion).
     */
    COEFFICIENT
}
