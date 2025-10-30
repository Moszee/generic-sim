package com.genericsim.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for generic resource storage functionality
 */
class ResourceStorageTest {

    private ResourceStorage storage;

    @BeforeEach
    void setUp() {
        storage = new ResourceStorage();
        storage.initializeDefaults();
    }

    @Test
    void testInitializeDefaults() {
        // All resource types should be initialized to 0
        assertEquals(0, storage.getAmount(ResourceType.FOOD));
        assertEquals(0, storage.getAmount(ResourceType.WATER));
        assertEquals(0, storage.getAmount(ResourceType.STONE));
        assertEquals(0, storage.getAmount(ResourceType.WOOD));
    }

    @Test
    void testSetAndGetAmount() {
        storage.setAmount(ResourceType.FOOD, 100);
        assertEquals(100, storage.getAmount(ResourceType.FOOD));
        
        storage.setAmount(ResourceType.STONE, 50);
        assertEquals(50, storage.getAmount(ResourceType.STONE));
    }

    @Test
    void testAddAmount() {
        storage.setAmount(ResourceType.FOOD, 50);
        storage.addAmount(ResourceType.FOOD, 30);
        assertEquals(80, storage.getAmount(ResourceType.FOOD));
    }

    @Test
    void testRemoveAmount() {
        storage.setAmount(ResourceType.WATER, 100);
        
        assertTrue(storage.removeAmount(ResourceType.WATER, 30));
        assertEquals(70, storage.getAmount(ResourceType.WATER));
        
        assertFalse(storage.removeAmount(ResourceType.WATER, 100));
        assertEquals(70, storage.getAmount(ResourceType.WATER)); // Should remain unchanged
    }

    @Test
    void testHasAmount() {
        storage.setAmount(ResourceType.WOOD, 50);
        
        assertTrue(storage.hasAmount(ResourceType.WOOD, 50));
        assertTrue(storage.hasAmount(ResourceType.WOOD, 30));
        assertFalse(storage.hasAmount(ResourceType.WOOD, 51));
    }

    @Test
    void testNegativeAmountNotAllowed() {
        storage.setAmount(ResourceType.FOOD, -10);
        assertEquals(0, storage.getAmount(ResourceType.FOOD));
    }

    @Test
    void testMultipleResourceTypes() {
        storage.setAmount(ResourceType.FOOD, 100);
        storage.setAmount(ResourceType.WATER, 150);
        storage.setAmount(ResourceType.STONE, 30);
        storage.setAmount(ResourceType.WOOD, 40);
        
        assertEquals(100, storage.getAmount(ResourceType.FOOD));
        assertEquals(150, storage.getAmount(ResourceType.WATER));
        assertEquals(30, storage.getAmount(ResourceType.STONE));
        assertEquals(40, storage.getAmount(ResourceType.WOOD));
    }
}
