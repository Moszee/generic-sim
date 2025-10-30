package com.genericsim.backend.service;

import com.genericsim.backend.config.ResourceConfigurationProperties;
import com.genericsim.backend.model.ResourceOrCoefficientConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service for managing resource and coefficient configurations.
 * Loads configurations from YAML/JSON and provides access to them.
 */
@Service
public class ResourceConfigService {
    
    private final ResourceConfigurationProperties configProperties;
    
    /**
     * Map of all configurations by ID
     */
    private final Map<String, ResourceOrCoefficientConfig> configMap = new LinkedHashMap<>();
    
    public ResourceConfigService(ResourceConfigurationProperties configProperties) {
        this.configProperties = configProperties;
    }
    
    @PostConstruct
    public void initialize() {
        // Load resources
        for (ResourceOrCoefficientConfig config : configProperties.getResources()) {
            if (config.getType() == null) {
                config.setType("resource");
            }
            configMap.put(config.getId(), config);
        }
        
        // Load coefficients
        for (ResourceOrCoefficientConfig config : configProperties.getCoefficients()) {
            if (config.getType() == null) {
                config.setType("coefficient");
            }
            configMap.put(config.getId(), config);
        }
    }
    
    /**
     * Get configuration by ID
     */
    public ResourceOrCoefficientConfig getConfig(String id) {
        return configMap.get(id);
    }
    
    /**
     * Get all configurations
     */
    public Map<String, ResourceOrCoefficientConfig> getAllConfigs() {
        return new LinkedHashMap<>(configMap);
    }
    
    /**
     * Check if a configuration exists
     */
    public boolean hasConfig(String id) {
        return configMap.containsKey(id);
    }
    
    /**
     * Get all resource configurations
     */
    public Map<String, ResourceOrCoefficientConfig> getResourceConfigs() {
        Map<String, ResourceOrCoefficientConfig> resources = new LinkedHashMap<>();
        for (Map.Entry<String, ResourceOrCoefficientConfig> entry : configMap.entrySet()) {
            if ("resource".equalsIgnoreCase(entry.getValue().getType())) {
                resources.put(entry.getKey(), entry.getValue());
            }
        }
        return resources;
    }
    
    /**
     * Get all coefficient configurations
     */
    public Map<String, ResourceOrCoefficientConfig> getCoefficientConfigs() {
        Map<String, ResourceOrCoefficientConfig> coefficients = new LinkedHashMap<>();
        for (Map.Entry<String, ResourceOrCoefficientConfig> entry : configMap.entrySet()) {
            if ("coefficient".equalsIgnoreCase(entry.getValue().getType())) {
                coefficients.put(entry.getKey(), entry.getValue());
            }
        }
        return coefficients;
    }
}
