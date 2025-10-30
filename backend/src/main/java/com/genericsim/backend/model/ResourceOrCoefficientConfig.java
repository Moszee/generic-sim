package com.genericsim.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for a resource or coefficient.
 * This represents the definition loaded from YAML/JSON configuration files.
 * 
 * Example YAML:
 * <pre>
 * id: "food"
 * type: "resource"
 * name: "Jedzenie"
 * min: 0
 * max: 10000
 * defaultValue: 100
 * production: 
 *   gathering: 1.0
 *   farming: 2.0
 * consumption:
 *   family: 3.0
 *   event: 1.0
 * affects: {}
 * description: "Basic sustenance for survival"
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
public class ResourceOrCoefficientConfig {
    
    /**
     * Unique identifier for this resource/coefficient (e.g., "food", "stability")
     */
    private String id;
    
    /**
     * Display name for UI (e.g., "Jedzenie", "Stabilność")
     */
    private String name;
    
    /**
     * Type: "resource" or "coefficient"
     */
    private String type;
    
    /**
     * Minimum allowed value
     */
    private double min = 0.0;
    
    /**
     * Maximum allowed value
     */
    private double max = 10000.0;
    
    /**
     * Default/initial value
     */
    private double defaultValue = 0.0;
    
    /**
     * Production rates by source (e.g., gathering: 1.0, farming: 2.0)
     * Maps source name to production rate multiplier
     */
    private Map<String, Double> production = new HashMap<>();
    
    /**
     * Consumption rates by consumer (e.g., family: 3.0, event: 1.0)
     * Maps consumer name to consumption rate
     */
    private Map<String, Double> consumption = new HashMap<>();
    
    /**
     * Effects on other resources/coefficients
     * Maps target id to effect strength (can be negative)
     */
    private Map<String, Double> affects = new HashMap<>();
    
    /**
     * Human-readable description
     */
    private String description;
    
    /**
     * Weight for transport (resources only)
     */
    private double weight = 1.0;
    
    /**
     * Decay rate per interval (resources only)
     */
    private double decayRate = 0.0;
    
    /**
     * Storage capacity (resources only)
     */
    private int storageCapacity = 1000;
    
    /**
     * Whether resource can be gathered/regenerated (resources only)
     */
    private boolean renewable = true;
}
