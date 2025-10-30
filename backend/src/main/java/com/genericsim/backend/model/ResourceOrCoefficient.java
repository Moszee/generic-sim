package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic entity representing either a resource or a coefficient in the simulation.
 * This entity stores the runtime state of a resource/coefficient for a specific tribe or context.
 * The configuration/definition comes from ResourceOrCoefficientConfig.
 */
@Entity
@Table(name = "resources_or_coefficients")
@Getter
@Setter
@NoArgsConstructor
public class ResourceOrCoefficient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to configuration ID (e.g., "food", "stability")
     */
    @Column(nullable = false)
    private String configId;
    
    /**
     * Type: RESOURCE or COEFFICIENT
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceOrCoefficientType type;
    
    /**
     * Current value
     */
    @Column(nullable = false)
    private double currentValue;
    
    /**
     * Minimum value (copied from config for runtime checks)
     */
    @Column(nullable = false)
    private double minValue;
    
    /**
     * Maximum value (copied from config for runtime checks)
     */
    @Column(nullable = false)
    private double maxValue;
    
    /**
     * Optional: production rates by source
     * Stored as JSON in database
     */
    @ElementCollection
    @CollectionTable(name = "resource_coefficient_production_rates",
                     joinColumns = @JoinColumn(name = "resource_coefficient_id"))
    @MapKeyColumn(name = "source")
    @Column(name = "rate")
    private Map<String, Double> productionRates = new HashMap<>();
    
    /**
     * Optional: consumption rates by consumer
     * Stored as JSON in database
     */
    @ElementCollection
    @CollectionTable(name = "resource_coefficient_consumption_rates",
                     joinColumns = @JoinColumn(name = "resource_coefficient_id"))
    @MapKeyColumn(name = "consumer")
    @Column(name = "rate")
    private Map<String, Double> consumptionRates = new HashMap<>();
    
    public ResourceOrCoefficient(String configId, ResourceOrCoefficientType type, 
                                 double currentValue, double minValue, double maxValue) {
        this.configId = configId;
        this.type = type;
        this.currentValue = currentValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    /**
     * Add to current value, respecting min/max bounds
     */
    public void addValue(double amount) {
        this.currentValue = Math.min(maxValue, Math.max(minValue, currentValue + amount));
    }
    
    /**
     * Subtract from current value, respecting min/max bounds
     * @return true if subtraction was successful, false if insufficient value
     */
    public boolean subtractValue(double amount) {
        if (currentValue >= amount) {
            this.currentValue = Math.max(minValue, currentValue - amount);
            return true;
        }
        return false;
    }
    
    /**
     * Set current value, respecting min/max bounds
     */
    public void setValue(double value) {
        this.currentValue = Math.min(maxValue, Math.max(minValue, value));
    }
}
