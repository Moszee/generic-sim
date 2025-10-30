package com.genericsim.backend.policy;

/**
 * Enumeration of simulation phases where policy effects can be applied.
 * Each phase represents a distinct point in the tick processing lifecycle.
 */
public enum PolicyPhase {
    /**
     * Resource gathering phase - when tribe members collect food and water.
     * Effects here can modify gathering rates, apply taxes, or adjust incentives.
     */
    GATHERING,
    
    /**
     * Post-gathering phase - after resources are collected but before consumption.
     * Effects here can process collected resources (e.g., central storage taxation).
     */
    POST_GATHERING,
    
    /**
     * Family upkeep phase - when families consume resources for daily needs.
     * Effects here can modify consumption rates or resource sharing behavior.
     */
    FAMILY_UPKEEP,
    
    /**
     * Storage decay phase - periodic spoilage of stored resources.
     * Effects here apply decay rules to family and central storage.
     */
    STORAGE_DECAY,
    
    /**
     * Aging and role transition phase - annual age advancement and role changes.
     * Effects here can modify aging rates or role transition rules.
     */
    AGING,
    
    /**
     * Progress calculation phase - computing progress points for technology/culture.
     * Effects here can modify progress generation or decay rates.
     */
    PROGRESS_CALCULATION,
    
    /**
     * Cleanup phase - removal of deceased members and final state updates.
     * Effects here handle end-of-tick cleanup and state reconciliation.
     */
    CLEANUP
}
