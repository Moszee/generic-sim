package com.genericsim.backend.integration;

import com.genericsim.backend.model.GenericResourceStorage;
import com.genericsim.backend.model.ResourceOrCoefficientConfig;
import com.genericsim.backend.repository.GenericResourceStorageRepository;
import com.genericsim.backend.service.ResourceConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating the complete flexible resource and coefficient system.
 * This test validates that configurations are loaded from YAML and can be used with storage.
 */
@SpringBootTest
@Transactional
class FlexibleResourceIntegrationTest {
    
    @Autowired
    private ResourceConfigService configService;
    
    @Autowired
    private GenericResourceStorageRepository storageRepository;
    
    @Test
    void testConfigurationsAreLoadedFromYaml() {
        // Verify resources are loaded
        assertNotNull(configService.getConfig("food"));
        assertNotNull(configService.getConfig("water"));
        assertNotNull(configService.getConfig("stone"));
        assertNotNull(configService.getConfig("wood"));
        
        // Verify coefficients are loaded
        assertNotNull(configService.getConfig("stability"));
        assertNotNull(configService.getConfig("morale"));
        assertNotNull(configService.getConfig("cohesion"));
        assertNotNull(configService.getConfig("demand"));
        assertNotNull(configService.getConfig("technology"));
    }
    
    @Test
    void testResourceConfigurationDetails() {
        ResourceOrCoefficientConfig food = configService.getConfig("food");
        
        assertEquals("food", food.getId());
        assertEquals("Jedzenie", food.getName());
        assertEquals("resource", food.getType());
        assertEquals(0, food.getMin());
        assertEquals(10000, food.getMax());
        assertEquals(100, food.getDefaultValue());
        assertEquals("Basic sustenance for survival", food.getDescription());
        
        // Check production rates
        assertNotNull(food.getProduction());
        assertEquals(1.0, food.getProduction().get("gathering"));
        assertEquals(2.0, food.getProduction().get("farming"));
        
        // Check consumption rates
        assertNotNull(food.getConsumption());
        assertEquals(3.0, food.getConsumption().get("family"));
        assertEquals(1.0, food.getConsumption().get("event"));
        
        // Resource-specific properties
        assertEquals(0.1, food.getDecayRate());
        assertEquals(1000, food.getStorageCapacity());
        assertEquals(1.0, food.getWeight());
        assertTrue(food.isRenewable());
    }
    
    @Test
    void testCoefficientConfigurationDetails() {
        ResourceOrCoefficientConfig morale = configService.getConfig("morale");
        
        assertEquals("morale", morale.getId());
        assertEquals("Morale", morale.getName());
        assertEquals("coefficient", morale.getType());
        assertEquals(0, morale.getMin());
        assertEquals(1, morale.getMax());
        assertEquals(0.5, morale.getDefaultValue());
        
        // Check affects relationship
        assertNotNull(morale.getAffects());
        assertEquals(0.2, morale.getAffects().get("stability"));
    }
    
    @Test
    void testStorageInitializationFromConfigs() {
        GenericResourceStorage storage = new GenericResourceStorage();
        storage.initializeFromConfigs(configService.getAllConfigs());
        
        // Verify resources initialized with default values
        assertEquals(100.0, storage.getValue("food"));
        assertEquals(100.0, storage.getValue("water"));
        assertEquals(0.0, storage.getValue("stone"));
        assertEquals(0.0, storage.getValue("wood"));
        
        // Verify coefficients initialized with default values
        assertEquals(0.5, storage.getValue("stability"));
        assertEquals(0.5, storage.getValue("morale"));
        assertEquals(0.5, storage.getValue("cohesion"));
        assertEquals(10.0, storage.getValue("demand"));
        assertEquals(0.0, storage.getValue("technology"));
    }
    
    @Test
    void testStoragePersistence() {
        // Create and initialize storage
        GenericResourceStorage storage = new GenericResourceStorage();
        storage.initializeFromConfigs(configService.getAllConfigs());
        
        // Modify some values
        storage.setValue("food", 250.0);
        storage.setValue("stability", 0.8);
        
        // Save to database
        GenericResourceStorage saved = storageRepository.save(storage);
        assertNotNull(saved.getId());
        
        // Clear persistence context
        storageRepository.flush();
        
        // Reload from database
        GenericResourceStorage loaded = storageRepository.findById(saved.getId()).orElse(null);
        assertNotNull(loaded);
        
        // Verify values persisted
        assertEquals(250.0, loaded.getValue("food"));
        assertEquals(0.8, loaded.getValue("stability"));
        assertEquals(100.0, loaded.getValue("water")); // Unchanged value
    }
    
    @Test
    void testExportImportState() {
        GenericResourceStorage storage = new GenericResourceStorage();
        storage.initializeFromConfigs(configService.getAllConfigs());
        
        // Set some custom values
        storage.setValue("food", 300.0);
        storage.setValue("water", 150.0);
        storage.setValue("stability", 0.9);
        storage.setValue("morale", 0.7);
        
        // Export state
        Map<String, Double> exportedState = storage.exportState();
        assertEquals(300.0, exportedState.get("food"));
        assertEquals(150.0, exportedState.get("water"));
        assertEquals(0.9, exportedState.get("stability"));
        assertEquals(0.7, exportedState.get("morale"));
        
        // Create new storage and import state
        GenericResourceStorage newStorage = new GenericResourceStorage();
        newStorage.importState(exportedState);
        
        // Verify imported values
        assertEquals(300.0, newStorage.getValue("food"));
        assertEquals(150.0, newStorage.getValue("water"));
        assertEquals(0.9, newStorage.getValue("stability"));
        assertEquals(0.7, newStorage.getValue("morale"));
    }
    
    @Test
    void testResourceOperations() {
        GenericResourceStorage storage = new GenericResourceStorage();
        storage.setValue("food", 100.0);
        
        // Add resource
        storage.addValue("food", 50.0);
        assertEquals(150.0, storage.getValue("food"));
        
        // Remove resource successfully
        assertTrue(storage.removeValue("food", 30.0));
        assertEquals(120.0, storage.getValue("food"));
        
        // Try to remove more than available
        assertFalse(storage.removeValue("food", 200.0));
        assertEquals(120.0, storage.getValue("food")); // Should remain unchanged
        
        // Check if has enough
        assertTrue(storage.hasValue("food", 100.0));
        assertFalse(storage.hasValue("food", 150.0));
    }
    
    @Test
    void testCoefficientOperations() {
        GenericResourceStorage storage = new GenericResourceStorage();
        
        // Set coefficient
        storage.setValue("stability", 0.5);
        assertEquals(0.5, storage.getValue("stability"));
        
        // Increase coefficient
        storage.addValue("stability", 0.3);
        assertEquals(0.8, storage.getValue("stability"));
        
        // Decrease coefficient
        storage.removeValue("stability", 0.2);
        assertEquals(0.6, storage.getValue("stability"), 0.0001);
    }
    
    @Test
    void testMixedResourcesAndCoefficients() {
        GenericResourceStorage storage = new GenericResourceStorage();
        storage.initializeFromConfigs(configService.getAllConfigs());
        
        // Verify we can work with both resources and coefficients simultaneously
        storage.setValue("food", 200.0);
        storage.setValue("water", 150.0);
        storage.setValue("stability", 0.8);
        storage.setValue("morale", 0.9);
        
        assertEquals(200.0, storage.getValue("food"));
        assertEquals(150.0, storage.getValue("water"));
        assertEquals(0.8, storage.getValue("stability"));
        assertEquals(0.9, storage.getValue("morale"));
        
        // Verify separate resource and coefficient configs
        Map<String, ResourceOrCoefficientConfig> resourceConfigs = configService.getResourceConfigs();
        Map<String, ResourceOrCoefficientConfig> coefficientConfigs = configService.getCoefficientConfigs();
        
        assertTrue(resourceConfigs.containsKey("food"));
        assertFalse(resourceConfigs.containsKey("stability"));
        
        assertTrue(coefficientConfigs.containsKey("stability"));
        assertFalse(coefficientConfigs.containsKey("food"));
    }
    
    @Test
    void testConfigurationImmutability() {
        // Get config multiple times
        ResourceOrCoefficientConfig food1 = configService.getConfig("food");
        ResourceOrCoefficientConfig food2 = configService.getConfig("food");
        
        // Should be the same instance (singleton behavior)
        assertSame(food1, food2);
        
        // Configuration should exist
        assertTrue(configService.hasConfig("food"));
        assertFalse(configService.hasConfig("nonexistent"));
    }
}
