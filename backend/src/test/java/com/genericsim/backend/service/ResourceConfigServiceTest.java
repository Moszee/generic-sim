package com.genericsim.backend.service;

import com.genericsim.backend.config.ResourceConfigurationProperties;
import com.genericsim.backend.model.ResourceOrCoefficientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ResourceConfigService - configuration loading and management
 */
class ResourceConfigServiceTest {
    
    private ResourceConfigService service;
    private ResourceConfigurationProperties properties;
    
    @BeforeEach
    void setUp() {
        properties = new ResourceConfigurationProperties();
        
        // Add sample resources
        List<ResourceOrCoefficientConfig> resources = new ArrayList<>();
        
        ResourceOrCoefficientConfig food = new ResourceOrCoefficientConfig();
        food.setId("food");
        food.setName("Jedzenie");
        food.setType("resource");
        food.setMin(0);
        food.setMax(10000);
        food.setDefaultValue(100);
        resources.add(food);
        
        ResourceOrCoefficientConfig water = new ResourceOrCoefficientConfig();
        water.setId("water");
        water.setName("Woda");
        water.setType("resource");
        water.setMin(0);
        water.setMax(10000);
        water.setDefaultValue(100);
        resources.add(water);
        
        properties.setResources(resources);
        
        // Add sample coefficients
        List<ResourceOrCoefficientConfig> coefficients = new ArrayList<>();
        
        ResourceOrCoefficientConfig stability = new ResourceOrCoefficientConfig();
        stability.setId("stability");
        stability.setName("Stabilność");
        stability.setType("coefficient");
        stability.setMin(0);
        stability.setMax(1);
        stability.setDefaultValue(0.5);
        coefficients.add(stability);
        
        ResourceOrCoefficientConfig morale = new ResourceOrCoefficientConfig();
        morale.setId("morale");
        morale.setName("Morale");
        morale.setType("coefficient");
        morale.setMin(0);
        morale.setMax(1);
        morale.setDefaultValue(0.5);
        
        Map<String, Double> affects = new HashMap<>();
        affects.put("stability", 0.2);
        morale.setAffects(affects);
        
        coefficients.add(morale);
        
        properties.setCoefficients(coefficients);
        
        service = new ResourceConfigService(properties);
        service.initialize();
    }
    
    @Test
    void testGetConfig() {
        ResourceOrCoefficientConfig food = service.getConfig("food");
        assertNotNull(food);
        assertEquals("food", food.getId());
        assertEquals("Jedzenie", food.getName());
        assertEquals("resource", food.getType());
    }
    
    @Test
    void testGetConfigReturnsNullForNonExistent() {
        assertNull(service.getConfig("nonexistent"));
    }
    
    @Test
    void testHasConfig() {
        assertTrue(service.hasConfig("food"));
        assertTrue(service.hasConfig("stability"));
        assertFalse(service.hasConfig("nonexistent"));
    }
    
    @Test
    void testGetAllConfigs() {
        Map<String, ResourceOrCoefficientConfig> configs = service.getAllConfigs();
        assertEquals(4, configs.size());
        assertTrue(configs.containsKey("food"));
        assertTrue(configs.containsKey("water"));
        assertTrue(configs.containsKey("stability"));
        assertTrue(configs.containsKey("morale"));
    }
    
    @Test
    void testGetResourceConfigs() {
        Map<String, ResourceOrCoefficientConfig> resources = service.getResourceConfigs();
        assertEquals(2, resources.size());
        assertTrue(resources.containsKey("food"));
        assertTrue(resources.containsKey("water"));
        assertFalse(resources.containsKey("stability"));
    }
    
    @Test
    void testGetCoefficientConfigs() {
        Map<String, ResourceOrCoefficientConfig> coefficients = service.getCoefficientConfigs();
        assertEquals(2, coefficients.size());
        assertTrue(coefficients.containsKey("stability"));
        assertTrue(coefficients.containsKey("morale"));
        assertFalse(coefficients.containsKey("food"));
    }
    
    @Test
    void testConfigurationWithProductionRates() {
        ResourceOrCoefficientConfig food = service.getConfig("food");
        assertNotNull(food.getProduction());
    }
    
    @Test
    void testConfigurationWithAffects() {
        ResourceOrCoefficientConfig morale = service.getConfig("morale");
        assertNotNull(morale.getAffects());
        assertEquals(0.2, morale.getAffects().get("stability"));
    }
    
    @Test
    void testDefaultValueIsSet() {
        ResourceOrCoefficientConfig food = service.getConfig("food");
        assertEquals(100, food.getDefaultValue());
        
        ResourceOrCoefficientConfig stability = service.getConfig("stability");
        assertEquals(0.5, stability.getDefaultValue());
    }
    
    @Test
    void testMinMaxValuesAreSet() {
        ResourceOrCoefficientConfig food = service.getConfig("food");
        assertEquals(0, food.getMin());
        assertEquals(10000, food.getMax());
        
        ResourceOrCoefficientConfig stability = service.getConfig("stability");
        assertEquals(0, stability.getMin());
        assertEquals(1, stability.getMax());
    }
}
