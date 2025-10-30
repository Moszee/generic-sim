package com.genericsim.backend.policy;

import com.genericsim.backend.model.*;
import com.genericsim.backend.policy.effects.StorageDecayEffect;
import com.genericsim.backend.service.FamilyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StorageDecayEffectTest {

    private StorageDecayEffect effect;

    @Autowired
    private FamilyService familyService;

    @BeforeEach
    public void setUp() {
        effect = new StorageDecayEffect();
    }

    @Test
    public void testPhaseAndPriority() {
        assertEquals(PolicyPhase.STORAGE_DECAY, effect.getPhase());
        assertEquals(100, effect.getPriority());
    }

    @Test
    public void testShouldApplyOnCorrectInterval() {
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setStorageDecayInterval(20);
        tribe.setPolicy(policy);

        TickContext context = new TickContext(tribe, familyService, new Random());

        // Should not apply on tick 0
        tribe.setCurrentTick(0);
        assertTrue(effect.shouldApply(context)); // 0 % 20 == 0

        // Should not apply on tick 10
        tribe.setCurrentTick(10);
        assertFalse(effect.shouldApply(context));

        // Should apply on tick 20
        tribe.setCurrentTick(20);
        assertTrue(effect.shouldApply(context));

        // Should apply on tick 40
        tribe.setCurrentTick(40);
        assertTrue(effect.shouldApply(context));
    }

    @Test
    public void testShouldNotApplyWhenIntervalIsZero() {
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setStorageDecayInterval(0);
        tribe.setPolicy(policy);
        tribe.setCurrentTick(20);

        TickContext context = new TickContext(tribe, familyService, new Random());

        assertFalse(effect.shouldApply(context));
    }

    @Test
    public void testApplyDecayReducesStorage() {
        // Setup tribe with 10% decay rate
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setStorageDecayRate(0.1); // 10% decay
        policy.setStorageDecayInterval(20);
        tribe.setPolicy(policy);
        tribe.setCurrentTick(20);
        tribe.setCentralStorage(new Resources(100, 100));

        // Create a family with storage
        Family family = new Family("TestFamily");
        family.setStorage(new Resources(100, 80));
        family.setTribe(tribe);
        tribe.addFamily(family);

        TickContext context = new TickContext(tribe, familyService, new Random());

        // Apply decay
        effect.apply(context);

        // Verify 10% decay was applied
        // Family: 100 * 0.9 = 90, 80 * 0.9 = 72
        assertEquals(90, family.getStorage().getFood());
        assertEquals(72, family.getStorage().getWater());

        // Central storage: 100 * 0.9 = 90, 100 * 0.9 = 90
        assertEquals(90, tribe.getCentralStorage().getFood());
        assertEquals(90, tribe.getCentralStorage().getWater());
    }
}
