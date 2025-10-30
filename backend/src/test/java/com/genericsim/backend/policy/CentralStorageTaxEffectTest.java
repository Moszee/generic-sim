package com.genericsim.backend.policy;

import com.genericsim.backend.model.*;
import com.genericsim.backend.policy.effects.CentralStorageTaxEffect;
import com.genericsim.backend.service.FamilyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CentralStorageTaxEffectTest {

    private CentralStorageTaxEffect effect;

    @Mock
    private FamilyService familyService;

    @BeforeEach
    public void setUp() {
        effect = new CentralStorageTaxEffect();
    }

    @Test
    public void testPhaseAndPriority() {
        assertEquals(PolicyPhase.POST_GATHERING, effect.getPhase());
        assertEquals(100, effect.getPriority());
    }

    @Test
    public void testShouldApplyWhenCentralStorageEnabled() {
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setEnableCentralStorage(true);
        tribe.setPolicy(policy);

        TickContext context = new TickContext(tribe, familyService, new Random());

        assertTrue(effect.shouldApply(context));
    }

    @Test
    public void testShouldNotApplyWhenCentralStorageDisabled() {
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setEnableCentralStorage(false);
        tribe.setPolicy(policy);

        TickContext context = new TickContext(tribe, familyService, new Random());

        assertFalse(effect.shouldApply(context));
    }

    @Test
    public void testApplyTaxOnGatheredResources() {
        // Setup tribe with central storage enabled and 10% tax
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setEnableCentralStorage(true);
        policy.setCentralStorageTaxRate(10);
        tribe.setPolicy(policy);
        tribe.setCentralStorage(new Resources(0, 0));

        // Create a family with initial storage
        Family family = new Family("TestFamily");
        family.setStorage(new Resources(50, 40));
        family.setTribe(tribe);
        tribe.addFamily(family);

        // Create context and snapshot initial storage
        TickContext context = new TickContext(tribe, familyService, new Random());
        context.snapshotFamilyStorage(family);

        // Simulate gathering: add 100 food and 80 water
        family.getStorage().setFood(150);
        family.getStorage().setWater(120);

        // Apply the tax effect
        effect.apply(context);

        // Verify 10% tax was collected
        // Gathered: 100 food, 80 water
        // Tax (10%): 10 food, 8 water
        // Family should have: 150 - 10 = 140 food, 120 - 8 = 112 water
        // Central should have: 10 food, 8 water
        assertEquals(140, family.getStorage().getFood());
        assertEquals(112, family.getStorage().getWater());
        assertEquals(10, tribe.getCentralStorage().getFood());
        assertEquals(8, tribe.getCentralStorage().getWater());
    }

    @Test
    public void testNoTaxWhenNothingGathered() {
        // Setup tribe with central storage enabled and 10% tax
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setEnableCentralStorage(true);
        policy.setCentralStorageTaxRate(10);
        tribe.setPolicy(policy);
        tribe.setCentralStorage(new Resources(0, 0));

        // Create a family with storage
        Family family = new Family("TestFamily");
        family.setStorage(new Resources(50, 40));
        family.setTribe(tribe);
        tribe.addFamily(family);

        // Create context and snapshot - no gathering happens
        TickContext context = new TickContext(tribe, familyService, new Random());
        context.snapshotFamilyStorage(family);
        // Don't add any resources - simulate no gathering

        // Apply the tax effect
        effect.apply(context);

        // Verify no tax was collected (nothing was gathered)
        assertEquals(50, family.getStorage().getFood());
        assertEquals(40, family.getStorage().getWater());
        assertEquals(0, tribe.getCentralStorage().getFood());
        assertEquals(0, tribe.getCentralStorage().getWater());
    }

    @Test
    public void testMultipleFamiliesTaxed() {
        // Setup tribe
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        policy.setEnableCentralStorage(true);
        policy.setCentralStorageTaxRate(20); // 20% tax
        tribe.setPolicy(policy);
        tribe.setCentralStorage(new Resources(0, 0));

        // Create two families
        Family family1 = new Family("Family1");
        family1.setStorage(new Resources(10, 10));
        family1.setTribe(tribe);
        tribe.addFamily(family1);

        Family family2 = new Family("Family2");
        family2.setStorage(new Resources(20, 20));
        family2.setTribe(tribe);
        tribe.addFamily(family2);

        // Create context and snapshot
        TickContext context = new TickContext(tribe, familyService, new Random());
        context.snapshotFamilyStorage(family1);
        context.snapshotFamilyStorage(family2);

        // Simulate gathering
        family1.getStorage().setFood(110); // gathered 100
        family1.getStorage().setWater(90);  // gathered 80
        family2.getStorage().setFood(70);  // gathered 50
        family2.getStorage().setWater(70);  // gathered 50

        // Apply the tax effect
        effect.apply(context);

        // Family 1: 20% of 100 food = 20, 20% of 80 water = 16
        assertEquals(90, family1.getStorage().getFood());
        assertEquals(74, family1.getStorage().getWater());

        // Family 2: 20% of 50 food = 10, 20% of 50 water = 10
        assertEquals(60, family2.getStorage().getFood());
        assertEquals(60, family2.getStorage().getWater());

        // Central storage: 20 + 10 = 30 food, 16 + 10 = 26 water
        assertEquals(30, tribe.getCentralStorage().getFood());
        assertEquals(26, tribe.getCentralStorage().getWater());
    }
}
