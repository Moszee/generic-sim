package com.genericsim.backend.controller;

import com.genericsim.backend.model.ResourceOrCoefficientConfig;
import com.genericsim.backend.service.ResourceConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for accessing resource and coefficient configurations.
 */
@RestController
@RequestMapping("/api/config")
public class ResourceConfigController {
    
    private final ResourceConfigService configService;
    
    public ResourceConfigController(ResourceConfigService configService) {
        this.configService = configService;
    }
    
    /**
     * Get all configurations (resources and coefficients)
     */
    @GetMapping
    public ResponseEntity<Map<String, ResourceOrCoefficientConfig>> getAllConfigs() {
        return ResponseEntity.ok(configService.getAllConfigs());
    }
    
    /**
     * Get a specific configuration by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceOrCoefficientConfig> getConfig(@PathVariable String id) {
        ResourceOrCoefficientConfig config = configService.getConfig(id);
        if (config != null) {
            return ResponseEntity.ok(config);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Get all resource configurations
     */
    @GetMapping("/resources")
    public ResponseEntity<Map<String, ResourceOrCoefficientConfig>> getResourceConfigs() {
        return ResponseEntity.ok(configService.getResourceConfigs());
    }
    
    /**
     * Get all coefficient configurations
     */
    @GetMapping("/coefficients")
    public ResponseEntity<Map<String, ResourceOrCoefficientConfig>> getCoefficientConfigs() {
        return ResponseEntity.ok(configService.getCoefficientConfigs());
    }
}
