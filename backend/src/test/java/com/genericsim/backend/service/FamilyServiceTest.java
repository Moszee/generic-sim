package com.genericsim.backend.service;

import com.genericsim.backend.model.*;
import com.genericsim.backend.repository.FamilyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FamilyServiceTest {

    @Autowired
    private FamilyService familyService;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private TribeService tribeService;

    private Tribe testTribe;

    @BeforeEach
    public void setUp() {
        testTribe = tribeService.createTribe("Test Tribe", "A test tribe for family tests");
    }

    @Test
    public void testInitializeFamilies() {
        // Verify families were created
        assertFalse(testTribe.getFamilies().isEmpty());
        
        // Verify all members are assigned to families
        for (Person person : testTribe.getMembers()) {
            assertNotNull(person.getFamily());
        }
        
        // Verify family storage is initialized
        for (Family family : testTribe.getFamilies()) {
            assertNotNull(family.getStorage());
            assertTrue(family.getStorage().getFood() > 0);
            assertTrue(family.getStorage().getWater() > 0);
        }
    }

    @Test
    public void testConsumeFamilyResources() {
        Family family = testTribe.getFamilies().get(0);
        int initialFood = family.getStorage().getFood();
        int initialWater = family.getStorage().getWater();
        int memberCount = family.getMembers().size();
        
        boolean hasSufficient = familyService.consumeFamilyResources(family);
        
        // Should consume 3 food and 4 water per member
        assertEquals(initialFood - (memberCount * 3), family.getStorage().getFood());
        assertEquals(initialWater - (memberCount * 4), family.getStorage().getWater());
    }

    @Test
    public void testConsumeFamilyResourcesInsufficient() {
        Family family = testTribe.getFamilies().get(0);
        // Set resources very low
        family.getStorage().setFood(1);
        family.getStorage().setWater(1);
        
        boolean hasSufficient = familyService.consumeFamilyResources(family);
        
        assertFalse(hasSufficient);
        // Resources should not go below 0
        assertEquals(0, family.getStorage().getFood());
        assertEquals(0, family.getStorage().getWater());
    }

    @Test
    public void testBorrowResourcesSuccess() {
        // Set up two families - one rich, one poor
        Family richFamily = testTribe.getFamilies().get(0);
        Family poorFamily = testTribe.getFamilies().get(1);
        
        richFamily.getStorage().setFood(200);
        richFamily.getStorage().setWater(200);
        poorFamily.getStorage().setFood(5);
        poorFamily.getStorage().setWater(5);
        
        // Set high bond level for guaranteed success
        testTribe.setBondLevel(100);
        
        boolean borrowed = familyService.borrowResources(poorFamily, testTribe, 20, 20);
        
        assertTrue(borrowed);
        // Poor family should have received resources
        assertTrue(poorFamily.getStorage().getFood() > 5);
        assertTrue(poorFamily.getStorage().getWater() > 5);
        // Rich family should have given resources
        assertTrue(richFamily.getStorage().getFood() < 200);
    }

    @Test
    public void testBorrowResourcesFailsWithLowBond() {
        Family richFamily = testTribe.getFamilies().get(0);
        Family poorFamily = testTribe.getFamilies().get(1);
        
        richFamily.getStorage().setFood(200);
        richFamily.getStorage().setWater(200);
        poorFamily.getStorage().setFood(5);
        poorFamily.getStorage().setWater(5);
        
        // Set very low bond level
        testTribe.setBondLevel(0);
        
        boolean borrowed = familyService.borrowResources(poorFamily, testTribe, 20, 20);
        
        // With 0% bond, borrowing should fail
        assertFalse(borrowed);
        // Bond should decrease on failed sharing
        assertEquals(0, testTribe.getBondLevel()); // Already at 0, can't decrease
    }

    @Test
    public void testAccessCentralStorageWhenEnabled() {
        // Enable central storage
        testTribe.getPolicy().setEnableCentralStorage(true);
        testTribe.getCentralStorage().setFood(100);
        testTribe.getCentralStorage().setWater(100);
        
        Family family = testTribe.getFamilies().get(0);
        family.getStorage().setFood(0);
        family.getStorage().setWater(0);
        
        boolean accessed = familyService.accessCentralStorage(family, testTribe, 20, 20);
        
        assertTrue(accessed);
        assertEquals(20, family.getStorage().getFood());
        assertEquals(20, family.getStorage().getWater());
        assertEquals(80, testTribe.getCentralStorage().getFood());
        assertEquals(80, testTribe.getCentralStorage().getWater());
    }

    @Test
    public void testAccessCentralStorageWhenDisabled() {
        // Disable central storage
        testTribe.getPolicy().setEnableCentralStorage(false);
        
        Family family = testTribe.getFamilies().get(0);
        family.getStorage().setFood(0);
        family.getStorage().setWater(0);
        
        boolean accessed = familyService.accessCentralStorage(family, testTribe, 20, 20);
        
        assertFalse(accessed);
        assertEquals(0, family.getStorage().getFood());
        assertEquals(0, family.getStorage().getWater());
    }

    @Test
    public void testSelectMemberToSufferElder() {
        Family family = testTribe.getFamilies().get(0);
        testTribe.getPolicy().setSharingPriority(Policy.SharingPriority.ELDER);
        
        Person selected = familyService.selectMemberToSuffer(family, testTribe.getPolicy());
        
        assertNotNull(selected);
        // Should select elder or oldest person
        assertTrue(selected.getRole() == Person.PersonRole.ELDER || 
                   selected.getAge() >= family.getMembers().stream()
                       .mapToInt(Person::getAge).max().orElse(0));
    }

    @Test
    public void testSelectMemberToSufferChild() {
        Family family = testTribe.getFamilies().get(0);
        testTribe.getPolicy().setSharingPriority(Policy.SharingPriority.CHILD);
        
        Person selected = familyService.selectMemberToSuffer(family, testTribe.getPolicy());
        
        assertNotNull(selected);
        // Should prefer child or youngest person
    }

    @Test
    public void testSelectMemberToSufferHunter() {
        Family family = testTribe.getFamilies().get(0);
        testTribe.getPolicy().setSharingPriority(Policy.SharingPriority.HUNTER);
        
        Person selected = familyService.selectMemberToSuffer(family, testTribe.getPolicy());
        
        assertNotNull(selected);
    }

    @Test
    public void testApplyStorageDecay() {
        Family family = testTribe.getFamilies().get(0);
        family.getStorage().setFood(100);
        family.getStorage().setWater(100);
        
        testTribe.getCentralStorage().setFood(100);
        testTribe.getCentralStorage().setWater(100);
        
        double decayRate = 0.1; // 10% decay
        familyService.applyStorageDecay(testTribe, decayRate);
        
        // Should have 90% of original resources
        assertEquals(90, family.getStorage().getFood());
        assertEquals(90, family.getStorage().getWater());
        assertEquals(90, testTribe.getCentralStorage().getFood());
        assertEquals(90, testTribe.getCentralStorage().getWater());
    }

    @Test
    public void testGetFamiliesByTribe() {
        List<Family> families = familyService.getFamiliesByTribe(testTribe.getId());
        
        assertNotNull(families);
        assertFalse(families.isEmpty());
        assertEquals(testTribe.getFamilies().size(), families.size());
    }
}
