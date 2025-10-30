package com.genericsim.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GenericResourceStorage - the flexible resource/coefficient storage system
 */
class GenericResourceStorageTest {
    
    private GenericResourceStorage storage;
    
    @BeforeEach
    void setUp() {
        storage = new GenericResourceStorage();
    }
    
    @Test
    void testGetValueReturnsZeroForNonExistentResource() {
        assertEquals(0.0, storage.getValue("food"));
    }
    
    @Test
    void testSetAndGetValue() {
        storage.setValue("food", 100.0);
        assertEquals(100.0, storage.getValue("food"));
    }
    
    @Test
    void testSetValueDoesNotAllowNegative() {
        storage.setValue("food", -50.0);
        assertEquals(0.0, storage.getValue("food"));
    }
    
    @Test
    void testAddValue() {
        storage.setValue("food", 50.0);
        storage.addValue("food", 30.0);
        assertEquals(80.0, storage.getValue("food"));
    }
    
    @Test
    void testRemoveValueSuccess() {
        storage.setValue("food", 100.0);
        assertTrue(storage.removeValue("food", 30.0));
        assertEquals(70.0, storage.getValue("food"));
    }
    
    @Test
    void testRemoveValueFailsWhenInsufficient() {
        storage.setValue("food", 20.0);
        assertFalse(storage.removeValue("food", 30.0));
        assertEquals(20.0, storage.getValue("food"));
    }
    
    @Test
    void testHasValue() {
        storage.setValue("food", 50.0);
        assertTrue(storage.hasValue("food", 30.0));
        assertTrue(storage.hasValue("food", 50.0));
        assertFalse(storage.hasValue("food", 60.0));
    }
    
    @Test
    void testInitializeFromConfigs() {
        Map<String, ResourceOrCoefficientConfig> configs = new HashMap<>();
        
        ResourceOrCoefficientConfig foodConfig = new ResourceOrCoefficientConfig();
        foodConfig.setId("food");
        foodConfig.setDefaultValue(100.0);
        configs.put("food", foodConfig);
        
        ResourceOrCoefficientConfig waterConfig = new ResourceOrCoefficientConfig();
        waterConfig.setId("water");
        waterConfig.setDefaultValue(50.0);
        configs.put("water", waterConfig);
        
        storage.initializeFromConfigs(configs);
        
        assertEquals(100.0, storage.getValue("food"));
        assertEquals(50.0, storage.getValue("water"));
    }
    
    @Test
    void testInitializeFromConfigsDoesNotOverrideExisting() {
        storage.setValue("food", 200.0);
        
        Map<String, ResourceOrCoefficientConfig> configs = new HashMap<>();
        ResourceOrCoefficientConfig foodConfig = new ResourceOrCoefficientConfig();
        foodConfig.setId("food");
        foodConfig.setDefaultValue(100.0);
        configs.put("food", foodConfig);
        
        storage.initializeFromConfigs(configs);
        
        // Should not override existing value
        assertEquals(200.0, storage.getValue("food"));
    }
    
    @Test
    void testExportState() {
        storage.setValue("food", 100.0);
        storage.setValue("water", 50.0);
        storage.setValue("stability", 0.8);
        
        Map<String, Double> state = storage.exportState();
        
        assertEquals(3, state.size());
        assertEquals(100.0, state.get("food"));
        assertEquals(50.0, state.get("water"));
        assertEquals(0.8, state.get("stability"));
    }
    
    @Test
    void testImportState() {
        Map<String, Double> state = new HashMap<>();
        state.put("food", 150.0);
        state.put("water", 75.0);
        state.put("morale", 0.9);
        
        storage.importState(state);
        
        assertEquals(150.0, storage.getValue("food"));
        assertEquals(75.0, storage.getValue("water"));
        assertEquals(0.9, storage.getValue("morale"));
    }
    
    @Test
    void testImportStateClearsPreviousValues() {
        storage.setValue("stone", 50.0);
        
        Map<String, Double> state = new HashMap<>();
        state.put("food", 100.0);
        
        storage.importState(state);
        
        assertEquals(100.0, storage.getValue("food"));
        assertEquals(0.0, storage.getValue("stone")); // Should be cleared
    }
    
    @Test
    void testMultipleResourcesAndCoefficients() {
        // Resources
        storage.setValue("food", 100.0);
        storage.setValue("water", 50.0);
        storage.setValue("stone", 30.0);
        storage.setValue("wood", 20.0);
        
        // Coefficients
        storage.setValue("stability", 0.8);
        storage.setValue("morale", 0.7);
        storage.setValue("cohesion", 0.9);
        
        assertEquals(100.0, storage.getValue("food"));
        assertEquals(0.8, storage.getValue("stability"));
        assertEquals(0.9, storage.getValue("cohesion"));
    }
}
