package com.genericsim.backend.policy;

/**
 * Enumeration of simulation phases where policy effects can be applied.
 * Each phase represents a distinct point in the tick processing lifecycle.
 * 
 * These generic phases are designed to be applicable across different simulation types,
 * not just hunter-gatherer tribes.
 */
public enum PolicyPhase {
    /**
     * Resource collection phase - when entities gather or acquire resources.
     * Effects here can modify collection rates, apply taxes, or adjust incentives.
     * Examples: hunting, gathering, mining, harvesting, scavenging.
     */
    RESOURCE_COLLECTION,
    
    /**
     * Production phase - when entities create or transform resources into goods.
     * Effects here can modify production rates, efficiency, or output quality.
     * Examples: crafting, manufacturing, processing, refining.
     */
    PRODUCTION,
    
    /**
     * Upkeep phase - when entities consume resources for maintenance and daily needs.
     * Effects here can modify consumption rates, sharing behavior, or resource allocation.
     * Examples: eating, drinking, shelter maintenance, tool upkeep.
     */
    UPKEEP,
    
    /**
     * Resource decay phase - periodic degradation or spoilage of stored resources.
     * Effects here apply decay rules to various storage types.
     * Examples: food spoilage, material degradation, tool wear.
     */
    RESOURCE_DECAY,
    
    /**
     * Population progress phase - individual entity advancement and changes.
     * Effects here can modify aging, skill development, role transitions, or health changes.
     * Examples: aging, profession changes, skill learning, promotions.
     */
    POPULATION_PROGRESS,
    
    /**
     * Society progress phase - collective advancement in culture, technology, and organization.
     * Effects here can modify research, cultural development, or societal evolution.
     * Examples: technology research, cultural milestones, organizational improvements.
     */
    SOCIETY_PROGRESS,
    
    /**
     * Cleanup phase - removal of invalid entities and final state reconciliation.
     * Effects here handle end-of-tick cleanup and state consistency.
     * Examples: removing deceased entities, garbage collection, state validation.
     */
    CLEANUP
}
