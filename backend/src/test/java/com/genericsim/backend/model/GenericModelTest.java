package com.genericsim.backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the generic model - resources, technologies, and lifestyles
 */
class GenericModelTest {

    @Test
    void testResourceTypeEnum() {
        // Verify all resource types exist
        assertNotNull(ResourceType.FOOD);
        assertNotNull(ResourceType.WATER);
        assertNotNull(ResourceType.STONE);
        assertNotNull(ResourceType.WOOD);
        
        // Verify display names
        assertEquals("Food", ResourceType.FOOD.getDisplayName());
        assertEquals("Stone", ResourceType.STONE.getDisplayName());
    }

    @Test
    void testTechnologyTypeEnum() {
        // Verify all technology types exist
        assertNotNull(TechnologyType.FIRE);
        assertNotNull(TechnologyType.STONE_TOOLS);
        assertNotNull(TechnologyType.AGRICULTURE);
        assertNotNull(TechnologyType.ANIMAL_HUSBANDRY);
        
        // Verify display names
        assertEquals("Fire", TechnologyType.FIRE.getDisplayName());
        assertEquals("Agriculture", TechnologyType.AGRICULTURE.getDisplayName());
    }

    @Test
    void testLifestyleTypeEnum() {
        // Verify all lifestyle types exist
        assertNotNull(LifestyleType.HUNTER_GATHERER);
        assertNotNull(LifestyleType.NOMADIC);
        assertNotNull(LifestyleType.SETTLED);
        
        // Verify display names
        assertEquals("Hunter-Gatherer", LifestyleType.HUNTER_GATHERER.getDisplayName());
        assertEquals("Nomadic", LifestyleType.NOMADIC.getDisplayName());
    }

    @Test
    void testResourceDefinition() {
        ResourceDefinition def = new ResourceDefinition(ResourceType.FOOD, 1.0, 3.0);
        
        assertEquals(ResourceType.FOOD, def.getResourceType());
        assertEquals(1.0, def.getGatheringRate());
        assertEquals(3.0, def.getConsumptionRate());
        assertTrue(def.isRenewable());
    }

    @Test
    void testTechnologyDefinition() {
        TechnologyDefinition def = new TechnologyDefinition(TechnologyType.FIRE, 50);
        
        assertEquals(TechnologyType.FIRE, def.getTechnologyType());
        assertEquals(50, def.getResearchCost());
        
        // Test adding prerequisites
        def.getPrerequisites().add(TechnologyType.STONE_TOOLS);
        assertTrue(def.getPrerequisites().contains(TechnologyType.STONE_TOOLS));
        
        // Test resource costs
        def.getResourceCosts().put(ResourceType.WOOD, 10);
        assertEquals(10, def.getResourceCosts().get(ResourceType.WOOD));
    }

    @Test
    void testLifestyleDefinition() {
        LifestyleDefinition def = new LifestyleDefinition(LifestyleType.HUNTER_GATHERER, 1.0, 0.8);
        
        assertEquals(LifestyleType.HUNTER_GATHERER, def.getLifestyleType());
        assertEquals(1.0, def.getMobilityFactor());
        assertEquals(0.8, def.getCohesionFactor());
        
        // Test resource gathering modifiers
        def.getResourceGatheringModifiers().put(ResourceType.FOOD, 1.2);
        assertEquals(1.2, def.getResourceGatheringModifiers().get(ResourceType.FOOD));
        
        // Test required technologies
        def.getRequiredTechnologies().add(TechnologyType.FIRE);
        assertTrue(def.getRequiredTechnologies().contains(TechnologyType.FIRE));
    }

    @Test
    void testTribeTechnologies() {
        Tribe tribe = new Tribe("Test Tribe", "A test tribe");
        tribe.setLifestyle(LifestyleType.HUNTER_GATHERER);
        
        // Initially no technologies
        assertFalse(tribe.hasTechnology(TechnologyType.FIRE));
        
        // Add technology
        tribe.addTechnology(TechnologyType.FIRE);
        assertTrue(tribe.hasTechnology(TechnologyType.FIRE));
        
        // Add more technologies
        tribe.addTechnology(TechnologyType.STONE_TOOLS);
        assertTrue(tribe.hasTechnology(TechnologyType.STONE_TOOLS));
        
        // Remove technology
        tribe.removeTechnology(TechnologyType.FIRE);
        assertFalse(tribe.hasTechnology(TechnologyType.FIRE));
    }

    @Test
    void testTribeLifestyle() {
        Tribe tribe = new Tribe("Test Tribe", "A test tribe");
        
        // Default lifestyle
        assertEquals(LifestyleType.HUNTER_GATHERER, tribe.getLifestyle());
        
        // Change lifestyle
        tribe.setLifestyle(LifestyleType.NOMADIC);
        assertEquals(LifestyleType.NOMADIC, tribe.getLifestyle());
    }

    @Test
    void testGenericStorageInTribe() {
        Tribe tribe = new Tribe("Test Tribe", "A test tribe");
        ResourceStorage storage = new ResourceStorage();
        storage.initializeDefaults();
        tribe.setGenericStorage(storage);
        
        assertNotNull(tribe.getGenericStorage());
        assertEquals(0, tribe.getGenericStorage().getAmount(ResourceType.FOOD));
    }

    @Test
    void testGenericStorageInFamily() {
        Family family = new Family("Test Family");
        
        assertNotNull(family.getGenericStorage());
        assertEquals(0, family.getGenericStorage().getAmount(ResourceType.FOOD));
        assertEquals(0, family.getGenericStorage().getAmount(ResourceType.STONE));
    }
}
