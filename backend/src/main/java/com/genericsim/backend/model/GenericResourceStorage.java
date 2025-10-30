package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic storage for resources and coefficients with string-based IDs.
 * This replaces the enum-based ResourceStorage with a fully flexible system.
 */
@Entity
@Table(name = "generic_resource_storage")
@Getter
@Setter
@NoArgsConstructor
public class GenericResourceStorage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Map of resource/coefficient ID to current value
     * Example: {"food": 100.0, "water": 50.0, "stability": 0.8}
     */
    @ElementCollection
    @CollectionTable(name = "generic_resource_storage_values",
                     joinColumns = @JoinColumn(name = "storage_id"))
    @MapKeyColumn(name = "resource_id")
    @Column(name = "amount")
    private Map<String, Double> values = new HashMap<>();
    
    /**
     * Get value of a specific resource/coefficient
     */
    public double getValue(String resourceId) {
        return values.getOrDefault(resourceId, 0.0);
    }
    
    /**
     * Set value of a specific resource/coefficient
     */
    public void setValue(String resourceId, double value) {
        values.put(resourceId, Math.max(0.0, value));
    }
    
    /**
     * Add value to a specific resource/coefficient
     */
    public void addValue(String resourceId, double amount) {
        double current = getValue(resourceId);
        setValue(resourceId, current + amount);
    }
    
    /**
     * Remove value from a specific resource/coefficient
     * @return true if successful, false if insufficient value
     */
    public boolean removeValue(String resourceId, double amount) {
        double current = getValue(resourceId);
        if (current >= amount) {
            setValue(resourceId, current - amount);
            return true;
        }
        return false;
    }
    
    /**
     * Check if storage has at least the specified value
     */
    public boolean hasValue(String resourceId, double amount) {
        return getValue(resourceId) >= amount;
    }
    
    /**
     * Initialize with default values from configurations
     */
    public void initializeFromConfigs(Map<String, ResourceOrCoefficientConfig> configs) {
        for (Map.Entry<String, ResourceOrCoefficientConfig> entry : configs.entrySet()) {
            String id = entry.getKey();
            ResourceOrCoefficientConfig config = entry.getValue();
            values.putIfAbsent(id, config.getDefaultValue());
        }
    }
    
    /**
     * Export current state as a map
     */
    public Map<String, Double> exportState() {
        return new HashMap<>(values);
    }
    
    /**
     * Import state from a map
     */
    public void importState(Map<String, Double> state) {
        this.values.clear();
        this.values.putAll(state);
    }
}
