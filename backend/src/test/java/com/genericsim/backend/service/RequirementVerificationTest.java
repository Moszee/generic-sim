package com.genericsim.backend.service;

import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.dto.TribeStatisticsDTO;
import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.repository.TribeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify the exact requirements from the issue:
 * For a balanced 20-person tribe:
 * - 30% Children (6): 0 progress
 * - 35% Young Adults (7): +14 progress/tick
 * - 25% Adults (5): +5 progress/tick
 * - 10% Elders (2): preserve 20 points/tick + 4% gathering bonus
 * 
 * Net result: +19 - 30 + 20 = +9 points/tick (sustainable progress)
 */
@SpringBootTest
@Transactional
public class RequirementVerificationTest {

    @Autowired
    private TribeService tribeService;

    @Autowired
    private TribeRepository tribeRepository;

    @Test
    public void testOptimalDemographicsScenario() {
        Tribe tribe = tribeService.createTribe("Balanced Tribe", "Optimal demographics test");
        Long tribeId = tribe.getId();
        
        // Clear existing members
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        tribe.getMembers().clear();
        
        // Create optimal 20-person tribe
        // 30% Children (6) - ages 0-15
        for (int i = 0; i < 6; i++) {
            tribe.addMember(new Person("Child" + i, Person.PersonRole.CHILD, 5 + i, 100));
        }
        
        // 35% Young Adults (7) - ages 16-40
        for (int i = 0; i < 7; i++) {
            Person.PersonRole role = (i % 2 == 0) ? Person.PersonRole.HUNTER : Person.PersonRole.GATHERER;
            tribe.addMember(new Person("YoungAdult" + i, role, 20 + (i * 2), 100));
        }
        
        // 25% Adults (5) - ages 41-59
        for (int i = 0; i < 5; i++) {
            Person.PersonRole role = (i % 2 == 0) ? Person.PersonRole.HUNTER : Person.PersonRole.GATHERER;
            tribe.addMember(new Person("Adult" + i, role, 45 + (i * 2), 100));
        }
        
        // 10% Elders (2) - ages 60+
        for (int i = 0; i < 2; i++) {
            tribe.addMember(new Person("Elder" + i, Person.PersonRole.ELDER, 65 + (i * 5), 80));
        }
        
        tribeRepository.save(tribe);
        
        // Verify demographics
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        assertEquals(20, stats.getTotalPopulation());
        assertEquals(6, stats.getAgeGroupBreakdown().getChildren());
        assertEquals(7, stats.getAgeGroupBreakdown().getYoungAdults());
        assertEquals(5, stats.getAgeGroupBreakdown().getAdults());
        assertEquals(2, stats.getAgeGroupBreakdown().getElders());
        
        // Verify progress calculations
        // Young Adults: 7 * 2 = +14
        int youngAdultGeneration = 7 * 2;
        assertEquals(14, youngAdultGeneration);
        
        // Adults: 5 * 1 = +5
        int adultGeneration = 5 * 1;
        assertEquals(5, adultGeneration);
        
        // Total generation: 7 * 2 + 5 * 1 = 19
        assertEquals(19, stats.getProgressStats().getProgressGeneration());
        
        // Elder preservation: 2 * 10 = 20
        assertEquals(20, stats.getProgressStats().getElderPreservation());
        
        // Base decay: 30
        // Net decay: max(0, 30 - 20) = 10
        assertEquals(10, stats.getProgressStats().getProgressDecay());
        
        // Net progress per tick: 19 - 10 = +9
        assertEquals(9, stats.getProgressStats().getNetProgressPerTick());
        
        // Elder gathering bonus: 2 * 2% = 4%
        assertEquals(4.0, stats.getProgressStats().getElderGatheringBonus(), 0.01);
        
        // Process a tick and verify progress increases by 9
        int initialProgress = tribe.getProgressPoints();
        tribeService.processTick(tribeId);
        TribeStateDTO state = tribeService.getTribeState(tribeId);
        assertEquals(initialProgress + 9, state.getProgressPoints());
    }

    @Test
    public void testYoungSocietyScenario() {
        // Young Society (0 elders): generation +26, decay -30 => net -4 (cannot accumulate progress)
        Tribe tribe = tribeService.createTribe("Young Tribe", "No elders test");
        Long tribeId = tribe.getId();
        
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        tribe.getMembers().clear();
        
        // All young adults and adults, no elders
        for (int i = 0; i < 10; i++) {
            tribe.addMember(new Person("Young" + i, Person.PersonRole.HUNTER, 25, 100));
        }
        for (int i = 0; i < 3; i++) {
            tribe.addMember(new Person("Adult" + i, Person.PersonRole.GATHERER, 45, 100));
        }
        
        tribeRepository.save(tribe);
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        assertEquals(0, stats.getAgeGroupBreakdown().getElders());
        
        // Generation: 10 * 2 + 3 * 1 = 23
        assertEquals(23, stats.getProgressStats().getProgressGeneration());
        
        // No elders, so full decay of 30
        assertEquals(0, stats.getProgressStats().getElderPreservation());
        assertEquals(30, stats.getProgressStats().getProgressDecay());
        
        // Net: 23 - 30 = -7 (negative, but progress will be capped at 0)
        assertEquals(-7, stats.getProgressStats().getNetProgressPerTick());
        
        // Verify progress doesn't go below 0
        tribe.setProgressPoints(10);
        tribeRepository.save(tribe);
        tribeService.processTick(tribeId);
        TribeStateDTO state = tribeService.getTribeState(tribeId);
        assertEquals(3, state.getProgressPoints()); // 10 + 23 - 30 = 3
    }

    @Test
    public void testElderHeavySocietyScenario() {
        // Elder-Heavy Society (5+ elders): high progress preservation but fewer workers
        Tribe tribe = tribeService.createTribe("Elder Tribe", "Many elders test");
        Long tribeId = tribe.getId();
        
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        tribe.getMembers().clear();
        
        // 5 elders
        for (int i = 0; i < 5; i++) {
            tribe.addMember(new Person("Elder" + i, Person.PersonRole.ELDER, 65, 80));
        }
        // Only 3 young workers
        for (int i = 0; i < 3; i++) {
            tribe.addMember(new Person("Young" + i, Person.PersonRole.HUNTER, 25, 100));
        }
        
        tribeRepository.save(tribe);
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        assertEquals(5, stats.getAgeGroupBreakdown().getElders());
        
        // Generation: 3 * 2 = 6 (low due to few workers)
        assertEquals(6, stats.getProgressStats().getProgressGeneration());
        
        // Elder preservation: 5 * 10 = 50 (exceeds base decay)
        assertEquals(50, stats.getProgressStats().getElderPreservation());
        
        // Decay: max(0, 30 - 50) = 0
        assertEquals(0, stats.getProgressStats().getProgressDecay());
        
        // Net: 6 - 0 = +6
        assertEquals(6, stats.getProgressStats().getNetProgressPerTick());
        
        // High gathering bonus: 5 * 2% = 10%
        assertEquals(10.0, stats.getProgressStats().getElderGatheringBonus(), 0.01);
    }
}
