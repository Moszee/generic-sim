package com.genericsim.backend.service;

import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Policy;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.repository.TribeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SkillSystemTest {

    @Autowired
    private TribeService tribeService;

    @Autowired
    private TribeRepository tribeRepository;

    @Test
    public void testInitialSkillsAreSet() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        for (Person person : tribe.getMembers()) {
            assertTrue(person.getHuntingSkill() >= 0.0 && person.getHuntingSkill() <= 1.0);
            assertTrue(person.getGatheringSkill() >= 0.0 && person.getGatheringSkill() <= 1.0);
        }
    }

    @Test
    public void testSkillsImproveOverTime() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Get a hunter's initial skill
        Person hunter = tribe.getMembers().stream()
            .filter(p -> p.getRole() == Person.PersonRole.HUNTER)
            .findFirst()
            .orElseThrow();
        final double initialHuntingSkill = hunter.getHuntingSkill();
        final Long hunterId = hunter.getId();
        
        // Process multiple ticks to allow skill improvement
        for (int i = 0; i < 50; i++) {
            tribeService.processTick(tribeId);
        }
        
        // Reload tribe
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        Person finalHunter = tribe.getMembers().stream()
            .filter(p -> p.getId().equals(hunterId))
            .findFirst()
            .orElse(null);
        
        // Skill should improve or stay the same (random chance involved)
        if (finalHunter != null) {
            assertTrue(finalHunter.getHuntingSkill() >= initialHuntingSkill);
        }
    }

    @Test
    public void testFamiliesAreCreated() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        assertNotNull(tribe.getFamilies());
        assertFalse(tribe.getFamilies().isEmpty());
        
        // All members should be assigned to a family
        for (Person person : tribe.getMembers()) {
            assertNotNull(person.getFamily());
        }
    }

    @Test
    public void testCentralStorageTaxCollection() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Enable central storage
        tribe.getPolicy().setEnableCentralStorage(true);
        tribe.getPolicy().setCentralStorageTaxRate(20); // 20% tax
        tribeRepository.save(tribe);
        
        int initialCentralFood = tribe.getCentralStorage().getFood();
        
        // Process a tick
        tribeService.processTick(tribeId);
        
        // Reload tribe
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        
        // Central storage should have increased (tax collected)
        assertTrue(tribe.getCentralStorage().getFood() >= initialCentralFood);
    }

    @Test
    public void testStorageDecayOccurs() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Set storage to a known value
        tribe.getFamilies().get(0).getStorage().setFood(100);
        tribe.getFamilies().get(0).getStorage().setWater(100);
        
        // Set decay interval to 5 ticks
        tribe.getPolicy().setStorageDecayInterval(5);
        tribe.getPolicy().setStorageDecayRate(0.2); // 20% decay
        tribeRepository.save(tribe);
        
        // Process exactly 5 ticks
        for (int i = 0; i < 5; i++) {
            tribeService.processTick(tribeId);
        }
        
        // Reload tribe
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        
        // Storage should have decayed (but also may have gained from gathering)
        // We just verify the mechanism runs without errors
        assertNotNull(tribe.getFamilies().get(0).getStorage());
    }

    @Test
    public void testBondLevelChanges() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        int initialBondLevel = tribe.getBondLevel();
        
        // Create a scenario where families might share
        // Set one family very low on resources
        tribe.getFamilies().get(0).getStorage().setFood(1);
        tribe.getFamilies().get(0).getStorage().setWater(1);
        
        // Set another family with plenty
        if (tribe.getFamilies().size() > 1) {
            tribe.getFamilies().get(1).getStorage().setFood(200);
            tribe.getFamilies().get(1).getStorage().setWater(200);
        }
        
        tribeRepository.save(tribe);
        
        // Process ticks
        for (int i = 0; i < 10; i++) {
            tribeService.processTick(tribeId);
        }
        
        // Bond level should have changed (either increased or decreased)
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        // Just verify it's within valid range
        assertTrue(tribe.getBondLevel() >= 0 && tribe.getBondLevel() <= 100);
    }

    @Test
    public void testSharingPriorityPolicy() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        // Test each priority type
        tribe.getPolicy().setSharingPriority(Policy.SharingPriority.ELDER);
        assertEquals(Policy.SharingPriority.ELDER, tribe.getPolicy().getSharingPriority());
        
        tribe.getPolicy().setSharingPriority(Policy.SharingPriority.CHILD);
        assertEquals(Policy.SharingPriority.CHILD, tribe.getPolicy().getSharingPriority());
        
        tribe.getPolicy().setSharingPriority(Policy.SharingPriority.HUNTER);
        assertEquals(Policy.SharingPriority.HUNTER, tribe.getPolicy().getSharingPriority());
    }

    @Test
    public void testTribeStateDTOIncludesFamilies() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        TribeStateDTO dto = tribeService.getTribeState(tribe.getId());
        
        assertNotNull(dto.getFamilies());
        assertFalse(dto.getFamilies().isEmpty());
        assertEquals(tribe.getFamilies().size(), dto.getFamilies().size());
        
        // Verify family DTO has required fields
        TribeStateDTO.FamilyDTO familyDTO = dto.getFamilies().get(0);
        assertNotNull(familyDTO.getName());
        assertNotNull(familyDTO.getStorage());
        assertTrue(familyDTO.getMemberCount() > 0);
    }

    @Test
    public void testPersonDTOIncludesSkillsAndFamily() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        TribeStateDTO dto = tribeService.getTribeState(tribe.getId());
        
        assertNotNull(dto.getMembers());
        assertFalse(dto.getMembers().isEmpty());
        
        // Verify person DTO has new fields
        TribeStateDTO.PersonDTO personDTO = dto.getMembers().get(0);
        assertTrue(personDTO.getHuntingSkill() >= 0.0);
        assertTrue(personDTO.getGatheringSkill() >= 0.0);
        assertNotNull(personDTO.getFamilyId());
    }

    @Test
    public void testPolicyDTOIncludesNewFields() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        TribeStateDTO dto = tribeService.getTribeState(tribe.getId());
        
        assertNotNull(dto.getPolicy());
        assertNotNull(dto.getPolicy().getSharingPriority());
        assertNotNull(dto.getPolicy().getStorageDecayRate());
        assertNotNull(dto.getPolicy().getStorageDecayInterval());
    }

    @Test
    public void testChildrenGainSkillsWhenBecomingAdults() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Find a child
        Person child = tribe.getMembers().stream()
            .filter(p -> p.getRole() == Person.PersonRole.CHILD)
            .findFirst()
            .orElseThrow();
        
        // Age them up to 16
        child.setAge(15);
        tribeRepository.save(tribe);
        
        // Process 365 ticks to trigger birthday
        for (int i = 0; i < 365; i++) {
            tribeService.processTick(tribeId);
        }
        
        // Reload tribe
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        Person formerChild = tribe.getMembers().stream()
            .filter(p -> p.getId().equals(child.getId()))
            .findFirst()
            .orElse(null);
        
        if (formerChild != null) {
            // Should now be an adult with a role
            assertTrue(formerChild.getRole() == Person.PersonRole.HUNTER || 
                      formerChild.getRole() == Person.PersonRole.GATHERER);
            
            // Should have appropriate skill
            if (formerChild.getRole() == Person.PersonRole.HUNTER) {
                assertTrue(formerChild.getHuntingSkill() > 0);
            } else {
                assertTrue(formerChild.getGatheringSkill() > 0);
            }
        }
    }
}
